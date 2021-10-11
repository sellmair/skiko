package org.jetbrains.skiko.redrawer

import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaLayerProperties
import kotlin.system.getTimeNanos

internal class OpenGLRedrawer(
    private val layer: SkiaLayer,
    private val properties: SkiaLayerProperties
) : Redrawer {
    override fun dispose() {
        println("dispose")
    }

    override fun syncSize() {
        println("syncSize")
    }

    override fun needRedraw() {
        println("needRedraw")
    }

    override fun redrawImmediately() {
        layer.update(getTimeNanos())
    }
}
