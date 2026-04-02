/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> has the newest version of `./susuwu/SimUsages.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

package susuwu; /* Usage: `import susuwu.SimUsages;` */

/**
 * {@code class SimUsages} shows {@code FpsTextMode} statistics such as {@code fps} or {@code ms}.
 * Requirements: some render loop (for measurements). Is not specific to the renderer used.
 * Was produced for {@code susuwu.FishSim}, so the text (plus comments) assume the organisms are {@code class Fish}, but {@code SimUsages} is not specific to {@code class Fish}
 * Some {@code assert}s follow, thus document which arguments to use with this (without {@code -enableassertions}, thus are not enforced).
 * Some "Usage:" comments follow, which document how to use this.
 * Usage: {@code SimUsages usages = new SimUsages(); usages.show(); usages.fpsTextMode = FpsTextMode.fps.value | FpsTextMode.ms.value;}
 * Text is rendered via {@link SdlGles2#setWindowTitle} (replaces {@code javafx.scene.text.Text}).
 */
public class SimUsages {
	/* `public` members */
	public long fpsTextMode = FpsTextMode.allUsages.value; // Usage `usages.fpsTextModeFps = FpsTextMode.fps.value;` to just show `fps`
	public double secondsPerFpsTextRefresh = 1.0; // Usage: `usages.secondsPerFpsTextRefresh = 0.2;` to give more current values, or `= 2.0;` to give more smooth values. In `postRefresh()`: if `secondsPerFpsTextRefresh` elapses, `lastTime = System.nanoTime(); fpsTextRefresh();``
	public double renderMs = Double.NaN; // Usage: `functionWhichUsesRenderMs(usages.renderMs);`. Stores average **ms** from `startRender()` to `postRender()` (`renderNs / renderCounter / 1_000_000.0`)
	public double physicsMs = Double.NaN; // Usage: `functionWhichUsesPhysicsMs(usages.physicsMs);`. Stores average **ms** from `startPhysics()` to `postPhysics()` (`physicsNs / physicsCounter / 1_000_000.0`)
	public double fps = Double.NaN; // Usage: `functionWhichUsesFps(usages.fps);`. Stores `renderCounter / (System.nanoTime() - lastTime) / 1_000_000_000.0`

	/* `private` or almost-`private` members */
	public long lastTime = System.nanoTime(); // Stores `System.nanoTime()` when `renderCounter = 0`.
	public int refreshCounter = 0; // Sum of `postRefresh()` uses since `SimUsages(Pane)`.
	public int renderCounter = 0; // Sum of `postRender()` uses since `lastTime = System.nanoTime()`.
	public int physicsCounter = 0; // Sum of `postPhysics()` uses since `lastTime = System.nanoTime()`.
	public long physicsNs = -1; // Sum of `nanoTime()` at `postRender()` minus `nanoTime()` at `startRender()` since `lastTime = System.nanoTime()`.
	public long renderNs = -1; // Sum of `nanoTime()` at `postRender()` minus `nanoTime()` at `startRender()` since `lastTime = System.nanoTime()`.
	private String fpsText = "0 FPS";
	public SimUsages() {
		/* No Pane or display object needed: text is shown via SdlGles2.setWindowTitle(). */
	}
	static public enum FpsTextMode { // `FpsTextMode` says which resources `fpsText` will show.
		none      (0     ), // `fpsText = "";`
		fps       (1 << 0), // `fpsText` += `fps` "FPS";
		ms        (2 << 1), // `fpsText` += `ms` "ms"; /* Notice: `ms = 1000 / fps;`, so includes idle CPU */
		msSpec    (1 << 2), // `fpsText` += `renderMs` "renderMs," `physicsMs` "physicsMs"; /* Notice: uses `System.nanoTime()`, does not include idle CPU */
		msFish    (1 << 3), // `fpsText` += `renderMs / fishShown` "renderMs / Fish shown," `physicsMs / fishListSize` "physicsMs / Fish";
		fish      (1 << 4), // `fpsText` += `fishListSize` "Fish";
		fishShown (1 << 5), // `fpsText` += `fishShown` "Fish shown";
		allUsages (FpsTextMode.fps.value | FpsTextMode.ms.value | FpsTextMode.msSpec.value | FpsTextMode.msFish.value | FpsTextMode.fish.value | FpsTextMode.fishShown.value); // shows as all supported usages
		long value; // Stores bitwise-or of those.
		FpsTextMode(long value) { this.value = value; }
	}; // TODO: replace manual bitshifts with `java.util.EnumSet<E>`?

	public void show() {
		SdlGles2.setWindowTitle("Fish Simulation (Boids) - " + fpsText); /* Replaces `Text.setX/Y/setFill(Color.WHITE)`: text is in window title. */
	}

	/* Measurement funtions
	 * Usage: ```
	 * refreshLoop() {
	 * 	usages.startRefresh();
	 * 	usages.startPhysics(); physics(fishList);   usages.postPhysics();
	 * 	usages.startRender();  render(visibleFish); usages.postRender();
	 * 	usages.postRefresh(System.nanoTime(), visibleFish.size(), fishList.size());
	 * }```
	 * "Refresh loop" is the loop which invokes the render loop plus the physics loop. `class SimUsages` does not assume that the physics loop is invoked as often as the render loop is. If the renderer has its own separate loop, the render loop is the refresh loop.
	 */
	public void startRefresh() { // Usage: `startRender();` at start of refresh loop.
	}
	public void postRefresh(long now, long fishShown, long fishListSize) { // Usage: `postRefresh(System.nanoTime(), visibleObjects.size(), physisObjects.size());`
		double elapsed = (now - lastTime) / 1_000_000_000.0;
		if(elapsed >= secondsPerFpsTextRefresh) {
			lastTime = now;
			fps = renderCounter / elapsed;
			renderMs = renderNs / renderCounter / 1_000_000.0;
			physicsMs = physicsNs / physicsCounter / 1_000_000.0;
			fpsTextRefresh(fishShown, fishListSize); /* Replaces `Platform.runLater(...)`: SDL2 has no UI thread restriction, so call directly. */
			renderCounter = 1;
			renderNs = -1;
			physicsCounter = 1;
			physicsNs = -1;
		}
		refreshCounter++;
	}
	private long renderNsStart, physicsNsStart;
	public void startRender() { // Usage: `startRender();` at start of render loop
		renderNsStart = System.nanoTime();
	}
	public void postRender() { // Usage: `startRender();` at closure of render loop
		renderNs += System.nanoTime() - renderNsStart;
		renderCounter++;
	}
	public void startPhysics() { // Usage: `startPhysics();` at start of physics loop
		physicsNsStart = System.nanoTime();
	}
	public void postPhysics() { // Usage: `startPhysics();` at closure of physics loop
		physicsNs += System.nanoTime() - physicsNsStart;
		physicsCounter++;
	}

	private void fpsTextRefresh(long fishShown, long fishListSize) { /* Usage: `usages.fpsTextModeFps(visibleFish.size(), fishList.size())` */
		boolean fpsTextModeFps = (0 != (FpsTextMode.fps.value & fpsTextMode));
		boolean fpsTextModeMs = (0 != (FpsTextMode.ms.value & fpsTextMode));
		boolean fpsTextModeMsSpec = (0 != (FpsTextMode.msSpec.value & fpsTextMode));
		boolean fpsTextMsSpecFish = (0 != ((FpsTextMode.msSpec.value | FpsTextMode.msFish.value) & fpsTextMode)); // TODO: replace manual bitshifts with `java.util.EnumSet<E>`?
		boolean fpsTextModeMsFish = (0 != (FpsTextMode.msFish.value & fpsTextMode));
		boolean fpsTextModeFish = (0 != (FpsTextMode.fish.value & fpsTextMode));
		boolean fpsTextModeFishShown = (0 != (FpsTextMode.fishShown.value & fpsTextMode));
		double totalMs = 1 / fps * 1000;
		String fpsTextStr = "";
		String strSep = ", ", strJoin = " (";
		if(FpsTextMode.none.value == fpsTextMode) { return; }
		if(fpsTextModeFps) {
			fpsTextStr += String.format("%4.2f FPS" + strSep, fps);
		}
		if(fpsTextModeMs) {
			fpsTextStr += String.format("%4.2f MS" + (fpsTextMsSpecFish ? strJoin : strSep), totalMs);
		}
		if(fpsTextModeMsSpec) {
			fpsTextStr += String.format("%4.2f drawMS, %4.2f physicsMS", renderMs, physicsMs);
			fpsTextStr += (fpsTextModeMsFish ? strSep : "");
		}
		if(fpsTextModeMsFish) {
			fpsTextStr += String.format("%2.4f drawMS / Fish shown, %2.4f physicsMS / Fish", renderMs / fishShown, physicsMs / fishListSize);
		}
		if(fpsTextMsSpecFish) {
			if(0 != ((FpsTextMode.ms.value & fpsTextMode))) {
				fpsTextStr += ")";
			}
			fpsTextStr += strSep;
		}
		if(fpsTextModeFish) {
			fpsTextStr += String.format("%4d Fish", fishListSize);
			fpsTextStr += (fpsTextModeFishShown ? strJoin : strSep);
		}
		if(fpsTextModeFishShown) {
			fpsTextStr += String.format(fpsTextModeFish ? "%4d shown)" : "%4d Fish shown", fishShown);
			fpsTextStr += strSep;
		}
		fpsText = fpsTextStr.substring(0, fpsTextStr.length() - strSep.length());
		SdlGles2.setWindowTitle("Fish Simulation (Boids) - " + fpsText); /* Replaces `Text.setText(...)`: update window title with FPS stats. */
	}
};
