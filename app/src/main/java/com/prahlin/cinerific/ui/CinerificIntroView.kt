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
import android.graphics.Typeface
import android.os.SystemClock
import android.text.InputType
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.core.content.res.ResourcesCompat
import com.prahlin.cinerific.R
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

private const val FIGMA_FRAME_WIDTH = 1194f
private const val FIGMA_FRAME_HEIGHT = 834f
private const val SCREEN_BLACK_HOLD_MS = 1000
private const val SCREEN_PINK_MS = 1000
private const val LOGO_ENTRY_START_MS = 250
private const val LOGO_ENTRY_END_MS = 1687
private const val FINAL_SETTLE_START_MS = LOGO_ENTRY_END_MS
private const val FINAL_SETTLE_END_MS = 4812
private const val BOOT_ANIMATION_MS = FINAL_SETTLE_END_MS
private const val MOCK_SIGN_IN_TRANSITION_MS = 720
private const val MOCK_SIGN_IN_STACK_EXIT_Y = 430f
private const val MOCK_FORM_FOCUS_TRANSITION_MS = 520

private const val COLOR_FRAME_1 = 0xFF000000.toInt()
private const val COLOR_FRAME_2 = 0xFF600878.toInt()
private const val COLOR_GRADIENT_TOP = 0xFF050006.toInt()
private const val COLOR_GRADIENT_CENTER = 0xFF600878.toInt()
private const val COLOR_GRADIENT_BOTTOM = 0xFF100114.toInt()
private const val STANDARD_TABLET_LONG_EDGE = 2560
private const val STANDARD_TABLET_SHORT_EDGE = 1600
private const val STANDARD_TABLET_SIZE_TOLERANCE = 8
private const val STANDARD_BACKGROUND_SETTLED_ALPHA = 0.5f
private const val SIGN_IN_AVATAR_SIZE = 176f
private const val SIGN_IN_PORTRAIT_STACK_SHIFT_Y = -51f
private const val SIGN_IN_LANDSCAPE_STACK_SHIFT_Y = -61f
private const val SIGN_IN_NAME_TOP = 655f
private const val SIGN_IN_LANDSCAPE_ACCOUNT_PROMPT_EXTRA_SHIFT_Y = -36f
private const val ACCOUNT_PROMPT_CREATE_TEXT = "Create Account"
private const val ACCOUNT_PROMPT_SIGN_IN_TEXT = "Sign In"
private const val ACCOUNT_PROMPT_FORGOT_TEXT = "Forgot Password"
private const val ACCOUNT_PROMPT_LEFT_ANCHOR_X = 152f
private const val ACCOUNT_PROMPT_SIGN_IN_CENTER_X = 597f
private const val ACCOUNT_PROMPT_RIGHT_ANCHOR_X = 1042f
private const val ACCOUNT_PROMPT_CENTER_Y = 792f
private const val ACCOUNT_PROMPT_TEXT_SIZE = 30f
private const val ACCOUNT_PROMPT_LANDSCAPE_TEXT_SIZE = 24.3f
private const val ACCOUNT_PROMPT_SECONDARY_TEXT_SCALE = 0.8f
private const val ACCOUNT_PROMPT_SIGN_IN_HIT_WIDTH = 180f
private const val ACCOUNT_PROMPT_SIGN_IN_HIT_HEIGHT = 80f
private const val MOCK_FORM_Y = 420f
private const val MOCK_FORM_ENTRY_Y = 82f
private const val MOCK_FORM_WIDTH = 330f
private const val MOCK_FORM_FIELD_HEIGHT = 64f
private const val MOCK_FORM_FIELD_GAP = 34f
private const val MOCK_FORM_FIELD_RADIUS = 10f
private const val MOCK_FORM_LABEL_TEXT_SIZE = 24f
private const val MOCK_FORM_FLOATING_LABEL_SCALE = 0.15f
private const val MOCK_FORM_LABEL_FLOAT_ANIMATION_MS = 180
private const val MOCK_FORM_FIELD_STROKE_WIDTH = 2f
private const val MOCK_FORM_LABEL_INSET_X = 22f
private const val MOCK_FORM_LABEL_FLOAT_INSET_X = 16f
private const val MOCK_FORM_LABEL_FLOAT_TOP_INSET_Y = 7f
private const val MOCK_FORM_INPUT_BASELINE_FROM_TOP = 49f
private const val MOCK_FORM_INPUT_MAX_CHARS = 34
private const val MOCK_FORM_USERNAME_TEXT = "Username:"
private const val MOCK_FORM_PASSWORD_TEXT = "Password:"
private const val LOGO_FINAL_TOP = 11f
private const val LOGO_FINAL_HEIGHT = 428f
private const val LOGO_FINAL_CENTER_Y = LOGO_FINAL_TOP + LOGO_FINAL_HEIGHT / 2f
private const val MOCK_FORM_STACK_HEIGHT = MOCK_FORM_FIELD_HEIGHT * 2f + MOCK_FORM_FIELD_GAP
private const val MOCK_FORM_STACK_CENTER_Y = MOCK_FORM_Y + MOCK_FORM_STACK_HEIGHT / 2f
private const val MOCK_FORM_FOCUS_SHIFT_Y = LOGO_FINAL_CENTER_Y - MOCK_FORM_STACK_CENTER_Y

