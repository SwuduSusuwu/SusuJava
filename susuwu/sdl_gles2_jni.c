/* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
 * <https://github.com/SwuduSusuwu/SusuJava/> has the newest version of `./susuwu/sdl_gles2_jni.c` (henceforth "*this source code*").
 * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
 * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those.
 */

/* JNI native implementation for `susuwu.SdlGles2`. Build: see `build.sh`. */
/* Usage: compile as shared library, then `System.loadLibrary("sdl_gles2_jni")` loads this. */

#include <jni.h>       /* JNI types + macros */
#include <SDL2/SDL.h>  /* SDL_Init, SDL_CreateWindow, SDL_GL_CreateContext, SDL_PollEvent, SDL_GL_SwapWindow, SDL_Quit */
#include <GLES2/gl2.h> /* glClear, glClearColor, glCreateShader, glCreateProgram, glDrawArrays, ... */
#include <stdio.h>     /* fprintf, stderr */
#include <string.h>    /* NULL */

static SDL_Window   *g_window  = NULL;
static SDL_GLContext g_context = NULL;
static GLuint        g_program = 0;
static GLint         g_posAttrib        = -1;
static GLint         g_colorUniform     = -1;
static GLint         g_resolutionUniform = -1;
static int           g_width = 0, g_height = 0;

/* Minimal vertex shader: converts pixel coords to clip space, flips Y so (0,0) is top-left (matches JavaFX canvas). */
static const char *VERT_SRC =
	"attribute vec2 a_position;\n"
	"uniform vec2 u_resolution;\n"
	"void main() {\n"
	"    vec2 zeroToOne = a_position / u_resolution;\n"
	"    vec2 clipSpace = zeroToOne * 2.0 - 1.0;\n"
	"    gl_Position = vec4(clipSpace * vec2(1.0, -1.0), 0.0, 1.0);\n"
	"}\n";

/* Minimal fragment shader: outputs a uniform solid color. */
static const char *FRAG_SRC =
	"precision mediump float;\n"
	"uniform vec4 u_color;\n"
	"void main() {\n"
	"    gl_FragColor = u_color;\n"
	"}\n";

static GLuint compile_shader(GLenum type, const char *src) {
	GLuint shader = glCreateShader(type);
	glShaderSource(shader, 1, &src, NULL);
	glCompileShader(shader);
	GLint compiled = 0;
	glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
	if(!compiled) {
		char log[512];
		glGetShaderInfoLog(shader, sizeof(log), NULL, log);
		fprintf(stderr, "sdl_gles2_jni: shader compile error: %s\n", log);
		glDeleteShader(shader);
		return 0;
	}
	return shader;
}

/* Java_susuwu_SdlGles2_init: creates SDL2 window + GLES2 context + compiles shaders. */
JNIEXPORT jboolean JNICALL Java_susuwu_SdlGles2_init(JNIEnv *env, jclass cls, jint width, jint height, jstring jtitle) {
	if(SDL_Init(SDL_INIT_VIDEO) < 0) {
		fprintf(stderr, "sdl_gles2_jni: SDL_Init failed: %s\n", SDL_GetError());
		return JNI_FALSE;
	}

	SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 2);
	SDL_GL_SetAttribute(SDL_GL_CONTEXT_MINOR_VERSION, 0);
	SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK, SDL_GL_CONTEXT_PROFILE_ES);
	SDL_GL_SetAttribute(SDL_GL_DOUBLEBUFFER, 1);

	const char *title = (*env)->GetStringUTFChars(env, jtitle, NULL);
	g_window = SDL_CreateWindow(title, SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED,
	                            (int)width, (int)height, SDL_WINDOW_OPENGL | SDL_WINDOW_SHOWN);
	(*env)->ReleaseStringUTFChars(env, jtitle, title);

	if(!g_window) {
		fprintf(stderr, "sdl_gles2_jni: SDL_CreateWindow failed: %s\n", SDL_GetError());
		SDL_Quit();
		return JNI_FALSE;
	}

	g_context = SDL_GL_CreateContext(g_window);
	if(!g_context) {
		fprintf(stderr, "sdl_gles2_jni: SDL_GL_CreateContext failed: %s\n", SDL_GetError());
		SDL_DestroyWindow(g_window);
		g_window = NULL;
		SDL_Quit();
		return JNI_FALSE;
	}

	g_width  = (int)width;
	g_height = (int)height;

	GLuint vert = compile_shader(GL_VERTEX_SHADER,   VERT_SRC);
	GLuint frag = compile_shader(GL_FRAGMENT_SHADER, FRAG_SRC);
	if(!vert || !frag) {
		if(vert) glDeleteShader(vert);
		if(frag) glDeleteShader(frag);
		SDL_GL_DeleteContext(g_context); g_context = NULL;
		SDL_DestroyWindow(g_window);    g_window  = NULL;
		SDL_Quit();
		return JNI_FALSE;
	}

	g_program = glCreateProgram();
	glAttachShader(g_program, vert);
	glAttachShader(g_program, frag);
	glLinkProgram(g_program);
	glDeleteShader(vert);
	glDeleteShader(frag);

	GLint linked = 0;
	glGetProgramiv(g_program, GL_LINK_STATUS, &linked);
	if(!linked) {
		char log[512];
		glGetProgramInfoLog(g_program, sizeof(log), NULL, log);
		fprintf(stderr, "sdl_gles2_jni: program link error: %s\n", log);
		glDeleteProgram(g_program); g_program = 0;
		SDL_GL_DeleteContext(g_context); g_context = NULL;
		SDL_DestroyWindow(g_window);    g_window  = NULL;
		SDL_Quit();
		return JNI_FALSE;
	}

	g_posAttrib         = glGetAttribLocation(g_program,  "a_position");
	g_colorUniform      = glGetUniformLocation(g_program, "u_color");
	g_resolutionUniform = glGetUniformLocation(g_program, "u_resolution");

	glUseProgram(g_program);
	glUniform2f(g_resolutionUniform, (float)g_width, (float)g_height);
	glViewport(0, 0, g_width, g_height);
	return JNI_TRUE;
}

