package SkijaInjectSample

import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skija.*
import org.jetbrains.skija.paragraph.FontCollection
import org.jetbrains.skija.paragraph.ParagraphBuilder
import org.jetbrains.skija.paragraph.ParagraphStyle
import org.jetbrains.skija.paragraph.TextStyle
import org.jetbrains.skiko.*
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.*
import javax.swing.*
import kotlin.math.cos
import kotlin.math.sin
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.Point

fun main(args: Array<String>) {
    val panel = WindowDockArea()
    val mainWindow = JFrame("MainWindow").apply {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        size = Dimension(300, 300)
        contentPane.add(panel)
    }
    val skiaWindow = createWindow("SkiaWindow").apply {
        isVisible = true
    }

    panel.childWindow = skiaWindow
    mainWindow.isVisible = true
}

fun createWindow(title: String): SkiaWindow {
    val window = SkiaWindow()
    window.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    window.title = title

    val state = State()
    state.text = title

    window.setUndecorated(true)

    var mouseX = 0
    var mouseY = 0
    window.layer.renderer = Renderer(window.layer) {
        renderer, w, h, nanoTime -> displayScene(renderer, w, h, nanoTime, mouseX, mouseY, state)
    }
    window.layer.addMouseMotionListener(object : MouseMotionAdapter() {
        override fun mouseMoved(event: MouseEvent) {
            mouseX = event.x
            mouseY = event.y
        }
    })

    return window
}

class WindowDockArea : JPanel() {
    val root: JFrame?
        get() = SwingUtilities.getWindowAncestor(this) as JFrame

    var childWindow: SkiaWindow? = null
        set(value) {
            value?.setFocusableWindowState(false)
            value?.setAlwaysOnTop(false)
            value?.toBack()
            field = value
            if (root != null && root!!.isVisible) {
                syncLocation()
                syncSize()
                root?.toBack()
            }
        }

    override fun addNotify() {
        super.addNotify()

        root!!.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) = syncSize()
            override fun componentMoved(e: ComponentEvent) = syncLocation()
        })
        root!!.addWindowFocusListener(object : WindowAdapter() {
            override fun windowGainedFocus(event: WindowEvent) = toTop()
            override fun windowLostFocus(event: WindowEvent) = toBottom()
        })
        root!!.addWindowStateListener(object : WindowAdapter() {
            override fun windowStateChanged(event: WindowEvent) {
                val state = root!!.extendedState
                childWindow?.extendedState = state
                if (state == JFrame.MAXIMIZED_BOTH) {
                    syncLocation()
                    syncSize()
                }
            }
        })
    }

    private fun syncSize() {
        val currentSize = size
        childWindow?.setSize(currentSize.width, currentSize.height)
        if (childWindow != null) {
            SwingUtilities.updateComponentTreeUI(childWindow)
        }
    }

    private fun syncLocation() {
        val currentLocation = getLocationOnScreen()
        childWindow?.location = Point(currentLocation.x, currentLocation.y)
    }

    private fun toTop() {
        // вот здесь (и еще в [toBottom()]) предполагается использовать не тот подход что ниже (хотя и он с оговорками работает нормально)
        // а распольгать окно поверх главного с испльзованием нативных функций:
        // Windows: https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-setwindowpos
        // Mac OS: https://developer.apple.com/documentation/appkit/nswindow/1419672-order
        // Linux: хер его знает, но наверняка есть))
        childWindow?.setAlwaysOnTop(true)
        childWindow?.toFront()
    }

    private fun toBottom() {
        childWindow?.setAlwaysOnTop(false)
        childWindow?.toBack()
        root?.toBack()
    }

    override fun removeNotify() {
        super.removeNotify()
    }
}

