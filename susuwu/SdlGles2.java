/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> has the newest version of `./susuwu/SdlGles2.java` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

package susuwu; /* Usage: `import susuwu.SdlGles2;` */

/**
 * {@code class SdlGles2} is a thin JNI bridge to SDL2 and OpenGL ES 2.0.
 * Replaces JavaFX {@code Stage}, {@code Scene}, {@code Canvas}, {@code GraphicsContext}, {@code AnimationTimer}, {@code Timeline}.
 * Usage: {@code SdlGles2.init(width, height, title);} then render loop, then {@code SdlGles2.destroy();}
 */
public class SdlGles2 {
	static {
		System.loadLibrary("sdl_gles2_jni"); /* Loads `libsdl_gles2_jni.so` (or `.dll`/`.dylib`). Build: see `build.sh`. */
	}

	public static final int GL_COLOR_BUFFER_BIT = 0x00004000; /* Matches `GL_COLOR_BUFFER_BIT` from `<GLES2/gl2.h>` */

	/** Creates the SDL2 window + OpenGL ES 2.0 context. Returns {@code true} on success. Replaces {@code Stage}, {@code Scene}. */
	public static native boolean init(int width, int height, String title);
	/** Destroys the SDL2 window + context. Calls {@code SDL_Quit()}. Replaces {@code stage.close()}. */
	public static native void destroy();

	/** Polls SDL events; returns {@code true} if SDL_QUIT or Escape was received (main loop should exit). Replaces {@code AnimationTimer}/{@code Timeline} termination. */
	public static native boolean pollQuit();

	/** Sets the GLES2 clear color. Replaces {@code Scene} background color. */
	public static native void glClearColor(float r, float g, float b, float a);
	/** Clears the GLES2 framebuffer. {@code mask} should be {@link #GL_COLOR_BUFFER_BIT}. Replaces {@code gc.clearRect()}. */
	public static native void glClear(int mask);
	/** Swaps front/back buffers (presents the rendered frame). Replaces implicit JavaFX frame commit. */
	public static native void swapWindow();

	/** Sets the window title. Used for FPS text display. Replaces {@code javafx.scene.text.Text}. */
	public static native void setWindowTitle(String title);

	/**
	 * Draws a filled polygon as triangles in screen space.
	 * {@code vertices}: interleaved {@code [x0,y0, x1,y1, ...]} in pixels (already transformed to screen coords).
	 * Vertex count must be a multiple of 3 (pre-triangulated input). Replaces {@code gc.beginPath/moveTo/lineTo/fill}.
	 */
	public static native void drawFilledPolygon(float[] vertices, float r, float g, float b, float a);
}
