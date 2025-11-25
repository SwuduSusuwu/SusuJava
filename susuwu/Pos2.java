/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> stores the newest version of `./susuwu/Pos2.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

package susuwu; // Usage: `import susuwu.Pos2;`
/**
 * {@code class Pos2} is the 2-dimensional specialization of {@code class Pos}, which stores constant vectors (first-order tensors).
 * {@code class Pos2} does not use generics since [{@code java} generics do not allow primitives](https://stackoverflow.com/questions/2721546/why-dont-java-generics-support-primitive-types), but {@code :%s/double/float/} in {@code vim} will produce the {@code float} version ({@code :%s/double/long/} produces the {@code long} version). [Valhalla is a possible solution for this](https://openjdk.org/jeps/218)
 * Some {@code assert}s follow, thus document which arguments to use with this (without {@code -enableassertions}, thus are not enforced).
 * Some "Usage:" comments follow, which document how to use this.
 * Notice: was unaware of <a href="https://docs.oracle.com/en/java/javase/21/docs/api/jdk.incubator.vector/jdk/incubator/vector/DoubleVector.html">DoubleVector</a> when produced this, so no {@code implements DoubleVector} for now.
 * Usage: {@code Pos2 position = Pos2(coord[0], coord[1]);}
 * @var pos Stores tensor of position coordinates (for internal {@code package susuwu} uses). Other {@code package}s should use: {@link #dims()}, {@link #at(int)}, {@link #set(int, double)}
 */
public class Pos2 extends Pos {
	/* Constructor functions */
	public Pos2() {} /* Usage: as default constructor */
	public Pos2(double pos0, double pos1) { /* Usage: as manual constructor */
		pos[0] = pos0;
		pos[1] = pos1;
	}
	public Pos2(double... o) { /* Usage: `double[] o; Pos2 pos = Pos2(o);` conversion constructor */
		assert null != o;
		assert 2 == o.length; // Notice: `2` is the hardcoded value for `Pos2.dims()`, just for internal use.
		pos = o.clone();
	}
	public Pos2(Pos2 o) { /* Usage: as clone constructor */
		assert null != o;
		pos[0] = o.pos[0];
		pos[1] = o.pos[1];
	}
	public Pos2(ImmutablePos2 o) { /* Usage: as conversion constructor. */
		assert null != o;
		pos[0] = o.pos[0];
		pos[1] = o.pos[1];
	}
	public Pos2(ImmutablePos o) { /* Usage: as conversion constructor. */
		assert null != o;
		assert 2 == o.pos.length; // Notice: `2` is the hardcoded value for `Pos2.dims()`
		pos[0] = o.pos[0];
		pos[1] = o.pos[1];
	}
	public Pos2(Pos o) { /* Usage: as conversion constructor. */
		assert null != o;
		assert 2 == o.pos.length; // Notice: `2` is the hardcoded value for `Pos2.dims()`
		pos[0] = o.pos[0]; // Notice: is optimization of `set(0, o.at(0));` ..., just for internal use.
		pos[1] = o.pos[1]; // Notice: assumes `o.dims() >= 2`
	}
	@Override
	public Pos clone() { /* Usage: `ImmutablePos pos = o.clone(); assert o.dims() == pos.dims(); for(int index = pos.dims(); index--; ) { assert o.at(index) == pos.at(index); }` */
		return new Pos2(pos[0], pos[1]);
	}
	@Override
	public Pos zeros() { /* Usage: `ImmutablePos pos = o.zeros(); assert o.dims() == pos.dims(); for(int index = pos.dims(); index--; ) { assert 0 == pos.at(index); }` */
		return new Pos2();
	}
	@Override
	public Pos ones() { /* Usage: `ImmutablePos pos = o.ones(); assert o.dims() == pos.dims(); for(int index = pos.dims(); index--; ) { assert 1 == pos.at(index); }` */
		return new Pos2(1, 1);
	}

	/* `pos` functions: */
	@Override
	public int dims() {
		return 2; // Notice: is optimization of `return pos.length;`, plus allows future changes of `double[] pos;` to `double pos0, pos1;`
	}
	@Override
	public double at(int index) { /* Usage: source which `assert ImmutablePos.at(index) == ImmutablePos.pos[index];` allows. */
		assert 0 <= index && 2 > index; // Notice: `2` is hardcoded value of `Pos2.dims()`, just for internal use.
		return pos[index]; // Notice: this trusts `java` to enforce `Array` bounds
	}
	@Override
	public void set(int index, double newValue) { /* Usage: `Pos.set(index, newValue)`. */
		assert 0 <= index && 2 > index; // Notice: `2` is hardcoded value of `Pos2.dims()`, just for internal use.
		pos[index] = newValue; // Notice: this trusts `java` to enforce `Array` bounds
	}

	/* Comparison functions: */
	@Override
	public boolean equals(Object o) { /* Usage: `if(!equals(o)) { System.err.println("non-similar values"); }` */
		if(null == o || !(o instanceof ImmutablePos)) {
			return false;
		}
		ImmutablePos oPos = (ImmutablePos)o;
		return 2 == oPos.pos.length && // Notice: `2 == oPos.pos.length` is optimization of `dims() == oPos.dims()`, for internal use
			oPos.pos[0] == pos[0] && oPos.pos[1] == pos[1]; // Notice: `pos[dim]` is optimization of `at(dim)`, for internal use
	}
	public boolean equals(ImmutablePos2 o) { /* Usage: is `equals(Object o)` with low CPU use*/
		return null != o && o.pos[0] == pos[0] && o.pos[1] == pos[1]; // Notice: `pos[dim]` is optimization of `at(dim)`, for internal use
	}
	public boolean equals(Pos2 o) { /* Usage: is `equals(Object o)` with low CPU use*/
		return null != o && o.pos[0] == pos[0] && o.pos[1] == pos[1];
	}

	/* Immutable trigo functions: */
	@Override
	public double volume() { /* Usage: `double arithmeticProduct = volume();` */
		return pos[0] * pos[1]; // Notice: `pos[0]` is optimization of `at(0)`, just for internal use.
	}
	@Override
	public double magnitudePow2() { /* Usage: `double euclideanDistanceToOriginPow2 = magnitudePow2();` */
		double distancePow2 = 0;
		distancePow2 += Calculus.pow2(pos[0]); // Notice: `pos[0]` is optimization of `at(0)`, just for internal use.
		distancePow2 += Calculus.pow2(pos[1]); // Notice: `pos[1]` is optimization of `at(1)`, just for internal use.
		return distancePow2;
	}
	@Override
	public double magnitude() { /* Usage: `double euclideanDistanceToOrigin = magnitude();` */
		return Math.sqrt(magnitudePow2());
	}
	@Override
	public double distanceToPow2(ImmutablePos o) { /* Usage: if absolute values are not used, replaces `if(threshold < distanceTo(position))` (which uses expensive `Math.sqrt`) with `if(pow2(threshold) < distanceToPow2(position))` (which does not) to improve CPU use. */
		assert null != o;
		assert 2 == o.pos.length; // Notice: is optimization of `assert dims() == o.dims();`, for internal use.
		double distancePow2 = 0;
		distancePow2 += Calculus.pow2(pos[0] - o.pos[0]); // Notice: is optimization of `at(0) - o.at(0)`
		distancePow2 += Calculus.pow2(pos[1] - o.pos[1]); // Notice: is optimization of `at(1) - o.at(1)`
		return distancePow2;
	}
	@Override
	public double distanceTo(ImmutablePos o) { /* `return`s Euclidean distance. Usage: `double distToO = ImmutablePos.distanceTo(o);` */
		return Math.sqrt(distanceToPow2(o));
	}

	/* Vector (tensor) functions with `double...`: */
	@Override
	public void plusEquals(double... o) {
		assert null != o;
		assert 2 == o.length; // Notice: `2` is the hardcoded value for `Pos2.dims()`, just for internal use.
		pos[0] += o[0];
		pos[1] += o[1];
	}
	@Override
	public void minusEquals(double... o) {
		assert null != o;
		assert 2 == o.length;
		pos[0] -= o[0];
		pos[1] -= o[1];
	}
	@Override
	public void starEquals(double... o) {
		assert null != o;
		assert 2 == o.length;
		pos[0] *= o[0];
		pos[1] *= o[1];
	}
	@Override
	public void slashEquals(double... o) {
		assert null != o;
		assert 2 == o.length;
		pos[0] /= o[0];
		pos[1] /= o[1];
	}
	@Override
	public void moduloEquals(double... o) {
		assert null != o;
		assert 2 == o.length;
		pos[0] %= o[0];
		pos[1] %= o[1];
	}
	@Override
	public void powEquals(double... o) {
		assert null != o;
		assert 2 == o.length;
		pos[0] = Math.pow(pos[0], o[0]);
		pos[1] = Math.pow(pos[1], o[1]);
	}

	/* Vector (tensor) functions with `ImmutablePos`: */
	@Override
	public void plusEquals(ImmutablePos o) {
		assert null != o;
		assert 2 == o.pos.length; // Notice: `2` is the hardcoded value for `Pos2.dims()`
		pos[0] += o.pos[0];
		pos[1] += o.pos[1];
	}
	@Override
	public void minusEquals(ImmutablePos o) {
		assert null != o;
		assert 2 == o.pos.length;
		pos[0] -= o.pos[0];
		pos[1] -= o.pos[1];
	}
	@Override
	public void starEquals(ImmutablePos o) {
		assert null != o;
		assert 2 == o.pos.length;
		pos[0] *= o.pos[0];
		pos[1] *= o.pos[1];
	}
	@Override
	public void slashEquals(ImmutablePos o) {
		assert null != o;
		assert 2 == o.pos.length;
		pos[0] /= o.pos[0];
		pos[1] /= o.pos[1];
	}
	@Override
	public void moduloEquals(ImmutablePos o) {
		assert null != o;
		assert 2 == o.pos.length;
		pos[0] %= o.pos[0];
		pos[1] %= o.pos[1];
	}
	@Override
	public void powEquals(ImmutablePos o) {
		assert null != o;
		assert 2 == o.pos.length;
		pos[0] = Math.pow(pos[0], o.pos[0]);
		pos[1] = Math.pow(pos[1], o.pos[1]);
	}

	/* Vector (tensor) functions with scalars:
	 * Notice: almost used `double s` (acronym for "scalar"), but all similar functions use `o` (acronym for "otherInstance") */
	@Override
	public void plusEqualsScalar(double o) { /* Usage: `plusEquals(new double[]{o, o})` compresses down to `plusEqualsScalar(o)` */
		pos[0] += o;
		pos[1] += o;
	}
	@Override
	public void minusEqualsScalar(double o) { /* Usage: `minusEquals(new double[]{o, o})` compresses down to `minusEqualsScalar(o)` */
		pos[0] -= o;
		pos[1] -= o;
	}
	@Override
	public void starEqualsScalar(double o) { /* Usage: `starEquals(new double[]{o, o})` compresses down to `starEqualsScalar(o)` */
		pos[0] *= o;
		pos[1] *= o;
	}
	@Override
	public void slashEqualsScalar(double o) { /* Usage: `slashEquals(new double[]{o, o})` compresses down to `slashEqualsScalar(o)` */
		pos[0] /= o;
		pos[1] /= o;
	}
	@Override
	public void moduloEqualsScalar(double o) { /* Usage: `moduloEquals(new double[]{o, o})` compresses down to `moduloEqualsScalar(o)` */
		pos[0] %= o;
		pos[1] %= o;
	}
	@Override
	public void powEqualsScalar(double o) { /* Usage: `powEquals(new double[]{o, o})` compresses down to `powEqualsScalar(o)` */
		pos[0] = Math.pow(pos[0], o);
		pos[1] = Math.pow(pos[1], o);
	}
};