private val FINAL_AVATAR_TARGETS = listOf(
    AvatarTarget(CinerificProfile.Steve, Bounds(152f, 450f, SIGN_IN_AVATAR_SIZE, SIGN_IN_AVATAR_SIZE)),
    AvatarTarget(CinerificProfile.Martin, Bounds(390f, 450f, SIGN_IN_AVATAR_SIZE, SIGN_IN_AVATAR_SIZE)),
    AvatarTarget(CinerificProfile.Janny, Bounds(628f, 454f, SIGN_IN_AVATAR_SIZE, SIGN_IN_AVATAR_SIZE)),
    AvatarTarget(CinerificProfile.Guest, Bounds(866f, 450f, SIGN_IN_AVATAR_SIZE, SIGN_IN_AVATAR_SIZE))
)

internal class CinerificIntroView(context: Context) : View(context) {
    var bootStartMillis: Long = SystemClock.uptimeMillis()
        set(value) {
            if (field == value) return
            field = value
            postInvalidateOnAnimation()
        }
    var onAvatarSelected: ((CinerificProfile) -> Unit)? = null

    private val bitmapOptions = BitmapFactory.Options().apply {
        inScaled = false
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    private val logoSimple = decode(R.drawable.logo_simple_large)
    private val logoEyes = decode(R.drawable.logo_eyes_large)
    private val logoCombined = combineBitmaps(logoSimple, logoEyes)
    private val steveAvatar = decode(R.drawable.steve_avatar_bubble_edge50_body0_test)
    private val martinAvatar = decode(R.drawable.martin_avatar_bubble_edge50_body0_test)
    private val jannyAvatar = decode(R.drawable.janny_avatar_bubble_edge50_body0_test)
    private val guestAvatar = decode(R.drawable.guest_avatar_bubble_edge50_body0_test)
    private val steveName = decode(R.drawable.steve_name)
    private val martinName = decode(R.drawable.martin_name)
    private val jannyName = decode(R.drawable.janny_name)
    private val guestName = decode(R.drawable.guest_name)
    private val standardLandscapeBackground = decode(R.drawable.signin_background_standard_landscape)
    private val standardPortraitBackground = decode(R.drawable.signin_background_standard_portrait)

    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
    private val backgroundPaint = Paint()
    private val accountPromptPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = ResourcesCompat.getFont(context, R.font.manrope)
    }
    private val accountPromptBoldPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        color = Color.WHITE
        isFakeBoldText = true
        style = Paint.Style.FILL_AND_STROKE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(ResourcesCompat.getFont(context, R.font.manrope), Typeface.BOLD)
    }
    private val formLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.LEFT
        typeface = ResourcesCompat.getFont(context, R.font.manrope)
    }
    private val formInputPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.LEFT
        typeface = ResourcesCompat.getFont(context, R.font.manrope)
    }
    private val formCaretPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }
    private val formFieldFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val formFieldStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val tempRect = RectF()
    private var pressedAvatarProfile: CinerificProfile? = null
    private var clickAvatarProfile: CinerificProfile? = null
    private var pressedSignInPrompt = false
    private var mockSignInStartMillis: Long? = null
    private var pressedMockField: MockSignInField? = null
    private var focusedMockField: MockSignInField? = null
    private var usernameText = ""
    private var passwordText = ""
    private var activeComposingText = ""
    private var usernameLabelFloatProgress = 0f
    private var passwordLabelFloatProgress = 0f
    private var lastFormLabelAnimationMillis = SystemClock.uptimeMillis()
    private var mockFormFocusProgress = 0f
    private var lastFormFocusAnimationMillis = SystemClock.uptimeMillis()

    init {
        isClickable = true
        isFocusable = true
        isFocusableInTouchMode = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postInvalidateOnAnimation()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        pressedAvatarProfile = null
        clickAvatarProfile = null
        pressedSignInPrompt = false
        pressedMockField = null
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val progress = bootProgressAt(bootStartMillis)
        val blackToPurple = linearSegmentMs(
            progress,
            SCREEN_BLACK_HOLD_MS,
            SCREEN_BLACK_HOLD_MS + SCREEN_PINK_MS
        )
        val purpleToSettle = easedSegmentMs(progress, 2500, 3900)
        drawIntroBackground(canvas, blackToPurple, purpleToSettle)

        val stage = currentStageMetrics() ?: return
        val mockProgress = mockSignInProgress()
        updateMockFormFocusAnimation()
        val formFocusMotion = FastOutSlowInEasing.transform(mockFormFocusProgress)

        val logoAlpha = linearSegmentMs(progress, 90, LOGO_ENTRY_START_MS)
        if (logoAlpha > 0.01f) {
            drawSettlingLogo(canvas, stage.left, stage.top, stage.scale, progress, logoAlpha, formFocusMotion)
        }

        val avatarAlpha = easedSegmentMs(progress, 2300, FINAL_SETTLE_END_MS)
        val mockMotion = FastOutSlowInEasing.transform(mockProgress)
        if (avatarAlpha > 0.01f) {
            val stackAlpha = avatarAlpha * (1f - mockMotion)
            val y = lerpFloat(56f, 0f, avatarAlpha) + MOCK_SIGN_IN_STACK_EXIT_Y * mockMotion
            val stackShiftY = signInStackShiftY()
            if (stackAlpha > 0.01f) {
                drawFigmaBitmap(canvas, steveAvatar, Bounds(152f, 450f + stackShiftY + y, SIGN_IN_AVATAR_SIZE, SIGN_IN_AVATAR_SIZE), stage.left, stage.top, stage.scale, stackAlpha)
                drawFigmaBitmap(canvas, martinAvatar, Bounds(390f, 450f + stackShiftY + y, SIGN_IN_AVATAR_SIZE, SIGN_IN_AVATAR_SIZE), stage.left, stage.top, stage.scale, stackAlpha)
                drawFigmaBitmap(canvas, jannyAvatar, Bounds(628f, 454f + stackShiftY + y, SIGN_IN_AVATAR_SIZE, SIGN_IN_AVATAR_SIZE), stage.left, stage.top, stage.scale, stackAlpha)
                drawFigmaBitmap(canvas, guestAvatar, Bounds(866f, 450f + stackShiftY + y, SIGN_IN_AVATAR_SIZE, SIGN_IN_AVATAR_SIZE), stage.left, stage.top, stage.scale, stackAlpha)

                drawFigmaBitmap(canvas, steveName, Bounds(130f, SIGN_IN_NAME_TOP + stackShiftY + y, 220f, 72f), stage.left, stage.top, stage.scale, stackAlpha)
                drawFigmaBitmap(canvas, martinName, Bounds(368f, SIGN_IN_NAME_TOP + stackShiftY + y, 220f, 72f), stage.left, stage.top, stage.scale, stackAlpha)
                drawFigmaBitmap(canvas, jannyName, Bounds(606f, SIGN_IN_NAME_TOP + stackShiftY + y, 220f, 72f), stage.left, stage.top, stage.scale, stackAlpha)
                drawFigmaBitmap(canvas, guestName, Bounds(854f, SIGN_IN_NAME_TOP + stackShiftY + y, 200f, 72f), stage.left, stage.top, stage.scale, stackAlpha)
                drawAccountPrompt(canvas, stage.left, stage.top, stage.scale, y, stackAlpha)
            }
        }
        if (mockProgress > 0f) {
            drawMockSignInForm(canvas, stage.left, stage.top, stage.scale, mockMotion, formFocusMotion)
        }

        if (progress < 1f || isMockSignInAnimating() || isMockFormLabelAnimating() || isMockFormFocusAnimating()) {
            postInvalidateOnAnimation()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (mockSignInStartMillis != null) {
                    pressedMockField = mockSignInFieldHit(event.x, event.y)
                    return true
                }
                pressedSignInPrompt = settledSignInPromptHit(event.x, event.y)
                pressedAvatarProfile = if (pressedSignInPrompt) {
                    null
                } else {
                    settledAvatarHitProfile(event.x, event.y)
                }
                pressedSignInPrompt || pressedAvatarProfile != null
            }
            MotionEvent.ACTION_UP -> {
                if (mockSignInStartMillis != null) {
                    val releasedField = mockSignInFieldHit(event.x, event.y)
                    val shouldFocusField = releasedField != null &&
                        (pressedMockField == null || pressedMockField == releasedField)
                    pressedMockField = null
                    super.performClick()
                    if (shouldFocusField && releasedField != null) {
                        focusMockSignInField(releasedField)
                    } else {
                        clearMockSignInFieldFocus()
                    }
                    return true
                }
                val shouldOpenMockSignIn = pressedSignInPrompt && settledSignInPromptHit(event.x, event.y)
                val releasedAvatarProfile = settledAvatarHitProfile(event.x, event.y)
                val shouldNavigate = pressedAvatarProfile != null && pressedAvatarProfile == releasedAvatarProfile
                clickAvatarProfile = pressedAvatarProfile
                pressedAvatarProfile = null
                pressedSignInPrompt = false
                if (shouldOpenMockSignIn) {
                    super.performClick()
                    openMockSignInScreen()
                    true
                } else if (shouldNavigate) {
                    performClick()
                    true
                } else {
                    clickAvatarProfile = null
                    false
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                pressedAvatarProfile = null
                clickAvatarProfile = null
                pressedSignInPrompt = false
                pressedMockField = null
                false
            }
            else -> pressedSignInPrompt || pressedAvatarProfile != null || mockSignInStartMillis != null
        }
    }

    override fun onCheckIsTextEditor(): Boolean {
        return mockSignInStartMillis != null
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val field = focusedMockField ?: return null
        outAttrs.inputType = if (field == MockSignInField.Password) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        }
        outAttrs.imeOptions = if (field == MockSignInField.Username) {
            EditorInfo.IME_ACTION_NEXT
        } else {
            EditorInfo.IME_ACTION_DONE
        }
        val textLength = mockSignInFieldText(field).length
        outAttrs.initialSelStart = textLength
        outAttrs.initialSelEnd = textLength
        return MockSignInInputConnection()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (focusedMockField != null) {
            return handleMockSignInKeyEvent(keyCode, event) || super.onKeyDown(keyCode, event)
        }
        return super.onKeyDown(keyCode, event)
    }

    @Suppress("DEPRECATION")
    override fun onKeyMultiple(keyCode: Int, repeatCount: Int, event: KeyEvent): Boolean {
        val characters = event.getCharacters()
        if (focusedMockField != null && !characters.isNullOrEmpty()) {
            return appendMockSignInInput(characters)
        }
        return super.onKeyMultiple(keyCode, repeatCount, event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (!isFinalFrameSettled()) return false
        val profile = clickAvatarProfile ?: return false
        onAvatarSelected?.invoke(profile)
        clickAvatarProfile = null
        return true
    }

    private fun drawSettlingLogo(
        canvas: Canvas,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        progress: Float,
        alpha: Float,
        formFocusMotion: Float
    ) {
        val logoEntry = bouncySegmentMs(progress, LOGO_ENTRY_START_MS, LOGO_ENTRY_END_MS)
        val finalProgress = easedSegmentMs(progress, FINAL_SETTLE_START_MS, FINAL_SETTLE_END_MS)
        val focusShiftY = mockLogoFocusShiftY(formFocusMotion)
        val base = offsetBounds(Bounds(222f, 150f, 750f, 535f), focusShiftY)
        val final = offsetBounds(Bounds(297f, LOGO_FINAL_TOP, 600f, LOGO_FINAL_HEIGHT), focusShiftY)

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
        val standardSignInBackground = standardSignInBackgroundForSize()
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

        if (standardSignInBackground != null) {
            drawStandardSignInBlackMatte(canvas, gradientProgress)
            drawStandardSignInBackground(canvas, standardSignInBackground, gradientProgress)
        }
    }

    private fun drawStandardSignInBlackMatte(canvas: Canvas, alpha: Float) {
        backgroundPaint.color = COLOR_FRAME_1
        backgroundPaint.alpha = (alpha.coerceIn(0f, 1f) * 255f).roundToInt()
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        backgroundPaint.alpha = 255
    }

    private fun drawStandardSignInBackground(canvas: Canvas, bitmap: Bitmap, alpha: Float) {
        tempRect.set(0f, 0f, width.toFloat(), height.toFloat())
        imagePaint.alpha = (alpha.coerceIn(0f, 1f) * STANDARD_BACKGROUND_SETTLED_ALPHA * 255f).roundToInt()
        canvas.drawBitmap(bitmap, null, tempRect, imagePaint)
        imagePaint.alpha = 255
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

    private fun drawAccountPrompt(
        canvas: Canvas,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        yOffset: Float,
        alpha: Float
    ) {
        val isLandscape = width > height
        val textSize = signInAccountPromptTextSize() * stageScale
        val secondaryTextSize = textSize * ACCOUNT_PROMPT_SECONDARY_TEXT_SCALE
        val paintAlpha = (alpha.coerceIn(0f, 1f) * 255f).roundToInt()
        accountPromptPaint.textSize = secondaryTextSize
        accountPromptBoldPaint.textSize = textSize
        accountPromptBoldPaint.strokeWidth = 0.65f * stageScale
        accountPromptPaint.alpha = paintAlpha
        accountPromptBoldPaint.alpha = paintAlpha

        val accountPromptShiftY = if (isLandscape) SIGN_IN_LANDSCAPE_ACCOUNT_PROMPT_EXTRA_SHIFT_Y else 0f
        val centerY = stageTop +
            (ACCOUNT_PROMPT_CENTER_Y + signInStackShiftY() + accountPromptShiftY + yOffset) * stageScale
        val secondaryMetrics = accountPromptPaint.fontMetrics
        val primaryMetrics = accountPromptBoldPaint.fontMetrics
        val secondaryBaselineY = centerY - (secondaryMetrics.ascent + secondaryMetrics.descent) / 2f
        val primaryBaselineY = centerY - (primaryMetrics.ascent + primaryMetrics.descent) / 2f
        accountPromptPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(
            ACCOUNT_PROMPT_CREATE_TEXT,
            stageLeft + ACCOUNT_PROMPT_LEFT_ANCHOR_X * stageScale,
            secondaryBaselineY,
            accountPromptPaint
        )
        accountPromptBoldPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            ACCOUNT_PROMPT_SIGN_IN_TEXT,
            stageLeft + ACCOUNT_PROMPT_SIGN_IN_CENTER_X * stageScale,
            primaryBaselineY,
            accountPromptBoldPaint
        )
        accountPromptPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            ACCOUNT_PROMPT_FORGOT_TEXT,
            stageLeft + ACCOUNT_PROMPT_RIGHT_ANCHOR_X * stageScale,
            secondaryBaselineY,
            accountPromptPaint
        )
        accountPromptPaint.alpha = 255
        accountPromptPaint.textAlign = Paint.Align.CENTER
        accountPromptBoldPaint.alpha = 255
    }

    private fun drawMockSignInForm(
        canvas: Canvas,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        progress: Float,
        formFocusMotion: Float
    ) {
        updateMockFormLabelAnimation()

        val alpha = progress.coerceIn(0f, 1f)
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - alpha)
        val fieldFillAlpha = (alpha * 26f).roundToInt()
        val fieldStrokeAlpha = (alpha * 185f).roundToInt()
        val fieldX = mockFormX()
        val usernameY = MOCK_FORM_Y + yOffset
        val passwordY = usernameY + MOCK_FORM_FIELD_HEIGHT + MOCK_FORM_FIELD_GAP

        formFieldFillPaint.color = Color.argb(fieldFillAlpha, 255, 255, 255)
        formFieldStrokePaint.color = Color.argb(fieldStrokeAlpha, 255, 255, 255)
        formFieldStrokePaint.strokeWidth = MOCK_FORM_FIELD_STROKE_WIDTH * stageScale
        formCaretPaint.strokeWidth = 1.45f * stageScale

        val focusShiftY = mockFormFocusShiftY(formFocusMotion)
        drawMockSignInField(canvas, MockSignInField.Username, fieldX, usernameY + focusShiftY, stageLeft, stageTop, stageScale, alpha)
        drawMockSignInField(canvas, MockSignInField.Password, fieldX, passwordY + focusShiftY, stageLeft, stageTop, stageScale, alpha)

        formLabelPaint.alpha = 255
        formInputPaint.alpha = 255
        formCaretPaint.alpha = 255
    }

    private fun drawMockSignInField(
        canvas: Canvas,
        field: MockSignInField,
        x: Float,
        y: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float
    ) {
        tempRect.set(
            stageLeft + x * stageScale,
            stageTop + y * stageScale,
            stageLeft + (x + MOCK_FORM_WIDTH) * stageScale,
            stageTop + (y + MOCK_FORM_FIELD_HEIGHT) * stageScale
        )
        val radius = MOCK_FORM_FIELD_RADIUS * stageScale
        canvas.drawRoundRect(tempRect, radius, radius, formFieldFillPaint)
        canvas.drawRoundRect(tempRect, radius, radius, formFieldStrokePaint)

        val label = mockSignInFieldLabel(field)
        val labelProgress = mockSignInFieldLabelProgress(field)
        val textAlpha = (alpha.coerceIn(0f, 1f) * 255f).roundToInt()
        formLabelPaint.textSize = lerpFloat(
            MOCK_FORM_LABEL_TEXT_SIZE,
            MOCK_FORM_LABEL_TEXT_SIZE * MOCK_FORM_FLOATING_LABEL_SCALE,
            labelProgress
        ) * stageScale
        formLabelPaint.alpha = textAlpha
        formLabelPaint.textAlign = Paint.Align.LEFT
        val metrics = formLabelPaint.fontMetrics
        val placeholderBaselineY = tempRect.centerY() - (metrics.ascent + metrics.descent) / 2f
        val floatingBaselineY = tempRect.top + MOCK_FORM_LABEL_FLOAT_TOP_INSET_Y * stageScale - metrics.ascent
        val labelX = lerpFloat(
            tempRect.left + MOCK_FORM_LABEL_INSET_X * stageScale,
            tempRect.left + MOCK_FORM_LABEL_FLOAT_INSET_X * stageScale,
            labelProgress
        )
        val labelBaselineY = lerpFloat(placeholderBaselineY, floatingBaselineY, labelProgress)
        canvas.drawText(
            label,
            labelX,
            labelBaselineY,
            formLabelPaint
        )

        val fieldText = mockSignInFieldDisplayText(field)
        val shouldDrawInput = fieldText.isNotEmpty() || focusedMockField == field
        if (shouldDrawInput) {
            val inputAlpha = (alpha.coerceIn(0f, 1f) * labelProgress * 255f).roundToInt()
            formInputPaint.textSize = MOCK_FORM_LABEL_TEXT_SIZE * stageScale
            formInputPaint.alpha = inputAlpha
            formCaretPaint.alpha = inputAlpha

            val inputX = tempRect.left + MOCK_FORM_LABEL_INSET_X * stageScale
            val inputBaselineY = tempRect.top + MOCK_FORM_INPUT_BASELINE_FROM_TOP * stageScale
            val maxInputWidth = MOCK_FORM_WIDTH * stageScale - MOCK_FORM_LABEL_INSET_X * stageScale * 2f
            val visibleText = trailingFittingText(fieldText, maxInputWidth, formInputPaint)
            canvas.drawText(visibleText, inputX, inputBaselineY, formInputPaint)

            if (focusedMockField == field) {
                val caretX = inputX + formInputPaint.measureText(visibleText) + 2f * stageScale
                val inputMetrics = formInputPaint.fontMetrics
                canvas.drawLine(
                    caretX,
                    inputBaselineY + inputMetrics.ascent,
                    caretX,
                    inputBaselineY + inputMetrics.descent,
                    formCaretPaint
                )
            }
        }
    }

    private fun decode(resId: Int): Bitmap {
        return BitmapFactory.decodeResource(resources, resId, bitmapOptions)
    }

    private fun standardSignInBackgroundForSize(): Bitmap? {
        return when {
            isSizeCloseTo(width, STANDARD_TABLET_LONG_EDGE) &&
                isSizeCloseTo(height, STANDARD_TABLET_SHORT_EDGE) -> standardLandscapeBackground
            isSizeCloseTo(width, STANDARD_TABLET_SHORT_EDGE) &&
                isSizeCloseTo(height, STANDARD_TABLET_LONG_EDGE) -> standardPortraitBackground
            else -> null
        }
    }

    private fun isSizeCloseTo(actual: Int, expected: Int): Boolean {
        return abs(actual - expected) <= STANDARD_TABLET_SIZE_TOLERANCE
    }

    private fun isFinalFrameSettled(): Boolean {
        return bootProgressAt(bootStartMillis) >= 1f
    }

    private fun settledAvatarHitProfile(x: Float, y: Float): CinerificProfile? {
        if (mockSignInStartMillis != null) return null
        if (!isFinalFrameSettled()) return null

        val stage = currentStageMetrics() ?: return null

        val stackShiftY = signInStackShiftY()
        return FINAL_AVATAR_TARGETS.firstOrNull { target ->
            val bounds = target.bounds
            val centerX = stage.left + (bounds.x + bounds.w / 2f) * stage.scale
            val centerY = stage.top + (bounds.y + stackShiftY + bounds.h / 2f) * stage.scale
            val radius = min(bounds.w, bounds.h) * stage.scale / 2f
            val dx = x - centerX
            val dy = y - centerY
            dx * dx + dy * dy <= radius * radius
        }?.profile
    }

    private fun settledSignInPromptHit(x: Float, y: Float): Boolean {
        if (mockSignInStartMillis != null || !isFinalFrameSettled()) return false

        val stage = currentStageMetrics() ?: return false
        val centerX = stage.left + ACCOUNT_PROMPT_SIGN_IN_CENTER_X * stage.scale
        val centerY = stage.top +
            (ACCOUNT_PROMPT_CENTER_Y + signInStackShiftY() + signInAccountPromptExtraShiftY()) * stage.scale
        val halfWidth = ACCOUNT_PROMPT_SIGN_IN_HIT_WIDTH * stage.scale / 2f
        val halfHeight = ACCOUNT_PROMPT_SIGN_IN_HIT_HEIGHT * stage.scale / 2f
        return x in (centerX - halfWidth)..(centerX + halfWidth) &&
            y in (centerY - halfHeight)..(centerY + halfHeight)
    }

    private fun mockSignInFieldHit(x: Float, y: Float): MockSignInField? {
        if (mockSignInStartMillis == null) return null

        val stage = currentStageMetrics() ?: return null
        val progress = FastOutSlowInEasing.transform(mockSignInProgress())
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - progress)
        val focusShiftY = mockFormFocusShiftY(FastOutSlowInEasing.transform(mockFormFocusProgress))
        val fieldX = mockFormX()
        val usernameY = MOCK_FORM_Y + yOffset + focusShiftY
        val passwordY = usernameY + MOCK_FORM_FIELD_HEIGHT + MOCK_FORM_FIELD_GAP
        return when {
            mockSignInFieldContains(x, y, fieldX, usernameY, stage) -> MockSignInField.Username
            mockSignInFieldContains(x, y, fieldX, passwordY, stage) -> MockSignInField.Password
            else -> null
        }
    }

    private fun mockSignInFieldContains(
        pointerX: Float,
        pointerY: Float,
        fieldX: Float,
        fieldY: Float,
        stage: StageMetrics
    ): Boolean {
        val left = stage.left + fieldX * stage.scale
        val top = stage.top + fieldY * stage.scale
        val right = stage.left + (fieldX + MOCK_FORM_WIDTH) * stage.scale
        val bottom = stage.top + (fieldY + MOCK_FORM_FIELD_HEIGHT) * stage.scale
        return pointerX in left..right && pointerY in top..bottom
    }

    private fun focusMockSignInField(field: MockSignInField) {
        if (focusedMockField != field) {
            activeComposingText = ""
            focusedMockField = field
            restartMockFormLabelAnimation()
            restartMockFormFocusAnimation()
        }
        requestFocus()
        post {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.restartInput(this)
            inputMethodManager?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
        postInvalidateOnAnimation()
    }

    private fun clearMockSignInFieldFocus() {
        if (focusedMockField == null) return
        activeComposingText = ""
        focusedMockField = null
        restartMockFormLabelAnimation()
        restartMockFormFocusAnimation()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
        clearFocus()
        postInvalidateOnAnimation()
    }

    private fun mockFormX(): Float {
        return (FIGMA_FRAME_WIDTH - MOCK_FORM_WIDTH) / 2f
    }

    private fun updateMockFormLabelAnimation() {
        val now = SystemClock.uptimeMillis()
        val elapsed = (now - lastFormLabelAnimationMillis).coerceIn(0L, 48L)
        lastFormLabelAnimationMillis = now
        val step = elapsed / MOCK_FORM_LABEL_FLOAT_ANIMATION_MS.toFloat()
        usernameLabelFloatProgress = moveToward(
            usernameLabelFloatProgress,
            mockSignInFieldLabelTarget(MockSignInField.Username),
            step
        )
        passwordLabelFloatProgress = moveToward(
            passwordLabelFloatProgress,
            mockSignInFieldLabelTarget(MockSignInField.Password),
            step
        )
    }

    private fun updateMockFormFocusAnimation() {
        val now = SystemClock.uptimeMillis()
        val elapsed = (now - lastFormFocusAnimationMillis).coerceIn(0L, 48L)
        lastFormFocusAnimationMillis = now
        val step = elapsed / MOCK_FORM_FOCUS_TRANSITION_MS.toFloat()
        mockFormFocusProgress = moveToward(mockFormFocusProgress, mockFormFocusTarget(), step)
    }

    private fun isMockFormLabelAnimating(): Boolean {
        return usernameLabelFloatProgress != mockSignInFieldLabelTarget(MockSignInField.Username) ||
            passwordLabelFloatProgress != mockSignInFieldLabelTarget(MockSignInField.Password)
    }

    private fun isMockFormFocusAnimating(): Boolean {
        return mockFormFocusProgress != mockFormFocusTarget()
    }

    private fun restartMockFormLabelAnimation() {
        lastFormLabelAnimationMillis = SystemClock.uptimeMillis() - 16L
    }

    private fun restartMockFormFocusAnimation() {
        lastFormFocusAnimationMillis = SystemClock.uptimeMillis() - 16L
    }

    private fun mockFormFocusTarget(): Float {
        return if (
            focusedMockField != null ||
            usernameText.isNotEmpty() ||
            passwordText.isNotEmpty()
        ) {
            1f
        } else {
            0f
        }
    }

    private fun mockFormFocusShiftY(formFocusMotion: Float): Float {
        return MOCK_FORM_FOCUS_SHIFT_Y * formFocusMotion.coerceIn(0f, 1f)
    }

    private fun mockLogoFocusShiftY(formFocusMotion: Float): Float {
        val focusMotion = formFocusMotion.coerceIn(0f, 1f)
        return if (width > height) {
            -(LOGO_FINAL_TOP + LOGO_FINAL_HEIGHT + 24f) * focusMotion
        } else {
            mockFormFocusShiftY(focusMotion)
        }
    }

    private fun mockSignInFieldLabelTarget(field: MockSignInField): Float {
        return if (focusedMockField == field || mockSignInFieldText(field).isNotEmpty()) 1f else 0f
    }

    private fun mockSignInFieldLabelProgress(field: MockSignInField): Float {
        return when (field) {
            MockSignInField.Username -> usernameLabelFloatProgress
            MockSignInField.Password -> passwordLabelFloatProgress
        }
    }

    private fun mockSignInFieldLabel(field: MockSignInField): String {
        return when (field) {
            MockSignInField.Username -> MOCK_FORM_USERNAME_TEXT
            MockSignInField.Password -> MOCK_FORM_PASSWORD_TEXT
        }
    }

    private fun mockSignInFieldText(field: MockSignInField): String {
        return when (field) {
            MockSignInField.Username -> usernameText
            MockSignInField.Password -> passwordText
        }
    }

    private fun mockSignInFieldDisplayText(field: MockSignInField): String {
        return when (field) {
            MockSignInField.Username -> usernameText
            MockSignInField.Password -> "*".repeat(passwordText.length)
        }
    }

    private fun setMockSignInFieldText(field: MockSignInField, text: String) {
        val limitedText = text.take(MOCK_FORM_INPUT_MAX_CHARS)
        when (field) {
            MockSignInField.Username -> usernameText = limitedText
            MockSignInField.Password -> passwordText = limitedText
        }
        restartMockFormLabelAnimation()
        restartMockFormFocusAnimation()
        postInvalidateOnAnimation()
    }

    private fun appendMockSignInInput(rawInput: String): Boolean {
        val field = focusedMockField ?: return false
        val input = sanitizeMockSignInInput(rawInput)
        if (input.isEmpty()) return true
        val currentText = mockSignInFieldText(field)
        val baseText = currentText.dropLast(activeComposingText.length.coerceAtMost(currentText.length))
        val availableChars = (MOCK_FORM_INPUT_MAX_CHARS - baseText.length).coerceAtLeast(0)
        val insertedText = input.take(availableChars)
        setMockSignInFieldText(field, baseText + insertedText)
        activeComposingText = ""
        return true
    }

    private fun replaceMockSignInComposingText(rawInput: String): Boolean {
        val field = focusedMockField ?: return false
        val input = sanitizeMockSignInInput(rawInput)
        val currentText = mockSignInFieldText(field)
        val baseText = currentText.dropLast(activeComposingText.length.coerceAtMost(currentText.length))
        val availableChars = (MOCK_FORM_INPUT_MAX_CHARS - baseText.length).coerceAtLeast(0)
        val composingText = input.take(availableChars)
        setMockSignInFieldText(field, baseText + composingText)
        activeComposingText = composingText
        return true
    }

    private fun finishMockSignInComposingText(): Boolean {
        activeComposingText = ""
        return true
    }

    private fun deleteMockSignInInput(beforeLength: Int): Boolean {
        val field = focusedMockField ?: return false
        val currentText = mockSignInFieldText(field)
        if (currentText.isEmpty()) {
            activeComposingText = ""
            return true
        }
        val deleteCount = beforeLength.coerceAtLeast(1).coerceAtMost(currentText.length)
        activeComposingText = ""
        setMockSignInFieldText(field, currentText.dropLast(deleteCount))
        return true
    }

    private fun handleMockSignInEditorAction(actionCode: Int): Boolean {
        activeComposingText = ""
        if (focusedMockField == MockSignInField.Username && actionCode != EditorInfo.IME_ACTION_DONE) {
            focusMockSignInField(MockSignInField.Password)
        } else {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
        }
        return true
    }

    private fun handleMockSignInKeyEvent(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return true
        return when (keyCode) {
            KeyEvent.KEYCODE_DEL -> deleteMockSignInInput(1)
            KeyEvent.KEYCODE_ENTER -> handleMockSignInEditorAction(EditorInfo.IME_ACTION_DONE)
            else -> {
                val typedCharacter = event.unicodeChar.takeIf { it != 0 }?.toChar()?.toString()
                typedCharacter?.let(::appendMockSignInInput) ?: false
            }
        }
    }

    private fun sanitizeMockSignInInput(input: String): String {
        return buildString {
            input.forEach { character ->
                if (character != '\n' && character != '\r' && !character.isISOControl()) {
                    append(character)
                }
            }
        }
    }

    private fun trailingFittingText(text: String, maxWidth: Float, paint: Paint): String {
        if (paint.measureText(text) <= maxWidth) return text

        var startIndex = 0
        while (startIndex < text.length && paint.measureText(text.substring(startIndex)) > maxWidth) {
            startIndex += 1
        }
        return text.substring(startIndex)
    }

    private fun moveToward(current: Float, target: Float, amount: Float): Float {
        return when {
            current < target -> (current + amount).coerceAtMost(target)
            current > target -> (current - amount).coerceAtLeast(target)
            else -> current
        }
    }

    private fun currentStageMetrics(): StageMetrics? {
        if (width <= 0 || height <= 0) return null

        val stageScale = min(width / FIGMA_FRAME_WIDTH, height / FIGMA_FRAME_HEIGHT)
        return StageMetrics(
            left = (width - FIGMA_FRAME_WIDTH * stageScale) / 2f,
            top = (height - FIGMA_FRAME_HEIGHT * stageScale) / 2f,
            scale = stageScale
        )
    }

    private fun signInStackShiftY(): Float {
        return if (width > height) {
            SIGN_IN_LANDSCAPE_STACK_SHIFT_Y
        } else {
            SIGN_IN_PORTRAIT_STACK_SHIFT_Y
        }
    }

    private fun signInAccountPromptTextSize(): Float {
        return if (width > height) ACCOUNT_PROMPT_LANDSCAPE_TEXT_SIZE else ACCOUNT_PROMPT_TEXT_SIZE
    }

    private fun signInAccountPromptExtraShiftY(): Float {
        return if (width > height) SIGN_IN_LANDSCAPE_ACCOUNT_PROMPT_EXTRA_SHIFT_Y else 0f
    }

    private fun openMockSignInScreen() {
        if (mockSignInStartMillis != null) return
        mockSignInStartMillis = SystemClock.uptimeMillis()
        postInvalidateOnAnimation()
    }

    private fun mockSignInProgress(): Float {
        val startMillis = mockSignInStartMillis ?: return 0f
        val elapsed = SystemClock.uptimeMillis() - startMillis
        return (elapsed / MOCK_SIGN_IN_TRANSITION_MS.toFloat()).coerceIn(0f, 1f)
    }

    private fun isMockSignInAnimating(): Boolean {
        val startMillis = mockSignInStartMillis ?: return false
        return SystemClock.uptimeMillis() - startMillis < MOCK_SIGN_IN_TRANSITION_MS
    }

    private inner class MockSignInInputConnection : BaseInputConnection(this@CinerificIntroView, false) {
        override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
            return appendMockSignInInput(text?.toString().orEmpty())
        }

        override fun setComposingText(text: CharSequence?, newCursorPosition: Int): Boolean {
            return replaceMockSignInComposingText(text?.toString().orEmpty())
        }

        override fun finishComposingText(): Boolean {
            return finishMockSignInComposingText()
        }

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            return deleteMockSignInInput(beforeLength)
        }

        override fun deleteSurroundingTextInCodePoints(beforeLength: Int, afterLength: Int): Boolean {
            return deleteSurroundingText(beforeLength, afterLength)
        }

        override fun getTextBeforeCursor(n: Int, flags: Int): CharSequence {
            val text = focusedMockField?.let(::mockSignInFieldText).orEmpty()
            return text.takeLast(n.coerceAtLeast(0))
        }

        override fun getTextAfterCursor(n: Int, flags: Int): CharSequence {
            return ""
        }

        override fun getSelectedText(flags: Int): CharSequence {
            return ""
        }

        override fun performEditorAction(actionCode: Int): Boolean {
            return handleMockSignInEditorAction(actionCode)
        }

        override fun sendKeyEvent(event: KeyEvent): Boolean {
            if (event.action != KeyEvent.ACTION_DOWN) return true
            return when (event.keyCode) {
                KeyEvent.KEYCODE_DEL -> deleteMockSignInInput(1)
                KeyEvent.KEYCODE_ENTER -> handleMockSignInEditorAction(EditorInfo.IME_ACTION_DONE)
                else -> {
                    val typedCharacter = event.unicodeChar.takeIf { it != 0 }?.toChar()?.toString()
                    if (typedCharacter != null) {
                        appendMockSignInInput(typedCharacter)
                    } else {
                        super.sendKeyEvent(event)
                    }
                }
            }
        }
    }
}

private enum class MockSignInField {
    Username,
    Password
}

private data class AvatarTarget(
    val profile: CinerificProfile,
    val bounds: Bounds
)

private data class StageMetrics(
    val left: Float,
    val top: Float,
    val scale: Float
)

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

private fun offsetBounds(bounds: Bounds, yOffset: Float) = Bounds(
    x = bounds.x,
    y = bounds.y + yOffset,
    w = bounds.w,
    h = bounds.h
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
