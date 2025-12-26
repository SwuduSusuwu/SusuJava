/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> has the newest version of `./susuwu/FishSim.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

package susuwu; /* Usage: `import susuwu.FishSim;` */

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import susuwu.SimUsages; /* `class SimUsages`, `enum FpsTextMode` */
import susuwu.Calculus; /* `Calculus.pow2()` */
import susuwu.Forces; /* `class Forces implements java.lang.Cloneable` */
import susuwu.ImmutablePosBounds; /* `enum PosBoundsMode`: which stores how sims enforce bounds. */
import susuwu.PosBounds; /* `class PosBounds : extends ImmutablePosBounds`, `PosBounds.set*(PosBounds*)` */
import susuwu.ImmutablePos; /* `class ImmutablePos implements java.util.RandomAccess` */
import susuwu.Pos; /* `class Pos extends ImmutablePos` */
import susuwu.Pos2; /* `class Pos extends Pos` */
//    public static class Pos2 extends double[2] {} // `{Pos2[0], Pos2[1]}` is `{x, y}` position (or resolution), or is `{pos[0], pos[0]}` motion (derivative of position), or is is `{d2x, d2y}` acceleration (derivative number 2). This was supposed to do what `typedef` does (wish for future-proof (limitless dimensions) virtual `class` with functions for numerous transforms).
// TODO: test how much of `java`'s [static `Array` overhead](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/Physics_sims_which_structures_to_use.md#separate-variables-versus-dim-lists) `java`'s toolkit optimizes for you. If performance is a problem, choose a new approach to use.

/**
 * Simple [*JavaFX*](https://github.com/openjdk/jfx) fish sim, which includes reusable {@code public class}s (for new sims to use). Most of the reusable {@code public class}s are in other {@code .java} sources for {@code package susuwu}
 * This ([`./susuwu/FishSim.java`](./FishSim.java)) uses pseudo-*Markdown* for comments, but [`./posts/FishSim.md`](../posts/FishSim.md) is the actual [*Markdown*](https://github.github.com/gfm/) document for this.
 * Notice: replaced most of [*Solar-Pro-2*'s original `FishSim.java`](https://github.com/SwuduSusuwu/SusuJava/blob/solarPro2FishSim/susuwu/FishSim.java), as [`./posts/FishSim.md#intro`](../posts/FishSim.md#intro) documents (plus [*GitHub*'s `/compare/` tool shows](https://github.com/SwuduSusuwu/SusuJava/compare/solarPro2FishSim..susuFishSim#diff-8c440bb92bc6939e1450542897e0bbb1a8737b93808ea63ed32784edfacef4b4).
 */
public class FishSim extends Application {
	public enum PhysicsMode { // `PhysicsMode` says how to execute `updateFish()`
		synchronousHomo,      // `updateFish()` once per `refreshLoop()`.
		synchronousInterval,  // `updateFish()` per `positionInterval` `refreshLoop()`s.
		asynchronousHomo,     // `executor.submit(() -> updateFish());` once per `refreshLoop()`.
		asynchronousInterval, // `executor.submit(() -> updateFish());` per `positionInterval` `refreshLoop()`s.
		separateUnbound,      // `new AnimationTimer() { public void handle(long now) { updateFish(); }`
		separateFps,          // `Timeline timeline = new Timeline( new KeyFrame(Duration.millis(1000.0 / physicsRefreshHertz), event -> { updateFish(); })`
	}
	private static PhysicsMode monitorRefreshMode = PhysicsMode.separateFps; // `monitorRefreshMode` must use `.separateUnbound` or `.separateFps`.
	private static PhysicsMode physicsMode = PhysicsMode.separateFps; // Notice: if `PhysicsMode.*Interval`, must set `positionInterval`. if `PhysicsMode.separateFps`, must set `physicsRefreshHertz`.

