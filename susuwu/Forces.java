/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> stores the newest version of `./susuwu/Forces.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

/* `class Forces` stores values for physics forces (so other `double`s are not confused with thus), plus includes a few functions for use with thus.
 * `class Forces` does not use generics since [`java` generics do not allow primitives](https://stackoverflow.com/questions/2721546/why-dont-java-generics-support-primitive-types), but `:%s/double/float/` in `vim` will produce the `float` version (`:%s/double/long/` produces the `long` version). [Valhalla is a possible solution for this](https://openjdk.org/jeps/218)
 * Some `assert`s follow, thus document which arguments to use with this (without `-enableassertions`, thus are not enforced).
 * Some "Usage:" comments follow, which document how to use this.
 */
package susuwu; /* Usage: `import susuwu.Forces;` */
public class Forces implements java.lang.Cloneable { /* Usage: replaces `double fooDistance; double fooFactor;` with `Forces fooForces;` */
	/* Member variables & constructors:
	 * Notice: future versions will move `double distance, factor` into `class ImmutableForces`, with `class Forces extends ImmutableForces`. */
	public double distance = 0.0; // Notice: future versions will use `protected double distance`. Usercode should access through `setDistance(double)` or `getDistance()`.
//	public double distancePow2 = Calculus.pow2(distance); /* Usage: stores the `pow(distance, 2)` value, for reuses */
	public double factor = 0.0; // Notice: future versions will use `protected double factor`. Usercode should access through `setFactor(double)` or `getFactor()`.
	public Forces(double distance, double factor) {
		setFactor(factor);
		setDistance(distance);
	}
	public Forces(Forces o) {
		set(o);
	}

	/* Public setter functions: */
	public void set(Forces o) {
		setFactor(o.factor);
		setDistance(o.distance);
	}
	public void setFactor(double factor) {
		this.factor = factor;
	}
	public void setDistance(double distance) {
		this.distance = distance;
//		this.distancePow2 = Calculus.pow2(distance); // Notice: if uncomment `double distancePow2`, then uncomment this
	}

	/* Public getter functions:
	 * Notice: future versions will move `get*()` into `class ImmutableForces`, with `class Forces extends ImmutableForces`. */
	public double getFactor() { return this.factor; }
	public double getDistance() { return this.distance; }
	public double getDistancePow2() { /* Usage: `if(this.getDistancePow2() < Calculus.distancePow2(double...))` replaces `if(this.getDistance() < Calculus.hypotenus(double...))` */
//		return this.distancePow2; // Notice: if uncomment `double distancePow2`, then uncomment this
		return Calculus.pow2(this.distance);
	}

	/* Comparison functions:
	 * Notice: future versions will move {`equals(o)`, `hashCode()`} into `class ImmutableForces`, with `class Forces extends ImmutableForces`. */
	@Override
	public boolean equals(Object o) { /* Usage: `if(!equals(o)) { System.err.println("non-similar values"); }` */
		if(null == o || !(o instanceof Forces)) {
			return false;
		}
		return equals((Forces)o);
	}
	public boolean equals(Forces o) { /* Usage: is `equals(Object o)` with low CPU use */
		return o.distance == distance && o.factor == factor;
	}
	@Override
	public int hashCode() { /* Usage: `if(hashCode() != hashCode(o)) { System.err.println("non-similar values"); }` */
		double[] doubleView = { distance, factor };
		return java.util.Arrays.hashCode(doubleView);
	}

	/* `ImmutableForces` functions, which accept `double[]` (future versions also accept `ImmutablePos`).
	 * For now, this is just functions for Boids groups (future versions will include physics functions, such as {attraction of masses, {repulsion, attraction} of {opposite, similar} charges}).
	 */
	// Usage: for Boids groups: `if(posIfDistSum(averageDposOfGroup, dposOfIndividual, distanceToIndividual) { ++sizeOfGroup; }`
	public boolean posIfDistScaleSum(double[] posDes, double[] posSource, double dist) {
		dist = Math.max(dist, Double.MIN_NORMAL);  // Notice: `MIN_VALUE` (as epsilon) gives rounding errors, so use minimum normal value
		return posIfDistSum(posDes, new double[] {posSource[0] / dist, posSource[1] / dist},  dist);
	}
	public boolean posIfDistSum(double[] posDes, double[] posSource, double dist) {
		if(dist < distance) {
			posDes[0] += posSource[0];
			posDes[1] += posSource[1];
			return true;
		}
		return false;
	}
	public boolean posIfDistPow2Sum(double[] posDes, double[] posSource, double distPow2) {
		if(distPow2 < getDistancePow2()) {
			posDes[0] += posSource[0];
			posDes[1] += posSource[1];
			return true;
		}
		return false;
	}
//TODO:	public boolean posIfDistScaleSum(Pos posDes, ImmutablePos posSource, double dist) {
//TODO:	public boolean posIfDistSum(Pos posDes, ImmutablePos posSource, double dist) {
//TODO:	public boolean posIfDistPow2Sum(Pos posDes, ImmutablePos posSource, double distPow2) {

	// Usage: for Boids groups: `if(dposScaleSum(derivativeOfPosition, secondDerivOfPos, averageDposOfGroup)) { position += derivativeOfPosition; }`
	public boolean dposScaleSum(double[] dposDes, double d2posDes, double[] dposSource) {
		double d2posSource = Math.sqrt(Calculus.pow2(dposSource[0]) + Calculus.pow2(dposSource[1]));
		if(d2posSource > 0) {
			dposDes[0] += (dposSource[0] / d2posSource) * d2posDes * factor;
			dposDes[1] += (dposSource[1] / d2posSource) * d2posDes * factor;
			return true;
		}
		return false;
/*
		// Is faster, if `Math.sqrt(double)` costs more than `double[] dposSourceMsb = { Math.signum(double), Math.signum(double) }`
		double[] dposSourcePow2 = { Calculus.pow2(dposSource[0]),  Calculus.pow2(dposSource[1]) };
		double[] dposSourceMsb = { Math.signum(dposSource[0]),  Math.signum(dposSource[1]) };
		double d2posSourcePow2 = dposSourcePow2[0] + dposSourcePow2[1];
		if(d2posSourcePow2 > 0) { // TODO: ensure this scales to the original (with `Math.sqrt(double)`) values
			dposDes[0] += (dposSourceMsb[0] * dposSourcePow2[0] / d2posSourcePow2) * d2posDes * factor;
			dposDes[1] += (dposSourceMsb[1] * dposSourcePow2[1] / d2posSourcePow2) * d2posDes * factor;
			return true;
		}
		return false;
*/
	}
//TODO: public boolean dposScaleSum(Pos dposDes, double d2posDes, ImmutablePos dposSource) {
};

