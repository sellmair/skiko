package org.jetbrains.skia.paragraph

import org.jetbrains.skia.*
import org.jetbrains.skia.impl.Managed

expect class Paragraph internal constructor(ptr: Long, text: ManagedString?) : Managed {

    override fun close()

    val maxWidth: Float
    val height: Float
    val minIntrinsicWidth: Float
    val maxIntrinsicWidth: Float
    val alphabeticBaseline: Float
    val ideographicBaseline: Float
    val longestLine: Float

    fun didExceedMaxLines(): Boolean

    fun layout(width: Float): Paragraph

    fun paint(canvas: Canvas?, x: Float, y: Float): Paragraph

    /**
     * Returns a vector of bounding boxes that enclose all text between
     * start and end char indices, including start and excluding end.
     */
    fun getRectsForRange(
        start: Int,
        end: Int,
        rectHeightMode: RectHeightMode,
        rectWidthMode: RectWidthMode
    ): Array<TextBox>

    val rectsForPlaceholders: Array<org.jetbrains.skia.paragraph.TextBox>

    fun getGlyphPositionAtCoordinate(dx: Float, dy: Float): PositionWithAffinity

    fun getWordBoundary(offset: Int): IRange

    val lineMetrics: Array<LineMetrics?>

    val lineNumber: Long

    fun markDirty(): Paragraph

    val unresolvedGlyphsCount: Int

    fun updateAlignment(alignment: Alignment): Paragraph

    // public Paragraph updateText(int from, String text) {
    //     Stats.onNativeCall();
    //     _nUpdateText(_ptr, from, text);
    //     // TODO: update _text
    //     return this;
    // }
    fun updateFontSize(from: Int, to: Int, size: Float): Paragraph

    fun updateForegroundPaint(from: Int, to: Int, paint: Paint?): Paragraph

    fun updateBackgroundPaint(from: Int, to: Int, paint: Paint?): Paragraph
}