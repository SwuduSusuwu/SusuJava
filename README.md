(C) 2024 Swudu Susuwu, dual licenses: choose [_GPLv2_](./LICENSE_GPLv2) or [_Creative Commons Attribution 2_](./LICENSE) (allows all uses).

*Notice*: You switched to the [`preview`](https://github.com/SwuduSusuwu/SusuJava/tree/preview/) branch, which has the newest posts, but is unstable, and has much use of `git rebase` + `git push --force` (which require you to use `git pull --rebase`); switch to [`trunk`](https://github.com/SwuduSusuwu/SusuJava/blob/trunk/README.md#table-of-contents) (`git switch trunk`) for posts which are more stable plus have more support.
- This `preview` branch is for [beta tests (public review)](#beta-tests-preview-branch) / [continuous integration (autonomous review)](https://google.com?q=continuous-integration-branch).

# Table of Contents
- [Purposes](#purposes)
- [How to use this](#how-to-use-this)
  - [Download](#download)
  - [Signatures / certificates](#signatures--certificates)
- [How to contribute](#how-to-contribute)
  - [Beta tests (preview branch)](#beta-tests-preview-branch)
  - [Good first issues to contribute to](https://github.com/SwuduSusuwu/SusuJava/contribute)
  - [Contributor conventions / rules](#contributor-conventions--rules)
    - [_Markdown_](#markdown)
    - [`git`](#git)
    - [`java`](#java)
    - [Code generation tool use.](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/Code_generation_attributions.md)
  - [Sponsor](#sponsor)
    - [Escrow](#escrow)
    - [Affiliates](#affiliates)
  - [Tools used](#tools-used)
  - [Contributors / sponsors list](#contributors--sponsors)

# Purposes
[`./.ssh/`](./.ssh/) is to [use signatures / certificates](#signatures--certificates).

[`./susuwu/`](./susuwu/) stores `package susuwu;`, which is [`java`](https://www.java.com/en/download/help/whatis_java.html) source code (`*.java`) for <https://GitHub.com/SwuduSusuwu/> (which is the `git` version of <https://SwuduSusuwu.SubStack.com/>). The goal is to publish modular (reusable) source code which future sims can use:
- [`./susuwu/Calculus.java`](./susuwu/Calculus.java): `` public class Calculus { /* `class Calculus` houses simple trigonometric (transcendental) `public static` functions. Future versions will include true calculus functions (such as "area-under-curve" integrals, or "False Position" or "Quadratic Interpolation" root formulas). */ ``
- [`./susuwu/Forces.java`](./susuwu/Forces.java): `public class Forces implements java.lang.Cloneable` "/\* Usage: `import susuwu.Forces;` ... replaces `double fooDistance; double fooFactor;` with `Forces fooForces;`, so other `double`s are not confused with those. \*/"
- [`./susuwu/SimUsages.java`](./susuwu/SimUsages.java): `class SimUsages` shows `FpsTextMode` statistics such as `fps` or `ms`. Requirements: some render loop (for measurements). Is not specific to the renderer used. Was produced for `class FishSim`, so the text (plus comments) assume the organisms are `class Fish`, but `SimUsages` is not specific to `class Fish`.
- [`./susuwu/FishSim.java`](./susuwu/FishSim.java): "Simple [*JavaFX*](https://github.com/openjdk/jfx) fish sim. Usage: `import susuwu.FishSim;` includes `public class`s (for new sims to use)"

[`./posts/`](./posts/) is *Markdown* which has to do with `java` sims, plus references to *Markdown* from other repos which has some use for `java` sims:
- [`./posts/FishSim.md`](./posts/FishSim.md) is split from [`../SusuPosts/posts/Human_ancestors_are_fish.md#request-java-fish`](https://github.com/SwuduSusuwu/SusuPosts/blob/69b7b1545ab51a1c1a562c0ac838a950bb086442/posts/Human_ancestors_are_fish.md#request-java-fish). **Notice**: moved source code from thus into [`./susuwu/FishSim.java`](./susuwu/FishSim.java) (`package susuwu.FishSim;`, so *GitHub* can do unit tests plus code reviews).
- [`../SusuLib/posts/`](https://github.com/SwuduSusuwu/SusuPosts/tree/preview/posts/) is a work-in-progress which is supposed to mirror all posts from <https://SwuduSusuwu.SubStack.com/>, which starts with the oldest posts (so for now this does not include new posts, which <https://github.com/SwuduSusuwu/SusuLib/tree/preview/posts/> does, but `SusuLib` just includes posts which have use for `g++` tools).
- [`../SusuLib/posts/CnsCompress.md`](https://github.com/SwuduSusuwu/SusuLib/blob/preview/posts/CnsCompress.md) discusses formulas for computers to produce textures plus meshes.
- [`../SusuLib/posts/SimdGpgpuTpu.md`](https://github.com/SwuduSusuwu/SusuLib/blob/preview/posts/SimdGpgpuTpu.md) is discussions of how to improve performance (which sims are sensitive to) of programs.
- [`../SusuPosts/posts/SakuraSchoolHowto.md`](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/SakuraSchoolHowto.md) is discussions of sims for *Android OS* (*Android OS* uses *Java* for most programs).
- [`../SusuPosts/posts/2_dimensional_forge.md`](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/2_dimensional_forge.md) is discussions of how to render on computers.
- [`../SusuPosts/posts/GoogleStore_MicrosoftStore_Ubuntu_scripted_tool_sims.md#google-store-question`](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/GoogleStore_MicrosoftStore_Ubuntu_scripted_tool_sims.md#google-store-question) is discussions of sims for *Android OS* (*Android OS* uses *Java* for most programs).
- [`../SusuPosts/posts/Code_generation_attributions.md`](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/Code_generation_attributions.md) is how to give code generation tools attributions.
- [`../SusuPosts/posts/Physics_sims_which_structures_to_use.md`](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/Physics_sims_which_structures_to_use.md) discusses reasons to use `long`s versus `double`s (plus reasons to use primitives versus `class`s) for positions (plus for derivatives of positions).
- [`../SusuPosts/posts/Tensorflow_produces_meshes.md`](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/Tensorflow_produces_meshes.md) discusses how to give simple textual descriptions to `tensorflow` to produce meshes (produce edges plus vertices).

[`./hooks/`](./hooks/) is `git` scripts ([`man githooks`](https://git-scm.com/docs/githooks)) which assist you; install with `cp -ra ./hooks/* ./.git/hooks/`.
- [`./hooks/pre-commit`](./pre-commit) is [custom `pre-commit`](https://git-scm.com/book/en/v2/Customizing-Git-Git-Hooks) [`.git/hooks/pre-commit.sample` (scans for non-ASCII filenames, conflict markers or whitespace errors)](https://github.com/auth0/gitzero/blob/master/tests/example/_git/hooks/pre-commit.sample)

# How to use this
This repo is new. So that fixes do not require use of `git push --force` on the [`trunk`](https://github.com/SwuduSusuwu/SusuJava/tree/trunk/) branch (or tons of trivial fixes which bloat `git log`), new posts go to the [`preview`](https://github.com/SwuduSusuwu/SusuJava/tree/preview/) branch for review (which can last months).
- For now, just the `preview` branch has posts.
## Download
Download with `git clone https://github.com/SwuduSusuwu/SusuJava.git` and browse local with `cd mid/ && ls`.
- To opt-in to the beta (the preview), use `git switch preview` (opt-out with `get switch trunk`).
## Signatures / certificates
[`./.ssh/setup.sh`](./.ssh/setup.sh) is to setup `gpg.ssh.allowedSignersFile` (allows to use `git verify <ref>` or `git log --show-signature`).
- `git verify <ref>` or `git log --show-signature` shall match [`./.ssh/sha256.sig`](./.ssh/sha256.sig) for [new `commit`s](https://github.com/SwuduSusuwu/SusuJava/commit/5bec751e2bcff9dfd03eef2b3645c9028e4ce7c2#diff-f6ed156e4bf5c791680662464b94ea5d753f219ee816b385f67870e2c0d7d4c7)
- You can compare those certificates to [this post](https://swudususuwu.substack.com/p/s256_kz1xw3wo0bpceuvl3qh4nulf0hnll4nnkehiko6zk0).)

\[Notice: This [public crypto](https://docs.gitlab.com/ee/user/project/repository/signed_commits/ssh.html#verify-commits-locally) "signature", is not related to "signature analysis" ([Substr scans](https://github.com/SwuduSusuwu/blob/preview/SusuLib/README.md#purposes)).\]

# How to contribute
View [documented issues](https://github.com/SwuduSusuwu/SusuJava/issues/) (for ideas on how to contribute, plus so you do not report documented issues.)
## Beta tests (`preview` branch)
`git branch`s:
- [`trunk`](https://github.com/SwuduSusuwu/SusuJava/tree/trunk) publishes what most projects call "releases", with changes limited to what [*SemVer* calls "Patch" revisions](https://semver.org/spec/v2.0.0.html#spec-item-6)
- [`new`](https://github.com/SwuduSusuwu/SusuJava/tree/new) publishes what most projects call "release candidates", with changes limited to what [*SemVer* calls "Minor" revisions](https://semver.org/spec/v2.0.0.html#spec-item-7)
- [`preview`](https://github.com/SwuduSusuwu/SusuJava/tree/preview) publishes what most projects call "beta tests", with changes up to what [*SemVer* calls "Major" revisions](https://semver.org/spec/v2.0.0.html#spec-item-8)
  - [`pos2`](https://github.com/SwuduSusuwu/SusuJava/tree/pos2) is one of what most projects call "topic branches" (or `experimental` branch), a work-in-progress which does not pass unit tests. For now, `pos2` has a few `java` exceptions, but those are limited to a few rows of code in [`FishSim.java`](https://github.com/SwuduSusuwu/SusuJava/blob/pos2/susuwu/FishSim.java) which have trouble with the switch from `double[]` to `Pos2`.

How to use the most new sources:
- `git switch preview && ./build.sh`
  - View results for symptoms of new issues (hint: look for "Warning:"s or "Error:"s).
  - If you found new issue(s) (which aren't due to misconfigurations in your system), [comment on this pull request](https://github.com/SwuduSusuwu/SusuJava/pull/1) or [post new issue(s)](https://github.com/SwuduSusuwu/SusuLib/issues/new).
    - Notice: [sensitive issue(s) have this separate mechanism to use](./SECURITY.md#sensitive-issues).

# Contributor conventions/rules
General comment/message syntax rules: `<>` goes around type of option/argument (such as `<commit-hash>`, `[]` goes around optional comments/options/arguments (such as `[<optional fallback value>]`, `...` is affixed to allow multiple options/arguments (such as `[; optional extra arguments]...`). This rule is used to document function arguments (such as `sh`, `C` or `C++` use), plus to document `git` uses.
To ensure consistent code, submissions of code (such as through [pull requests](https://github.com/SwuduSusuwu/SusuJava/pulls)) have language-specific syntax rules:
## _Markdown_
`` *.md `` shall use:
- [_GitHub flavored Markdown_](https://github.github.com/gfm/), which is not just compatible with [_GitHub_](https://github.com) but also:
  - Has numerous [unit tests](https://wikipedia.org/wiki/Unit_test#Agile). Most of the differences from the original _Markdown_ are just so rules are less ambiguous.
  - Is close to the original _Markdown_ (thus compatible with most _Markdown_ tools, such as [`glow`](https://github.com/charmbracelet/glow?tab=readme-ov-file#glow)).
- [_ISO 8601_](https://wikipedia.org/wiki/ISO_8601), which
  - Is the most popular national standard format.
  - Versus formats which use locale-dependent names of months, is more portable and less ambiguous.
  - Versus formats which use backslashes, is more portable (filesystem paths can include).
- [_Unix_](https://wikipedia.org/wiki/Unix) paths start with `./` (if relative) or `/` (if absolute), so that [`sed`](https://manpages.org/sed) (and [`grep`](https://manpages.org/grep)) [performance is improved](https://poe.com/s/NX7kVKtCL9k04WIqieoh).
  - That is, paths shall match the [Regular Expression](https://wikipedia.org/wiki/Regular_expression) `^\.*\/[\w]*` (more than 1 `.` is allowed).
  - [_Microsoft Windows_](https://wikipedia.org/wiki/Microsoft_Windows) can use _Unix_ paths, except that absolute paths must start with the drive prefix (`[A-Z]:/` versus `/`).
  - [_HTTP_](https://wikipedia.org/wiki/HTTP) can use _Unix_ paths, except that absolute paths must start with the protocol (`http[s]*://` versus `/`).

## `git`
If `git commit` introduces/removes functions, have `./README.md#purposes` include this.
Do atomic commits: if swapping the new commit with a previous commit (such as through `git rebase -i`) -- or if `git revert` of a previous commit -- causes  `./build.sh` to return a non-0 exit status, `git commit`'s message shall include such as:
> Is followup to: \<ref | commit-hash\> \(\<commit-message\>\)\[, comment\] \[; \<ref | commit-hash\> \(\<commit-message\>\)\[, comment\]\]...

- This shows the temporal order of commits required for `./build.sh` to pass.
- `<commit-message>` is so that `git rebase` (which changes `<commit-hash>`) does not make it impossible to follow (plus, so comments are reduced), thus you should use the exact message. You can use ellipsis (`...`) to omit extra lines, but it is best if the first line is exact (left as-is).
- This format allows comments to include `<commit-hash>`'s plus `,`'s (just not `;`'s).

`git commit` message format/syntax:
- prefix "./" onto relative paths (such as `./README.md`), so those are not confused with *DNS references* (nor with shortcuts for *Uniform Resource Locators*). Exception: since `git commit` limits `COMMIT_EDITMSG`'s first row to `size() <= 50`, the first row can skip "./".
- affix "()" onto functions (regardless of number of arguments), such as `function()`, or use the function name (such as `function`) alone.
- if commit does `git add NewFile`: message has `` +`NewFile` ``.
- if `git rm Exists`: `` -`Exists` ``.
- if `touch Exists && git add Exists`: `` @`Exists` `` or `` ?`Exists` ``.
  - Simple wildcards/regex for altered functions: `` `%s/oldFunction()/newFunction()/` ``.
  - `*` is not used as update prefix, since `*` has much other use: for _Regular Expressions_ (wildcards), plus for _C++_ (such as block comments, dereferences, or math).
    - From the root commit through 159940fb8b60b176a38a13cdfbd9393596daa9b5 (Date:   Thu Jul 4 07:56:01 2024 -0700), '@' was the prefix for updates. From then until this commit, '?' was the prefix for updates.
    - From this commit on (this is the successor to commit 0ae6233c02d9e04fca60027b1e32b885eb69bb8a (Date:   Sat Nov 30 17:50:40 2024 -0800)), '@' is (once more) the prefix for updates, due to: it is more common for projects to so use '@'.
- if `echo "int newFunction() {...}" >> Exists && git add Exists`: `` @`Exists`: +`NewFunction()` ``.
- if `git mv OldPath/ NewPath/`: `` `OldPath/` -> `NewPath/` `` or `` `mv OldPath/ NewPath/` ``.
- as default branch, choose `master`, `main` or `trunk` (do not have more than 1 of those branches, or [`./Macros.sh:SUSUWU_DEFAULT_BRANCH()`](./Macros.sh) is ambiguous).
- to indent: use tabs to form blocks, such as:
```
@`README.md`:
	@`#How-to-use-this`:
	Split into:
		+`## Download`: new; howto clone, howto switch branches.
		+`## Optionssetup`: "Options/setup"; howto use `./build.sh` (with or without options.)
	@`#How-to-contribute`,
	@[Good first issues to contribute to]: (moved into `#How-to-contribute`)
```
\[Notice: Commit titles can omit backticks (``) if not enough room; the backticks just allow _GitHub_ to do _Markdown_-format code / paths.\]

## `java`
*Java* source code uses similar rules as `../SusuLib/`'s [**C**/**C++** source](https://github.com/SwuduSusuwu/SusuLib/blob/preview/README.md#cc-source) uses, with a few differences:
* Both should use `if(...)` for conditions (since searches for `if ` will include *English* sentences), which allows searches for `if(`.
* Both should use `for(...)` for loops (since searches for `for ` will include *English* sentences), which allows searches for `for(`.
* Both should use "*camelCase*" for local values (such as `double localValue;`), since this is the most common practice.
* Both should use "*PascalCase*" for names of types (such as `class NewClass {};` or `enum NewEnum {enumValue};`), since this is the most common practice.
* Since `java` (not *English*) is the "lowest common denominator":
  * use `assert`s to document which arguments `interface`s (or `public` functions) can use.
  * use `` `pseudocode` `` to document how `class`s (or `public` functions) are used.
* Since *Java* does not have `#define`` macros,  `static` constant values at the top of `class`s can use "*CONSTANT_CASE*" (such as `static double VALUE_WHICH_IS_GLOBAL_TO_CLASS;`), which *C++* reserves for macros.
* Since *Java*'s `Array` is improved (versus **C++**'s primitive version) to allow `primitive[] VALUE_WHICH_IS_AN_ARR;` (which is more simple to use with regular expressions such as `sed` than `primitive VALUE_WHICH_IS_AN_ARR[]` is), use `primitive[] VALUE_WHICH_IS_AN_ARR;`
* Document new functions thus: `` +`Class::newFunction()` ``, or `` +`Class.newFunction()` ``. TODO: rules for which to use. For now, suggest to use `Class::newFunction()` for `static` (global) functions, but `Class.newFunction()` for instanced (local) functions.
* `class`s with numerous functions (or with functions with numerous arguments) which accept other instances, should use `function(Class o)` (instead of `function(Class other)`) so the source code is brevose.
* The syntax for classes is `class PascalCase {\n ... \n};`, since thus is most popular.
  * If the window does not show the start of the `{\n ... \n}` enclosure, then `};` prevents confusion of `class`'s last `}` with `if(...) {\n ... \n}`'s last `}` .
* How comments are used (notice: for now, not all versions follow this, have much to do):
  * The `/* ... */` block quote format (as opposed to `// ...`) is used:
    * to disable source code which still has possible future use (such as `^/*\n<source code>\n*/$`), or
    * to store contiguous comments with multiple-rows (such as how to use the whole `class`).
  * `// Notice: ...` is used for comments to users.
  * `// TODO: ...` is used for internal dev comments. (Users, except prospective contributors to `SusuJava`, should ignore thus.)
  * `// <source code>` is used to disable individual rows of source.

## Sponsor
To sponsor this (which allows us to produce more source codes), you can use crypto (such as [**Bitcoin**](https://wikipedia.org/wiki/Bitcoin)) to [produce a one-time-use address](https://poe.com/s/IPhIMyuMY6SnYM0yqEJl) (which you deposit funds into), and send the address&private-key to [a contact which `./SECURITY.md` lists](./SECURITY.md#sensitive-issues).
- Rather than us publish a send-to address (for a particular protocol), this allows us to accept all forms of crypto.
- If asked, will include sponsorship info into the [contributors / sponsors list](#contributors--sponsors).
- If amount is more than $100 and you don't trust the contact platforms, use [`./.ssh/id_ed25519.pub`](./.ssh/id_ed25519.pub) to [secure those](https://superuser.com/questions/576506/how-to-use-ssh-rsa-public-key-to-encrypt-a-text/1850928#1850928).
- If crypto is not suitable, you can also send gift card codes or [virtual cards](https://www.forbes.com/advisor/credit-cards/virtual-credit-card-numbers-guide/) to use.
### Escrow
If you want proof that your crypto/cash will go to produce specific systems, use [**escrow** services](https://wikipedia.org/wiki/Escrow) (what you send the **escrow** is: crypto/cash, plus contract which references an [open issue which you choose](https://github.com/SwuduSusuwu/SusuJava/issues/)).
- If none of those issues match what you want, you can [post your own issue](https://github.com/SwuduSusuwu/SusuJava/issues/new) for this.
- Ensure that the **escrow** contract includes specifics as to what will count as "issue closed" [to the **escrow** service (so you do not have to trust the author),](https://wikipedia.org/wiki/Online_dispute_resolution) which will release the crypto/cash (once the **escrow** service considers your issue as closed).
  - For example; "The **source code** (through `./build.sh`), must produce a **system** (a **shared object** or **executable**) which uses just half of the training data to [setup its neural network, which must produce virtual synapses](https://wikipedia.org/wiki/Backpropagation) which the **system** [uses to produce **accurate** results](https://wikipedia.org/wiki/Residual_neural_network#Forward_propagation) on the other half, where **accurate** (for [classifiers](https://wikipedia.org/wiki/Learning_classifier_system)) is less than 2% false negatives and less than 2% false positives, and **accurate** (for [generators](https://wikipedia.org/wiki/Generative_artificial_intelligence)) is [divergence](https://wikipedia.org/wiki/Kullback%E2%80%93Leibler_divergence) of less than 2%." is a contract which an **escrow** can use for [issue #6](https://github.com/SwuduSusuwu/SusuLib/issues/6).
### Affiliates
You can use [_Capital 1_'s affiliate program](https://i.capitalone.com/JgR02Y4pE) to allow us to publish more for you.

## Tools used
For now, <https://github.com/SwuduSusuwu> just accepts anonymous sponsors. If thus is not suitable, sponsor tools used (which accept more popular sponsorship protocols) such as:
* *Ubuntu*: <https://documentation.ubuntu.com/project/community/contribute/contribute/> <https://ubuntu.com/download/desktop/thank-you>
* *AOSP*: <https://source.android.com/docs/setup/contribute>
* *Termux*: <https://github.com/sponsors/termux> (<https://termux.dev/en/donate> has numerous protocols for sponsorships).
* `/bin/sh`: On most systems this uses `dash`: <https://git.kernel.org/pub/scm/utils/dash/dash.git>.
  * `g++`: <https://github.com/gcc-mirror/gcc?tab=contributing-ov-file> says browse to <https://gcc.gnu.org/contribute.html>
  * `clang++`: <https://github.com/llvm/llvm-project?tab=contributing-ov-file> says browse to <https://llvm.org/docs/Contributing.html>
  * `java`: <https://github.com/openjdk/jdk?tab=contributing-ov-file> says browse to <https://dev.java/contribute/openjdk/> <https://openjdk.org/guide/>
  * `git`: <https://github.com/git/git?tab=contributing-ov-file> says browse to <https://git-scm.com/community#contributing>

## Contributors / sponsors
This section will show contributors (of code, or sponsors) who request inclusion into the contributors / sponsors list.
* Since published, `SusuJava` has no follows, no sponsors. The producers of [tools used](#tools-used) are indirect contributors.

