/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> stores the newest version of `./susuwu/PosBounds.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

/* `class ClassBounds` stores constant vectors (first-order tensors).
 * `class ClassBounds` does not use generics since [`java` generics do not allow primitives](https://stackoverflow.com/questions/2721546/why-dont-java-generics-support-primitive-types), but `:%s/double/float/` in `vim` will produce the `float` version (`:%s/double/long/` produces the `long` version). [Valhalla is a boundssible solution for this](https://openjdk.org/jeps/218)
 * Some `assert`s follow, thus document which arguments to use with this (without `-enableassertions`, thus are not enforced).
 * Some "Usage:" comments follow, which document how to use this.
 */
package susuwu; // Usage: `import susuwu.ClassBounds;`
public class ClassBounds implements java.lang.Cloneable, java.util.RandomAccess { /* Usage: ``. */
	/* Member variables:
	 * Notice: `ClassBounds` was almost set to `abstract`, due to confusion over which default `bounds.length` to use: future versions could use `bounds = {}`, `bounds = {0}` or `bounds = {0, 0, 0}` */
	double[] bounds = {0, 0};

	/* Constructor functions: */
	public ClassBounds() {}
	public ClassBounds(ClassBounds o) { /* Usage: as clone constructor. TODO: clone virtual function addresses. */
		bounds = o.bounds.clone();
	}
	@Override
	public Object clone() { /* Usage: `ClassBounds bounds = o.clone(); assert o.dims() == bounds.dims(); for(int index = bounds.dims(); index--; ) { assert o.at(index) == bounds.at(index); }` */
		return new ClassBounds(this);
	}
	public Pos zeros() { /* Usage: `ClassBounds bounds = o.zeros(); assert o.dims() == bounds.dims(); for(int index = bounds.dims(); index--; ) { assert 0 == bounds.at(index); }` */
		return new Pos();
	}
	public Pos ones() { /* Usage: `ClassBounds bounds = o.ones(); assert o.dims() == bounds.dims(); for(int index = bounds.dims(); index--; ) { assert 1 == bounds.at(index); }` */
		Pos bounds = new Pos();
//		bounds.fill_(1);
		return bounds;
	}

	/* Immutable `bounds` access functions: */
	public int dims() { /* Usage: `for(int index = ClassBounds.dims(); index--; ) { sum += ClassBounds.at(index); }` */
		return bounds.length; // Notice: `bounds.length` is the optimization of `bounds.size();`, for internal use.
	}
	public int size() { /* Usage: adapter for `dims()`, so `class ClassBounds` is compatible with popular `java` `interface`s such as `DoubleArrayView` */
		return dims();
	}
	public double at(int index) { /* Usage: source which `assert ClassBounds.at(index) == ClassBounds.bounds[index];` allows. */
		assert 0 <= index && bounds.length > index; // Notice: `bounds.length` is the deprecated optimization of `dims()`, for internal use.
		return bounds[index]; // Notice: `bounds[index]` trusts `java` to enforce `Array` bounds, is for internal use.
	}
	public double get(int index) { /* Usage: adapter for `at()`, so `class ClassBounds` is compatible with popular `java` `interface`s such as `DoubleArrayView` */
		return at(index);
	}

	/* Comparison functions: */
	@Override
	public boolean equals(Object o) { /* Usage: `if(!equals(o)) { System.err.println("non-similar values"); }` */
		if(null == o || !(o instanceof ClassBounds)) {
			return false;
		}
		ClassBounds oPos = (ClassBounds)o;
		if(bounds.length != oPos.bounds.length) { // Notice: `bounds.length` is the optimization of `dims()`, for internal use.
			return false;
		}
		for(int dim = 0; dim < bounds.length; ++dim) {
			if(oPos.bounds[dim] != bounds[dim]) { // Notice: `bounds[dim]` is the optimization of `at(dim)`, for internal use.
				return false;
			}
		}
		return true;
	}
	@Override
	public int hashCode() { /* Usage: `if(hashCode() != hashCode(o)) { System.err.println("non-similar values"); }` */
		return java.util.Arrays.hashCode(bounds);
	}
};

