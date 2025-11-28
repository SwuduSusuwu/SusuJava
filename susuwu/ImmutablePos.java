/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> stores the newest version of `./susuwu/ImmutablePos.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

package susuwu; // Usage: `import susuwu.ImmutablePos;`
import java.util.Arrays; // `Arrays.toString([])`
/**
 * {@code class ImmutablePos} stores constant vectors (first-order tensors).
 * {@code class ImmutablePos} does not use generics since [{@code java} generics do not allow primitives](https://stackoverflow.com/questions/2721546/why-dont-java-generics-support-primitive-types), but {@code :%s/double/float/} in {@code vim} will produce the {@code float} version ({@code :%s/double/long/} produces the {@code long} version). [Valhalla is a possible solution for this](https://openjdk.org/jeps/218)
 * Some {@code assert}s follow, thus document which arguments to use with this (without {@code -enableassertions}, thus are not enforced).
 * Some "Usage:" comments follow, which document how to use this.
 * Notice: was unaware of <a href="https://docs.oracle.com/en/java/javase/21/docs/api/jdk.incubator.vector/jdk/incubator/vector/DoubleVector.html">DoubleVector</a> when produced this, so no {@code implements DoubleVector} for now.
 * @var pos Stores tensor of position coordinates (for internal {@code package susuwu} uses). Other {@code package}s should use {@link #dims()}, {@link #at(int)}, {@link #set(int, double)}. Notice: {@code class ImmutablePos} was almost set to {@code abstract}, due to confusion over which default {@code pos.length} to use: future versions could use {@code pos = {}}, {@code pos = {0}} or {@code pos = {0, 0, 0}}
 * Usage: {@code double acceptsConsts(ImmutablePos pos)}
 */
public class ImmutablePos implements java.lang.Cloneable, java.util.RandomAccess {
	protected double[] pos = {0, 0};

	/* Constructor functions: */
	public ImmutablePos() {}
	public ImmutablePos(double... o) { /* Usage: `double[] o; ImmutablePos pos = ImmutablePos(o);` conversion constructor, or `ImmutablePos pos = ImmutablePos(0, 0);` manual constructor */
		pos = o.clone();
	}
	public ImmutablePos(ImmutablePos o) { /* Usage: as clone constructor. TODO: clone virtual function addresses. */
		pos = o.pos.clone();
	}
	@Override
	public Pos clone() { /* Usage: `ImmutablePos pos = o.clone(); assert o.dims() == pos.dims(); for(int index = pos.dims(); index--; ) { assert o.at(index) == pos.at(index); }` */
		return new Pos(this);
	}
	public Pos zeros() { /* Usage: `ImmutablePos pos = o.zeros(); assert o.dims() == pos.dims(); for(int index = pos.dims(); index--; ) { assert 0 == pos.at(index); }` */
		return new Pos();
	}
	public Pos ones() { /* Usage: `ImmutablePos pos = o.ones(); assert o.dims() == pos.dims(); for(int index = pos.dims(); index--; ) { assert 1 == pos.at(index); }` */
		Pos pos = new Pos();
		pos.fill_(1);
		return pos;
	}

	/* Immutable `pos` access functions: */
	@Override
	public String toString() { /* Usage: `String serialized = "pos = " + pos.toString() + ";";` */
		return Arrays.toString(pos);
	}
	public int dims() { /* Usage: `for(int index = ImmutablePos.dims(); index--; ) { sum += ImmutablePos.at(index); }` */
		return pos.length; // Notice: `pos.length` is the deprecated optimization of `pos.size();`, for internal uses
	}
	public int size() { /* Usage: adapter for `dims()`, so `class ImmutablePos` is compatible with popular `java` `interface`s such as `DoubleArrayView` */
		return dims();
	}
	public double at(int index) { /* Usage: source which `assert ImmutablePos.at(index) == ImmutablePos.pos[index];` allows. */
		assert 0 <= index && pos.length > index;
		return pos[index]; // Notice: `pos[index]` trusts `java` to enforce `Array` bounds, is for internal use.
	}
	public double get(int index) { /* Usage: adapter for `at()`, so `class ImmutablePos` is compatible with popular `java` `interface`s such as `DoubleArrayView` */
		return at(index);
	}

	/* Comparison functions: */
	@Override
	public boolean equals(Object o) { /* Usage: `if(!equals(o)) { System.err.println("non-similar values"); }` */
		if(null == o || !(o instanceof ImmutablePos)) {
			return false;
		}
		ImmutablePos oPos = (ImmutablePos)o;
		if(pos.length != oPos.pos.length) {
			return false;
		}
		for(int dim = 0; dim < pos.length; ++dim) {
			if(oPos.pos[dim] != pos[dim]) {
				return false;
			}
		}
		return true;
	}
	@Override
	public int hashCode() { /* Usage: `if(hashCode() != hashCode(o)) { System.err.println("non-similar values"); }` */
		return java.util.Arrays.hashCode(pos);
	}

	/* Immutable simple trigo functions: */
	public double volume() { /* Usage: `double arithmeticProduct = ImmutablePos.volume();` */
		/*
		// Notice: uncomment this if `double[] pos;` is replaced with `double pos0, pos1, ...;`
		double volume_ = 1;
		for(for int dim = 0, end = dims(): end > dim; dim++) {
			volume_ *= get(dim); // Notice: `get(dim)` is slower than direct access, but allows discontinuous storage
		}
		return volume_;
		*/
		return Calculus.volume(pos); // Notice: assumes continuous storage
	}
	public double magnitudePow2() { /* Is tensor version of `Calculus.pow2(double) + Calculus.pow2(double)`. Usage: `double distanceToOriginPow2 = ImmutablePos.magnitudePow2();` */
		/*
		// Notice: uncomment this if `double[] pos;` is replaced with `double pos0, pos1, ...;`
		double distancePow2_ = 0;
		for(for int dim = 0, end = dims(): end > dim; dim++) {
			distancePow2 += Calculus.pow2(get(dim)); // Notice: `get(dim)` is slower than direct access, but allows discontinuous storage
		}
		return distancePow2_;
		*/
		return Calculus.distancePow2(pos); // Notice: assumes continuous storage
	}
	public double magnitude() { /* Is tensor version of `java`'s `double Math.hypot(double, double)`. Usage: `double distanceToOrigin = ImmutablePos.magnitude();` */
		return Math.sqrt(magnitudePow2());
	}
	public double distanceToPow2(ImmutablePos o) { /* Usage: if absolute values are not used, replaces `if(threshold < distanceTo(position))` (which uses expensive `Math.sqrt`) with `if(pow2(threshold) < distanceToPow2(position))` (which does not) to improve CPU use. */
		int end = pos.length;
		assert end == o.pos.length;
		double distancePow2 = 0;
		for(int dim = 0; end > dim; ++dim) {
			distancePow2 += Calculus.pow2(pos[dim] - o.pos[dim]);
		}
		return distancePow2;
	}
	public double distanceTo(ImmutablePos o) { /* `return`s Euclidean distance. Usage: `double distToO = ImmutablePos.distanceTo(o);` */
		return Math.sqrt(distanceToPow2(o));
	}

	/* Immutable vector (simple tensor) functions which accept other tensors:
	 * Notice: the functions after this row all require `Pos clone()` for the generic versions, thus no reason exists not to `return` the `Pos` version (which allows implicit conversion to `ImmutablePos`) */
	public Pos plus(double... o) { /* Usage: `Pos.plus(o)` is the tensor version of `(Pos + o)` */
		Pos pos = clone();
		pos.plusEquals(o);
		return pos;
	}
	public Pos minus(double... o) { /* Usage: `Pos.minus(o)` is the tensor version of `(Pos - o)` */
		Pos pos = clone();
		pos.minusEquals(o);
		return pos;
	}
	public Pos star(double... o) { /* Usage: `Pos.star(o)` is the tensor version of `(Pos * o)` */
		Pos pos = clone();
		pos.starEquals(o);
		return pos;
	}
	public Pos slash(double... o) { /* Usage: `Pos.slash(o)` is the tensor version of `(Pos / o)` */
		Pos pos = clone();
		pos.slashEquals(o);
		return pos;
	}
	public Pos modulo(double... o) { /* Usage: `Pos.modulo(o)` is the tensor version of `(Pos % o)` */
		Pos pos = clone();
		pos.moduloEquals(o);
		return pos;
	}
	public Pos pow(double... o) { /* Usage: `Pos.pow(o)` is the tensor version of `Math.pow(Pos, o)` */
		Pos pos = clone();
		pos.powEquals(o);
		return pos;
	}
	public Pos plus(ImmutablePos o) { /* Usage: `Pos.plus(o)` is the tensor version of `(Pos + o)` */
		Pos pos = clone();
		pos.plusEquals(o);
		return pos;
	}
	public Pos minus(ImmutablePos o) { /* Usage: `Pos.minus(o)` is the tensor version of `(Pos - o)` */
		Pos pos = clone();
		pos.minusEquals(o);
		return pos;
	}
	public Pos star(ImmutablePos o) { /* Usage: `Pos.star(o)` is the tensor version of `(Pos * o)` */
		Pos pos = clone();
		pos.starEquals(o);
		return pos;
	}
	public Pos slash(ImmutablePos o) { /* Usage: `Pos.slash(o)` is the tensor version of `(Pos / o)` */
		Pos pos = clone();
		pos.slashEquals(o);
		return pos;
	}
	public Pos modulo(ImmutablePos o) { /* Usage: `Pos.modulo(o)` is the tensor version of `(Pos % o)` */
		Pos pos = clone();
		pos.moduloEquals(o);
		return pos;
	}
	public Pos pow(ImmutablePos o) { /* Usage: `Pos.pow(o)` is the tensor version of `Math.pow(Pos, o)` */
		Pos pos = clone();
		pos.powEquals(o);
		return pos;
	}

	/* Immutable vector (simple tensor) functions which accept scalars:
	 * Notice: almost used `double s` (acronym for "scalar"), but all similar functions use `o` (acronym for "other") */
	public Pos plusScalar(double o) { /* Usage: `plusScalar(o)` is the compressed version of `plus(new double[]{o, o})` */
		Pos pos = clone();
		pos.plusEqualsScalar(o);
		return pos;
	}
	public Pos minusScalar(double o) { /* Usage: `minusScalar(o)` is the compressed version of `minus(new double[]{o, o})` */
		Pos pos = clone();
		pos.minusEqualsScalar(o);
		return pos;
	}
	public Pos starScalar(double o) { /* Usage: `starScalar(o)` is the compressed version of `star(new double[]{o, o})` */
		Pos pos = clone();
		pos.starEqualsScalar(o);
		return pos;
	}
	public Pos slashScalar(double o) { /* Usage: `slashScalar(o)` is the compressed version of `slash(new double[]{o, o})` */
		Pos pos = clone();
		pos.slashEqualsScalar(o);
		return pos;
	}
	public Pos moduloScalar(double o) { /* Usage: `moduloScalar(o)` is the compressed version of `modulo(new double[]{o, o})` */
		Pos pos = clone();
		pos.moduloEqualsScalar(o);
		return pos;
	}
	public Pos powScalar(double o) { /* Usage: `powScalar(o)` is the compressed version of `pow(new double[]{o, o})` */
		Pos pos = clone();
		pos.powEqualsScalar(o);
		return pos;
	}
};

