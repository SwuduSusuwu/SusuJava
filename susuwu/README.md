(C) 2024 Swudu Susuwu, dual licenses: choose [_GPLv2_](./LICENSE_GPLv2) or [_Creative Commons Attribution 2_](./LICENSE) (allows all uses).

*Notice*: You switched to the [`preview`](https://github.com/SwuduSusuwu/SusuJava/tree/preview/) branch, which has the newest source code, but is unstable, plus has much use of `git rebase` + `git push --force` (which require you to use `git pull --rebase`); switch to [`trunk`](https://github.com/SwuduSusuwu/SusuJava/blob/trunk/README.md#table-of-contents) (`git switch trunk`) for source code which is more stable plus has more support (is more sure to execute on all computers)..
- This `preview` branch is for [beta tests (public review)](#beta-test-preview-branch) / [continuous integration (autonomous review)](https://google.com?q=continuous-integration-branch).

# Table of Contents
- [Purposes](#purposes)
- [How to contribute](../README.md#how-to-contribute)
  - [Sponsor](../README.md#sponsor)

# Purposes
[`./susuwu/`](./) stores `package susuwu;`, which is [`java`](https://www.java.com/en/download/help/whatis_java.html) source code (`*.java`) for <https://GitHub.com/SwuduSusuwu/> (which is the `git` version of <https://SwuduSusuwu.SubStack.com/>). The goal is to publish modular (reusable) source code which future sims can use:
- [`./susuwu/Calculus.java`](./Calculus.java): `` public class Calculus { /* `class Calculus` houses simple trigonometric (transcendental) `public static` functions. Future versions will include true calculus functions (such as "area-under-curve" integrals, or "False Position" or "Quadratic Interpolation" root formulas). */ ``
- [`./susuwu/ImmutablePos.java`](./ImmutablePos.java): `` public class ImmutablePos implements java.lang.Cloneable, java.util.RandomAccess { /* `ImmutablePos` stores constant vectors (first-order tensors). Usage: `double acceptsConsts(ImmutablePos pos)`. */ ``
  - [`./susuwu/Pos.java`](./Pos.java): `` public class Pos extends ImmutablePos { /* `Pos` stores mutable vectors (first-order tensors). Usage: `void setsPos(Pos pos)`. */ ``
  - [`./susuwu/ImmutablePos2.java`](./ImmutablePos2.java): `` public class ImmutablePos2 extends ImmutablePos { /* `ImmutablePos2` is the 2-dimensional specialization of `class ImmutablePos`. Usage: `double acceptsConsts(ImmutablePos2 pos2)`. */ ``
  - [`./susuwu/Pos2.java`](./Pos2.java): `` public class Pos2 extends Pos { /* `class Pos2` is the 2-dimensional specialization of `class Pos`. Usage: `Pos2 position;` */ ``
- [`./susuwu/Forces.java`](./Forces.java): `public class Forces implements java.lang.Cloneable` "/\* Usage: `import susuwu.Forces;` ... replaces `double fooDistance; double fooFactor;` with `Forces fooForces;`, so other `double`s are not confused with those. \*/"
  - `boolean posIfDistSum(posDes, posSource, dist)`: for *Boids* groups: `if(posIfDistPow2Sum(averagePosOfGroup, posOfIndividual, distPow2ToIndividual) { ++sizeOfGroup; }`
  - `boolean dposScaleSum(dposDes, d2pos, dposSource)`: Usage: for Boids groups: `if(posIfDistSum(averageDposOfGroup, dposOfIndividual, distanceToIndividual) { ++sizeOfGroup; }`
- [`./susuwu/ImmutablePosBounds.java`](./ImmutablePosBounds.java): `public class ImmutablePosBounds implements java.lang.Cloneable` \* Usage: `ImmutablePosBounds posBounds(PosBoundsMode);`. \*/"
  - `enum ImmutablePosBounds`: stores how sims enforce bounds.
  - [`./susuwu/PosBounds.java`](./PosBounds.java): `public class PosBounds extend ImmutablePosBounds` is the mutable (`public void set`) version of `ImmutablePosBounds`.
- [`./susuwu/SimUsages.java`](./SimUsages.java): `class SimUsages` shows `FpsTextMode` statistics such as `fps` or `ms`. Requirements: some render loop (for measurements). Is not specific to the renderer used. Was produced for `class FishSim`, so the text (plus comments) assume the organisms are `class Fish`, but `SimUsages` is not specific to `class Fish`.
- [`./susuwu/FishSim.java`](./FishSim.java): "Simple [*JavaFX*](https://github.com/openjdk/jfx) fish sim. Usage: `import susuwu.FishSim;` includes `public class`s (for new sims to use)"

