/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> stores the newest version of `./susuwu/PosBounds.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

/**
 * `class PosBounds` says how sims enforce position bounds.
 * Usage: `PosBounds posBounds;`
 * `class PosBounds` does not use generics since [`java` generics do not allow primitives](https://stackoverflow.com/questions/2721546/why-dont-java-generics-support-primitive-types), but `:%s/double/float/` in `vim` will produce the `float` version (`:%s/double/long/` produces the `long` version). [Valhalla is a possible solution for this](https://openjdk.org/jeps/218)
 * Some `assert`s follow, thus document which arguments to use with this (without `-enableassertions`, thus are not enforced).
 * Some "Usage:" comments follow, which document how to use this.
 */
package susuwu; /* Usage: `import susuwu.PosBounds;` */
public class PosBounds extends ImmutablePosBounds { /* Usage: replaces `double fooDistance; double fooFactor;` with `PosBounds fooPosBounds;` */
	/* Member variables & constructors: */
	public PosBounds() {
		posBounds = PosBoundsMode.boundless;
	}
	public PosBounds(ImmutablePosBounds o) {
		set_(o);
	}
	public PosBounds(PosBoundsMode o) {
		setPosBoundsMode_(o);
	}
	@Override
	public PosBounds clone() { /* Usage: `PosBounds pos = o.clone(); assert o.equals(pos);` */
		return new PosBounds(this);
	}

	/* Public setter functions: `class PosBounds extends PosBounds`. */
	public void setPosBoundsMode(PosBoundsMode o) {
		setPosBoundsMode_(o);
	}
	public void set(ImmutablePosBounds o) {
		set_(o);
	}

	/* Comparison functions: */
	public boolean equals(PosBounds o) { /* Usage: is `equals(Object o)` with low CPU use */
		return o.posBounds == posBounds;
	}
};