class Renderer(
    val layer: SkiaLayer,
    val displayScene: (Renderer, Int, Int, Long) -> Unit
): SkiaRenderer {
    val typeface = Typeface.makeFromFile("fonts/JetBrainsMono-Regular.ttf")
    val font = Font(typeface, 40f)
    val paint = Paint().apply {
            setColor(0xff9BC730L.toInt())
            setMode(PaintMode.FILL)
            setStrokeWidth(1f)
    }

    var canvas: Canvas? = null

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        this.canvas = canvas
        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)
        displayScene(this, (width / contentScale).toInt(), (height / contentScale).toInt(), nanoTime)

        // Alpha layers test
        val rectW = 100f
        val rectH = 100f
        val left = (width / layer.contentScale - rectW) / 2f
        val top = (height / layer.contentScale - rectH) / 2f
        val pictureRecorder = PictureRecorder()
        val pictureCanvas = pictureRecorder.beginRecording(
            Rect.makeLTRB(left, top, left + rectW, top + rectH)
        )
        pictureCanvas.drawLine(left, top, left + rectW, top + rectH, Paint())
        val picture = pictureRecorder.finishRecordingAsPicture()
        canvas.drawPicture(picture, null, Paint())
        canvas.drawLine(left, top + rectH, left + rectW, top, Paint())

        layer.needRedraw()
    }
}

class State {
    var frame: Int = 0
    var text: String = "Hello Skija"
}

private val fontCollection = FontCollection()
    .setDefaultFontManager(FontMgr.getDefault())

fun displayScene(renderer: Renderer, width: Int, height: Int, nanoTime: Long, xpos: Int, ypos: Int, state: State) {
    val canvas = renderer.canvas!!
    val watchFill = Paint().setColor(0xFFFFFFFF.toInt())
    val watchStroke = Paint().setColor(0xFF000000.toInt()).setMode(PaintMode.STROKE).setStrokeWidth(1f)
    val watchStrokeAA = Paint().setColor(0xFF000000.toInt()).setMode(PaintMode.STROKE).setStrokeWidth(1f)
    val watchFillHover = Paint().setColor(0xFFE4FF01.toInt())
    for (x in 0 .. (width - 50) step 50) {
        for (y in 20 .. (height - 50) step 50) {
            val hover = xpos > x + 0 && xpos < x + 50 && ypos > y + 0 && ypos < y + 50
            val fill = if (hover) watchFillHover else watchFill
            val stroke = if (x > width / 2) watchStrokeAA else watchStroke
            canvas.drawOval(Rect.makeXYWH(x + 5f, y + 5f, 40f, 40f), fill)
            canvas.drawOval(Rect.makeXYWH(x + 5f, y + 5f, 40f, 40f), stroke)
            var angle = 0f
            while (angle < 2f * Math.PI) {
                canvas.drawLine(
                        (x + 25 - 17 * sin(angle)),
                        (y + 25 + 17 * cos(angle)),
                        (x + 25 - 20 * sin(angle)),
                        (y + 25 + 20 * cos(angle)),
                        stroke
                )
                angle += (2.0 * Math.PI / 12.0).toFloat()
            }
            val time = (nanoTime / 1E6) % 60000 +
                    (x.toFloat() / width * 5000).toLong() +
                    (y.toFloat() / width * 5000).toLong()

            val angle1 = (time.toFloat() / 5000 * 2f * Math.PI).toFloat()
            canvas.drawLine(x + 25f, y + 25f,
                    x + 25f - 15f * sin(angle1),
                    y + 25f + 15 * cos(angle1),
                    stroke)

            val angle2 = (time / 60000 * 2f * Math.PI).toFloat()
            canvas.drawLine(x + 25f, y + 25f,
                    x + 25f - 10f * sin(angle2),
                    y + 25f + 10f * cos(angle2),
                    stroke)
        }
    }
    val text = "${state.text} ${state.frame++}!"
    canvas.drawString(text, xpos.toFloat(), ypos.toFloat(), renderer.font, renderer.paint)

    val style = ParagraphStyle()
    val paragraph = ParagraphBuilder(style, fontCollection)
            .pushStyle(TextStyle().setColor(0xFF000000.toInt()))
            .addText("Graphics API: ${renderer.layer.renderApi} ✿ﾟ ${currentSystemTheme}")
            .popStyle()
            .build()
    paragraph.layout(Float.POSITIVE_INFINITY)
    paragraph.paint(canvas, 5f, 5f)
}
