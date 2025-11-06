**\[Preview\] Simple fish sim for `java`, which will include reusable `public class`s (for new sims to use)**

\[*Notice*: <https://github.com/SwuduSusuwu/> publishes all posts (which includes [this post](https://github.com/SwuduSusuwu/SusuJava/blob/preview/posts/FishSim.md)) through [*Creative Commons Attribution 2*](https://creativecommons.org/licenses/by/2.0/), which allows all uses (with just this notice (which allows users to browse to old or new versions) as attribution.).\]

# Table of Contents
- [Intro](#intro)
- [How to improve](#how-to-improve)
- [Synopsis](#synopsis)

# Intro
[`./posts/FishSim.md`](#table-of-contents) is split from [`../SusuPosts/posts/Human_ancestors_are_fish.md#request-java-fish`](https://github.com/SwuduSusuwu/SusuPosts/blob/69b7b1545ab51a1c1a562c0ac838a950bb086442/posts/Human_ancestors_are_fish.md#request-java-fish).
* The build script moved to [`./susuwu/build.sh`](../susuwu/build.sh). Usage: `./susuwu/build.sh`
* The source code moved to [`./susuwu/FishSim.java`](../susuwu/FishSim.java) (`package susuwu.FishSim;`).
  * The [original version of this source code](https://github.com/SwuduSusuwu/SusuJava/blob/solarPro2FishSim/susuwu/FishSim.java) was [produced through *Solar-Pro-2*](https://poe.com/s/ehlOJYRJNsrGJttfJ4HK), but the goal is just to use thus as a template (for future versions to replace all with own source code).
  * Uses [`./susuwu/SimUsages.java`](../susuwu/SimUsages.java): `class SimUsages` shows `FpsTextMode` statistics such as `fps` or `ms`. Requirements: some render loop (for measurements). Is not specific to the renderer used. Was produced for `class FishSim`, so the text (plus comments) assume the organisms are `class Fish`, but `SimUsages` is not specific to `class Fish`.
  * Uses [`./susuwu/Calculus.java`](../susuwu/Calculus.java): `` public class Calculus { /* `class Calculus` houses simple trigonometric (transcendental) `public static` functions. Future versions will include true calculus functions (such as "area-under-curve" integrals, or "False Position" or "Quadratic Interpolation" root formulas). */ ``
  * Will use [`./susuwu/ImmutablePos.java`](../susuwu/ImmutablePos.java): `public class ImmutablePos implements java.lang.Cloneable, java.util.RandomAccess` stores constant vectors (first-order tensors), to future-proof (for volumetrics). [Usage: `double acceptsConsts(ImmutablePos pos)`](https://github.com/SwuduSusuwu/SusuJava/compare/preview..pos2#diff-8c440bb92bc6939e1450542897e0bbb1a8737b93808ea63ed32784edfacef4b4).
    * [`./susuwu/Pos.java`](../susuwu/Pos.java): `public class Pos extends ImmutablePos` stores mutable vectors (first-order tensors). Usage: `void setsPos(Pos pos)`.
    * [`./susuwu/ImmutablePos2.java`](../susuwu/ImmutablePos2.java): `public class ImmutablePos2 extends ImmutablePos` is the 2-dimensional specialization of `class ImmutablePos`. Usage: `double acceptsConsts(ImmutablePos2 pos2)`.
    * [`./susuwu/Pos2.java`](../susuwu/Pos2.java): `public class Pos2 extends Pos` is the 2-dimensional specialization of `class Pos`. Usage: `Pos2 position;`.
  * Uses [`./susuwu/Forces.java`](../susuwu/Forces.java): `public class Forces implements java.lang.Cloneable` "/\* Usage: `import susuwu.Forces;` ... replaces `double fooDistance; double fooFactor;` with `Forces fooForces;`, so other `double`s are not confused with those. \*/"
    * `Forces.posIfDistSum(posDes, posSource, dist)`: for *Boids* groups: `if(posIfDistPow2Sum(averagePosOfGroup, posOfIndividual, distPow2ToIndividual) { ++sizeOfGroup; }`. For `Fish::apply*()`, reduces duplicate code.
    * `Forces.dposScaleSum(dposDes, d2pos, dposSource)`: for Boids groups: `if(dposScaleSum(derivativeOfPosition, secondDerivOfPos, averageDposOfGroup)) { position += derivativeOfPosition; }`, reduces duplicate code.
  * Uses [`./susuwu/ImmutablePosBounds.java`](../susuwu/ImmutablePosBounds.java): `public class ImmutablePosBounds implements java.lang.Cloneable`, usage: `ImmutablePosBounds posBounds(PosBoundsMode);`
    * +`enum ImmutablePosBounds`: stores how sims enforce bounds.
    * [`./susuwu/PosBounds.java`](../susuwu/PosBounds.java): `public class PosBounds extend ImmutablePosBounds` is the mutable (`public void set`) version of `ImmutablePosBounds`.
    * [`./susuwu/ImmutablePosBounds.java`](../susuwu/ImmutablePosBounds.java): `public class ImmutablePosBounds implements java.lang.Cloneable` Usage: `ImmutablePosBounds posBounds(PosBoundsMode);`
      * `enum ImmutablePosBounds`: stores how sims enforce bounds.
        * if `PosBoundsMode.wrapAroundResolution`, with `boundsResolutionFactor = 2` the view is close to a natural ocean.
      * `ImmutablePosBounds::getBounds()`: to replace `FishSim::resolution` for physics uses. Introduced `bounds` for this (to allow out-of-view positions). Notice: for simple sims, this can `return resolutionf;`.
        * `setBounds({resolution[0] * 2, resolution[1] * 2};`
        * `boolean isPosInBounds(double[] pos)`: replaces duplicate code which tests for if `pos` is in bounds. Allows 2-dimensions or volumetric.
        * `boolean posBound(double[] pos, ImmutablePosBounds posBounds)`: enforces bounds onto `pos` (`Fish::setPos(newPos)` uses this). If `PosBoundsMode.boundless`, just tests `pos`.
        * `public double[] posDiff(double[] pos, double[] o)`: reduces duplicate code for complex (such as `PosBoundsMode.wrapAroundResolution`) distances.
          * `Fish::getPosDiff(Fish o)`: uses `FishSim::posBounds.posDiff` so distances follow `PosBoundsMode.wrapAroundResolution`.

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
  * @`fishCount`: (from `50`) to `102`, since the window now has more room.
* @`Fish::createFishShape()`: produce 2 colors of fish.
  * @`class FishSim`: is now close to a fluid particle sim which has 2 types of molecules which group to similar molecules (such as [oleophilic compounds](https://thepetrosolutions.com/forums/topic/difference-between-oleophobic-and-oleophilic-impurities/#post-3508)) plus separate from nonsimilar molecules ([such as oleophobic compounds](https://poe.com/s/dYx54tOaDTaDnaBT9TRm)), except the numerous steps of *Boids* formula cause some emergent phenomenon which simple molecules do not possess.

******************************************

Notice: [Used *Solar-Pro-2* to improve codeflow](https://github.com/SwuduSusuwu/SusuJava/commit/6242d2045d619dd664c9a8eea9141c5d178a2ce8) (of the ancestor `git commit` --- which was half (`1 / 2`) human-produced source code --- to thus) [so `fps` improves](https://poe.com/s/ifeHY8AcpVmVC7R5aPB7):
* @`class FishSim`: move `class Fish`-specific values into @`class Fish`.
* @`class FishSim`: use `java.util.concurrent.Executor{s,Service}` to offload `updateFish()` physics (now uses 2 **CPU**s).
* +`gridResolution`, @`updateFish()`: use `gridResolution` to split `List<Fish> fishList` into `List<Fish>[][] grid` (which reduces *O(n^2)* to *O(n^2 / (resolution[] / gridResolution)))* **CPU** use)).
* -`javafx.scene.shape.Polygon`, +`javafx.scene.canvas.Canvas`, +`javafx.scene.canvas.GraphicsContext`: improves renderer **CPU** use?
* @`class FishSim`: replaces `1.0 / 2 < random.nextDouble()` with `random.nextBoolean()`.
* {-`Fish::createFishShape()`, -`Fish::getShape()`}, {+`Fish::render()`, +`FishSim::renderFish()`}: switch to `GraphicsContext`.
* @`Fish::update`: `Fish` now wrap around (to opposite edges) if out-of-bounds.
* Notice: the list which follows is all own improvements (versus version above). Own version:
  * @`FishSim::*`, @`Fish::*`: now mutable (since future versions will allow to resize windows plus configure distances). TODO: introduce `get*()` methods (so that typos do not reconfigure constants, such as view distances).
  * @`gridResolution`: documents minimum value which enforces `*_DISTANCE`s.
  * @`applyFlockingRules()`: replaces magic constants (`100`) with `gridResolution` (fixes undefined behaviour if `gridResolution` changes).
  * @`Fish::applyFlockingRules()`, @`Fish::update()`: Replaces magic constants ({`2600`, `1600`}) with {`resolution[0]`, `resolution[1]`}.
  * @`class FishSim`: reduces `positionInterval` (from `5`) to `2` (since `ExecutorService` is used, this does not lower `fps`) so physics is smooth.
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
    * +`FishSim::outOfBounds()`: improves @`FishSim::updateFish()` (which now uses this if `Fish` not in `grid` bounds).
    * +`void Fish::setPos(double[] newPos)`: if `Fish` not in bounds, uses `FishSim::outOfBounds()`.
  * @`FishSim::updateFish()`: replaces magic constants (`resolution[] / gridResolution`) with `grid.length`, to ensure correct access if the code which produces `grid` changes.
    * @`FishSim::updateFish()`: produces extra `grid`s if `resolution[]` is not a multiple of `gridResolution`, so that `Fish` with position close to the resolution (close to edges / bounds) are still included.
    * @`FishSim::updateFish()`: moves bounds test into `FishSim::posBounds.posBound()`, which `Fish::setPos()` uses.
    * @`class FishSim`: +`fishVolume`, +`fishLengthsSep`, `fishPerVolume`: so `fishCount` scales to resolution.
    * @`FishSim::renderFish()`: `if(posBounds.isPosInBounds(fish.pos))` reduces calls to `fish.render()` (improves `fps` for sims with huge unshown groups of fish).
      * @`class Fish`: +`boolean isInBounds;` stores `boolean posBounds.posBound()`'s `return` value (improves CPU use). TODO: rename to `isVisible`?
        * @`FishSim::updateFish()`: `if(fish.isInBounds) {}` around `grid[gridPos[0]][gridPos[1]].add(fish);`, so `FishSim` allows out-of-bounds `Fish`.
      * @`class Fish`: +`boolean isVisible`: improves `FishSim::renderFish()` (reduces calls to `fish.render()`, which improves `fps` for sims with huge unshown groups of fish).
    * +`boolean FishSim::setResolution(newResolution)`: this sets all variables (plus uses all functions) required for `class FishSim` to switch to `newResolution`. Blocks unless has exclusive access to `ReentrantLock updateFishLock, renderFishLock;`.
      * +`ReentrantLock updateFishLock`: @`updateFish()` blocks unless has exclusive access to this.
      * +`ReentrantLock renderFishLock`: @`renderFish()` blocks unless has exclusive access to this.

Notice: replaced most of [*Solar-Pro-2*'s original `FishSim.java`](https://github.com/SwuduSusuwu/SusuJava/blob/solarPro2FishSim/susuwu/FishSim.java), as this intro documents (plus [*GitHub*'s `/compare/` tool shows](https://github.com/SwuduSusuwu/SusuJava/compare/solarPro2FishSim..susuFishSim#diff-8c440bb92bc6939e1450542897e0bbb1a8737b93808ea63ed32784edfacef4b4)).

******************************************

## How to improve
* One obvious submodule to introduce is "predator / prey" dynamics, but for now have chosen not to introduce thus, [due to ethical concerns such as *existence monism*](./ConcernsOfExistenceMonism.md).
* Improve **CPU** use (do not know how to, have chose not to use **OOP** `class`s for most of the physics due to concerns for the **CPU** use). `FishSim` is physics-bound, guess offload to **GPGPU** can improve this?
  * Since `updateFish()`'s formulas use much CPU, choose how to show smooth motion with `24 > physicsFps`.
  * @`updateFish()`: move `fish.pos += fish.dpos` into `refreshLoop()`, as ? Or into a separate +`physicsLoop()`?
  * Replace `fish.pos += fish.dpos` with `fish.pos += fish.dpos * factor` to ensure motion is continuous.
  * `factor = physicsRefreshHertz / fps` or `factor = physicsRefreshHertz / physicsFps`?
* Improve the user interface (now is just [`fpsTextRefresh()`, which shows resource use](https://github.com/SwuduSusuwu/SusuJava/blob/d21ed39be6640e94f3091e934717e8c7cfbdc758/susuwu/SimUsages.java#L103-L143), plus [`renderFish()`](https://github.com/SwuduSusuwu/SusuJava/blob/bff7b245616f80327587b3ba1fa8a254acdad5b8/susuwu/FishSim.java#L314-L327)' viewport which shows the models of `class Fish`), to allow to scroll the viewport around (requires [`resolution`](https://github.com/SwuduSusuwu/SusuJava/blob/bff7b245616f80327587b3ba1fa8a254acdad5b8/susuwu/FishSim.java#L150) to include 2 more `double`s, to store viewport offset, which `fish.isVisible` must use), plus allow to set numerous options (which for now have "functional constness" so those invariants allow `java` to use more efficient execution), plus allow the user to control one of the organisms (which for now are just `Fish`).
  * `FishSim`'s renderer (`renderFish`) is separate from [`FishSim`'s physics loop (`updateFish`)](https://github.com/SwuduSusuwu/SusuJava/blob/bff7b245616f80327587b3ba1fa8a254acdad5b8/susuwu/FishSim.java#L298-L312) (`PhysicsMode physicsMode` allows to execute on separate **CPU**s through [`AnimationTimer`](https://github.com/SwuduSusuwu/SusuJava/blob/bff7b245616f80327587b3ba1fa8a254acdad5b8/susuwu/FishSim.java#L230-L235), [`Timeline`](https://github.com/SwuduSusuwu/SusuJava/blob/bff7b245616f80327587b3ba1fa8a254acdad5b8/susuwu/FishSim.java#L236-L242), or [`ExecutorService`](https://github.com/SwuduSusuwu/SusuJava/blob/bff7b245616f80327587b3ba1fa8a254acdad5b8/susuwu/FishSim.java#L260-L264)), but still shares the process with the physics loop.
    * Can [still execute `updateFish()` as part of `renderFish()` if `physicsMode = PhysicsMode.synchronous*`](https://github.com/SwuduSusuwu/SusuJava/blob/bff7b245616f80327587b3ba1fa8a254acdad5b8/susuwu/FishSim.java#L252-L256), but future versions are supposed to drop synchronous modes.
    * Should split `updateFish()` into a new executable (such as +`./susuwu/PhysicsLoop.java`) which produces (plus moves) instances of `class Fish`, with `Fish` positions sent to +`./susuwu/GraphicsShow.java` (which shows `Fish` at those positions), to allow servers for `FishSim`.
    * `updateFish()` is difficult to do at 24fps (the minimum which shows smooth motion to humans) with thousands of `Fish`. If `FishSim`'s [`monitorRefreshHertz`](https://github.com/SwuduSusuwu/SusuJava/blob/bff7b245616f80327587b3ba1fa8a254acdad5b8/susuwu/FishSim.java#L164) is > [`physicsRefreshHertz`](https://github.com/SwuduSusuwu/SusuJava/blob/bff7b245616f80327587b3ba1fa8a254acdad5b8/susuwu/FishSim.java#L165) (in particular, if `24 > physicsRefreshHertz`): sum `pos += dpos` on monitor refresh (or some sort of simple motion loop), so movement is smooth.
  * Introduce [servers](https://codingtechroom.com/question/create-basic-java-server) for shared experiences. But until `FishSim` has interfaces for user interactions, servers are limited to **RO** `./susuwu/PhysicsLoop.java` hosts with passive users. Due to variable latencies to / from servers, what those `./susuwu/PhysicsLoop.java` servers must do:
    * [`PhysicsMode physicsMode`](https://github.com/SwuduSusuwu/SusuJava/blob/bff7b245616f80327587b3ba1fa8a254acdad5b8/susuwu/FishSim.java#L45) = [`PhysicsMode.separateFps`](https://github.com/SwuduSusuwu/SusuJava/blob/bff7b245616f80327587b3ba1fa8a254acdad5b8/susuwu/FishSim.java#L42).
    * Send the derivative of position (`dpos`), so that (even in the presence of server congestion) each monitor refresh continues to show smooth movement of `Fish`s.
    * Send the absolute positions (`pos`), so that position drifts (due to server congestion, or due to insufficient monitor refresh to follow the physics loop) are set back to shared, true position values.
* Introduce continuous goals for `class Fish` (with no predator / prey dynamics, though, goals are limited), whose progress is stored.
  * Introduce new organisms (for now `class FishSim` is limited to `class Fish`). Do not know specifics, but guess more numerous organisms improves the total list of goals for `FishSim` organisms to pursue.
* Introduce volumetrics (for now the renderer is 2-dimensional, but most of `FishSim` was produced as "dimension-agnostic" (future-proof) source code (on the [`pos2` branch version of `FishSim.java`](https://github.com/SwuduSusuwu/SusuJava/blob/pos2/susuwu/FishSim.java)), so guess is simple to do (but new to `java`, so do not know which libs to use). How to introduce pseudo-volumetric visuals with the current (`javafx`, 2-dimensional) canvas:
  * If "depth" is introduced (if {`Fish.pos`, `Fish.dpos`} include distance to *viewport*), `Fish::render()` must introduce occlusion (must hide sections of distant `Fish` which overlap with `Fish` which are close to *viewport*).
  * If `Fish::render()` uses distance to *viewport* to scale (*geometric resize*) the `Fish` images (which for now simple geometric primitives), the result is pseudo-volumetric models.
  * If `Fish::render()` uses distance to *viewport* to "scale" (*geometric translation*) the `Fish` position (`Fish.pos`) within the *frustum*, the result is parallax perspective.
  * If `class FishSim` allows to "tilt" (*trapezoidal distortion*) the *viewport perspective*, with the other improvements of this list (occlusion, distance scales, parallax perspectives), the result is similar to "2.5 dimensional" (such as *Starcraft: Brood Wars*)  visuals.

******************************************

# Synopsis
* **Q**: "Produce a C++ `class fish` which does a fish sim". [*Grok-2*'s *C++* fish sim](https://poe.com/s/fPpFTNwFxeOUjXuKnSu4)
* **Q**: "Use GLFW+Vulkan to produce virtual fish which swim around". [*Solar-Pro-2*](https://poe.com/Solar-Pro-2)'s [*GLFW*](https://github.com/glfw/glfw) + [*Vulkan*](https://github.com/KhronosGroup?q=Vulkan) [fish sim source code](https://poe.com/s/Ou5DxBJQKdLeaRKhvxYK)
* **Q**: "Produce *OpenGLES2* code which moves the sprites of some fish around." [*Grok-2*](https://poe.com/Grok-2)'s's [*OpenGLES2*](https://www.khronos.org/opengles/) [fish sim source code](https://poe.com/s/ZGtQKWGpBLkOzaTr6SkA).
  * [`../SusuPosts/` published this *C++* version](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/Human_ancestors_are_fish.md#request-opengles2-fish).

