package com.prahlin.cinerific.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import androidx.compose.animation.core.FastOutSlowInEasing
import com.prahlin.cinerific.R
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

private const val FIGMA_FRAME_WIDTH = 1194f
private const val FIGMA_FRAME_HEIGHT = 834f
private const val SCREEN_RED_MS = 1000
private const val LOGO_ENTRY_START_MS = 250
private const val LOGO_ENTRY_END_MS = 1687
private const val FINAL_SETTLE_START_MS = LOGO_ENTRY_END_MS
private const val FINAL_SETTLE_END_MS = 4812
private const val BOOT_ANIMATION_MS = FINAL_SETTLE_END_MS

private const val COLOR_FRAME_1 = 0xFF000000.toInt()
private const val COLOR_FRAME_2 = 0xFF62070D.toInt()
private const val COLOR_GRADIENT_TOP = 0xFF050000.toInt()
private const val COLOR_GRADIENT_CENTER = 0xFF62070D.toInt()
private const val COLOR_GRADIENT_BOTTOM = 0xFF100102.toInt()

private val FINAL_AVATAR_BOUNDS = listOf(
    Bounds(130f, 428f, 220f, 220f),
    Bounds(368f, 428f, 220f, 220f),
    Bounds(606f, 432f, 220f, 220f),
    Bounds(844f, 428f, 220f, 220f)
)

internal class CinerificIntroView(context: Context) : View(context) {
    var bootStartMillis: Long = SystemClock.uptimeMillis()
        set(value) {
            if (field == value) return
            field = value
            postInvalidateOnAnimation()
        }
    var onAvatarSelected: (() -> Unit)? = null

