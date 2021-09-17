package org.jetbrains.skiko.wasm

import kotlinx.dom.createElement
import kotlinx.browser.document
import org.jetbrains.skia.impl.NativePointer
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.Promise

external val wasmSetup: Promise<Boolean>
external fun onWasmReady(onReady: () -> Unit)

external fun org_jetbrains_skiko_MakeOnScreenGLSurface(ptr: NativePointer): NativePointer
external fun org_jetbrains_skiko_MakeGrContext(ptr: NativePointer): NativePointer
external fun GetWebGLContext(canvas: HTMLCanvasElement, attrs: dynamic): NativePointer

@JsExport
fun createSkikoCanvas(): HTMLCanvasElement {
    return document.createElement("canvas") {} as HTMLCanvasElement
}