	static ReentrantLock renderFishLock = new ReentrantLock();
	static ReentrantLock updateFishLock = new ReentrantLock();
	// TODO: remove `static` from {`resolution`, `bounds`}, to allow to remove `static` from {`setResolution()`, `renderFishLock, `updateFishLock`}, so `FishSim` allows numerous windows
	public static boolean setResolution(int[] newResolution) {
		assert 2 == newResolution.length;
		assert 0 < newResolution[0]; //TODO: allow "headless" instances with `resolution = {0, 0}`?
		assert 0 < newResolution[1];
		updateFishLock.lock();
		renderFishLock.lock();
		resolution = newResolution;
		resolutionf.pos[0] = resolution[0]; resolutionf.pos[1] = resolution[1];
		resolutionfSlash2.pos[0] = resolution[0] / 2; resolutionfSlash2.pos[1] = resolution[1] / 2;
//		resolutionfSlash2 = resolutionf.slashScalar(2); // TODO: if sure that no functions store references to the original instance's address, replace the above row with this (since simple source code is less bug prone)
		resVolume = resolution[0] * resolution[1];
//		resVolume = (int)resolutionf.volume(); // TODO: if this rounds, replace the above row with this, since simple source is less bug prone
		posBounds.setBounds(new double[] { resolution[0] * boundsResolutionFactor, resolution[1] * boundsResolutionFactor });
//		posBounds.setBounds(resolutionf.starScalar(boundsResolutionFactor).pos); // TODO: if sure that no functions store references to the original instance, replace the above row with this (since simple source code is less bug prone)
		// canvas = new Canvas(resolution[0], resolution[1]); // TODO: replace with `canvas.setWidth(resolution[0]); canvas.setHeight(resolution[1]);`?
		// gc = canvas.getGraphicsContext2D();
		// scene = new Scene(root, resolution[0], resolution[1], Color.LIGHTBLUE); // replace with `scene.widthProperty().bind(primaryStage.widthProperty());`?
		// stage.setScene(scene);
		renderFishLock.unlock();
		updateFishLock.unlock();
		return true;
	}
	private static int[] resolution = {1280, 720};
	private static Pos2 resolutionf = new Pos2(resolution[0], resolution[1]);
	private static Pos resolutionfSlash2 = resolutionf.slashScalar(2); // Improves execution of inner loops which use this
	private static int resVolume = resolution[0] * resolution[1];
//	private static int resVolume = (int)resolutionf.volume(); // TODO: if this rounds, replace the above row with this, since simple source is less bug prone
	private static double boundsResolutionFactor = 2; // `resolution[dim] * 2` gives best results (sufficient room for natural ocean, small enough for most CPUs to process). Notice: Powers of 2 give improved versions of most formulas for computers, but for now this allows all values
	private static PosBounds posBounds = new PosBounds(PosBounds.PosBoundsMode.wrapAroundResolution, resolutionf.starScalar(boundsResolutionFactor)); // for simple sims, use `resolutionf.clone()`
	private static double fishVolume = 200; // Uses resolution of `Fish::render()`.
	private static double fishLengthsSep = 62; // Average `Fish`-lengths distance  from `Fish` to `Fish`.
	private static double fishPerVolume = 1 / fishVolume / fishLengthsSep; // `Fish` per volume (for 2D, volume is resolution).
	private static int fishCount = (int)(posBounds.getBoundsVolume() * fishPerVolume);
	private static int gridResolution = 100; // Notice: set this to `Colllections.max({forces*.distance})` (which should equal what most sims call "view distance"), so that all relevent `Fish` are processed.
	private static int positionInterval = 2; // The `refreshCounter` per `Fish::applyFlockingRulesUpdate()`
	public static double monitorRefreshHertz = 60.0; // The `SimUsages.fps` to wish for // Notice: since this limits `SimUsages.fps` to `monitorRefreshHertz`, this prevents benchmarks which use `FpsTextMode.fps` (or `FpsTextMode.ms`). Benchmarks can still use `FpsTextMode.msSpec` (or `FpsTextMode.msFish`).
	public static double physicsRefreshHertz = monitorRefreshHertz / positionInterval; // The `1 / SimUsages.physicsMs` to wish for // Notice: unknown what `javafx.animation.Timeline` does if `physicsRefreshHertz > (1 / SimUsages.physicsMs)`, but guess thus stalls or consumes multiple executors

