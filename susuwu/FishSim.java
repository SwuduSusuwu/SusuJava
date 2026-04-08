/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> has the newest version of `./susuwu/FishSim.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

package susuwu; /* Usage: `import susuwu.FishSim;` */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import susuwu.SimUsages; /* `class SimUsages`, `enum FpsTextMode` */
import susuwu.SdlGles2; /* `class SdlGles2`: JNI bridge to SDL2 + GLES2 */
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
 * Simple SDL2+GLES2 fish sim (via JNI), which includes reusable {@code public class}s (for new sims to use). Most of the reusable {@code public class}s are in other {@code .java} sources for {@code package susuwu}
 * This ([`./susuwu/FishSim.java`](./FishSim.java)) uses pseudo-*Markdown* for comments, but [`./posts/FishSim.md`](../posts/FishSim.md) is the actual [*Markdown*](https://github.github.com/gfm/) document for this.
 * Notice: replaced most of [*Solar-Pro-2*'s original `FishSim.java`](https://github.com/SwuduSusuwu/SusuJava/blob/solarPro2FishSim/susuwu/FishSim.java), as [`./posts/FishSim.md#intro`](../posts/FishSim.md#intro) documents (plus [*GitHub*'s `/compare/` tool shows](https://github.com/SwuduSusuwu/SusuJava/compare/solarPro2FishSim..susuFishSim#diff-8c440bb92bc6939e1450542897e0bbb1a8737b93808ea63ed32784edfacef4b4).
 */
public class FishSim {
	/** Minimal {@code Color} replacement (replaces {@code javafx.scene.paint.Color}). Supports {@code getRed()}, {@code getGreen()}, {@code getBlue()} for {@code isSimilarTo()} comparisons. */
	public static class Color {
		private final double red, green, blue;
		private Color(double r, double g, double b) { this.red = r; this.green = g; this.blue = b; }
		public static Color color(double r, double g, double b) { return new Color(r, g, b); }
		public double getRed()   { return red; }
		public double getGreen() { return green; }
		public double getBlue()  { return blue; }
		public static final Color LIGHTBLUE = new Color(0.678, 0.847, 0.902); /* Light blue background (replaces `Color.LIGHTBLUE` from JavaFX) */
	}

	public enum PhysicsMode { // `PhysicsMode` says how to execute `updateFish()`
		synchronousHomo,      // `updateFish()` once per `refreshLoop()`.
		synchronousInterval,  // `updateFish()` per `positionInterval` `refreshLoop()`s.
		asynchronousHomo,     // `executor.submit(() -> updateFish());` once per `refreshLoop()`.
		asynchronousInterval, // `executor.submit(() -> updateFish());` per `positionInterval` `refreshLoop()`s.
		separateUnbound,      // `updateFish()` runs in a background thread continuously (replaces `AnimationTimer`). Notice: with `GLES2` this has an implicit bound to the monitor refresh (which `SimUsages` counts as "CPU use" (pause for Vertical Synchronization counts towards "drawMS")).
		separateFps,          // `updateFish()` runs via `ScheduledExecutorService` at `physicsRefreshHertz` (replaces `Timeline/KeyFrame`).
	}
	private static PhysicsMode monitorRefreshMode = PhysicsMode.separateFps; // `monitorRefreshMode` must use `.separateUnbound` or `.separateFps`.
	private static PhysicsMode physicsMode = PhysicsMode.separateFps; // Notice: if `PhysicsMode.*Interval`, must set `positionInterval`. if `PhysicsMode.separateFps`, must set `physicsRefreshHertz`.

