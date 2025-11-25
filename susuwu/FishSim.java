/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> has the newest version of `./susuwu/FishSim.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

package susuwu; /* Usage: `import susuwu.FishSim;` */
/* Simple [*JavaFX*](https://github.com/openjdk/jfx) fish sim, which will include reusable `public class`s (for new sims to use).
 * This ([`./susuwu/FishSim.java`](./FishSim.java)) uses pseudo-*Markdown* for comments, but [`./posts/FishSim.md`](../posts/FishSim.md) is the actual [*Markdown*](https://github.github.com/gfm/) document for this.
 * Notice: replaced most of [*Solar-Pro-2*'s original `FishSim.java`](https://github.com/SwuduSusuwu/SusuJava/blob/solarPro2FishSim/susuwu/FishSim.java), as [`./posts/FishSim.md#intro`](../posts/FishSim.md#intro) documents (plus [*GitHub*'s `/compare/` tool shows](https://github.com/SwuduSusuwu/SusuJava/compare/solarPro2FishSim..susuFishSim#diff-8c440bb92bc6939e1450542897e0bbb1a8737b93808ea63ed32784edfacef4b4). */

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
import susuwu.ImmutablePosBounds; /* `public enum PosBoundsMode`: which stores how sims enforce bounds. */

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

	public double[] posDiff(double[] pos, double[] o) {
		if(ImmutablePosBounds.PosBoundsMode.wrapAroundResolution == posBounds.getPosBoundsMode()) {
			double[] posDiff = new double[pos.length];
			for(int i = 0; pos.length > i; ++i) { /* Notice: ensure that `java` [unrolls this](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/Physics_sims_which_structures_to_use.md#separate-variables-versus-dim-lists) */
				posDiff[i] = pos[i] - o[i];
				if(getBoundsSlash2()[i] < posDiff[i]) {
					posDiff[i] -= getBounds()[i];
				} else if(-getBoundsSlash2()[i] > posDiff[i]) {
					posDiff[i] += getBounds()[i];
				}
			}
			return posDiff;
		} else {
			return new double[] {pos[0] - o[0], pos[1] - o[1]};
		}
	}

	public static double[] getBounds() {
		assert null != bounds;
		return bounds; // Notice: for simple sims, can `return resolutionf`.
	}
	public static double[] getBoundsSlash2() { // Caches `getBounds()[dim] / 2` for physics uses (improves inner loops).
		assert null != boundsSlash2;
		return boundsSlash2; // Notice: for simple sims, can `return resolutionfSlash2`.
	}

	public boolean isPosInBounds(double[] pos) throws IllegalArgumentException {
		if(getBounds().length != pos.length) {
			throw new IllegalArgumentException("`getBounds().length != pos.length`");
		} // TODO: If this test is used at the start of all `pos*()` functions, replace `[]` with `Pos2`, unless optimizer stores this.
		for(int i = 0; i < pos.length; i++) {
			if(0 > pos[i] || getBounds()[i] <= pos[i]) {
				return false;
			}
		} // TODO: replace `for(...) {...}` with `switch(pos.length) { case 2: ... }`, unless optimizer does this.
		return true;
	}

	public String posOutOfBoundsStr(double[] pos, String posStr) {
		return "`" + posStr + " = " + Arrays.toString(pos) + ";` `getBounds() = " + Arrays.toString(getBounds()) + ";` (`grid = new ArrayList[" + gridSize[0] + "][" + gridSize[1] + "];`), so `" + posStr + "` is out of bounds.";
	}

	public boolean posBound(double[] pos, ImmutablePosBounds posBounds) throws IllegalArgumentException { // If `PosBoundsMode.boundless != posBounds`, this ensures the invariant `0 <= pos[dim] && FishSim.getBounds()[dim] > pos[dim]` is established.
		switch(posBounds.getPosBoundsMode()) { // `PosBoundsMode.` is omitted from all `case`s, to support old `java --source` versions
		case invalidArgumentException:
			if(!isPosInBounds(pos)) {
				throw new IllegalArgumentException(posOutOfBoundsStr(pos, "double[] pos"));
				// return false; // Notice: unsure of codeflow after the exception is handled. This gives an error if uncommented, but without this, if the exception is handled, the function will fall through to `return true`.
			}
			break;
		case wrapAroundResolution:
			pos[0] = (pos[0] + getBounds()[0]) % getBounds()[0];
			pos[1] = (pos[1] + getBounds()[1]) % getBounds()[1];
			break;
		case clampToResolution:
			pos[0] = Math.max(0, Math.min(getBounds()[0] - 1, pos[0])); // TODO: if `java` does not precompute `getBounds()[dim] - 1`, store `boundsMinus1[]`
			pos[1] = Math.max(0, Math.min(getBounds()[1] - 1, pos[1]));
			break;
		case boundless:
			return isPosInBounds(pos);
		default:
			throw new IllegalArgumentException("Unknown `enum PosBoundsMode`: " + posBounds); // [The compiler does this for you](https://codingtechroom.com/question/what-exception-compiler-unknown-enum-values-switch-expressions), so this just serves to document the lack of `default` codeflow.
		}
		return true;
	}

