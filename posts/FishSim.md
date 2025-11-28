**\[Preview\] Simple fish sim for `java`, which will include reusable `public class`s (for new sims to use)**

\[*Notice*: <https://github.com/SwuduSusuwu/> publishes all posts (which includes [this post](https://github.com/SwuduSusuwu/SusuJava/blob/preview/posts/FishSim.md)) through [*Creative Commons Attribution 2*](https://creativecommons.org/licenses/by/2.0/), which allows all uses (with just this notice (which allows users to browse to old or new versions) as attribution.).\]

# Table of Contents
- [Intro](#intro)
- [Synopsis](#synopsis)

# Intro
[`./posts/FishSim.md`](#table-of-contents) is split from [`../SusuPosts/posts/Human_ancestors_are_fish.md#request-java-fish`](https://github.com/SwuduSusuwu/SusuPosts/blob/69b7b1545ab51a1c1a562c0ac838a950bb086442/posts/Human_ancestors_are_fish.md#request-java-fish).
* The build script moved to [`./susuwu/build.sh`](../susuwu/build.sh). Usage: `./susuwu/build.sh`
* The source code moved to [`./susuwu/FishSim.java`](../susuwu/FishSim.java) (`package susuwu.FishSim;`).
  * The [original version of this source code](https://github.com/SwuduSusuwu/SusuJava/blob/solarPro2FishSim/susuwu/FishSim.java) was [produced through *Solar-Pro-2*](https://poe.com/s/ehlOJYRJNsrGJttfJ4HK), but the goal is just to use thus as a template (for future versions to replace all with own source code).
  * Uses [`./susuwu/Calculus.java`](../susuwu/Calculus.java): `` public class Calculus { /* `class Calculus` houses simple trigonometric (transcendental) `public static` functions. Future versions will include true calculus functions (such as "area-under-curve" integrals, or "False Position" or "Quadratic Interpolation" root formulas). */ ``
  * Uses [`./susuwu/Forces.java`](../susuwu/Forces.java): `public class Forces implements java.lang.Cloneable` "/\* Usage: `import susuwu.Forces;` ... replaces `double fooDistance; double fooFactor;` with `Forces fooForces;`, so other `double`s are not confused with those. \*/"

******************************************

# Synopsis
* **Q**: "Produce a C++ `class fish` which does a fish sim". [*Grok-2*'s *C++* fish sim](https://poe.com/s/fPpFTNwFxeOUjXuKnSu4)
* **Q**: "Use GLFW+Vulkan to produce virtual fish which swim around". [*Solar-Pro-2*](https://poe.com/Solar-Pro-2)'s [*GLFW*](https://github.com/glfw/glfw) + [*Vulkan*](https://github.com/KhronosGroup?q=Vulkan) [fish sim source code](https://poe.com/s/Ou5DxBJQKdLeaRKhvxYK)
* **Q**: "Produce *OpenGLES2* code which moves the sprites of some fish around." [*Grok-2*](https://poe.com/Grok-2)'s's [*OpenGLES2*](https://www.khronos.org/opengles/) [fish sim source code](https://poe.com/s/ZGtQKWGpBLkOzaTr6SkA).
  * [`../SusuPosts/` published this *C++* version](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/Human_ancestors_are_fish.md#request-opengles2-fish).