	private List<Fish> fishList = new ArrayList<>();
	private int fishShown = 0;
	private List<Fish>[][] grid; /* `listToPartitions(List<>[][] grid, List<> list)` uses this */
	private Random random = new Random();
	private Pane root = new Pane();
	private Canvas canvas = new Canvas(resolution[0], resolution[1]);
	private GraphicsContext gc = canvas.getGraphicsContext2D();
	private Stage stage;
	SimUsages simUsages = new SimUsages(root);
//	simUsages.fpsTextMode = FpsTextMode.allUsages.value; // TODO: "error: <identifier> expected" solution

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// Initialize fish
		for(int i = 0; i < fishCount; i++) {
			Pos2 pos = new Pos2(random.nextDouble() * posBounds.getBounds(0), random.nextDouble() * posBounds.getBounds(1));
			Pos2 dpos = new Pos2((random.nextDouble() * 2 - 1) * Fish.dposMax, (random.nextDouble() * 2 - 1) * Fish.dposMax);
			fishList.add(new Fish(pos, dpos, Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble())));
		}

		root.getChildren().add(canvas);

		Scene scene = new Scene(root, resolution[0], resolution[1], Color.LIGHTBLUE);
		stage = primaryStage;
		primaryStage.setScene(scene);
		primaryStage.setTitle("Fish Simulation (Boids)");
		primaryStage.setResizable(false);
		primaryStage.show();
		simUsages.show();

		posBounds.setGridResolution(gridResolution);
		grid = new ArrayList[posBounds.getGridSize(0)][posBounds.getGridSize(1)]; /* `listToPartitions(List<>[][] grid, List<> list)` uses this */
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[i].length; j++) {
				grid[i][j] = new ArrayList<>();
			}
		}

		// Start animation loop
		switch(monitorRefreshMode) { // `PhysicsMode.` is omitted from all `case`s, to support old `java --source` versions
		case separateUnbound:
			new AnimationTimer() {
				@Override
				public void handle(long now) { refreshLoop(now); }
			}.start(); // Notice: replace `Timeline` with this for benchmarks which use `FpsTextMode.fps` (or `FpsTextMode.ms`).
			break;
		case separateFps:
			Timeline timeline = new Timeline(
				new KeyFrame(Duration.millis(1000.0 / monitorRefreshHertz), event -> { refreshLoop(System.nanoTime()); })
			); // Notice: since this limits `SimUsages.fps` to `monitorRefreshHertz`, this prevents benchmarks which use `FpsTextMode.fps` (or `FpsTextMode.ms`). Benchmarks can still use `FpsTextMode.msSpec` (or `FpsTextMode.msFish`).
			timeline.setCycleCount(Animation.INDEFINITE);
			timeline.play();
			break;
		default:
			throw new IllegalArgumentException("Unsupported `PhysicsMode monitorRefreshMode`: " + monitorRefreshMode);
		}
		switch(physicsMode) { // `PhysicsMode.` is omitted from all `case`s, to support old `java --source` versions
		case separateUnbound:
			new AnimationTimer() {
				@Override
				public void handle(long now) { updateFish(); }
			}.start();
			break;
		case separateFps:
			Timeline loopPerSecond = new Timeline(
				new KeyFrame(Duration.millis(1000.0 / physicsRefreshHertz), event -> { updateFish(); })
			);
			loopPerSecond.setCycleCount(Animation.INDEFINITE);
			loopPerSecond.play();
			break;
		}
	}

	private void refreshLoop(long now) {
		simUsages.startRefresh();
		switch(physicsMode) { // `PhysicsMode.` is omitted from all `case`s, to support old `java --source` versions
		case synchronousHomo:
			updateFish();
			break;
		case synchronousInterval:
			if(simUsages.refreshCounter % positionInterval == 0) {
				updateFish();
			}
			break;
		case asynchronousHomo:
			executor.submit(() -> updateFish());
			break;
		case asynchronousInterval:
			if(simUsages.refreshCounter % positionInterval == 0) {
				executor.submit(() -> updateFish());
			}
			break;
		case separateUnbound:
		case separateFps:
			break; // no-op for both, since `start(Stage primaryStage)` processes thus
		default:
			throw new IllegalArgumentException("Unsupported `PhysicsMode physicsMode`: " + physicsMode);
		}

		renderFish();
		simUsages.postRefresh(now, fishShown, fishList.size());
	}

	private void outOfBounds(String function, Fish fish) {
		/* Notice: `outOfBounds()` has numerous sensible actions other than to print to `stderr`: `fish.die()`, `fish.stop()`, `fish.reverse()`, `fish.wrapAround()` */
		System.err.println(function + ": " + posBounds.posOutOfBoundsStr(fish.pos, "Fish.pos"));
	}

	/* Spatial partitioning (simple grid system). TODO: generic version of this (accept all `class`s with `#isInBounds` plus `#pos`). */
	private void listToPartitions(List<Fish>[][] grid, List<Fish> list) {
		assert grid.length == (int)Math.ceil(posBounds.getBounds(0) / gridResolution);
		assert grid[0].length == (int)Math.ceil(posBounds.getBounds(1) / gridResolution);
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[i].length; j++) {
				grid[i][j].clear();
			}
		}
		for(Fish fish : list) { /* Assign list members to grid sections */
			if(fish.isInBounds) {
				int[] gridPos = {(int) (fish.pos.pos[0] / gridResolution), (int) (fish.pos.pos[1] / gridResolution)};
				grid[gridPos[0]][gridPos[1]].add(fish); // if `gridPos` is not in bounds, this will `throw new IndexOutOfBoundsException()`. But `Fish.setPos()` uses `PosBounds::posBounds.posBound()` which, which ensures `Fish.pos` bounds to `FishSim.resolution`, so this will not `throw`.
			}
		}
	}

	private void updateFish() {
		updateFishLock.lock();
		simUsages.startPhysics();

		listToPartitions(grid, fishList);

		// Update each fish
		for(Fish fish : fishList) {
			fish.applyFlockingRules(fishList, grid);
			fish.update();
		}

		simUsages.postPhysics();
		updateFishLock.unlock();
	}

	private void renderFish() {
		renderFishLock.lock();
		simUsages.startRender();
		fishShown = 0;
		gc.clearRect(0, 0, resolution[0], resolution[1]);
		for(Fish fish : fishList) {
			if(fish.isVisible) { // For `Fish` not shown, this condition improves `SimUsages.fps` (lowers `SimUsages.renderNs`).
				fishShown++;
				fish.render(gc);
			}
		}
		simUsages.postRender();
		renderFishLock.unlock();
	}

	@Override
	public void stop() {
		executor.shutdown();
	}

	public class Fish { /* `static Fish` causes "{posBounds,posBounds.posBound()} cannot be referenced from a static context" (unless those are set to `static`, which prevents `FishSim` from use of separate values with multiple windows) */
		public static Forces forcesSeparation = new Forces(22.0, 2.0);
		public static Forces forcesSeparationNonsimilar = new Forces(100.0, 2.2);
		public static Forces forcesAlignment = new Forces(100.0, 1.0);
		public static Forces forcesCohesion = new Forces(100.0, 1.0);
		public static Forces forcesBounds = new Forces(100.0, 2.0);
		private static double dposMax = 3.0;   // Motion lim (limit of derivative of position)
		private static double d2Pos = 0.1;     // Motion<sup>2</sup> (derivative #2 of position)
		private static double isSimilarTolerance = 0.2;
		public static boolean applyWallAvoidanceTru = (PosBounds.PosBoundsMode.wrapAroundResolution != posBounds.getPosBoundsMode());
		public static boolean redFishAreAggressiveOrPoisonous = true; // changes how `isSimilarTo(Fish other)` uses `color.getRed()`

		private Pos pos;        // Position
		private Pos dpos;       // Motion (derivative of position)
		private Color color;
		public boolean isInBounds;
		public boolean isVisible = false; // Just stores `0 <= pos[0] && resolution[0] > pos[0] && 0 <= pos[1]  && resolution[1] > pos[1]` for now.

		public Fish(ImmutablePos pos, ImmutablePos dpos, Color color) {
			this.pos = pos.clone(); // TODO: ensure this clones the actual (specialized) virtual function addresses, which improve CPU use
			this.dpos = dpos.clone();
			this.color = color;
		}
		public Fish(Pos pos, Pos dpos, Color color) { /* Notice: uses "placement moves" for {`pos`, `dpos`}. Gives `Fish` ownership of `pos`, ownership of `dpos`. */
			this.pos = pos;
			this.dpos = dpos;
			this.color = color;
		}

		public boolean isSimilarTo(Fish o) {
//			return color.equals(o.color); // Less CPU use, but requires that `fishList` has just a few colors.
//			Color colorDis = Color.color(color.getRed() - o.color.getRed(), color.getGreen() - o.color.getGreen(), color.getBlue() - o.color.getBlue()); // Notice: `Color` is more intuitive to use, but was concerned that `java` will not fold this
			double[] colorDis = {color.getRed() - o.color.getRed(), color.getGreen() - o.color.getGreen(), color.getBlue() - o.color.getBlue()};
			if(redFishAreAggressiveOrPoisonous) {
				colorDis[0] = Calculus.pow2(colorDis[0]);
			}
//			return isSimilarTolerance > (Math.hypot(Math.abs(colorDis[0]), Math.abs(colorDis[1]), Math.abs(colorDis[2]))); // [`Math.hypot()` still does not support > 2 dimensions?](https://esdiscuss.org/topic/how-about-more-args-for-math-hypot). Notice: if you use Euclidean distance, lower `isSimilarTolerance`.
			return isSimilarTolerance > (Calculus.pow2(colorDis[0]) + Calculus.pow2(colorDis[1]) + Calculus.pow2(colorDis[2]));
		} // TODO: Use a function (such as `javafx.scene.shape.Polygon.getPoints()`) for comparison of vertices. */

		public Pos getPosDiff(Fish o) {
			return posBounds.posDiff(pos, o.pos);
		}

		public synchronized void setPos(Pos newPos /* Notice: semantics of "placement move" */) {
			isInBounds = posBounds.posBound(newPos); // If `PosBoundsMode.boundless != posBounds.getPosBoundsMode()`, this ensures the invariant `0 <= pos[dim] && PosBounds.getBounds()[dim] > pos[dim]` is established.
			isVisible = (0 <= newPos.pos[0] && resolution[0] > newPos.pos[0] && 0 <= newPos.pos[1]  && resolution[1] > newPos.pos[1]); // TODO: +`Pos::isLessOrEquals(Pos)`, +`Pos::isMore(pos)`
			if((!isInBounds) && PosBounds.PosBoundsMode.boundless != posBounds.getPosBoundsMode()) {
				outOfBounds("Fish::setPos", this);
			}
			pos = newPos; /* Notice: does not use `newPos.clone()` since this function is used in inner loops. After this function returns, `this` has ownership of `newPos`. */
		}
		public synchronized void setPos(ImmutablePos newPos) {
			setPos(newPos.clone()); /* Notice: since this function is used in inner loops, `setPos(Pos newPos)` uses the semantics of "placement move", so if `newPos` is immutable, must clone. */
		}

		public synchronized void applyFlockingRules(List<Fish> allFish, List<Fish>[][] grid) {
			int[] gridPos = {(int) (pos.pos[0] / gridResolution), (int) (pos.pos[1] / gridResolution)};
			List<Fish> nearbyFish = new ArrayList<>();

			// Check neighboring grid cells
			if(PosBounds.PosBoundsMode.wrapAroundResolution == posBounds.getPosBoundsMode()) {
				for(int i = gridPos[0] - 1; i <= gridPos[0] + 1; i++) {
					for(int j = gridPos[1] - 1; j <= gridPos[1] + 1; j++) {
						nearbyFish.addAll(grid[(i + grid.length) % grid.length][(j + grid[0].length) % grid[0].length]);
					} /* TODO: move expensive `%`s into outer loop somehow. With `1000 == fishList.size() && 100 == monitorRefreshHertz`, 2 `%`s in inner loop executes 200,000 `%`/s. The most simple solution is to use outer branches to choose from loops which hardcode this, but thus duplicates codeflow. Does `java` do this for you if the loop uses most of the CPU? */
				}
			} else {
				for(int i = Math.max(0, gridPos[0] - 1); i <= Math.min(grid.length - 1, gridPos[0] + 1); i++) {
					for(int j = Math.max(0, gridPos[1] - 1); j <= Math.min(grid[0].length - 1, gridPos[1] + 1); j++) {
						nearbyFish.addAll(grid[i][j]);
					} /* TODO: move expensive `Math.{min,max}`s into outer loop somehow. With `1000 == fishList.size() && 100 == monitorRefreshHertz`, inner loop executes 200,000 `Math.{min,max}`/s */
				}
			}

			applySeparation(nearbyFish);
			applyAlignment(nearbyFish);
			applyCohesion(nearbyFish);
			if(applyWallAvoidanceTru) {
				applyWallAvoidance(posBounds.getBounds());
			}
		}

		private synchronized void applySeparation(List<Fish> nearbyFish) {
			Pos sepDpos = dpos.zeros();
			Pos sepNonsimilarDpos = dpos.zeros();
			int count = 0, countNonsimilar = 0;

			for(Fish o : nearbyFish) {
				if(o != this) {
					Pos posDiff = getPosDiff(o);
					double dist = posDiff.magnitude(); // Notice: in future, +`Pos::boundHypotenus(o)`
					if(isSimilarTo(o)) {
						if(forcesSeparation.posIfDistScaleSum(sepDpos, posDiff, dist)) {
							count++;
						}
					} else {
						if(forcesSeparationNonsimilar.posIfDistScaleSum(sepNonsimilarDpos, posDiff, dist)) {
							countNonsimilar++;
						}
					}
				}
			}

			if(count > 0) {
				sepDpos.slashEqualsScalar(count);
				forcesSeparation.dposScaleSum(dpos, d2Pos, sepDpos);
			}
			if(countNonsimilar > 0) {
				sepNonsimilarDpos.slashEqualsScalar(countNonsimilar);
				forcesSeparationNonsimilar.dposScaleSum(dpos, d2Pos, sepNonsimilarDpos);
			}
		}

		private synchronized void applyAlignment(List<Fish> nearbyFish) {
			Pos avgDpos = dpos.zeros();
			int count = 0;

			for(Fish o : nearbyFish) {
				if(o != this && isSimilarTo(o)) {
					Pos posDiff = getPosDiff(o);
					double dist = posDiff.magnitude();
					if(forcesAlignment.posIfDistSum(avgDpos, o.dpos, dist)) {
						count++;
					}
				}
			}

			if(count > 0) {
				avgDpos.slashEqualsScalar(count);
				forcesAlignment.dposScaleSum(dpos, d2Pos, avgDpos);
			}
		}

		private synchronized void applyCohesion(List<Fish> nearbyFish) {
			Pos avgPos = pos.zeros();
			int count = 0;

			for(Fish o : nearbyFish) {
				if(o != this && isSimilarTo(o)) {
					Pos posDiff = getPosDiff(o);
					double dist = posDiff.magnitude();
					if(forcesCohesion.posIfDistSum(avgPos, o.pos, dist)) {
						count++;
					}
				}
			}

			if(count > 0) {
				avgPos.slashEqualsScalar(count);
				forcesCohesion.dposScaleSum(dpos, d2Pos, posBounds.posDiff(avgPos, pos));
			}
		}

		private synchronized void applyWallAvoidance(double[] res) {
			Pos avoidancePos = dpos.zeros();

			if(pos.pos[0] < forcesBounds.distance) {
				avoidancePos.pos[0] += (forcesBounds.distance - pos.pos[0]);
			} else if(pos.pos[0] > res[0] - forcesBounds.distance) {
				avoidancePos.pos[0] -= (pos.pos[0] - (res[0] - forcesBounds.distance));
			}
			if(pos.pos[1] < forcesBounds.distance) {
				avoidancePos.pos[1] += (forcesBounds.distance - pos.pos[1]);
			} else if(pos.pos[1] > res[1] - forcesBounds.distance) {
				avoidancePos.pos[1] -= (pos.pos[1] - (res[1] - forcesBounds.distance));
			}

			avoidancePos.starEqualsScalar(d2Pos * forcesBounds.factor / forcesBounds.distance);
			dpos.plusEquals(avoidancePos);
		}

		public synchronized void update() {
			// Limit speed
			double speed = dpos.magnitude();
			if(speed > dposMax) {
				dpos.slashEqualsScalar(speed);
				dpos.starEqualsScalar(dposMax);
			}

			// Update position
			setPos(pos.plus(dpos));
		}

		public synchronized void render(GraphicsContext gc) {
			gc.save();
			gc.translate(pos.pos[0], pos.pos[1]);
			gc.rotate(Math.toDegrees(Math.atan2(dpos.pos[1], dpos.pos[0])) + 90);
			gc.setFill(color);
			gc.beginPath();
			gc.moveTo(0, -10);
			gc.lineTo(-5, 10);
			gc.lineTo(-2, 0);
			gc.lineTo(2, 0);
			gc.lineTo(5, 10);
			gc.closePath();
			gc.fill();
			gc.restore();
		}
	};
};

