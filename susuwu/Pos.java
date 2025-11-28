/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> stores the newest version of `./susuwu/Pos.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

package susuwu; // Usage: `import susuwu.Pos;`
/**
 * {@code class Pos} stores mutable vectors (first-order tensors).
 * {@code class Pos} does not use generics since [{@code java} generics do not allow primitives](https://stackoverflow.com/questions/2721546/why-dont-java-generics-support-primitive-types), but {@code :%s/double/float/} in {@code vim} will produce the {@code float} version ({@code :%s/double/long/} produces the {@code long} version). [Valhalla is a possible solution for this](https://openjdk.org/jeps/218)
 * Some {@code assert}s follow, thus document which arguments to use with this (without {@code -enableassertions}, thus are not enforced).
 * Some "Usage:" comments follow, which document how to use this.
 * Notice: was unaware of <a href="https://docs.oracle.com/en/java/javase/21/docs/api/jdk.incubator.vector/jdk/incubator/vector/DoubleVector.html">DoubleVector</a> when produced this, so no {@code implements DoubleVector} for now.
 * @var pos Stores tensor of position coordinates (for internal {@code package susuwu} uses). Other {@code package}s should use {@link #dims()}, {@link #at(int)}, {@link #set(int, double)}
 */
public class Pos extends ImmutablePos { /* Usage: `void setsPos(Pos pos) { pos.set(...); }`. */
	/* Constructor functions: */
	public Pos() {} /* Usage: as default constructor */
	public Pos(double... o) { /* Usage: `double[] o; ImmutablePos pos = Pos(o);` conversion constructor, or `ImmutablePos pos = Pos(0, 0);` manual constructor */
		pos = o.clone();
	}
	public Pos(ImmutablePos o) { /* Usage: as conversion constructor. TODO: clone virtual function addresses. */
		pos = o.pos.clone();
	}

	/* `pos` functions: */
	public double[] getPos() { /* Usage: use as `Pos.pos`. Notice: future versions could replace `double[] pos;` with `double pos0, pos1, ...;`, so use `.at(index)` or `.set(index, newValue)`. */
		return pos; /* Notice: this trusts `java` to enforce `Array` bounds */
	}
	public void set(int index, double newValue) { /* Usage: `Pos.set(index, newValue)`. */
		assert pos.length > index;
		pos[index] = newValue; // Notice: `pos[index]` trusts `java` to enforce `Array` bounds, is optimization for internal use.
	}

	/* Vector (tensor) functions which accept other tensors: */
	public void equals_(double[] o) { /* Usage: `Pos.equals_(o)` is the tensor version of `Pos = o` */
		assert pos.length == o.length;
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] = o[dim]; // Notice: `pos[index] = o` trusts `java` to enforce `Array` bounds, is optimization of `set(index, o)` for internal use.
		}
	}
	public void plusEquals(double[] o) { /* Usage: `Pos.plusEquals(o)` is the tensor version of `Pos += o` */
		assert pos.length == o.length;
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] += o[dim]; // Notice: `pos[index] += o` trusts `java` to enforce `Array` bounds, is optimization of `set(index, get(index) + o)` for internal use.
		}
	}
	public void minusEquals(double[] o) { /* Usage: `Pos.minusEquals(o)` is the tensor version of `Pos -= o` */
		assert pos.length == o.length;
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] -= o[dim]; // Notice: `pos[index] $op= o` trusts `java` to enforce `Array` bounds, is optimization of `set(index, get(index) $op o)` for internal use.
		}
	}
	public void starEquals(double[] o) { /* Usage: `Pos.starEquals(o)` is the tensor version of `Pos *= o` */
		assert pos.length == o.length;
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] *= o[dim];
		}
	}
	public void slashEquals(double[] o) { /* Usage: `Pos.slashEquals(o)` is the tensor version of `Pos /= o` */
		assert pos.length == o.length;
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] /= o[dim];
		}
	}
	public void moduloEquals(double[] o) { /* Usage: `Pos.moduloEquals(o)` is the tensor version of `Pos %= o` */
		assert pos.length == o.length;
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] %= o[dim];
		}
	}
	public void powEquals(double[] o) { /* Usage: `Pos.powEquals(o)` is the tensor version of `Pos = Math.pow(Pos, o)` */
		assert pos.length == o.length;
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] = Math.pow(pos[dim], o[dim]);
		}
	}

	/* Vector (tensor) functions which accept other tensors: */
	public void equals_(ImmutablePos o) { /* Usage: `Pos.equals_(o)` is the tensor version of `Pos = o` */
		equals_(o.pos);
	}
	public void plusEquals(ImmutablePos o) { /* Usage: `Pos.plusEquals(o)` is the tensor version of `Pos += o` */
		plusEquals(o.pos);
	}
	public void minusEquals(ImmutablePos o) { /* Usage: `Pos.minusEquals(o)` is the tensor version of `Pos -= o` */
		minusEquals(o.pos);
	}
	public void starEquals(ImmutablePos o) { /* Usage: `Pos.starEquals(o)` is the tensor version of `Pos *= o` */
		starEquals(o.pos);
	}
	public void slashEquals(ImmutablePos o) { /* Usage: `Pos.slashEquals(o)` is the tensor version of `Pos /= o` */
		slashEquals(o.pos);
	}
	public void moduloEquals(ImmutablePos o) { /* Usage: `Pos.moduloEquals(o)` is the tensor version of `Pos %= o` */
		moduloEquals(o.pos);
	}
	public void powEquals(ImmutablePos o) { /* Usage: `Pos.powEquals(o)` is the tensor version of `Pos Math.pow(Pos, o)` */
		powEquals(o.pos);
	}

	/* Vector (tensor) functions which accept scalars:
	 * Notice: almost used `double s` (acronym for "scalar"), but all similar functions use `o` (acronym for "otherInstance") */
	public void fill_(double o) { /* Usage: documents that `equalsScalar(o)` is analogous to the `fill_(o)` of popular tensor `class`s */
		equalsScalar(o);
	}
	public void equalsScalar(double o) { /* Usage: `equalsScalar(o)` broadcasts the scalar `o` to all, similar to `for(long index = dims(); index--; ) { set(index, o); }` */
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] = o; // Notice: `pos[index] = o` trusts `java` to enforce `Array` bounds, is optimization of `set(index, o)` for internal use.
		}
	}
	public void plusEqualsScalar(double o) { /* Usage: `plusEquals(new double[]{o, o})` compresses down to `plusEqualsScalar(o)` */
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] += o; // Notice: `pos[index] += o` trusts `java` to enforce `Array` bounds, is optimization of `set(index, get(index) + o)` for internal use.
		}
	}
	public void minusEqualsScalar(double o) { /* Usage: `minusEquals(new double[]{o, o})` compresses down to `minusEqualsScalar(o)` */
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] -= o; // Notice: `pos[index] $op= o` trusts `java` to enforce `Array` bounds, is optimization of `set(index, get(index) $op o)` for internal use.
		}
	}
	public void starEqualsScalar(double o) { /* Usage: `starEquals(new double[]{o, o})` compresses down to `starEqualsScalar(o)` */
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] *= o;
		}
	}
	public void slashEqualsScalar(double o) { /* Usage: `slashEquals(new double[]{o, o})` compresses down to `slashEqualsScalar(o)` */
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] /= o;
		}
	}
	public void moduloEqualsScalar(double o) { /* Usage: `moduloEquals(new double[]{o, o})` compresses down to `moduloEqualsScalar(o)` */
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] %= o;
		}
	}
	public void powEqualsScalar(double o) { /* Usage: `powEquals(new double[]{o, o})` compresses down to `powEqualsScalar(o)` */
		for(int dim = 0; dim < pos.length; ++dim) {
			pos[dim] = Math.pow(pos[dim], o);
		}
	}
};

