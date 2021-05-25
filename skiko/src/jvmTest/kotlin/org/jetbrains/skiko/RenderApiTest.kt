package org.jetbrains.skiko

import java.awt.Dimension
import org.junit.Test
import org.jetbrains.skija.*
import javax.swing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing

internal class RenderApiTest {
    private fun testWindow(): SkiaWindow {
        val window = SkiaWindow()
        window.layer.renderer = object: SkiaRenderer {
            override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
                val contentScale = window.layer.contentScale
                canvas.scale(contentScale, contentScale)
                val green = Paint().setColor(0xFF00FF00.toInt())
                canvas.drawRect(Rect.makeXYWH(0f, 0f, width.toFloat(), height.toFloat()), green)
                window.layer.needRedraw()
            }
        }
        window.preferredSize = Dimension(100, 100)
        window.pack()
        return window
    }

    @Test
    fun `default render api test`() = runBlocking(Dispatchers.Swing) {
        System.clearProperty("skiko.renderApi")
        System.clearProperty("skiko.test.failInitContext")
        val defaultGraphicsApi = SkikoProperties.bestRenderApiForCurrentOS()
        val layer = SkiaLayer()
        assert(layer.renderApi == defaultGraphicsApi)
        layer.dispose()
    }

    @Test
    fun `software render api test`() = runBlocking(Dispatchers.Swing) {
        System.setProperty("skiko.renderApi", "SOFTWARE")
        System.clearProperty("skiko.test.failInitContext")
        val layer = SkiaLayer()
        assert(layer.renderApi == GraphicsApi.SOFTWARE)
        layer.dispose()
    }

    @Test
    fun `fallback to next api test`() = runBlocking(Dispatchers.Swing) {
        System.clearProperty("skiko.renderApi")
        System.setProperty("skiko.test.failInitContext", "true")
        val window = testWindow()
        window.layer.awaitRedraw()
        assert(window.layer.renderApi == GraphicsApi.SOFTWARE)
        window.dispose()
    }
}