//    public static class Pos2 extends double[2] {} // `{Pos2[0], Pos2[1]}` is `{x, y}` position (or resolution), or is `{pos[0], pos[0]}` motion (derivative of position), or is is `{d2x, d2y}` acceleration (derivative number 2). This was supposed to do what `typedef` does (wish for future-proof (limitless dimensions) virtual `class` with functions for numerous transforms).
// Will use `double[]` for now. TODO: test how much of `java`'s [static `Array` overhead](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/Physics_sims_which_structures_to_use.md#separate-variables-versus-dim-lists) `java`'s toolkit optimizes for you. If performance is a problem, choose a new approach to use.

	private static ImmutablePosBounds posBounds = new ImmutablePosBounds(ImmutablePosBounds.PosBoundsMode.wrapAroundResolution);
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
		resolutionf[0] = resolution[0]; resolutionf[1] = resolution[1];
		resolutionfSlash2[0] = resolution[0] / 2; resolutionfSlash2[1] = resolution[1] / 2;
		resVolume = resolution[0] * resolution[1];
		getBounds()[0] = resolution[0];
		getBounds()[1] = resolution[1];
		getBoundsSlash2()[0] = getBounds()[0] / 2;
		getBoundsSlash2()[1] = getBounds()[1] / 2;
		boundsVolume = getBounds()[0] * getBounds()[1];
		// canvas = new Canvas(resolution[0], resolution[1]); // TODO: replace with `canvas.setWidth(resolution[0]); canvas.setHeight(resolution[1]);`?
		// gc = canvas.getGraphicsContext2D();
		// scene = new Scene(root, resolution[0], resolution[1], Color.LIGHTBLUE); // replace with `scene.widthProperty().bind(primaryStage.widthProperty());`?
		// stage.setScene(scene);
		renderFishLock.unlock();
		updateFishLock.unlock();
		return true;
	}
	private static int[] resolution = {1280, 720};
	private static double[] resolutionf = {resolution[0], resolution[1]};
	private static double[] resolutionfSlash2 = {resolution[0] / 2, resolution[1] / 2}; // Improves execution of inner loops which use this
	private static int resVolume = resolution[0] * resolution[1];
	private static double boundsResolutionFactor = 2; // `resolution[dim] * 2` gives best results (sufficient room for natural ocean, small enough for most CPUs to process). Notice: Powers of 2 give improved versions of most formulas for computers, but for now this allows all values
	private static double[] bounds = {resolution[0] * boundsResolutionFactor, resolution[1] * boundsResolutionFactor}; // for simple sims, use `bounds = resolutionf;`
	private static double[] boundsSlash2 = {getBounds()[0] / 2, getBounds()[1] / 2}; // Improves execution of inner loops which use this
	private static double boundsVolume = getBounds()[0] * getBounds()[1];
	private static double fishVolume = 200; // Uses resolution of `Fish::render()`.
	private static double fishLengthsSep = 62; // Average `Fish`-lengths distance  from `Fish` to `Fish`.
	private static double fishPerVolume = 1 / fishVolume / fishLengthsSep; // `Fish` per volume (for 2D, volume is resolution).
	private static int fishCount = (int)(boundsVolume * fishPerVolume);
	private static int gridResolution = 100; // Notice: set this to `Colllections.max({forces*.distance})` (which should equal what most sims call "view distance"), so that all relevent `Fish` are processed.
	private static int positionInterval = 2; // The `refreshCounter` per `Fish::applyFlockingRulesUpdate()`
	public static double monitorRefreshHertz = 60.0; // The `SimUsages.fps` to wish for // Notice: since this limits `SimUsages.fps` to `monitorRefreshHertz`, this prevents benchmarks which use `FpsTextMode.fps` (or `FpsTextMode.ms`). Benchmarks can still use `FpsTextMode.msSpec` (or `FpsTextMode.msFish`).
	public static double physicsRefreshHertz = monitorRefreshHertz / positionInterval; // The `1 / SimUsages.physicsMs` to wish for // Notice: unknown what `javafx.animation.Timeline` does if `physicsRefreshHertz > (1 / SimUsages.physicsMs)`, but guess thus stalls or consumes multiple executors

	private List<Fish> fishList = new ArrayList<>();
	private int fishShown = 0;
	private int[] gridSize = { (int)Math.ceil(getBounds()[0] / gridResolution), (int)Math.ceil(getBounds()[1] / gridResolution) };
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
			double[] pos = {random.nextDouble() * getBounds()[0], random.nextDouble() * getBounds()[1]};
			double[] dpos = {(random.nextDouble() * 2 - 1) * Fish.dposMax, (random.nextDouble() * 2 - 1) * Fish.dposMax};
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

		grid = new ArrayList[gridSize[0]][gridSize[1]]; /* `listToPartitions(List<>[][] grid, List<> list)` uses this */
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
		System.err.println(function + ": " + posOutOfBoundsStr(fish.pos, "Fish.pos"));
	}

	/* Spatial partitioning (simple grid system). TODO: generic version of this (accept all `class`s with `#isInBounds` plus `#pos`). */
	private void listToPartitions(List<Fish>[][] grid, List<Fish> list) {
		assert grid.length == (int)Math.ceil(getBounds()[0] / gridResolution);
		assert grid[0].length == (int)Math.ceil(getBounds()[1] / gridResolution);
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[i].length; j++) {
				grid[i][j].clear();
			}
		}
		for(Fish fish : list) { /* Assign list members to grid sections */
			if(fish.isInBounds) {
				int[] gridPos = {(int) (fish.pos[0] / gridResolution), (int) (fish.pos[1] / gridResolution)};
				grid[gridPos[0]][gridPos[1]].add(fish); // if `gridPos` is not in bounds, this will `throw new IndexOutOfBoundsException()`. But `Fish.setPos()` uses `FishSim::posBound()` which uses `FishSim::isPosInBounds()`, which ensures the `.pos` bounds to `resolution`.
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

	public class Fish { /* `static Fish` causes "{posBounds,posBound()} cannot be referenced from a static context" (unless those are set to `static`, which prevents `FishSim` from use of separate values with multiple windows) */
		public static Forces forcesSeparation = new Forces(22.0, 2.0);
		public static Forces forcesSeparationNonsimilar = new Forces(100.0, 2.2);
		public static Forces forcesAlignment = new Forces(100.0, 1.0);
		public static Forces forcesCohesion = new Forces(100.0, 1.0);
		public static Forces forcesBounds = new Forces(100.0, 2.0);
		private static double dposMax = 3.0;   // Motion lim (limit of derivative of position)
		private static double d2Pos = 0.1;     // Motion<sup>2</sup> (derivative #2 of position)
		private static double isSimilarTolerance = 0.2;
		public static boolean applyWallAvoidanceTru = (ImmutablePosBounds.PosBoundsMode.wrapAroundResolution != posBounds.getPosBoundsMode());
		public static boolean redFishAreAggressiveOrPoisonous = true; // changes how `isSimilarTo(Fish other)` uses `color.getRed()`

		private double[] pos;        // Position
		private double[] dpos;       // Motion (derivative of position)
		private Color color;
		public boolean isInBounds;
		public boolean isVisible = false; // Just stores `0 <= pos[0] && resolution[0] > pos[0] && 0 <= pos[1]  && resolution[1] > pos[1]` for now.

		public Fish(double[] pos, double[] dpos, Color color) {
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

		public double[] getPosDiff(Fish o) {
			return posDiff(pos, o.pos);
		}

		public synchronized void setPos(double[] newPos) { // If `PosBoundsMode.boundless != posBounds`, this ensures the invariant `0 <= pos[dim] && FishSim.getBounds()[dim] > pos[dim]` is established.
			isInBounds = posBound(newPos, posBounds);
			isVisible = (0 <= newPos[0] && resolution[0] > newPos[0] && 0 <= newPos[1]  && resolution[1] > newPos[1]);
			if((!isInBounds) && ImmutablePosBounds.PosBoundsMode.boundless != posBounds.getPosBoundsMode()) {
				outOfBounds("Fish::setPos", this);
			}
			pos = newPos;
		}

		public synchronized void applyFlockingRules(List<Fish> allFish, List<Fish>[][] grid) {
			int[] gridPos = {(int) (pos[0] / gridResolution), (int) (pos[1] / gridResolution)};
			List<Fish> nearbyFish = new ArrayList<>();

			// Check neighboring grid cells
			if(ImmutablePosBounds.PosBoundsMode.wrapAroundResolution == posBounds.getPosBoundsMode()) {
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
				applyWallAvoidance(getBounds());
			}
		}

		private synchronized void applySeparation(List<Fish> nearbyFish) {
			double[] sepDpos = {0, 0};
			double[] sepNonsimilarDpos = {0, 0};
			int count = 0, countNonsimilar = 0;

			for(Fish other : nearbyFish) {
				if(other != this) {
					double[] posDiff = getPosDiff(other);
					double dist = Math.hypot(posDiff[0], posDiff[1]);
					if(isSimilarTo(other)) {
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
				sepDpos[0] /= count;
				sepDpos[1] /= count;
				forcesSeparation.dposScaleSum(dpos, d2Pos, sepDpos);
			}
			if(countNonsimilar > 0) {
				sepNonsimilarDpos[0] /= countNonsimilar;
				sepNonsimilarDpos[1] /= countNonsimilar;
				forcesSeparationNonsimilar.dposScaleSum(dpos, d2Pos, sepNonsimilarDpos);
			}
		}

		private synchronized void applyAlignment(List<Fish> nearbyFish) {
			double[] avgDpos = {0, 0};
			int count = 0;

			for(Fish other : nearbyFish) {
				if(other != this && isSimilarTo(other)) {
					double[] posDiff = getPosDiff(other);
					double distPow2 = Calculus.pow2(posDiff[0]) + Calculus.pow2(posDiff[1]);
					if(forcesAlignment.posIfDistPow2Sum(avgDpos, other.dpos, distPow2)) {
						count++;
					}
				}
			}

			if(count > 0) {
				avgDpos[0] /= count;
				avgDpos[1] /= count;
				forcesAlignment.dposScaleSum(dpos, d2Pos, avgDpos);
			}
		}

		private synchronized void applyCohesion(List<Fish> nearbyFish) {
			double[] avgPos = {0, 0};
			int count = 0;

			for(Fish other : nearbyFish) {
				if(other != this && isSimilarTo(other)) {
					double[] posDiff = getPosDiff(other);
					double distPow2 = Calculus.pow2(posDiff[0]) + Calculus.pow2(posDiff[1]);
					if(forcesCohesion.posIfDistPow2Sum(avgPos, other.pos, distPow2)) {
						count++;
					}
				}
			}

			if(count > 0) {
				avgPos[0] /= count;
				avgPos[1] /= count;
				forcesCohesion.dposScaleSum(dpos, d2Pos, posDiff(avgPos, pos));
			}
		}

		private synchronized void applyWallAvoidance(double[] res) {
			double[] avoidancePos = {0, 0};

			if(pos[0] < forcesBounds.distance) {
				avoidancePos[0] += (forcesBounds.distance - pos[0]);
			}
			if(pos[0] > res[0] - forcesBounds.distance) {
				avoidancePos[0] -= (pos[0] - (res[0] - forcesBounds.distance));
			}
			if(pos[1] < forcesBounds.distance) {
				avoidancePos[1] += (forcesBounds.distance - pos[1]);
			}
			if(pos[1] > res[1] - forcesBounds.distance) {
				avoidancePos[1] -= (pos[1] - (res[1] - forcesBounds.distance));
			}

			dpos[0] += avoidancePos[0] / forcesBounds.distance * d2Pos * forcesBounds.factor;
			dpos[1] += avoidancePos[1] / forcesBounds.distance * d2Pos * forcesBounds.factor;
		}

		public synchronized void update() {
			// Limit speed
			double speed = Math.sqrt(dpos[0] * dpos[0] + dpos[1] * dpos[1]);
			if(speed > dposMax) {
				dpos[0] = (dpos[0] / speed) * dposMax;
				dpos[1] = (dpos[1] / speed) * dposMax;
			}

			// Update position
			setPos(new double[] {pos[0] + dpos[0], pos[1] + dpos[1]});
		}

		public synchronized void render(GraphicsContext gc) {
			gc.save();
			gc.translate(pos[0], pos[1]);
			gc.rotate(Math.toDegrees(Math.atan2(dpos[1], dpos[0])) + 90);
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