	static ReentrantLock renderFishLock = new ReentrantLock();
	static ReentrantLock updateFishLock = new ReentrantLock();
	// TODO: remove `static` from {`resolution`, `bounds`}, to allow to remove `static` from {`setResolution()`, `renderFishLock, `updateFishLock`}, so `FishSim` allows numerous windows
	public static boolean setResolution(int[] newResolution) { /* Notice: invalidates references to old `bounds`, `*Slash2` addresses. */ // TODO: remove `static` from {`resolution`, `bounds`}, to allow to remove `static` from {`setResolution()`, `renderFishLock, `updateFishLock`}, so `FishSim` allows numerous windows
		assert 2 == newResolution.length;
		assert 0 < newResolution[0]; //TODO: allow "headless" instances with `resolution = {0, 0}`?
		assert 0 < newResolution[1];
		updateFishLock.lock();
		renderFishLock.lock();
		resolution = newResolution;
		resolutionf.pos[0] = resolution[0]; resolutionf.pos[1] = resolution[1];
		resolutionfSlash2 = resolutionf.slashScalar(2); /* Notice: invalidates references which store the old address to `resolutionfSlash2`. */
		resVolume = (int)Math.round(resolutionf.volume()); /* Notice: uses `Pos::volume()` since simple source code is less bug prone. `Math.round` ensures 24-bit mantissas give accurate values */
		posBounds.setBounds(resolutionf.starScalar(boundsResolutionFactor).pos); // TODO: if sure that no functions store references to the original instance, replace the above row with this (since simple source code is less bug prone)
		// (Resize SDL window here if needed: SDL_SetWindowSize(window, resolution[0], resolution[1]))
		// (Resize GLES2 viewport here if needed: glViewport(0, 0, resolution[0], resolution[1]))
		// (Rebuild GLES2 u_resolution uniform: SdlGles2.glClearColor/etc. if resolution changes)
		renderFishLock.unlock();
		updateFishLock.unlock();
		return true;
	}
	private static int[] resolution = {1280, 720};
	private static Pos2 resolutionf = new Pos2(resolution[0], resolution[1]);
	private static Pos resolutionfSlash2 = resolutionf.slashScalar(2); /* Improves execution of inner loops which use this. Notice: `setResolution(newResolution)` invalidates stored references to `resolutionfSlash2` */
	private static int resVolume = (int)Math.round(resolutionf.volume()); /* Notice: uses `Pos::volume()` since simple source code is less bug prone. `Math.round` ensures 24-bit mantissas give accurate values */
	private static double boundsResolutionFactor = (1_000_000 > resVolume ? 2.0 : 1.2); // Ocean is `resolution[dim] * boundsResolutionFactor`. `2.0` gives more room for natural oceans, but old laptops with huge resolutions (such as `{2200, 1200}`) must use `1.2` so the load is low enough for old CPUs to process). TODO: include short benchmark (on startup) to set `boundsResolutionFactor` to optimal value, or reduce CPU use for unshown `Fish` (`if(!fish.isVisible)`, then execute `updateFish` just once per second (1 hertz), with larger steps).
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
	SimUsages simUsages = new SimUsages(); /* Replaces `new SimUsages(root)`: no Pane needed for SDL2 text (shown via window title). */
//	simUsages.fpsTextMode = FpsTextMode.allUsages.value; // TODO: "error: <identifier> expected" solution

	private volatile boolean quit = false; /* Set to `true` by `stop()` to signal the main SDL loop to exit. */
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(); /* Replaces `javafx.animation.Timeline` for `separateFps` physics. */

	public static void main(String[] args) {
		new FishSim().run(args); /* Replaces `launch(args)`: instantiate directly since there is no JavaFX Application lifecycle. */
	}

	/** Initializes SDL2+GLES2, populates fish, starts physics loops, then runs the render loop until quit. Replaces {@code start(Stage primaryStage)}. */
	public void run(String[] args) { /* `args` preserved for future CLI configuration (e.g., `--resolution`, `--physics-mode`); currently unused. */
		if(!SdlGles2.init(resolution[0], resolution[1], "Fish Simulation (Boids)")) {
			System.err.println("FishSim.run: SdlGles2.init failed; aborting.");
			return;
		}
		SdlGles2.glClearColor( /* Light-blue background (replaces `Color.LIGHTBLUE` passed to `new Scene(...)`) */
			(float)Color.LIGHTBLUE.getRed(), (float)Color.LIGHTBLUE.getGreen(), (float)Color.LIGHTBLUE.getBlue(), 1.0f);

		// Initialize fish
		for(int i = 0; i < fishCount; i++) {
			Pos2 pos = new Pos2(random.nextDouble() * posBounds.getBounds(0), random.nextDouble() * posBounds.getBounds(1));
			Pos2 dpos = new Pos2((random.nextDouble() * 2 - 1) * Fish.dposMax, (random.nextDouble() * 2 - 1) * Fish.dposMax);
			fishList.add(new Fish(pos, dpos, Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble())));
		}

