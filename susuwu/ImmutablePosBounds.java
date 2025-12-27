/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> stores the newest version of `./susuwu/ImmutablePosBounds.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

package susuwu; /* Usage: `import susuwu.ImmutablePosBounds;` */
import java.util.Arrays;
/**
 * `class ImmutablePosBounds` says how sims enforce position bounds.
 * Usage: `ImmutablePosBounds posBounds;`
 * `class ImmutablePosBounds` does not use generics since [`java` generics do not allow primitives](https://stackoverflow.com/questions/2721546/why-dont-java-generics-support-primitive-types), but `:%s/double/float/` in `vim` will produce the `float` version (`:%s/double/long/` produces the `long` version). [Valhalla is a possible solution for this](https://openjdk.org/jeps/218)
 * Some `assert`s follow, thus document which arguments to use with this (without `-enableassertions`, thus are not enforced).
 * Some "Usage:" comments follow, which document how to use this.
 */
public class ImmutablePosBounds implements java.lang.Cloneable { /* Usage: replaces `double fooDistance; double fooFactor;` with `ImmutablePosBounds fooImmutablePosBounds;` */
	/* Member variables & constructors: */
	public enum PosBoundsMode { // `PosBoundsMode` says how the sim must do `pos[dim] += dpos[dim]` (derivatives of positions).
		invalidArgumentException, // `if(!isPosInBounds(pos)) { throw new IllegalArgumentException(); }`
		wrapAroundResolution, // `pos[dim] = (getBounds()[dim] + pos[dim] + dpos[dim]) % getBounds()[dim];`.
		clampToResolution, // `pos[dim] = Math.max(0, Math.min(getBounds()[dim] - 1, pos[dim] + dpos[dim]));`.
		boundless, // `pos[dim] += dpos[dim];`.
	}; // Notice: to teleport to new positions, `dpos[dim] = newPos[dim] - pos[dim]`. but most sims use relative motions.
	protected PosBoundsMode posBounds;

	protected Pos bounds = new Pos(0, 0);
	protected Pos boundsSlash2 = bounds.slashScalar(2); // Improves execution of inner loops which use this
	protected double boundsVolume = bounds.volume();
	protected int gridResolution = 100;
	protected int[] gridSize = computeGridSize();
	public ImmutablePosBounds() {
		setPosBoundsMode_(PosBoundsMode.boundless);
	}
	public ImmutablePosBounds(ImmutablePosBounds o) {
		set_(o);
	}
	public ImmutablePosBounds(PosBoundsMode o, double[] resolution) {
		setPosBoundsMode_(o);
		setResolution_(resolution);
	}
	public ImmutablePosBounds(PosBoundsMode o, ImmutablePos resolution) {
		setPosBoundsMode_(o);
		setResolution_(resolution);
	}
	@Override
	public PosBounds clone() { /* Usage: `ImmutablePosBounds pos = o.clone(); assert o.equals(pos);` */
		return new PosBounds(this);
	}
	protected int[] computeGridSize() {
//		int[] gridSize_ = bounds.clone();
		int[] gridSize_ = new int[bounds.dims()];
		for(int i = 0; gridSize_.length > i; ++i) {
			gridSize_[i] = (int)Math.ceil(getBounds(i) / gridResolution);
		}
		return gridSize_;
	}

	/* Public setter functions: `class PosBounds extends ImmutablePosBounds`. */
	protected void setPosBoundsMode_(PosBoundsMode o) {
		posBounds = o;
	}
	protected void setResolution_(double[] resolution) {
		setBounds_(resolution);
		boundsSlash2 = bounds.slashScalar(2);
		boundsVolume = bounds.volume();
		gridSize = computeGridSize();
	}
	protected void setResolution_(ImmutablePos resolution) {
		setResolution_(resolution.pos);
	}
	protected void set_(ImmutablePosBounds o) {
		setPosBoundsMode_(o.posBounds);
		setResolution_(o.bounds);
		setGridResolution_(o.gridResolution);
	}
	protected void setBounds_(double[] resolution) {
//		assert null != resolution; // Notice: chose to use `java`'s default solution for `clone(null)`
		bounds = new Pos(resolution);
	}
	protected void setBounds_(ImmutablePos resolution) {
		bounds = resolution.clone();
	}
	protected void setBounds_(int index, double resolution) {
		bounds.set(index, resolution);
	}
	protected void setBoundsSlash2_(double[] resolution) {
//		assert null != resolution; // Notice: chose to use `java`'s default solution for `clone(null)`
		boundsSlash2 = new Pos(resolution);
	}
	protected void setBoundsSlash2_(ImmutablePos resolution) {
		boundsSlash2 = resolution.clone();
	}
	protected void setBoundsSlash2_(int index, double resolution) {
		boundsSlash2.set(index, resolution);
	}
	protected void setGridResolution_(int resolution) {
		gridResolution = resolution;
		gridSize = computeGridSize();
	}

	/* Public getter functions: */
	public PosBoundsMode getPosBoundsMode() { return this.posBounds; }
	public double[] getBounds() {
		assert null != bounds;
		assert null != bounds.pos;
		return bounds.pos; // Notice: should `return bounds.pos.clone()` to enforce "Immutable", but this is used in inner loops
	}
	public double[] getBoundsSlash2() { // Caches `getBounds()[dim] / 2` for physics uses (improves inner loops).
		assert null != boundsSlash2;
		assert null != boundsSlash2.pos;
		return boundsSlash2.pos; // Notice: should `return boundsSlash2.pos.clone()` to enforce "Immutable", but this is used in inner loops
	}
	public ImmutablePos getBoundsPos() {
		assert null != bounds; // Notice:  redundant (since `java` checks for `null` dereferences), but documents what this does
		return bounds;
	}
	public double getBounds(int index) {
//		assert null != bounds; // Notice: chose to use `java`'s default `throw`
//		assert null != bounds.pos; // Notice: chose to use `java`'s default `throw`
		return bounds.pos[index];
	}
	public ImmutablePos getBoundsSlash2Pos() { // Caches `getBounds().slash(2)` for physics uses (improves inner loops).
		assert null != boundsSlash2; // Notice:  redundant (since `java` checks for `null` dereferences), but documents what this does
		return boundsSlash2; // Notice: should `return boundsSlash2.clone()` to enforce "Immutable", but this is used in inner loops
	}
	public double getBoundsSlash2(int index) {
//		assert null != bounds; // Notice: chose to use `java`'s default `throw`
//		assert null != bounds.pos; // Notice: chose to use `java`'s default `throw`
		return boundsSlash2.pos[index];
	}
	public double getBoundsVolume() {
		return boundsVolume;
	}
	public int getGridSize(int index) {
		return gridSize[index];
	}
	public int[] getGridSize() {
		return gridSize.clone();
	}

	/* Comparison functions: */
	@Override
	public boolean equals(Object o) { /* Usage: `if(!equals(o)) { System.err.println("non-similar values"); }` */
		if(null == o || !(o instanceof ImmutablePosBounds)) {
			return false;
		}
		return equals((ImmutablePosBounds)o);
	}
	public boolean equals(ImmutablePosBounds o) { /* Usage: is `equals(Object o)` with low CPU use */
		return o.posBounds == posBounds &&
			o.bounds.equals(bounds) &&
			o.gridResolution == gridResolution;
//			o.boundsSlash2.equals(boundsSlash2) &&
//			o.boundsVolume == boundsVolume &&
	}
	@Override
	public int hashCode() { /* Usage: `if(hashCode() != hashCode(o)) { System.err.println("non-similar values"); }` */
		return posBounds.hashCode() ^ bounds.hashCode() ^ gridResolution; // Notice: if `boundsSlash2` is independent of `bounds`, then this should include ` ^ boundsSlash2.hashCode()`
	}

	/* Public `ImmutablePosBounds` functions, which accept `double[]` (future versions also accept `ImmutablePos`): */
	public String posOutOfBoundsStr(double[] pos, String posStr) {
		return "`" + posStr + " = " + Arrays.toString(pos) + ";` `getBounds() = " + bounds.toString() + ";` (`grid = new ArrayList[" + gridSize[0] + "][" + gridSize[1] + "];`), so `" + posStr + "` is out of bounds.";
	}
	public String posOutOfBoundsStr(ImmutablePos pos, String posStr) {
		return "`" + posStr + " = " + Arrays.toString(pos.pos) + ";` `getBounds() = " + bounds.toString() + ";` (`grid = new ArrayList[" + gridSize[0] + "][" + gridSize[1] + "];`), so `" + posStr + "` is out of bounds.";
	}

	public double[] posDiff(double[] pos, double[] o) {
		if(PosBoundsMode.wrapAroundResolution == getPosBoundsMode()) {
			double[] posDiff = new double[pos.length];
			for(int i = 0; pos.length > i; ++i) { /* Notice: ensure that `java` [unrolls this](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/Physics_sims_which_structures_to_use.md#separate-variables-versus-dim-lists) */
				posDiff[i] = pos[i] - o[i];
				if(boundsSlash2.pos[i] < posDiff[i]) {
					posDiff[i] -= bounds.pos[i];
				} else if(-boundsSlash2.pos[i] > posDiff[i]) {
					posDiff[i] += bounds.pos[i];
				}
			}
			return posDiff;
		} else {
			return new double[] {pos[0] - o[0], pos[1] - o[1]};
		}
	}
	public Pos posDiff(ImmutablePos pos, ImmutablePos o) {
		Pos posDiff = pos.minus(o);
		if(PosBoundsMode.wrapAroundResolution == getPosBoundsMode()) {
			for(int i = 0; pos.pos.length > i; ++i) { /* Notice: ensure that `java` [unrolls this](https://github.com/SwuduSusuwu/SusuPosts/blob/preview/posts/Physics_sims_which_structures_to_use.md#separate-variables-versus-dim-lists) */
				if(boundsSlash2.pos[i] < posDiff.pos[i]) {
					posDiff.pos[i] -= bounds.pos[i];
				} else if(-boundsSlash2.pos[i] > posDiff.pos[i]) {
					posDiff.pos[i] += bounds.pos[i];
				}
			}
		}
		return posDiff;
	}

	public boolean isPosInBounds(double[] pos) throws IllegalArgumentException {
		if(bounds.dims() != pos.length) {
			throw new IllegalArgumentException("`bounds.dims() != pos.length`");
		} // TODO: If this test is used at the start of all `pos*()` functions, replace `[]` with `Pos2`, unless optimizer stores this.
		for(int i = 0; i < pos.length; i++) {
			if(0 > pos[i] || bounds.at(i) <= pos[i]) {
				return false;
			}
		} // TODO: replace `for(...) {...}` with `switch(pos.length) { case 2: ... }`, unless optimizer does this.
		return true;
	}
	public boolean isPosInBounds(ImmutablePos pos) throws IllegalArgumentException {
		if(getBoundsPos().dims() != pos.dims()) {
			throw new IllegalArgumentException("`getBoundsPos().dims() != pos.dims()`");
		} // TODO: If this test is used at the start of all `pos*()` functions, replace `[]` with `Pos2`, unless optimizer stores this.
		for(int i = 0; i < pos.dims(); i++) {
			if(0 > pos.pos[i] || bounds.pos[i] <= pos.pos[i]) {
				return false;
			}
		} // TODO: replace `for(...) {...}` with +`ImmutablePos::isInBounds(ImmutablePos bounds)`, which can use `switch(pos.length) { case 2: ... }` or specialized virtual functions
		return true;
	}

	public boolean posBound(double[] pos) throws IllegalArgumentException { // If `PosBoundsMode.boundless != posBounds`, this ensures the invariant `0 <= pos[dim] && FishSim.getBounds()[dim] > pos[dim]` is established.
		switch(getPosBoundsMode()) { // `PosBoundsMode.` is omitted from all `case`s, to support old `java --source` versions
		case invalidArgumentException:
			if(!isPosInBounds(pos)) {
				throw new IllegalArgumentException(posOutOfBoundsStr(pos, "double[] pos"));
				// return false; // Notice: unsure of codeflow after the exception is handled. This gives an error if uncommented, but without this, if the exception is handled, the function will fall through to `return true`.
			}
			break;
		case wrapAroundResolution:
			pos[0] = ((pos[0] % bounds.pos[0]) + bounds.pos[0]) % bounds.pos[0];
			pos[1] = ((pos[1] % bounds.pos[1]) + bounds.pos[1]) % bounds.pos[1];
			break;
		case clampToResolution:
			pos[0] = Math.max(0, Math.min(bounds.pos[0] - 1, pos[0])); // TODO: if `java` does not precompute `bounds.at(dim) - 1`, store `boundsMinus1[]`
			pos[1] = Math.max(0, Math.min(bounds.pos[1] - 1, pos[1]));
			break;
		case boundless:
			return isPosInBounds(pos);
		default:
			throw new IllegalArgumentException("Unknown `enum PosBoundsMode`: " + posBounds); // [The compiler does this for you](https://codingtechroom.com/question/what-exception-compiler-unknown-enum-values-switch-expressions), so this just serves to document the lack of `default` codeflow.
		}
		return true;
	}
	public boolean posBound(Pos pos) throws IllegalArgumentException { // If `PosBoundsMode.boundless != posBounds`, this ensures the invariant `0 <= pos[dim] && FishSim.getBounds()[dim] > pos[dim]` is established.
		assert getBoundsPos().dims() == pos.dims(); // Notice: `bounds` for volumetric physics allows 2-dimensional `pos`, but `posBounds` does not implement such codeflow
		switch(getPosBoundsMode()) { // `PosBoundsMode.` is omitted from all `case`s, to support old `java --source` versions
		case invalidArgumentException:
			if(!isPosInBounds(pos)) {
				throw new IllegalArgumentException(posOutOfBoundsStr(pos, "Pos pos"));
				// return false; // Notice: unsure of codeflow after the exception is handled. This gives an error if uncommented, but without this, if the exception is handled, the function will fall through to `return true`.
			}
			break;
		case wrapAroundResolution:
			pos.plusEquals(getBoundsPos());
			pos.moduloEquals(getBoundsPos());
			break;
		case clampToResolution:
			pos.pos[0] = Math.max(0, Math.min(getBounds()[0] - 1, pos.pos[0])); // TODO: +`Pos::{maxEquals(Pos), minEquals(Pos)}` or +`Pos::clamp(pos)`
			pos.pos[1] = Math.max(0, Math.min(getBounds()[1] - 1, pos.pos[1]));
			break;
		case boundless:
			return isPosInBounds(pos);
		default:
			throw new IllegalArgumentException("Unknown `enum PosBoundsMode`: " + posBounds); // [The compiler does this for you](https://codingtechroom.com/question/what-exception-compiler-unknown-enum-values-switch-expressions), so this just serves to document the lack of `default` codeflow.
		}
		return true;
	}

};

