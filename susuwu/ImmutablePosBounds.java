/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> stores the newest version of `./susuwu/ImmutablePosBounds.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

/**
 * `class ImmutablePosBounds` says how sims enforce position bounds.
 * Usage: `ImmutablePosBounds posBounds;`
 * `class ImmutablePosBounds` does not use generics since [`java` generics do not allow primitives](https://stackoverflow.com/questions/2721546/why-dont-java-generics-support-primitive-types), but `:%s/double/float/` in `vim` will produce the `float` version (`:%s/double/long/` produces the `long` version). [Valhalla is a possible solution for this](https://openjdk.org/jeps/218)
 * Some `assert`s follow, thus document which arguments to use with this (without `-enableassertions`, thus are not enforced).
 * Some "Usage:" comments follow, which document how to use this.
 */
package susuwu; /* Usage: `import susuwu.ImmutablePosBounds;` */
public class ImmutablePosBounds implements java.lang.Cloneable { /* Usage: replaces `double fooDistance; double fooFactor;` with `ImmutablePosBounds fooImmutablePosBounds;` */
	/* Member variables & constructors: */
	public enum PosBoundsMode { // `PosBoundsMode` says how the sim must do `pos[dim] += dpos[dim]` (derivatives of positions).
		invalidArgumentException, // `if(!isPosInBounds(pos)) { throw new IllegalArgumentException(); }`
		wrapAroundResolution, // `pos[dim] = (getBounds()[dim] + pos[dim] + dpos[dim]) % getBounds()[dim];`.
		clampToResolution, // `pos[dim] = Math.max(0, Math.min(getBounds()[dim] - 1, pos[dim] + dpos[dim]));`.
		boundless, // `pos[dim] += dpos[dim];`.
	}; // Notice: to teleport to new positions, `dpos[dim] = newPos[dim] - pos[dim]`. but most sims use relative motions.
	protected PosBoundsMode posBounds;
	public ImmutablePosBounds() {
		setPosBoundsMode_(PosBoundsMode.boundless);
	}
	public ImmutablePosBounds(ImmutablePosBounds o) {
		set_(o);
	}
	public ImmutablePosBounds(PosBoundsMode o) {
		setPosBoundsMode_(o);
	}
	@Override
	public ImmutablePosBounds clone() { /* Usage: `ImmutablePosBounds pos = o.clone(); assert o.equals(pos);` */
		return new ImmutablePosBounds(this);
	}

	/* Public setter functions: `class PosBounds extends ImmutablePosBounds`. */
	protected void setPosBoundsMode_(PosBoundsMode o) {
		posBounds = o;
	}
	protected void set_(ImmutablePosBounds o) {
		setPosBoundsMode_(o.posBounds);
	}

	/* Public getter functions: */
	public PosBoundsMode getPosBoundsMode() { return this.posBounds; }

	/* Comparison functions: */
	@Override
	public boolean equals(Object o) { /* Usage: `if(!equals(o)) { System.err.println("non-similar values"); }` */
		if(null == o || !(o instanceof ImmutablePosBounds)) {
			return false;
		}
		return equals((ImmutablePosBounds)o);
	}
	public boolean equals(ImmutablePosBounds o) { /* Usage: is `equals(Object o)` with low CPU use */
		return o.posBounds == posBounds;
	}
	@Override
	public int hashCode() { /* Usage: `if(hashCode() != hashCode(o)) { System.err.println("non-similar values"); }` */
		return posBounds.hashCode();
	}

	/* Public `ImmutablePosBounds` functions, which accept `double[]` (future versions also accept `ImmutablePos`): */
};

