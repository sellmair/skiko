package org.jetbrains.skia

import org.jetbrains.skiko.wasm.GetWebGLContext
import org.jetbrains.skiko.wasm.org_jetbrains_skiko_MakeGrContext
import org.jetbrains.skiko.wasm.org_jetbrains_skiko_MakeOnScreenGLSurface
import org.w3c.dom.HTMLCanvasElement

fun Surface.Companion.createFromCanvas(canvas: HTMLCanvasElement): Surface {
    val glContext = GetWebGLContext(canvas, js("{}"))
    println("gl context created: ${glContext}")
    val grContext = org_jetbrains_skiko_MakeGrContext(glContext)
    println("gr context created: ${grContext}")
    val glSurface = org_jetbrains_skiko_MakeOnScreenGLSurface(grContext)
    return Surface(glSurface)
}