		posBounds.setGridResolution(gridResolution);
		grid = new ArrayList[posBounds.getGridSize(0)][posBounds.getGridSize(1)]; /* `listToPartitions(List<>[][] grid, List<> list)` uses this */
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[i].length; j++) {
				grid[i][j] = new ArrayList<>();
			}
		}

		simUsages.show();

		// Start separate physics loop for `separateUnbound` / `separateFps` modes (replaces `AnimationTimer` / `Timeline`):
		switch(physicsMode) { // `PhysicsMode.` is omitted from all `case`s, to support old `java --source` versions
		case separateUnbound:
			executor.submit(() -> { while(!quit) { updateFish(); } });
			break;
		case separateFps:
			long physicsIntervalMs = Math.max(1L, (long)(1_000.0 / physicsRefreshHertz)); /* Use milliseconds for scheduler precision (avoids nanosecond scheduler overhead). */
			scheduledExecutor.scheduleAtFixedRate(() -> updateFish(), 0, physicsIntervalMs, TimeUnit.MILLISECONDS);
			break;
		default:
			break; /* synchronous* / asynchronous* modes: handled inside `refreshLoop()` */
		}

		// Main SDL render loop (replaces `AnimationTimer` / `Timeline` for `monitorRefreshMode`):
		long renderIntervalNs = (long)(1_000_000_000.0 / monitorRefreshHertz);
		long lastRenderTime = System.nanoTime();
		while(!quit && !SdlGles2.pollQuit()) {
			long now = System.nanoTime();
			boolean shouldRender;
			switch(monitorRefreshMode) { // `PhysicsMode.` is omitted from all `case`s, to support old `java --source` versions
			case separateUnbound: // TODO: since GLES2 this has an implicit bound to the monitor refresh, fix `SimUsages` to not count idle as "CPU use" (vertical synchronization for `SdlGles2.glClear(...);` should not count towards "drawMS")). Solution #1 is `SDL_GL_SetSwapInterval(0);` (Vertical Synchronization disabled), solution #2 is to subtract the time used for `glClear(...)` from `simUsages.renderNs`.
				shouldRender = true;
				break;
			case separateFps: /* fall-through */
			default:
				shouldRender = ((now - lastRenderTime) >= renderIntervalNs);
				break;
			}
			if(shouldRender) {
				refreshLoop(now);
				lastRenderTime = now;
			} else {
				try { Thread.sleep(1); } catch(InterruptedException e) { Thread.currentThread().interrupt(); break; } /* 1ms sleep avoids busy-waiting while still responding within 1 frame at 60fps (~16ms). */
			}
		}
		stop();
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
			break; // no-op for both, since `run()` processes thus
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
		SdlGles2.glClear(SdlGles2.GL_COLOR_BUFFER_BIT); // Replaces `gc.clearRect(0, 0, resolution[0], resolution[1])` // TODO: use `SDL_GL_SetSwapInterval(0);`, or do `SimUsages.preSynchro(); SdlGles2.glClear(SdlGles2.GL_COLOR_BUFFER_BIT); SimUsages.postSynchro();`, so `monitorRefreshMode = separateUnbound` does not include Vertical Synchronization into `drawMS`.
		for(Fish fish : fishList) {
			if(fish.isVisible) { // For `Fish` not shown, this condition improves `SimUsages.fps` (lowers `SimUsages.renderNs`).
				fishShown++;
				fish.render();
			}
		}
		SdlGles2.swapWindow(); // Presents the rendered frame (replaces implicit JavaFX frame commit).
		simUsages.postRender();
		renderFishLock.unlock();
	}

	/** Signals the main loop to exit, shuts down executor threads, and calls {@code SDL_Quit()} via {@link SdlGles2#destroy()}. Replaces {@code @Override stop()}. */
	public void stop() {
		quit = true;
		scheduledExecutor.shutdownNow();
		executor.shutdown();
		SdlGles2.destroy(); /* Replaces implicit JavaFX window teardown. */
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
		private Color color;    /* Replaces `javafx.scene.paint.Color`: uses `FishSim.Color` which supports `getRed()`, `getGreen()`, `getBlue()`. */
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
					double distPow2 = posDiff.magnitudePow2();
					if(forcesAlignment.posIfDistPow2Sum(avgDpos, o.dpos, distPow2)) {
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
					double distPow2 = posDiff.magnitudePow2();
					if(forcesCohesion.posIfDistPow2Sum(avgPos, o.pos, distPow2)) {
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

		/**
		 * Renders this fish using GLES2 via {@link SdlGles2#drawFilledPolygon}.
		 * Replicates the JavaFX {@code gc.save/translate/rotate/setFill/beginPath/moveTo/lineTo/closePath/fill/restore} sequence.
		 * Fish shape vertices (local space): {@code {0,-10}, {-5,10}, {-2,0}, {2,0}, {5,10}}.
		 * Triangulated (triangle fan from vertex 0): triangles {@code {0,1,2}, {0,2,3}, {0,3,4}}.
		 */
		public synchronized void render() {
			/* Replicate `gc.translate(tx,ty); gc.rotate(angleDeg+90)` as a 2-D rotation matrix. */
			double angle = Math.atan2(dpos.pos[1], dpos.pos[0]) + Math.PI / 2.0; /* equiv. to Math.toRadians(Math.toDegrees(atan2) + 90) */
			double cosA = Math.cos(angle);
			double sinA = Math.sin(angle);
			double tx = pos.pos[0];
			double ty = pos.pos[1];
			/* Fish shape local vertices: same coordinates as original JavaFX path */
			final double[][] lv = {{0,-10}, {-5,10}, {-2,0}, {2,0}, {5,10}};
			/* Triangulate polygon as fan from vertex 0: {0,1,2}, {0,2,3}, {0,3,4} -> 3 triangles, 9 verts, 18 floats */
			final int[][] tris = {{0,1,2}, {0,2,3}, {0,3,4}};
			float[] verts = new float[18];
			int vi = 0;
			for(int[] tri : tris) {
				for(int idx : tri) {
					double lx = lv[idx][0], ly = lv[idx][1];
					verts[vi++] = (float)(lx * cosA - ly * sinA + tx); /* x' = x*cos - y*sin + tx */
					verts[vi++] = (float)(lx * sinA + ly * cosA + ty); /* y' = x*sin + y*cos + ty */
				}
			}
			SdlGles2.drawFilledPolygon(verts, (float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), 1.0f);
		}
	};
};

