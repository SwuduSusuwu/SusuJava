/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> has the newest version of `./susuwu/FishSim.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

package susuwu; /* Usage: `import susuwu.FishSim;` */
/*
```markdown
Intro to simple [*JavaFX*](https://github.com/openjdk/jfx) fish sim. Usage: `import susuwu.FishSim;` includes `public class`s (for new sims to use),
* This ([`./susuwu/FishSim.java`](./FishSim.java)) uses pseudo-*Markdown* for comments, but [`./posts/FishSim.md`](../posts/FishSim.md) is the actual [*Markdown*](https://github.github.com/gfm/) document for this.
* [Comments with multiple rows](../README.md#java) tend to start all rows with " *", but this comment omits those, due to use of "* " for *Markdown* lists.
* This `java` source code is split from [`../SusuPosts/posts/Human_ancestors_are_fish.md#request-java-fish`](https://github.com/SwuduSusuwu/SusuPosts/blob/69b7b1545ab51a1c1a562c0ac838a950bb086442/posts/Human_ancestors_are_fish.md#request-java-fish).
* The [original version of this source code](https://github.com/SwuduSusuwu/SusuJava/blob/solarPro2FishSim/susuwu/FishSim.java) was [produced through *Solar-Pro-2*](https://poe.com/s/ehlOJYRJNsrGJttfJ4HK), but the goal is just to use thus as a template (for future versions to replace all with own source code).

******************************************

Prefixes (used for variables / functions / classes): `` +`Class` `` introduces `Class`, `` -`Class` `` removes `Class`, `` @`Class` `` changes (neutral or improves) `Class` (as [used for `git commit` messages](../README.md#git)). `:%s/from/to/` shows `vim` regular expressions.
**Notice**: this `git branch` improves [*Solar-Pro-2*'s original source code](https://github.com/SwuduSusuwu/SusuJava/blob/solarPro2FishSim/susuwu/FishSim.java) as this list (plus [*GitHub*'s `/compare/` tool shows](https://github.com/SwuduSusuwu/SusuJava/compare/solarPro2FishSim..susuFishSim#diff-8c440bb92bc6939e1450542897e0bbb1a8737b93808ea63ed32784edfacef4b4)) shows:
* `:%s/, 0, 0, 0/, 0, 0/`: fixes "error: method rotate in class Transform cannot be applied to given types; ... actual and formal argument lists differ in length"
* [+`frameCount`, +`lastTime`, +`fps`, +`fpsText`](https://github.com/SwuduSusuwu/SusuJava/commit/50319ff075fc3a31f761c8fdc1fcce46a2471218) to show **FPS** ([produced through _Solar-Pro-2_](https://poe.com/s/OSENRaU2uCb4TznzRPas)).
  * @`AnimationTimer::handle()`: show true **FPS**, plus do not to redraw `fpsText` unless `fps` changes.
* @`Fish::applySeparation()`: reuse values.
* @`Fish::applyWallAvoidance()`: reuse values, plus replace [magic constants](https://stackoverflow.com/questions/43950998/what-are-symbolic-constants-and-magic-constants) with `BOUNDS_DISTANCE`.
* @`Fish::update()`: if rotation is miniscule, this reuses transforms (to improve `fps`, but `fps` is too unstable to notice differences.)
* +`*_FACTOR`: (`= 1` for original results), scales `Fish::apply*()` forces.
  * @`SEPARATION_FACTOR`: (from `1`) to `2`, so schools are loose enough to view individual fish.
  * @`SEPARATION_DISTANCE`: (from `50`) to `22`, so fish still school.
* @`resolution`: (from {800, 600}) to {1280, 720}, since most computers (plus smartphones) can show *720p* resolution.
  * @`FISH_COUNT`: (from `50`) to `102`, since the window now has more room.
* @`Fish::createFishShape()`: produce 2 colors of fish.
  * @`class FishSim`: is now close to a fluid particle sim which has 2 types of molecules which group to similar molecules (such as [oleophilic compounds](https://thepetrosolutions.com/forums/topic/difference-between-oleophobic-and-oleophilic-impurities/#post-3508)) plus separate from nonsimilar molecules ([such as oleophobic compounds](https://poe.com/s/dYx54tOaDTaDnaBT9TRm)), except the numerous steps of *Boids* formula cause some emergent phenomenon which simple molecules do not possess.

******************************************

Notice: [Used *Solar-Pro-2* to improve codeflow](https://github.com/SwuduSusuwu/SusuJava/commit/6242d2045d619dd664c9a8eea9141c5d178a2ce8) (of the ancestor `git commit` --- which was half (`1 / 2`) human-produced source code --- to thus) [so `fps` improves](https://poe.com/s/ifeHY8AcpVmVC7R5aPB7):
* @`class FishSim`: move `class Fish`-specific values into @`class Fish`.
* @`class FishSim`: use `java.util.concurrent.Executor{s,Service}` to offload `updateFish()` physics (now uses 2 **CPU**s).
* +`GRID_SIZE`, @`updateFish()`: use `GRID_SIZE` to split `List<Fish> fishList` into `List<Fish>[][] grid` (which reduces *O(n^2)* to *O(n^2 / (resolution[] / GRID_SIZE)))* **CPU** use)).
* -`javafx.scene.shape.Polygon`, +`javafx.scene.canvas.Canvas`, +`javafx.scene.canvas.GraphicsContext`: improves renderer **CPU** use?
* @`class FishSim`: replaces `1.0 / 2 < random.nextDouble()` with `random.nextBoolean()`.
* {-`Fish::createFishShape()`, -`Fish::getShape()`}, {+`Fish::render()`, +`FishSim::renderFish()`}: switch to `GraphicsContext`.
* @`Fish::update`: `Fish` now wrap around (to opposite edges) if out-of-bounds.
* Notice: the list which follows is all own improvements (versus version above). Own version:
  * @`FishSim::*`, @`Fish::*`: now mutable (since future versions will allow to resize windows plus configure distances). TODO: introduce `get*()` methods (so that typos do not reconfigure constants, such as view distances).
  * @`GRID_SIZE`: documents minimum value which enforces `*_DISTANCE`s.
  * @`applyFlockingRules()`: replaces magic constants (`100`) with `GRID_SIZE` (fixes undefined behaviour if `GRID_SIZE` changes).
  * @`Fish::applyFlockingRules()`, @`Fish::update()`: Replaces magic constants ({`2600`, `1600`}) with {`resolution[0]`, `resolution[1]`}.
  * @`class FishSim`: reduces `UPDATE_INTERVAL` (from `5`) to `2` (since `ExecutorService` is used, this does not lower `fps`) so physics is smooth.
  * +`Fish::isSimilarTo()`, +`isSimilarTolerance`: limits schools to similar `Fish`. @`apply*()`: uses thus.
    * +`boolean redFishAreAggressiveOrPoisonous`: changes how `isSimilarTo(Fish other)` uses `color.getRed()`
    * @`FishSim::start()`: produces all possible colors of `Fish`. `Fish` schools are now more complex than fluid particles.
  * +`FishSim::refreshLoop()`: houses `FishSim::ApplicationTimer::handle()`'s codeflow. Reason: so is simple for future versions to switch `new AnimationTimer() {@Override public void handle(long now) { refreshLoop(); }}.start();` to alternatives (such as to `Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000.0 / monitorRefreshHertz), event -> { refreshLoop(); })); timeline.setCycleCount(Animation.INDEFINITE); timeline.play();`).
    * @`FishSim::fpsText`: `String.format("%4.2f", fps)` (`4.` so `fpsText.size()` does not change if `fps` magnitude does, `.2` to show miniscule differences).
    * @`FishSim::fpsText`: show milliseconds used per monitor refresh ("draw ms"), plus per `updateFish()` ("physics ms"), plus show `fishList.size()`, plus `fishShown`.
  * +`FishSim::fpsTextRefresh()`: houses the `FishSim::fpsText` codeflow, which `refreshLoop()` uses.
    * +`FishSim::FpsTextMode()`: says which resources for `fpsText` to show. `fpsTextRefresh()` uses this.
  * @`FishSim::*`: replaces pairs of 2 `int`s with `int[2]` (replaced 2 `double`s with `double[2]`), to future-proof (for `class Pos2`). Such as: -`WIDTH`, -`HEIGHT`, +`resolution[]`.
    * +`double[] resolutionf = {resolution[0], resolution[1]};`: for physics code which requires `double[]`.
    * +[`./susuwu/Calculus.java`](../susuwu/Calculus.java): `public class Calculus` houses simple trigonometric (transcendental) `public static` functions. Future versions will include true calculus functions (such as "False Position" or "Quadratic Interpolation").
    * +[`./susuwu/Forces.java`](../susuwu/Forces.java): replaces `double *Distance; double *Factor;` with `Forces forces*;`, so other `double`s are not confused with those.
      * +`Forces.dposScaleSum(dposDes, d2pos, dposSource)`: for Boids groups: `if(dposScaleSum(derivativeOfPosition, secondDerivOfPos, averageDposOfGroup)) { position += derivativeOfPosition; }`, reduces duplicate code.
    * +`class ImmutablePos`: stores constant vectors (first-order tensors), to future-proof (for volumetrics). [Usage: `double acceptsConsts(ImmutablePos pos)`](https://github.com/SwuduSusuwu/SusuJava/compare/preview..pos2#diff-8c440bb92bc6939e1450542897e0bbb1a8737b93808ea63ed32784edfacef4b4).
      * +`class Pos`: `Pos` stores vectors (first-order tensors), to future-proof (for volumetrics). Usage: `double setsMembersOfPos(Pos pos)`.
      * +`class ImmutablePos2`: 2-dimensional specialization of `class ImmutablePos`.
      * +`class Pos2`: 2-dimensional specialization of `class Pos`.
    * +`FishSim::outOfBounds()`: improves @`FishSim::updateFish()` (which now uses this if `Fish` not in `grid` bounds).
    * +`enum FishSim::PosBounds`: which stores how `posBound()` enforces bounds.
    * +`boolean FishSim::isPosInBounds(double[] pos)`: replaces duplicate code which tests for if `pos` is in bounds. Allows 2-dimensions or volumetric.
    * +`String posOutOfBoundsStr(double[] pos, String posStr)`: produces out-of-bounds messages for {`FishSim::posBound()`, `FishSim::outOfBounds()`}.
    * +`boolean posBound(double[] pos, PosBounds posBounds)`: enforces bounds onto `pos` (`Fish::setPos(newPos)` uses this). If `PosBounds.boundless`, just tests `pos`.
    * +`void Fish::setPos(double[] newPos)`: if `Fish` not in bounds, uses `FishSim::outOfBounds()`.
  * @`FishSim::updateFish()`: replaces magic constants (`resolution[] / GRID_SIZE`) with `grid.length`, to ensure correct access if the code which produces `grid` changes.
    * @`FishSim::updateFish()`: produces extra `grid`s if `resolution[]` is not a multiple of `GRID_SIZE`, so that `Fish` with position close to the resolution (close to edges / bounds) are still included.
    * @`FishSim::updateFish()`: moves bounds test into `FishSim::posBound()`, which `Fish::setPos()` uses.
    * @`class FishSim`: +`boundsVolume`, +`fishVolume`, +`fishLengthsSep`, `fishPerVolume`: so `FISH_COUNT` scales to resolution.
    * @`FishSim::renderFish()`: `if(isPosInBounds(fish.pos))` reduces calls to `fish.render()` (improves `fps` for sims with huge unshown groups of fish).
      * @`class Fish`: +`boolean isInBounds;` stores `boolean posBound()`'s `return` value (improves CPU use). TODO: rename to `isVisible`?
        * @`FishSim::updateFish()`: `if(fish.isInBounds) {}` around `grid[gridPos[0]][gridPos[1]].add(fish);`, so `FishSim` allows out-of-bounds `Fish`.
      * @`class Fish`: +`boolean isVisible`: improves `FishSim::renderFish()` (reduces calls to `fish.render()`, which improves `fps` for sims with huge unshown groups of fish).
    * +`boolean FishSim::setResolution(newResolution)`: this sets all variables (plus uses all functions) required for `class FishSim` to switch to `newResolution`.
  * +`FishSim::getBounds()`: to replace `FishSim::resolution` for physics uses. Introduced `bounds` for this (to allow out-of-view positions). Notice: for simple sims, this can `return resolutionf;`.
    * `bounds = {resolution[0] * 2, resolution[1] * 2};` `BOUNDS_FACTOR = (PosBounds.wrapAroundResolution == posBounds ? 0 : 2);`: if `wrapAroundResolution`, the view is close to a natural ocean.

``` end of *Markdown*
*/

import javafx.animation.Animation;
//import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import susuwu.Calculus; /* `Calculus.pow2()` */
import susuwu.Forces; /* `class Forces implements java.lang.Cloneable` */

public class FishSim extends Application {

	public enum PosBounds { // `PosBounds` says how the sim must do `pos[dim] += dpos[dim]` (derivatives of positions).
		invalidArgumentException, // `if(!isPosInBounds(pos)) { throw new IllegalArgumentException(); }`
		wrapAroundResolution, // `pos[dim] = (getBounds()[dim] + pos[dim] + dpos[dim]) % getBounds()[dim];`.
		clampToResolution, // `pos[dim] = Math.max(0, Math.min(getBounds()[dim] - 1, pos[dim] + dpos[dim]));`.
		boundless, // `pos[dim] += dpos[dim];`.
	} // Notice: to teleport to new positions, `dpos[dim] = newPos[dim] - pos[dim]`. but most sims use relative motions.

	public static double[] getBounds() {
		assert null != bounds;
		return bounds; // Notice: for simple sims, can `return resolutionf`.
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

	public boolean posBound(double[] pos, PosBounds posBounds) throws IllegalArgumentException { // If `PosBounds.boundless != posBounds`, this ensures the invariant `0 <= pos[dim] && FishSim.getBounds()[dim] > pos[dim]` is established.
		switch(posBounds) { // `PosBounds.` is omitted from all `case`s, to support old `java --source` versions
		case invalidArgumentException:
		case PosBounds.invalidArgumentException:
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
			throw new IllegalArgumentException("Unknown `PosBounds posbounds`: " + posBounds); // [The compiler does this for you](https://codingtechroom.com/question/what-exception-compiler-unknown-enum-values-switch-expressions), so this just serves to document the lack of `default` codeflow.
		}
		return true;
	}

//    public static class Pos2 extends double[2] {} // `{Pos2[0], Pos2[1]}` is `{x, y}` position (or resolution), or is `{pos[0], pos[0]}` motion (derivative of position), or is is `{d2x, d2y}` acceleration (derivative number 2). This was supposed to do what `typedef` does (wish for future-proof (limitless dimensions) virtual `class` with functions for numerous transforms).
// Will use `double[]` for now. TODO: test how much of `java`'s [static `Array` overhead](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/Physics_sims_which_structures_to_use.md#separate-variables-versus-dim-lists) `java`'s toolkit optimizes for you. If performance is a problem, choose a new approach to use.

	private static PosBounds posBounds = PosBounds.wrapAroundResolution;
	public static boolean setResolution(int[] newResolution) {
		assert 2 == newResolution.length;
		assert 0 < newResolution[0]; //TODO: allow "headless" instances with `resolution = {0, 0}`?
		assert 0 < newResolution[1];
		resolution = newResolution;
		resolutionf[0] = resolution[0]; resolutionf[1] = resolution[1];
		resVolume = resolution[0] * resolution[1];
		bounds[0] = resolution[0];
		bounds[1] = resolution[1];
		boundsVolume = bounds[0] * bounds[1];
		// canvas = new Canvas(resolution[0], resolution[1]); // TODO: replace with `canvas.setWidth(resolution[0]); canvas.setHeight(resolution[1]);`?
		// gc = canvas.getGraphicsContext2D();
		// scene = new Scene(root, resolution[0], resolution[1], Color.LIGHTBLUE); // replace with `scene.widthProperty().bind(primaryStage.widthProperty());`?
		// stage.setScene(scene);
		return true;
	} //TODO: lock `updateFish()` plus `renderFish()` for this
	private static int[] resolution = {1280, 720};
	private static double[] resolutionf = {resolution[0], resolution[1]};
	private static int resVolume = resolution[0] * resolution[1];
	private static double[] bounds = {resolution[0] * 2, resolution[1] * 2}; // for simple sims, use `bounds = resolutionf;`
	private static double boundsVolume = bounds[0] * bounds[1];
	private static double fishVolume = 200; // Uses resolution of `Fish::render()`.
	private static double fishLengthsSep = 62; // Average `Fish`-lengths distance  from `Fish` to `Fish`.
	private static double fishPerVolume = 1 / fishVolume / fishLengthsSep; // `Fish` per volume (for 2D, volume is resolution).
	private static int FISH_COUNT = (int)(boundsVolume * fishPerVolume);
	private static int GRID_SIZE = 100; // Notice: set this to `Colllections.max({forces*.distance})` (which should equal what most sims call "view distance"), so that all relevent `Fish` are processed.
	private static int UPDATE_INTERVAL = 2; // The `frameCounter` per `Fish::applyFlockingRulesUpdate()`
	public static double monitorRefreshHertz = 60.0; // The `fps` to wish for // Notice: since this limits `fps` to `monitorRefreshHertz`, this prevents benchmarks which use `FpsTextMode.fps` (or `FpsTextMode.ms`). Benchmarks can still use `FpsTextMode.msSpec` (or `FpsTextMode.msFish`).

	public enum FpsTextMode { // `FpsTextMode` says which resources `fpsText` will show.
		none      (0     ), // `fpsText = "";`
		fps       (1 << 0), // `fpsText` += `fps` "FPS";
		ms        (2 << 1), // `fpsText` += `ms` "ms"; /* Notice: `ms = 1000 / fps;`, so includes idle CPU */
		msSpec    (1 << 2), // `fpsText` += `renderMs` "renderMs," `physicsMs` "physicsMs"; /* Notice: uses `System.nanoTime()`, does not include idle CPU */
		msFish    (1 << 3), // `fpsText` += `renderMs / fishShown` "renderMs / Fish shown," `physicsMs / fishList.size()` "physicsMs / Fish";
		fish      (1 << 4), // `fpsText` += `fishList.size()` "Fish";
		fishShown (1 << 5); // `fpsText` += `fishShown` "Fish shown";
		long value; // Stores bitwise-or of those.
		FpsTextMode(long value) { this.value = value; }
	} // TODO: replace manual bitshifts with `java.util.EnumSet<E>`?
	public long fpsTextMode = FpsTextMode.fps.value | FpsTextMode.ms.value | FpsTextMode.msSpec.value | FpsTextMode.msFish.value | FpsTextMode.fish.value | FpsTextMode.fishShown.value;

	private List<Fish> fishList = new ArrayList<>();
	private int fishShown = 0;
	private int[] gridSize = { (int)Math.ceil(getBounds()[0] / GRID_SIZE), (int)Math.ceil(getBounds()[1] / GRID_SIZE) };
	private Random random = new Random();
	private Pane root = new Pane();
	private Canvas canvas = new Canvas(resolution[0], resolution[1]);
	private GraphicsContext gc = canvas.getGraphicsContext2D();
	private Stage stage;

	private Text fpsText = new Text("0 FPS");
	private int frameCount = 0; // Count of invocations (of functions such as `FishSim::renderFish()`) since `lastTime`.
	private long lastTime = System.nanoTime();
	private long physicsNs = -1; // Stores `nanoTime()` (at end of functions such as `FishSim::updateFish()`) minus `nanoTime()` at start of those.
	private long renderNs = -1; // Stores `frameCount` sum of `nanoTime()` (at end of functions such as `renderFish()`) minus `nanoTime()` at start of those.
	private double fps = 0;
	public double renderMs = Double.NaN; // Stores average **ms** of **CPU** used for graphics functions per second (`renderNs / frameCount / 1_000_000.0`)
	public double physicsMs = Double.NaN; // Stores average **ms** of **CPU** used for physics functions per second (`physicsNs / physicsCounter / 1_000_000.0`)
	private int frameCounter = 0;
	private int physicsCounter = 0;

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// Initialize fish
		for(int i = 0; i < FISH_COUNT; i++) {
			double[] pos = {random.nextDouble() * getBounds()[0], random.nextDouble() * getBounds()[1]};
			double[] dpos = {(random.nextDouble() * 2 - 1) * Fish.MAX_SPEED, (random.nextDouble() * 2 - 1) * Fish.MAX_SPEED};
			fishList.add(new Fish(pos, dpos, Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble())));
		}

		root.getChildren().add(canvas);
		root.getChildren().add(fpsText);

		Scene scene = new Scene(root, resolution[0], resolution[1], Color.LIGHTBLUE);
		stage = primaryStage;
		primaryStage.setScene(scene);
		primaryStage.setTitle("Fish Simulation (Boids)");
		primaryStage.setResizable(false);
		primaryStage.show();

		fpsText.setX(10);
		fpsText.setY(30);
		fpsText.setFill(Color.WHITE);

		// Start animation loop
/*
		new AnimationTimer() {
			@Override
			public void handle(long now) { refreshLoop(now); }
		}.start(); // Notice: replace `Timeline` with this for benchmarks which use `FpsTextMode.fps` (or `FpsTextMode.ms`).
*/
		Timeline timeline = new Timeline(
			new KeyFrame(Duration.millis(1000.0 / monitorRefreshHertz), event -> { refreshLoop(System.nanoTime()); })
		); // Notice: since this limits `fps` to `monitorRefreshHertz`, this prevents benchmarks which use `FpsTextMode.fps` (or `FpsTextMode.ms`). Benchmarks can still use `FpsTextMode.msSpec` (or `FpsTextMode.msFish`).
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	private void refreshLoop(long now) {
		frameCounter++;
		if(frameCounter % UPDATE_INTERVAL == 0) {
			executor.submit(() -> updateFish());
		}

		renderFish();

		double elapsed = (now - lastTime) / 1_000_000_000.0;
		if(elapsed >= 1.0) {
			lastTime = now;
			fps = frameCount / elapsed;
			renderMs = renderNs / frameCount / 1_000_000.0;
			physicsMs = physicsNs / physicsCounter / 1_000_000.0;
			Platform.runLater(() -> fpsTextRefresh());
			frameCount = 0;
			renderNs = -1;
			physicsCounter = 0;
			physicsNs = -1;
		} else {
			frameCount++;
		}
	}

	private void outOfBounds(String function, Fish fish) {
		/* Notice: `outOfBounds()` has numerous sensible actions other than to print to `stderr`: `fish.die()`, `fish.stop()`, `fish.reverse()`, `fish.wrapAround()` */
		System.err.println(function + ": " + posOutOfBoundsStr(fish.pos, "Fish.pos"));
	}

	private void updateFish() {
		long physicsNsStart = System.nanoTime();

		// Use spatial partitioning (simple grid system)
		List<Fish>[][] grid = new ArrayList[gridSize[0]][gridSize[1]];
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[i].length; j++) {
				grid[i][j] = new ArrayList<>();
			}
		}

		// Assign fish to grid cells
		for(Fish fish : fishList) {
			if(fish.isInBounds) { // TODO: allow `PosBounds.boundless == posBounds` (but more `grid`s will use more **CPU**).
				int[] gridPos = {(int) (fish.pos[0] / GRID_SIZE), (int) (fish.pos[1] / GRID_SIZE)};
				grid[gridPos[0]][gridPos[1]].add(fish); // if `gridPos` is not in bounds, this will `throw new IndexOutOfBoundsException()`. But `Fish.setPos()` uses `FishSim::posBound()` which uses `FishSim::isPosInBounds()`, which ensures the `.pos` bounds to `resolution`.
			}
		}

		// Update each fish
		for(Fish fish : fishList) {
			fish.applyFlockingRules(fishList, grid);
			fish.update();
		}

		physicsNs += System.nanoTime() - physicsNsStart;
		physicsCounter++;
	}

	private void renderFish() {
		long renderNsStart = System.nanoTime();
		fishShown = 0;
		gc.clearRect(0, 0, resolution[0], resolution[1]);
		for(Fish fish : fishList) {
			if(fish.isVisible) { // For `Fish` not shown, this condition improves `fps` (lowers `renderNs`).
				fishShown++;
				fish.render(gc);
			}
		}
		renderNs += System.nanoTime() - renderNsStart;
	}

	private void fpsTextRefresh() {
		boolean fpsTextModeFps = (0 != (FpsTextMode.fps.value & fpsTextMode));
		boolean fpsTextModeMs = (0 != (FpsTextMode.ms.value & fpsTextMode));
		boolean fpsTextModeMsSpec = (0 != (FpsTextMode.msSpec.value & fpsTextMode));
		boolean fpsTextMsSpecFish = (0 != ((FpsTextMode.msSpec.value | FpsTextMode.msFish.value) & fpsTextMode)); // TODO: replace manual bitshifts with `java.util.EnumSet<E>`?
		boolean fpsTextModeMsFish = (0 != (FpsTextMode.msFish.value & fpsTextMode));
		boolean fpsTextModeFish = (0 != (FpsTextMode.fish.value & fpsTextMode));
		boolean fpsTextModeFishShown = (0 != (FpsTextMode.fishShown.value & fpsTextMode));
		double totalMs = 1 / fps * 1000;
		String fpsTextStr = "";
		String strSep = ", ", strJoin = " (";
		if(FpsTextMode.none.value == fpsTextMode) { return; }
		if(fpsTextModeFps) {
			fpsTextStr += String.format("%4.2f FPS" + strSep, fps);
		}
		if(fpsTextModeMs) {
			fpsTextStr += String.format("%4.2f MS" + (fpsTextMsSpecFish ? strJoin : strSep), totalMs);
		}
		if(fpsTextModeMsSpec) {
			fpsTextStr += String.format("%4.2f drawMS, %4.2f physicsMS", renderMs, physicsMs);
			fpsTextStr += (fpsTextModeMsFish ? strSep : "");
		}
		if(fpsTextModeMsFish) {
			fpsTextStr += String.format("%2.4f drawMS / Fish shown, %2.4f physicsMS / Fish", renderMs / fishShown, physicsMs / fishList.size());
		}
		if(fpsTextMsSpecFish) {
			if(0 != ((FpsTextMode.ms.value & fpsTextMode))) {
				fpsTextStr += ")";
			}
			fpsTextStr += strSep;
		}
		if(fpsTextModeFish) {
			fpsTextStr += String.format("%4d Fish", fishList.size());
			fpsTextStr += (fpsTextModeFishShown ? strJoin : strSep);
		}
		if(fpsTextModeFishShown) {
			fpsTextStr += String.format(fpsTextModeFish ? "%4d shown)" : "%4d Fish shown", fishShown);
			fpsTextStr += strSep;
		}
		fpsText.setText(fpsTextStr.substring(0, fpsTextStr.length() - strSep.length()));
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
		private static double MAX_SPEED = 3.0;
		private static double d2Pos = 0.1;     // Motion<sup>2</sup> (derivative #2 of position)
		private static double isSimilarTolerance = 0.2;
		public static boolean applyWallAvoidanceTru = (PosBounds.wrapAroundResolution == posBounds);
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

		public void setPos(double[] newPos) { // If `PosBounds.boundless != posBounds`, this ensures the invariant `0 <= pos[dim] && FishSim.getBounds()[dim] > pos[dim]` is established.
			isInBounds = posBound(newPos, posBounds);
			isVisible = (0 <= newPos[0] && resolution[0] > newPos[0] && 0 <= newPos[1]  && resolution[1] > newPos[1]);
			if((!isInBounds) && PosBounds.boundless != posBounds) {
				outOfBounds("Fish::setPos", this);
			}
			pos = newPos;
		}

		public void applyFlockingRules(List<Fish> allFish, List<Fish>[][] grid) {
			int[] gridPos = {(int) (pos[0] / GRID_SIZE), (int) (pos[1] / GRID_SIZE)};
			List<Fish> nearbyFish = new ArrayList<>();

			// Check neighboring grid cells
			for(int i = Math.max(0, gridPos[0] - 1); i <= Math.min(grid.length - 1, gridPos[0] + 1); i++) {
				for(int j = Math.max(0, gridPos[1] - 1); j <= Math.min(grid[0].length - 1, gridPos[1] + 1); j++) {
					nearbyFish.addAll(grid[i][j]);
				}
			}

			applySeparation(nearbyFish);
			applyAlignment(nearbyFish);
			applyCohesion(nearbyFish);
			applyWallAvoidance(getBounds());
		}

		private void applySeparation(List<Fish> nearbyFish) {
			double[] sepDpos = {0, 0};
			double[] sepNonsimilarDpos = {0, 0};
			int count = 0, countNonsimilar = 0;

			for(Fish other : nearbyFish) {
				if(other != this) {
					double[] diffPos = {pos[0] - other.pos[0], pos[1] - other.pos[1]};
					double dist = Math.hypot(diffPos[0], diffPos[1]);
					if(isSimilarTo(other)) {
						if(dist < forcesSeparation.distance) {
							sepDpos[0] += diffPos[0] / dist;
							sepDpos[1] += diffPos[1] / dist;
							count++;
						}
					} else {
						if(dist < forcesSeparationNonsimilar.distance) {
							sepNonsimilarDpos[0] += diffPos[0] / dist;
							sepNonsimilarDpos[1] += diffPos[1] / dist;
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

		private void applyAlignment(List<Fish> nearbyFish) {
			double[] avgDpos = {0, 0};
			int count = 0;

			for(Fish other : nearbyFish) {
				if(other != this && isSimilarTo(other)) {
					double dist = Math.hypot(pos[0] - other.pos[0], pos[1] - other.pos[1]);
					if(dist < forcesAlignment.distance) {
						avgDpos[0] += other.dpos[0];
						avgDpos[1] += other.dpos[1];
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

		private void applyCohesion(List<Fish> nearbyFish) {
			double[] avgPos = {0, 0};
			int count = 0;

			for(Fish other : nearbyFish) {
				if(other != this && isSimilarTo(other)) {
					double dist = Math.hypot(pos[0] - other.pos[0], pos[1] - other.pos[1]);
					if(dist < forcesCohesion.distance) {
						avgPos[0] += other.pos[0];
						avgPos[1] += other.pos[1];
						count++;
					}
				}
			}

			if(count > 0) {
				avgPos[0] = (avgPos[0] / count) - pos[0];
				avgPos[1] = (avgPos[1] / count) - pos[1];
				forcesCohesion.dposScaleSum(dpos, d2Pos, avgPos);
			}
		}

		private void applyWallAvoidance(double[] res) {
			if(!applyWallAvoidanceTru) { return; }
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

		public void update() {
			// Limit speed
			double speed = Math.sqrt(dpos[0] * dpos[0] + dpos[1] * dpos[1]);
			if(speed > MAX_SPEED) {
				dpos[0] = (dpos[0] / speed) * MAX_SPEED;
				dpos[1] = (dpos[1] / speed) * MAX_SPEED;
			}

			// Update position
			setPos(new double[] {pos[0] + dpos[0], pos[1] + dpos[1]});
		}

		public void render(GraphicsContext gc) {
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

	/* Proof-of-concept versions of `class {ImmutablePos, Pos, ImmutablePos2, Pos2}`, will allow dimension-agnostic physics.
	 * Not used for now, due to concerns of virtual function RAM plus CPU usage.
	 * The actual `class`s will include numerous more functions, plus will move into `./susuwu/{ImmutablePos, Pos, ImmutablePos2, Pos2}.java`
	 */
	public abstract static class ImmutablePos { /* `ImmutablePos` stores constant vectors (first-order tensors). Usage: `double acceptsConsts(ImmutablePos pos)`. */
		double[] pos; /* Notice: future versions will use `private double[] pos;` */
		public double at(int index) { /* Usage: `ImmutablePos.at(index)` `return`s `pos[index]`. */
			assert pos.length > index; /* Notice: this trusts `java` to enforce `Array` bounds */
			return pos[index];
		}
		public int dims() { /* Usage: `for(int index = ImmutablePos.dims(); index--; ) { sum += ImmutablePos.at(index); }` */
			return pos.length;
		}
		abstract public Pos zeros(); /* Usage: `ImmutablePos pos = o.zeros(); assert o.dims() == pos.dims(); for(int index = pos.dims(); index--; ) { assert 0 == pos.at(index); }` */
		abstract public Pos ones(); /* Usage: `ImmutablePos pos = o.ones(); assert o.dims() == pos.dims(); for(int index = pos.dims(); index--; ) { assert 1 == pos.at(index); }` */
		abstract public Pos clone(); /* Usage: `ImmutablePos pos = o.clone(); assert o.dims() == pos.dims(); for(int index = pos.dims(); index--; ) { assert o.at(index) == pos.at(index); }` */
		abstract public double volume(); /* Usage: `double arithmeticProduct = ImmutablePos.volume(); //Cartesian-volume` */
	};
	public abstract static class Pos extends ImmutablePos { /* `Pos` stores vectors (first-order tensors). Usage: `double acceptsMutables(Pos pos)`.  */
		public void set(int index, double newValue) { /* Usage: `Pos.set(index, newValue)`. */
			assert pos.length > index; /* Notice: this trusts `java` to enforce `Array` bounds */
			pos[index] = newValue;
		}
		public abstract double volume(); /* Usage: `return`s the product (Cartestian-volume) of `pos` */
		public abstract void plusEquals(ImmutablePos o); /* Usage: `Pos.plusEquals(oPos)` is the tensor version of `Pos += o` */
		public abstract void minusEquals(ImmutablePos o); /* Usage: `Pos.minusEquals(oPos)` is the tensor version of `Pos -= o` */
		public abstract void starEquals(ImmutablePos o); /* Usage: `Pos.starEquals(oPos)` is the tensor version of `Pos *= o` */
		public abstract void slashEquals(ImmutablePos o); /* Usage: `Pos.slashEquals(oPos)` is the tensor version of `Pos /= o` */
		public abstract void moduloEquals(ImmutablePos o); /* Usage: `Pos.moduloEquals(oPos)` is the tensor version of `Pos %= o` */
	};
	public static class ImmutablePos2 extends ImmutablePos { /* `ImmutablePos2` is the 2-dimensional specialization of `class ImmutablePos`. Usage: `double acceptsConsts(ImmutablePos2 pos2)`. */
		double[] pos = {0, 0}; // `{pos[0], pos[1]}` replaces `{x, y}` position, `{WIDTH, HEIGHT}` resolution, `{dx, dy}` motion tensors, or `{d2x, d2y}` acceleration tensors.
		public ImmutablePos2() {}
		public ImmutablePos2(Pos2 o) { /* Notice: must use `interface` (or multiple inheritance) to allow implicit conversion of `Pos2` into `ImmutablePos` plus `ImmutablePos2` */
			pos[0] = o.pos[0];
			pos[1] = o.pos[1];
		}
		public ImmutablePos2(double pos0, double pos1) {
			pos[0] = pos0;
			pos[1] = pos1;
		}
		@Override
		public int dims() {
			return 2;
		}
		@Override
		public Pos zeros() {
			return new Pos2();
		}
		@Override
		public Pos ones() {
			return new Pos2(1, 1);
		}
		@Override
		public Pos clone() {
			return new Pos2(pos[0], pos[1]);
		}

		@Override
		public double volume() {
			return pos[0] * pos[1];
		}
	};
	public static class Pos2 extends Pos { /* `class Pos2` is the 2-dimensional specialization of `class Pos` */
		double[] pos = {0, 0};
		public Pos2() {}
		public Pos2(double pos0, double pos1) {
			pos[0] = pos0;
			pos[1] = pos1;
		}
		@Override
		public int dims() {
			return 2;
		}
		@Override
		public Pos zeros() {
			return new Pos2();
		}
		@Override
		public Pos ones() {
			return new Pos2(1, 1);
		}
		@Override
		public Pos clone() {
			return new Pos2(pos[0], pos[1]);
		}

		@Override
		public double volume() {
			return pos[0] * pos[1];
		}
		@Override
		public void plusEquals(ImmutablePos o) {
			pos[0] += o.pos[0];
			pos[1] += o.pos[1];
		}
		@Override
		public void minusEquals(ImmutablePos o) {
			pos[0] -= o.pos[0];
			pos[1] -= o.pos[1];
		}
		@Override
		public void starEquals(ImmutablePos o) {
			pos[0] *= o.pos[0];
			pos[1] *= o.pos[1];
		}
		@Override
		public void slashEquals(ImmutablePos o) {
			pos[0] /= o.pos[0];
			pos[1] /= o.pos[1];
		}
		@Override
		public void moduloEquals(ImmutablePos o) {
			pos[0] %= o.pos[0];
			pos[1] %= o.pos[1];
		}
	};
};

