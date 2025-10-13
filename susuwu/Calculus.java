/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> stores the newest version of `./susuwu/Calculus.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

/* `class Calculus` houses simple trigonometric (transcendental) `public static` functions. Future versions will include true calculus functions (such as "area-under-curve" integrals, or "False Position" or "Quadratic Interpolation" root formulas).
 * Notice: passing `null` (or `{}`) to variadic functions is undefined, future versions could `return Double.NaN;` or `throw new IllegalArgumentException("double... = null");`
 * `class Calculus` does not use generics since [`java` generics do not allow primitives](https://stackoverflow.com/questions/2721546/why-dont-java-generics-support-primitive-types), but `:%s/double/float/` in `vim` will produce the `float` version (`:%s/double/long/` produces the `long` version). [Valhalla is a possible solution for this](https://openjdk.org/jeps/218)
 * Some `assert`s follow, thus document which arguments to use with this (without `-enableassertions`, thus are not enforced).
 * Some "Usage:" comments follow, which document how to use this.
 */
package susuwu; /* Usage: `import susuwu.Calculus;` */
public class Calculus { /* Usage: `Calculus.function(arguments)` */
	private Calculus() {} // Notice: instantiation of `class Calculus` has no uses (no members, plus all functions are `static`).

	/* Simple trigonometric functions (with 1 source) which `return` 1 result value: */
	public static double pow2(double value) { // Usage: for code which otherwise uses `Math.pow(temp, 2)` (or `indirectGet() * indirectGet()`) this improves CPU use. Versus `varX * varX`, `pow2(varX)` is just 1 token to replace to switch to `varW`, thus less bug-prone for future versions.
		return value * value;
	}
	public static double volume(double... position) { // Usage: is vararg version of "Cartesian volume"
		double volume_ = 1;
		for(double posW : position) {
			volume_ *= pow2(posW);
		}
		return volume_;
	}
	public static double average(double... position) { // Usage: is vararg version of "Arithmetic average"
		return volume(position) / position.length;
	}
	public static double sum(double... position) { // Usage: `return`s sum of `position[dim]`
		double sum_ = 0;
		for(double posW : position) {
			sum_ += pow2(posW);
		}
		return sum_;
	}
	public static double distancePow2(double... position) { // Usage: if absolute values are not used, replaces `if(threshold < hypotenus(position))` (which uses expensive square roots) with `if(pow2(threshold) < distancePow2(position))` (which does not) to improve CPU use.
		double distancePow2_ = 0;
		for(double posW : position) {
			distancePow2_ += pow2(posW);
		}
		return distancePow2_;
	}
	public static double hypotenus(double... position) { // Usage: is vararg version of `java`'s `double Math.hypot(double, double)`;
		return Math.sqrt(distancePow2(position));
	}
};

