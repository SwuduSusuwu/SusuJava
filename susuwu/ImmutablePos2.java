/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> stores the newest version of `./susuwu/ImmutablePos2.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

package susuwu; // Usage: `import susuwu.ImmutablePos2;`
/**
 * {@code class ImmutablePos2} is the 2-dimensional specialization of {@code class ImmutablePos}, which stores constant vectors (first-order tensors).
 * {@code class ImmutablePos2} does not use generics since [{@code java} generics do not allow primitives](https://stackoverflow.com/questions/2721546/why-dont-java-generics-support-primitive-types), but {@code :%s/double/float/} in {@code vim} will produce the {@code float} version ({@code :%s/double/long/} produces the {@code long} version). [Valhalla is a possible solution for this](https://openjdk.org/jeps/218)
 * Some {@code assert}s follow, thus document which arguments to use with this (without {@code -enableassertions}, thus are not enforced).
 * Some "Usage:" comments follow, which document how to use this.
 * Notice: was unaware of <a href="https://docs.oracle.com/en/java/javase/21/docs/api/jdk.incubator.vector/jdk/incubator/vector/DoubleVector.html">DoubleVector</a> when produced this, so no {@code implements DoubleVector} for now.
 * Usage: {@code double acceptsConsts(ImmutablePos2 pos) { double coord = pos.at(index); }}
 * @var pos Stores tensor of position coordinates (for internal {@code package susuwu} uses). Other {@code package}s should use: {@link #dims()}, {@link #at(int)}, {@link #set(int, double)}
 */
public class ImmutablePos2 extends ImmutablePos {
	/* Constructor functions: */
	public ImmutablePos2() {} /* Usage: as default constructor */
	public ImmutablePos2(double pos0, double pos1) { /* Usage: as manual constructor */
		pos[0] = pos0;
		pos[1] = pos1;
	}
	public ImmutablePos2(double... o) { /* Usage: `double[] o; ImmutablePos2 pos = ImmutablePos2(o);` conversion constructor */
		assert null != o;
		assert 2 == o.length;
		pos = o.clone();
	}
	public ImmutablePos2(ImmutablePos2 o) { /* Usage: as clone constructor */
		assert null != o;
		pos[0] = o.pos[0];
		pos[1] = o.pos[1];
	}
	public ImmutablePos2(Pos2 o) { /* Usage: as conversion constructor. TODO: clone virtual function addresses. */
		assert null != o;
		pos[0] = o.pos[0];
		pos[1] = o.pos[1];
	}
	public ImmutablePos2(ImmutablePos o) { /* Usage: as conversion constructor. TODO: clone virtual function addresses. */
		assert null != o;
		assert 2 == o.pos.length; // Notice: `2` is optimization of `dims()`, for internal uses
		pos[0] = o.pos[0];
		pos[1] = o.pos[1]; // Notice: assumes `o.dims() >= 2`
	}
	public ImmutablePos2(Pos o) { /* Usage: as conversion constructor. TODO: clone virtual function addresses. */ /* Notice: must use `interface` (or multiple inheritance) to allow implicit conversion of `Pos2` into `ImmutablePos` plus into `ImmutablePos2` */
		assert null != o;
		assert 2 == o.pos.length; // Notice: `2` is optimization of `dims()`, for internal uses
		pos[0] = o.pos[0];
		pos[1] = o.pos[1]; // Notice: assumes `o.dims() >= 2`
	}
	@Override
	public Pos clone() { /* Usage: `ImmutablePos pos = o.clone(); assert o.dims() == pos.dims(); for(int index = pos.dims(); index--; ) { assert o.at(index) == pos.at(index); }` */
		return new Pos2(this);
	}
	@Override
	public Pos zeros() { /* Usage: `ImmutablePos pos = o.zeros(); assert o.dims() == pos.dims(); for(int index = pos.dims(); index--; ) { assert 0 == pos.at(index); }` */
		return new Pos2();
	}
	@Override
	public Pos ones() { /* Usage: `ImmutablePos pos = o.ones(); assert o.dims() == pos.dims(); for(int index = pos.dims(); index--; ) { assert 1 == pos.at(index); }` */
		return new Pos2(1, 1);
	}

	/* Immutable `pos` access functions */
	@Override
	public int dims() {
		return 2; // Notice: `2` is optimization of `dims()`, for internal uses
	}
	@Override
	public double at(int index) { /* Usage: source which `assert ImmutablePos.at(index) == ImmutablePos.pos[index];` allows. */
		assert 0 <= index && 2 > index; // Notice: `2` is optimization of `dims()`, for internal uses
		return pos[index]; // Notice: this trusts `java` to enforce `Array` bounds
	}

	/* Comparison functions: */
	@Override
	public boolean equals(Object o) { /* Usage: `if(!equals(o)) { System.err.println("non-similar values"); } */
		if(null == o || !(o instanceof ImmutablePos)) {
			return false;
		}
		ImmutablePos oPos = (ImmutablePos)o;
		return 2 == oPos.pos.length && // Notice: `2` is optimization of `dims()`, for internal uses
			oPos.pos[0] == pos[0] && oPos.pos[1] == pos[1];
	}
	public boolean equals(ImmutablePos2 o) { /* Usage: `if(!equals(o)) { System.err.println("non-similar values"); } */
		return null != o && o.pos[0] == pos[0] && o.pos[1] == pos[1];
	}
	public boolean equals(Pos2 o) { /* Usage: `if(!equals(o)) { System.err.println("non-similar values"); } */
		return null != o && o.pos[0] == pos[0] && o.pos[1] == pos[1];
	}

	/* Immutable trigo functions: */
	@Override
	public double volume() { /* Usage: `double arithmeticProduct = volume();` */
		return pos[0] * pos[1];
	}
	@Override
	public double magnitudePow2() { /* Usage: `double euclideanDistanceToOriginPow2 = magnitudePow2();` */
		double distancePow2 = 0;
		distancePow2 += Calculus.pow2(pos[0]);
		distancePow2 += Calculus.pow2(pos[1]);
		return distancePow2;
	}
	@Override
	public double magnitude() { /* Usage: `double euclideanDistanceToOrigin = magnitude();` */
		return Math.sqrt(magnitudePow2());
	}
	public double distanceToPow2(ImmutablePos2 o) { /* Usage: if absolute values are not used, replaces `if(threshold < distanceTo(position))` (which uses expensive `Math.sqrt`) with `if(pow2(threshold) < distanceToPow2(position))` (which does not) to improve CPU use. */
		assert null != o;
		double distancePow2 = 0;
		distancePow2 += Calculus.pow2(pos[0] - o.pos[0]);
		distancePow2 += Calculus.pow2(pos[1] - o.pos[1]);
		return distancePow2;
	}
	public double distanceTo(ImmutablePos2 o) { /* `return`s Euclidean distance. Usage: `double distToO = ImmutablePos.distanceTo(o);` */
		return Math.sqrt(distanceToPow2(o));
	}
	@Override
	public double distanceToPow2(ImmutablePos o) { /* Usage: if absolute values are not used, replaces `if(threshold < distanceTo(position))` (which uses expensive `Math.sqrt`) with `if(pow2(threshold) < distanceToPow2(position))` (which does not) to improve CPU use. */
		assert null != o;
		assert 2 == o.pos.length; // Notice: `2` is optimization of `dims()`, for internal uses
		return distanceToPow2(o);
	}
	@Override
	public double distanceTo(ImmutablePos o) { /* `return`s Euclidean distance. Usage: `double distToO = ImmutablePos.distanceTo(o);` */
		return Math.sqrt(distanceToPow2(o));
	}
};