/* Java_susuwu_SdlGles2_destroy: tears down GLES2 + SDL2. */
JNIEXPORT void JNICALL Java_susuwu_SdlGles2_destroy(JNIEnv *env, jclass cls) {
	if(g_program)  { glDeleteProgram(g_program);          g_program  = 0;    }
	if(g_context)  { SDL_GL_DeleteContext(g_context);     g_context  = NULL; }
	if(g_window)   { SDL_DestroyWindow(g_window);         g_window   = NULL; }
	SDL_Quit();
}

/* Java_susuwu_SdlGles2_pollQuit: drains SDL event queue; returns JNI_TRUE if app should exit. */
JNIEXPORT jboolean JNICALL Java_susuwu_SdlGles2_pollQuit(JNIEnv *env, jclass cls) {
	SDL_Event event;
	while(SDL_PollEvent(&event)) {
		if(SDL_QUIT == event.type) {
			return JNI_TRUE;
		}
		if(SDL_KEYDOWN == event.type && SDLK_ESCAPE == event.key.keysym.sym) {
			return JNI_TRUE;
		}
	}
	return JNI_FALSE;
}

JNIEXPORT void JNICALL Java_susuwu_SdlGles2_glClearColor(JNIEnv *env, jclass cls, jfloat r, jfloat g, jfloat b, jfloat a) {
	glClearColor(r, g, b, a);
}

JNIEXPORT void JNICALL Java_susuwu_SdlGles2_glClear(JNIEnv *env, jclass cls, jint mask) {
	glClear((GLbitfield)mask);
}

JNIEXPORT void JNICALL Java_susuwu_SdlGles2_swapWindow(JNIEnv *env, jclass cls) {
	SDL_GL_SwapWindow(g_window);
}

JNIEXPORT void JNICALL Java_susuwu_SdlGles2_setWindowTitle(JNIEnv *env, jclass cls, jstring jtitle) {
	if(!g_window) { return; }
	const char *title = (*env)->GetStringUTFChars(env, jtitle, NULL);
	SDL_SetWindowTitle(g_window, title);
	(*env)->ReleaseStringUTFChars(env, jtitle, title);
}

/* Java_susuwu_SdlGles2_drawFilledPolygon: draws pre-triangulated vertices (multiples of 3) in screen coords with solid color. */
JNIEXPORT void JNICALL Java_susuwu_SdlGles2_drawFilledPolygon(JNIEnv *env, jclass cls, jfloatArray jverts, jfloat r, jfloat g, jfloat b, jfloat a) {
	jsize len   = (*env)->GetArrayLength(env, jverts);
	jfloat *verts = (*env)->GetFloatArrayElements(env, jverts, NULL);

	glUseProgram(g_program);
	glUniform4f(g_colorUniform, r, g, b, a);
	glVertexAttribPointer(g_posAttrib, 2, GL_FLOAT, GL_FALSE, 0, verts);
	glEnableVertexAttribArray(g_posAttrib);
	glDrawArrays(GL_TRIANGLES, 0, (GLsizei)(len / 2));
	glDisableVertexAttribArray(g_posAttrib);

	(*env)->ReleaseFloatArrayElements(env, jverts, verts, JNI_ABORT);
}
