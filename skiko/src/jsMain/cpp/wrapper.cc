#include "common.h"

#include "GrBackendSurface.h"
#include "GrDirectContext.h"

#include "SkSurface.h"

#include <emscripten/bind.h>

#include "gl/GrGLInterface.h"
#include "gl/GrGLTypes.h"

#include <GLES3/gl3.h>
#include <emscripten/html5.h>

using namespace emscripten;

extern "C" void* init_surface() {
   return nullptr;
}

bool ping() {
    return true;
}

EMSCRIPTEN_BINDINGS(Skiko) {
    function("ping", &ping);
};

sk_sp<SkSurface> MakeOnScreenGLSurface(sk_sp<GrDirectContext> grContext, int width, int height);

SKIKO_EXPORT void* org_jetbrains_skia_MakeOnScreenGLSurface(sk_sp<GrDirectContext> grContext, int width, int height) {
    return MakeOnScreenGLSurface(grContext, width, height).release();
}

#if 0
sk_sp<SkSurface> MakeOnScreenGLSurface(sk_sp<GrDirectContext> grContext, int width, int height);

SKIKO_EXPORT sk_sp<SkSurface> org_jetbrains_skia_MakeOnScreenGLSurface(sk_sp<GrDirectContext> grContext, int width, int height) {
    return MakeOnScreenGLSurface(grContext, width, height);
}

SKIKO_EXPORT sk_sp<GrDirectContext> org_jetbrains_skia_MakeGrContext(EMSCRIPTEN_WEBGL_CONTEXT_HANDLE context) {
    EMSCRIPTEN_RESULT r = emscripten_webgl_make_context_current(context);
    if (r < 0) {
        printf("failed to make webgl context current %d\n", r);
        return nullptr;
    }
    // setup GrDirectContext
    auto interface = GrGLMakeNativeInterface();
    // setup contexts
    sk_sp<GrDirectContext> dContext(GrDirectContext::MakeGL(interface));
    return dContext;
}

#endif