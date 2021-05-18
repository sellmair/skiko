package org.jetbrains.skiko.context

import org.jetbrains.skija.Bitmap
import org.jetbrains.skija.Canvas
import org.jetbrains.skija.ColorType
import org.jetbrains.skija.ColorAlphaType
import org.jetbrains.skija.ImageInfo
import org.jetbrains.skija.Picture
import org.jetbrains.skiko.SkiaLayer
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.image.Raster
import java.awt.image.WritableRaster
import java.nio.ByteBuffer

internal class SoftwareContextHandler(layer: SkiaLayer) : ContextHandler(layer) {
    override val bleachConstant = -1 // it looks like java.awt.Canvas doesn't support transparency

    val colorModel = ComponentColorModel(
        ColorSpace.getInstance(ColorSpace.CS_sRGB),
        false,
        false,
        Transparency.OPAQUE,
        DataBuffer.TYPE_BYTE
    )
    val storage = Bitmap()
    var image: BufferedImage? = null
    var raster: WritableRaster? = null
    var isInited = false

    override fun initContext(): Boolean {
        // Raster does not need context
        if (!isInited) {
            if (System.getProperty("skiko.hardwareInfo.enabled") == "true") {
                println(rendererInfo())
            }
            isInited = true
        }
        return isInited
    }

    override fun initCanvas() {
        disposeCanvas()
        
        val scale = layer.contentScale
        val w = (layer.width * scale).toInt().coerceAtLeast(0)
        val h = (layer.height * scale).toInt().coerceAtLeast(0)
        
        if (storage.getWidth() != w || storage.getHeight() != h) {
            storage.allocPixelsFlags(ImageInfo(w, h, ColorType.RGB_888X, ColorAlphaType.UNPREMUL, org.jetbrains.skija.ColorSpace.getSRGB()), true)
        }

        canvas = Canvas(storage)
    }

    override fun drawOnCanvas(picture: Picture) {
        super.drawOnCanvas(picture)

        val scale = layer.contentScale
        val w = (layer.width * scale).toInt().coerceAtLeast(0)
        val h = (layer.height * scale).toInt().coerceAtLeast(0)

        val bytes = storage.peekPixels()
        if (bytes != null) {
            raster = Raster.createInterleavedRaster(
                DirectDataBuffer(bytes),
                w,
                h,
                w * 4, 4,
                intArrayOf(0, 1, 2), // BGRA order
                null
            )
            image = BufferedImage(colorModel, raster!!, false, null)
            layer.backedLayer.getGraphics()?.drawImage(image!!, 0, 0, layer.width, layer.height, null)
        }
    }

    override fun flush() {
        // Raster does not need to flush canvas
    }

    class DirectDataBuffer(val backing: ByteBuffer): DataBuffer(TYPE_BYTE, backing.limit()) {
        override fun getElem(bank: Int, index: Int): Int {
            return backing[index].toInt()
        }
        override fun setElem(bank: Int, index: Int, value: Int) {
            throw UnsupportedOperationException("no write access")
        }
    }
}