    private val bitmapOptions = BitmapFactory.Options().apply {
        inScaled = false
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    private val logoSimple = decode(R.drawable.logo_simple_large)
    private val logoEyes = decode(R.drawable.logo_eyes_large)
    private val logoCombined = combineBitmaps(logoSimple, logoEyes)
    private val steveAvatar = circularBitmap(decode(R.drawable.steve_avatar))
    private val martinAvatar = circularBitmap(decode(R.drawable.martin_avatar))
    private val jannyAvatar = circularBitmap(decode(R.drawable.janny_avatar))
    private val guestAvatar = circularBitmap(decode(R.drawable.guest_avatar))
    private val steveName = decode(R.drawable.steve_name)
    private val martinName = decode(R.drawable.martin_name)
    private val jannyName = decode(R.drawable.janny_name)
    private val guestName = decode(R.drawable.guest_name)

    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
    private val backgroundPaint = Paint()
    private val tempRect = RectF()
    private var avatarPressStarted = false

    init {
        isClickable = true
        isFocusable = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val progress = bootProgressAt(bootStartMillis)
        val blackToBurgundy = linearSegmentMs(progress, 0, SCREEN_RED_MS)
        val burgundyToSettle = easedSegmentMs(progress, 2500, 3900)
        drawIntroBackground(canvas, blackToBurgundy, burgundyToSettle)

        val stageScale = min(width / FIGMA_FRAME_WIDTH, height / FIGMA_FRAME_HEIGHT)
        val stageLeft = (width - FIGMA_FRAME_WIDTH * stageScale) / 2f
        val stageTop = (height - FIGMA_FRAME_HEIGHT * stageScale) / 2f

        val logoAlpha = linearSegmentMs(progress, 90, LOGO_ENTRY_START_MS)
        if (logoAlpha > 0.01f) {
            drawSettlingLogo(canvas, stageLeft, stageTop, stageScale, progress, logoAlpha)
        }

        val avatarAlpha = easedSegmentMs(progress, 2300, FINAL_SETTLE_END_MS)
        if (avatarAlpha > 0.01f) {
            val y = lerpFloat(56f, 0f, avatarAlpha)
            drawFigmaBitmap(canvas, steveAvatar, Bounds(130f, 428f + y, 220f, 220f), stageLeft, stageTop, stageScale, avatarAlpha)
            drawFigmaBitmap(canvas, martinAvatar, Bounds(368f, 428f + y, 220f, 220f), stageLeft, stageTop, stageScale, avatarAlpha)
            drawFigmaBitmap(canvas, jannyAvatar, Bounds(606f, 432f + y, 220f, 220f), stageLeft, stageTop, stageScale, avatarAlpha)
            drawFigmaBitmap(canvas, guestAvatar, Bounds(844f, 428f + y, 220f, 220f), stageLeft, stageTop, stageScale, avatarAlpha)

            drawFigmaBitmap(canvas, steveName, Bounds(130f, 683f + y, 220f, 72f), stageLeft, stageTop, stageScale, avatarAlpha)
            drawFigmaBitmap(canvas, martinName, Bounds(368f, 683f + y, 220f, 72f), stageLeft, stageTop, stageScale, avatarAlpha)
            drawFigmaBitmap(canvas, jannyName, Bounds(606f, 683f + y, 220f, 72f), stageLeft, stageTop, stageScale, avatarAlpha)
            drawFigmaBitmap(canvas, guestName, Bounds(854f, 683f + y, 200f, 72f), stageLeft, stageTop, stageScale, avatarAlpha)
        }

        if (progress < 1f) {
            postInvalidateOnAnimation()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                avatarPressStarted = isSettledAvatarHit(event.x, event.y)
                avatarPressStarted
            }
            MotionEvent.ACTION_UP -> {
                val shouldNavigate = avatarPressStarted && isSettledAvatarHit(event.x, event.y)
                avatarPressStarted = false
                if (shouldNavigate) {
                    performClick()
                    true
                } else {
                    false
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                avatarPressStarted = false
                false
            }
            else -> avatarPressStarted
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (!isFinalFrameSettled()) return false
        onAvatarSelected?.invoke()
        return true
    }

    private fun drawSettlingLogo(
        canvas: Canvas,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        progress: Float,
        alpha: Float
    ) {
        val logoEntry = bouncySegmentMs(progress, LOGO_ENTRY_START_MS, LOGO_ENTRY_END_MS)
        val finalProgress = easedSegmentMs(progress, FINAL_SETTLE_START_MS, FINAL_SETTLE_END_MS)
        val base = Bounds(222f, 150f, 750f, 535f)
        val final = Bounds(297f, 11f, 600f, 428f)

        if (logoEntry >= 0.999f) {
            drawFigmaBitmap(canvas, logoCombined, lerpBounds(base, final, finalProgress), stageLeft, stageTop, stageScale, alpha)
            return
        }

        val simpleEntry = lerpBounds(Bounds(447f, 1037f, 300f, 214f), base, logoEntry)
        val eyesEntry = lerpBounds(Bounds(453f, -417f, 300f, 214f), base, logoEntry)
        drawFigmaBitmap(canvas, logoSimple, lerpBounds(simpleEntry, final, finalProgress), stageLeft, stageTop, stageScale, alpha)
        drawFigmaBitmap(canvas, logoEyes, lerpBounds(eyesEntry, final, finalProgress), stageLeft, stageTop, stageScale, alpha)
    }

    private fun drawIntroBackground(canvas: Canvas, solidProgress: Float, gradientProgress: Float) {
        if (gradientProgress <= 0.001f) {
            canvas.drawColor(lerpColor(COLOR_FRAME_1, COLOR_FRAME_2, solidProgress))
            return
        }

        val solid = lerpColor(COLOR_FRAME_1, COLOR_FRAME_2, solidProgress)
        backgroundPaint.shader = LinearGradient(
            0f,
            0f,
            0f,
            height.toFloat(),
            intArrayOf(
                lerpColor(solid, COLOR_GRADIENT_TOP, gradientProgress),
                lerpColor(solid, COLOR_GRADIENT_CENTER, gradientProgress),
                lerpColor(solid, COLOR_GRADIENT_BOTTOM, gradientProgress)
            ),
            null,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        backgroundPaint.shader = null
    }

    private fun drawFigmaBitmap(
        canvas: Canvas,
        bitmap: Bitmap,
        bounds: Bounds,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float
    ) {
        tempRect.set(
            stageLeft + bounds.x * stageScale,
            stageTop + bounds.y * stageScale,
            stageLeft + (bounds.x + bounds.w) * stageScale,
            stageTop + (bounds.y + bounds.h) * stageScale
        )
        imagePaint.alpha = (alpha.coerceIn(0f, 1f) * 255f).roundToInt()
        canvas.drawBitmap(bitmap, null, tempRect, imagePaint)
        imagePaint.alpha = 255
    }

    private fun decode(resId: Int): Bitmap {
        return BitmapFactory.decodeResource(resources, resId, bitmapOptions)
    }

    private fun isFinalFrameSettled(): Boolean {
        return bootProgressAt(bootStartMillis) >= 1f
    }

    private fun isSettledAvatarHit(x: Float, y: Float): Boolean {
        if (!isFinalFrameSettled() || width <= 0 || height <= 0) return false

        val stageScale = min(width / FIGMA_FRAME_WIDTH, height / FIGMA_FRAME_HEIGHT)
        val stageLeft = (width - FIGMA_FRAME_WIDTH * stageScale) / 2f
        val stageTop = (height - FIGMA_FRAME_HEIGHT * stageScale) / 2f

        return FINAL_AVATAR_BOUNDS.any { bounds ->
            val centerX = stageLeft + (bounds.x + bounds.w / 2f) * stageScale
            val centerY = stageTop + (bounds.y + bounds.h / 2f) * stageScale
            val radius = min(bounds.w, bounds.h) * stageScale / 2f
            val dx = x - centerX
            val dy = y - centerY
            dx * dx + dy * dy <= radius * radius
        }
    }
}

private data class Bounds(
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float
)

private fun bootProgressAt(bootStartMillis: Long): Float {
    val elapsed = SystemClock.uptimeMillis() - bootStartMillis
    return (elapsed / BOOT_ANIMATION_MS.toFloat()).coerceIn(0f, 1f)
}

private fun linearSegmentMs(progress: Float, startMs: Int, endMs: Int): Float {
    val ms = progress.coerceIn(0f, 1f) * BOOT_ANIMATION_MS
    return ((ms - startMs) / (endMs - startMs).toFloat()).coerceIn(0f, 1f)
}

private fun easedSegmentMs(progress: Float, startMs: Int, endMs: Int): Float {
    return FastOutSlowInEasing.transform(linearSegmentMs(progress, startMs, endMs))
}

private fun bouncySegmentMs(progress: Float, startMs: Int, endMs: Int): Float {
    return bouncyEasing(linearSegmentMs(progress, startMs, endMs))
}

private fun bouncyEasing(value: Float): Float {
    val t = value.coerceIn(0f, 1f)
    val base = FastOutSlowInEasing.transform(t)
    val bounce = sin(t * PI.toFloat() * 4.5f) * (1f - t) * 0.13f
    return base + bounce
}

private fun lerpFloat(start: Float, end: Float, amount: Float) = start + (end - start) * amount

private fun lerpBounds(start: Bounds, end: Bounds, amount: Float) = Bounds(
    x = lerpFloat(start.x, end.x, amount),
    y = lerpFloat(start.y, end.y, amount),
    w = lerpFloat(start.w, end.w, amount),
    h = lerpFloat(start.h, end.h, amount)
)

private fun lerpColor(start: Int, end: Int, amount: Float): Int {
    val t = amount.coerceIn(0f, 1f)
    return Color.argb(
        lerpInt(Color.alpha(start), Color.alpha(end), t),
        lerpInt(Color.red(start), Color.red(end), t),
        lerpInt(Color.green(start), Color.green(end), t),
        lerpInt(Color.blue(start), Color.blue(end), t)
    )
}

private fun lerpInt(start: Int, end: Int, amount: Float): Int {
    return (start + (end - start) * amount).roundToInt()
}

private fun combineBitmaps(base: Bitmap, overlay: Bitmap): Bitmap {
    val output = Bitmap.createBitmap(base.width, base.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
    canvas.drawBitmap(base, 0f, 0f, paint)
    canvas.drawBitmap(overlay, 0f, 0f, paint)
    return output
}

private fun circularBitmap(source: Bitmap): Bitmap {
    val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val rect = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
    val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
    canvas.drawOval(rect, maskPaint)
    imagePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(source, null, rect, imagePaint)
    imagePaint.xfermode = null
    return output
}
