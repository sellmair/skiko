var wasmSetup = new Promise(function(resolve, reject) {
    Module['onRuntimeInitialized'] = _ => {
        resolve(Module);
    };
});

function onWasmReady(onReady) { wasmSetup.then(onReady); }

function Debug(msg) {
    console.warn(msg);
}
/** @const */ var IsDebug = true;

const CanvasKit = {

};

CanvasKit.MakeWebGLCanvasSurface = function(idOrElement, colorSpace, attrs) {
    colorSpace = colorSpace || null;
    var canvas = idOrElement;
    var isHTMLCanvas = typeof HTMLCanvasElement !== 'undefined' && canvas instanceof HTMLCanvasElement;
    var isOffscreenCanvas = typeof OffscreenCanvas !== 'undefined' && canvas instanceof OffscreenCanvas;
    if (!isHTMLCanvas && !isOffscreenCanvas) {
        canvas = document.getElementById(idOrElement);
        if (!canvas) {
            throw 'Canvas with id ' + idOrElement + ' was not found';
        }
    }

    var ctx = this.GetWebGLContext(canvas, attrs);
    if (!ctx || ctx < 0) {
        throw 'failed to create webgl context: err ' + ctx;
    }

    var grcontext = org_jetbrains_skia_MakeGrContext(ctx);

    // Note that canvas.width/height here is used because it gives the size of the buffer we're
    // rendering into. This may not be the same size the element is displayed on the page, which
    // constrolled by css, and available in canvas.clientWidth/height.
    var surface = org_jetbrains_skia_MakeOnScreenGLSurface(grcontext, canvas.width, canvas.height, colorSpace);
    if (!surface) {
        Debug('falling back from GPU implementation to a SW based one');
        // we need to throw away the old canvas (which was locked to
        // a webGL context) and create a new one so we can
        var newCanvas = canvas.cloneNode(true);
        var parent = canvas.parentNode;
        parent.replaceChild(newCanvas, canvas);
        // add a class so the user can detect that it was replaced.
        newCanvas.classList.add('ck-replaced');

        return org_jetbrains_skia_MakeSWCanvasSurface(newCanvas);
    }
    surface._context = ctx;
    surface.grContext = grcontext;
    surface.openGLversion = canvas.GLctxObject.version;
    return surface;
};

// Default to trying WebGL first.
CanvasKit.MakeCanvasSurface = CanvasKit.MakeWebGLCanvasSurface;