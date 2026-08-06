package com.prahlin.cinerific.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.os.SystemClock
import android.text.InputType
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowInsets
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
private const val MOCK_LANDSCAPE_INPUT_LIFT_TRANSITION_MS = 420
private const val MOCK_INPUT_DIM_TRANSITION_MS = 150
private const val MOCK_INPUT_DIM_ALPHA = 0.52f

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
private const val ACCOUNT_PROMPT_CREATE_TEXT = "Create My Account"
private const val ACCOUNT_PROMPT_SIGN_IN_TEXT = "Sign In"
private const val ACCOUNT_PROMPT_FORGOT_TEXT = "Forgot Username/Password"
private const val ACCOUNT_PROMPT_SIGN_IN_CENTER_X = 597f
private const val ACCOUNT_PROMPT_CENTER_Y = 792f
private const val ACCOUNT_PROMPT_TEXT_SIZE = 30f
private const val ACCOUNT_PROMPT_LANDSCAPE_TEXT_SIZE = 24.3f
private const val ACCOUNT_PROMPT_CREATE_HIT_WIDTH = 360f
private const val ACCOUNT_PROMPT_CREATE_HIT_HEIGHT = 80f
private const val ACCOUNT_PROMPT_SIGN_IN_HIT_WIDTH = 180f
private const val ACCOUNT_PROMPT_SIGN_IN_HIT_HEIGHT = 80f
private const val ACCOUNT_PROMPT_FORGOT_HIT_WIDTH = 430f
private const val ACCOUNT_PROMPT_FORGOT_HIT_HEIGHT = 80f
private const val MOCK_FORM_Y = 420f
private const val MOCK_FORM_ENTRY_Y = 82f
private const val MOCK_FORM_WIDTH = 330f
private const val MOCK_FORM_FIELD_HEIGHT = 64f
private const val MOCK_FORM_FIELD_GAP = 34f
private const val MOCK_CREATE_FORM_COLUMNS = 3
private const val MOCK_CREATE_FORM_ROWS = 3
private const val MOCK_CREATE_FORM_COLUMN_GAP = MOCK_FORM_FIELD_GAP
private const val MOCK_FORM_FIELD_RADIUS = 10f
private const val MOCK_FORM_LABEL_TEXT_SIZE = 24f
private const val MOCK_FORM_FLOATING_LABEL_SCALE = 0.30f
private const val MOCK_FORM_LABEL_FLOAT_ANIMATION_MS = 180
private const val MOCK_FORM_FIELD_STROKE_WIDTH = 2f
private const val MOCK_FORM_LABEL_INSET_X = 22f
private const val MOCK_FORM_LABEL_FLOAT_INSET_X = 16f
private const val MOCK_FORM_LABEL_FLOAT_TOP_INSET_Y = 7f
private const val MOCK_FORM_INPUT_BASELINE_FROM_TOP = 49f
private const val MOCK_FORM_INPUT_MAX_CHARS = 34
private const val MOCK_FORM_USERNAME_TEXT = "Username"
private const val MOCK_FORM_PASSWORD_TEXT = "Password"
private const val MOCK_FORM_CONFIRM_PASSWORD_TEXT = "Confirm Password"
private const val MOCK_FORM_EMAIL_TEXT = "Email Address"
private const val MOCK_FORM_SUBSCRIPTION_TIER_TEXT = "Subscription Tier"
private const val MOCK_FORM_MONTH_TEXT = "Mon"
private const val MOCK_FORM_DAY_TEXT = "Day"
private const val MOCK_FORM_YEAR_TEXT = "Year"
private const val MOCK_FORM_AVATAR_TEXT = "Avatar"
private const val MOCK_MEMBERSHIP_BASIC_TEXT = "Basic"
private const val MOCK_MEMBERSHIP_PRO_TEXT = "Pro"
private const val MOCK_MEMBERSHIP_ROCK_STAR_TEXT = "Rock Star"
private const val MOCK_MEMBERSHIP_LEGACY_OPTION_1_TEXT = "Option 1"
private const val MOCK_MEMBERSHIP_LEGACY_OPTION_2_TEXT = "Option 2"
private const val MOCK_MEMBERSHIP_LEGACY_OPTION_3_TEXT = "Option 3"
private const val MOCK_MEMBERSHIP_TIER_OPTION_ICON_SIZE = 114f
private const val MOCK_MEMBERSHIP_TIER_CONTROL_ICON_SIZE = 46f
private const val MOCK_MEMBERSHIP_TIER_ICON_TEXT_GAP = 10f
private const val MOCK_DATE_FIELD_GAP = 10f
private const val MOCK_DATE_MONTH_WIDTH = 96f
private const val MOCK_DATE_DAY_WIDTH = MOCK_DATE_MONTH_WIDTH
private const val MOCK_DATE_YEAR_WIDTH = MOCK_FORM_WIDTH - MOCK_DATE_MONTH_WIDTH - MOCK_DATE_DAY_WIDTH - MOCK_DATE_FIELD_GAP * 2f
private const val MOCK_DROPDOWN_OPTION_HEIGHT = 126f
private const val MOCK_DROPDOWN_CHEVRON_SIZE = 12f
private const val MOCK_DATE_DROPDOWN_VISIBLE_OPTION_COUNT = 5
private const val MOCK_DATE_DROPDOWN_VISIBLE_OPTION_COUNT_FLOAT = 5f
private const val MOCK_DATE_DROPDOWN_OPTION_HEIGHT = (MOCK_FORM_FIELD_HEIGHT + MOCK_FORM_FIELD_GAP) * 2f /
    MOCK_DATE_DROPDOWN_VISIBLE_OPTION_COUNT_FLOAT
private const val MOCK_DATE_DROPDOWN_OPTION_TEXT_SIZE = 12f
private const val MOCK_DATE_DROPDOWN_SCROLLBAR_WIDTH = 3.5f
private const val MOCK_DATE_DROPDOWN_SCROLLBAR_INSET_X = 4f
private const val MOCK_DATE_DROPDOWN_SCROLLBAR_INSET_Y = 4f
private const val MOCK_DATE_DROPDOWN_TEXT_SIZE = 22f
private const val MOCK_DATE_DROPDOWN_TEXT_INSET_X = 10f
private const val MOCK_DATE_DROPDOWN_CHEVRON_INSET_X = 10f
private const val MOCK_DATE_DROPDOWN_CHEVRON_SIZE = 9f
private const val MOCK_CREATE_AVATAR_SIZE = 176f
private const val MOCK_CREATE_AVATAR_COUNT = 3
private const val MOCK_CREATE_AVATAR_STACK_SCALE = 1.25f
private const val MOCK_CREATE_AVATAR_LABEL_BASELINE_FROM_TOP = 24f
private const val MOCK_CREATE_AVATAR_TOP_GAP = 16f
private const val MOCK_CREATE_AVATAR_CAROUSEL_MS = 280
private const val MOCK_CREATE_AVATAR_CHEVRON_SIDE_GAP = 20f
private const val MOCK_CREATE_AVATAR_CHEVRON_WIDTH = 18f
private const val MOCK_CREATE_AVATAR_CHEVRON_HEIGHT = 34f
private const val MOCK_CREATE_AVATAR_CHEVRON_STROKE_WIDTH = 2.2f
private const val MOCK_CREATE_AVATAR_CHEVRON_HIT_WIDTH = 62f
private const val MOCK_CREATE_AVATAR_CHEVRON_HIT_HEIGHT = 92f
private const val MOCK_CREATE_AVATAR_DRAG_COMMIT_PROGRESS = 0.24f
private const val MOCK_CREATE_AVATAR_BUBBLE_INNER_INSET_RATIO = 0.105f
private const val MOCK_FORM_TITLE_TEXT_SIZE = 33f
private const val MOCK_FORM_TITLE_BASELINE_GAP = 24f
private const val MOCK_FORGOT_PASSWORD_USERNAME_BODY_TEXT =
    "No Sign-In Information? No problem.\n\nSelect what to recover - Username or Password - and receive a helper email in just a few seconds.\n\nAs long as you have a Cinerific account, the email we send will contain all the information required to get you back on track, pronto."
private const val MOCK_FORGOT_PASSWORD_PASSWORD_BODY_TEXT =
    "No Sign-In Information? No problem.\n\nSelect what to recover - Username or Password - and receive a helper email in just a few seconds.\n\nAs long as you have a Cinerific account, the email we send will contain all the information required to get you back on track, pronto."
private const val MOCK_FORGOT_PASSWORD_EMPTY_HELP_TEXT =
    "Enter your email address to continue. We'll only reply if an account exists."
private const val MOCK_FORGOT_PASSWORD_USERNAME_HELP_TEXT =
    "If an account exists, a username reminder will be sent."
private const val MOCK_FORGOT_PASSWORD_PASSWORD_HELP_TEXT =
    "If an account exists, a reset link will be sent."
private const val MOCK_FORGOT_PASSWORD_LOADING_HELP_TEXT =
    "Sending instructions securely. This will only take a moment."
private const val MOCK_FORGOT_PASSWORD_USERNAME_SENT_HELP_TEXT =
    "Check your inbox for the username associated with this account."
private const val MOCK_FORGOT_PASSWORD_PASSWORD_SENT_HELP_TEXT =
    "Check your inbox for the next step. Reset links expire soon."
private const val MOCK_FORGOT_PASSWORD_SELECTOR_TEXT = "Recover my:"
private const val MOCK_FORGOT_PASSWORD_USERNAME_BUTTON_TEXT = "Send Reminder Link"
private const val MOCK_FORGOT_PASSWORD_PASSWORD_BUTTON_TEXT = "Send Reset Link"
private const val MOCK_FORGOT_PASSWORD_LOADING_BUTTON_TEXT = "Sending..."
private const val MOCK_FORGOT_PASSWORD_USERNAME_SENT_BUTTON_TEXT = "Username Sent"
private const val MOCK_FORGOT_PASSWORD_PASSWORD_SENT_BUTTON_TEXT = "Link Sent"
private const val MOCK_FORGOT_PASSWORD_SUBMIT_MS = 720
private const val MOCK_FORGOT_PASSWORD_SENT_CHECK_SIZE = 16f
private const val MOCK_FORGOT_PASSWORD_SENT_CHECK_GAP = 10f
private const val MOCK_FORGOT_PASSWORD_USABLE_WIDTH =
    MOCK_FORM_WIDTH * MOCK_CREATE_FORM_COLUMNS + MOCK_CREATE_FORM_COLUMN_GAP * (MOCK_CREATE_FORM_COLUMNS - 1)
private const val MOCK_FORGOT_PASSWORD_STACK_GAP = MOCK_CREATE_FORM_COLUMN_GAP * 2f
private const val MOCK_FORGOT_PASSWORD_CONTENT_WIDTH =
    MOCK_FORGOT_PASSWORD_USABLE_WIDTH - MOCK_FORGOT_PASSWORD_STACK_GAP
private const val MOCK_FORGOT_PASSWORD_COPY_WIDTH = MOCK_FORGOT_PASSWORD_CONTENT_WIDTH * 0.6f
private const val MOCK_FORGOT_PASSWORD_FORM_WIDTH = MOCK_FORGOT_PASSWORD_CONTENT_WIDTH * 0.4f
private const val MOCK_FORGOT_PASSWORD_BODY_TOP_GAP = 18f
private const val MOCK_FORGOT_PASSWORD_BODY_TEXT_SIZE = 19f
private const val MOCK_FORGOT_PASSWORD_BODY_LINE_HEIGHT = 27f
private const val MOCK_FORGOT_PASSWORD_BODY_MIDDLE_TEXT_SIZE = MOCK_FORGOT_PASSWORD_BODY_TEXT_SIZE + 3f
private const val MOCK_FORGOT_PASSWORD_BODY_MIDDLE_LINE_HEIGHT = MOCK_FORGOT_PASSWORD_BODY_LINE_HEIGHT + 4f
private const val MOCK_FORGOT_PASSWORD_BODY_LEAD_TEXT_SIZE = MOCK_FORGOT_PASSWORD_BODY_TEXT_SIZE + 6f
private const val MOCK_FORGOT_PASSWORD_BODY_LEAD_LINE_HEIGHT = MOCK_FORGOT_PASSWORD_BODY_LINE_HEIGHT + 7f
private const val MOCK_FORGOT_PASSWORD_HELP_TEXT_SIZE = 14f
private const val MOCK_FORGOT_PASSWORD_HELP_LINE_HEIGHT = 19f
private const val MOCK_FORGOT_PASSWORD_SELECTOR_TOP_GAP = 24f
private const val MOCK_FORGOT_PASSWORD_SELECTOR_LABEL_TEXT_SIZE = MOCK_FORGOT_PASSWORD_BODY_MIDDLE_TEXT_SIZE
private const val MOCK_FORGOT_PASSWORD_SELECTOR_OPTION_TEXT_SIZE = 17f
private const val MOCK_FORGOT_PASSWORD_SELECTOR_BOX_WIDTH = 90f
private const val MOCK_FORGOT_PASSWORD_SELECTOR_BOX_HEIGHT = 44f
private const val MOCK_FORGOT_PASSWORD_FIELD_OFFSET_Y = 96f
private const val MOCK_FORGOT_PASSWORD_BUTTON_TOP_GAP = 26f
private const val MOCK_FORGOT_PASSWORD_BUTTON_HEIGHT = 54f
private const val MOCK_FORGOT_PASSWORD_BUTTON_RADIUS = 10f
private const val MOCK_FORGOT_PASSWORD_HELP_TOP_GAP = 16f
private const val MOCK_FORGOT_PASSWORD_BACK_CHEVRON_TOP_GAP = 16f
private const val MOCK_REMEMBER_ME_TEXT = "Remember me"
private const val MOCK_REMEMBER_ME_TOP_GAP = 24f
private const val MOCK_REMEMBER_ME_BOX_SIZE = 22f
private const val MOCK_REMEMBER_ME_BOX_RADIUS = 5f
private const val MOCK_REMEMBER_ME_LABEL_GAP = 12f
private const val MOCK_REMEMBER_ME_TEXT_SIZE = 18f
private const val MOCK_REMEMBER_ME_HIT_TOP_PADDING = 11f
private const val MOCK_REMEMBER_ME_HIT_HEIGHT = 44f
private const val MOCK_BACK_CHEVRON_TOP_GAP = 50f
private const val MOCK_BACK_CHEVRON_WIDTH = 224f
private const val MOCK_BACK_CHEVRON_HEIGHT = 20f
private const val MOCK_BACK_CHEVRON_STROKE_WIDTH = 3.9375f
private const val MOCK_BACK_CHEVRON_HIT_WIDTH = 336f
private const val MOCK_BACK_CHEVRON_HIT_HEIGHT = 70f
private const val MOCK_BACK_DRAG_THRESHOLD = 64f
private const val LOGO_FINAL_TOP = 11f
private const val LOGO_FINAL_HEIGHT = 428f
private const val LOGO_FINAL_CENTER_Y = LOGO_FINAL_TOP + LOGO_FINAL_HEIGHT / 2f
private const val MOCK_FORM_STACK_HEIGHT = MOCK_FORM_FIELD_HEIGHT * 2f + MOCK_FORM_FIELD_GAP
private const val MOCK_FORM_STACK_CENTER_Y = MOCK_FORM_Y + MOCK_FORM_STACK_HEIGHT / 2f
private const val MOCK_FORM_FOCUS_SHIFT_Y = LOGO_FINAL_CENTER_Y - MOCK_FORM_STACK_CENTER_Y
private const val MOCK_FORM_LANDSCAPE_FOCUS_FIRST_FIELD_Y = MOCK_FORM_TITLE_BASELINE_GAP + MOCK_FORM_TITLE_TEXT_SIZE
private const val MOCK_FORM_LANDSCAPE_FOCUS_SHIFT_Y = MOCK_FORM_LANDSCAPE_FOCUS_FIRST_FIELD_Y - MOCK_FORM_Y

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
    var onIntroSnapshotChanged: ((CinerificIntroSnapshot) -> Unit)? = null
    private var appliedIntroSnapshot = CinerificIntroSnapshot()

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
    private val createAvatarBubbleShell = createAvatarBubbleShellBitmap(steveAvatar)
    private val steveCreateAvatarCharacter = createAvatarCharacterBitmap(steveAvatar)
    private val martinCreateAvatarCharacter = createAvatarCharacterBitmap(martinAvatar, includeDarkSaturatedPixels = true)
    private val jannyCreateAvatarCharacter = createAvatarCharacterBitmap(jannyAvatar)
    private val steveName = decode(R.drawable.steve_name)
    private val martinName = decode(R.drawable.martin_name)
    private val jannyName = decode(R.drawable.janny_name)
    private val guestName = decode(R.drawable.guest_name)
    private val standardLandscapeBackground = decode(R.drawable.signin_background_standard_landscape)
    private val standardPortraitBackground = decode(R.drawable.signin_background_standard_portrait)
    private val membershipTierBasicIcon = decode(R.drawable.membership_tier_basic)
    private val membershipTierProIcon = decode(R.drawable.membership_tier_pro)
    private val membershipTierRockStarIcon = decode(R.drawable.membership_tier_rock_star)
    private val membershipTierBasicArtworkIcon = decode(R.drawable.membership_tier_basic_art)
    private val membershipTierProArtworkIcon = decode(R.drawable.membership_tier_pro_art)
    private val membershipTierRockStarArtworkIcon = decode(R.drawable.membership_tier_rock_star_art)
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()

    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
    private val backgroundPaint = Paint()
    private val accountPromptPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = ResourcesCompat.getFont(context, R.font.manrope)
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
    private val formTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.LEFT
        typeface = ResourcesCompat.getFont(context, R.font.manrope)
    }
    private val formCaretPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }
    private val inputDimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
    }
    private val formFieldFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val formFieldStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val rememberMeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.LEFT
        typeface = ResourcesCompat.getFont(context, R.font.manrope)
    }
    private val rememberMeBoxFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rememberMeBoxStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val rememberMeCheckPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val backChevronPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val tempRect = RectF()
    private val tempIconRect = RectF()
    private val tempPath = Path()
    private val tempWindowRect = Rect()
    private var pressedAvatarProfile: CinerificProfile? = null
    private var clickAvatarProfile: CinerificProfile? = null
    private var pressedCreateAccountPrompt = false
    private var pressedSignInPrompt = false
    private var pressedForgotPasswordPrompt = false
    private var mockSignInStartMillis: Long? = null
    private var activeMockFlow: MockAccountFlow? = null
    private var mockSignInTransitionStartProgress = 0f
    private var mockSignInTransitionTargetProgress = 0f
    private var pressedMockField: MockSignInField? = null
    private var pressedRememberMe = false
    private var pressedBackChevron = false
    private var pressedMockCreateAvatarNav: MockAvatarCarouselDirection? = null
    private var activeMockCreateAvatarDrag = false
    private var mockCreateAvatarDragMoved = false
    private var mockCreateAvatarDragDeltaX = 0f
    private var pressedMockDropdown: MockDropdown? = null
    private var pressedMockDropdownOption: MockDropdownOption? = null
    private var pressedForgotRecoveryTarget: MockForgotRecoveryTarget? = null
    private var pressedForgotPasswordSubmit = false
    private var activeMockDropdownScroll: MockDropdown? = null
    private var mockDropdownScrollStartY = 0f
    private var mockDropdownScrollStartOffset = 0
    private var mockDropdownScrollMoved = false
    private var mockTouchDownX = 0f
    private var mockTouchDownY = 0f
    private var mockDragReturnInProgress = false
    private var focusedMockField: MockSignInField? = null
    private var mockInputDimProgress = 0f
    private var lastMockInputDimAnimationMillis = SystemClock.uptimeMillis()
    private var mockInputDimAwaitingField: MockSignInField? = null
    private var mockInputDimActiveField: MockSignInField? = null
    private var mockInputDimActiveDropdown: MockDropdown? = null
    private var rememberMeChecked = false
    private var usernameText = ""
    private var passwordText = ""
    private var confirmPasswordText = ""
    private var emailText = ""
    private var monthText = MOCK_FORM_MONTH_TEXT
    private var dayText = MOCK_FORM_DAY_TEXT
    private var yearText = MOCK_FORM_YEAR_TEXT
    private var subscriptionTierText = MOCK_FORM_SUBSCRIPTION_TIER_TEXT
    private var forgotRecoveryTarget = MockForgotRecoveryTarget.Password
    private var forgotPasswordSubmissionState = MockForgotPasswordSubmissionState.Idle
    private var forgotPasswordSubmissionStartMillis: Long? = null
    private var mockCreateAvatarIndex = 0
    private var mockCreateAvatarCarouselStartMillis: Long? = null
    private var mockCreateAvatarCarouselFromIndex = 0
    private var mockCreateAvatarCarouselToIndex = 0
    private var mockCreateAvatarCarouselDirection = MockAvatarCarouselDirection.Next
    private var mockCreateAvatarCarouselStartProgress = 0f
    private var expandedMockDropdown: MockDropdown? = null
    private var activeComposingText = ""
    private val mockFieldLabelFloatProgress = MockSignInField.values().associateWith { 0f }.toMutableMap()
    private val mockDropdownScrollOffsets = MockDropdown.values().associateWith { 0 }.toMutableMap()
    private var lastFormLabelAnimationMillis = SystemClock.uptimeMillis()
    private var mockFormFocusProgress = 0f
    private var lastFormFocusAnimationMillis = SystemClock.uptimeMillis()
    private var mockLandscapeInputLiftProgress = 0f
    private var lastMockLandscapeInputLiftAnimationMillis = SystemClock.uptimeMillis()

    init {
        isClickable = true
        isFocusable = true
        isFocusableInTouchMode = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postInvalidateOnAnimation()
    }

    fun restoreIntroSnapshot(snapshot: CinerificIntroSnapshot) {
        if (appliedIntroSnapshot == snapshot) return

        val restoredFlow = enumValueOrNull<MockAccountFlow>(snapshot.activeFlowName)
        val restoredRecoveryTarget = enumValueOrNull<MockForgotRecoveryTarget>(
            snapshot.forgotRecoveryTargetName
        ) ?: MockForgotRecoveryTarget.Password
        val restoredSubmissionState = enumValueOrNull<MockForgotPasswordSubmissionState>(
            snapshot.forgotPasswordSubmissionStateName
        ) ?: MockForgotPasswordSubmissionState.Idle

        pressedAvatarProfile = null
        clickAvatarProfile = null
        pressedCreateAccountPrompt = false
        pressedSignInPrompt = false
        pressedForgotPasswordPrompt = false
        pressedMockField = null
        pressedRememberMe = false
        pressedBackChevron = false
        pressedMockCreateAvatarNav = null
        activeMockCreateAvatarDrag = false
        mockCreateAvatarDragMoved = false
        mockCreateAvatarDragDeltaX = 0f
        pressedMockDropdown = null
        pressedMockDropdownOption = null
        pressedForgotRecoveryTarget = null
        pressedForgotPasswordSubmit = false
        activeMockDropdownScroll = null
        mockDropdownScrollMoved = false
        mockDragReturnInProgress = false
        activeComposingText = ""
        expandedMockDropdown = null
        focusedMockField = null
        mockInputDimAwaitingField = null
        mockInputDimActiveField = null
        mockInputDimActiveDropdown = null

        activeMockFlow = restoredFlow
        if (restoredFlow == null) {
            mockSignInStartMillis = null
            mockSignInTransitionStartProgress = 0f
            mockSignInTransitionTargetProgress = 0f
        } else {
            mockSignInStartMillis = SystemClock.uptimeMillis()
            mockSignInTransitionStartProgress = 1f
            mockSignInTransitionTargetProgress = 1f
        }

        rememberMeChecked = snapshot.rememberMeChecked
        usernameText = snapshot.usernameText
        passwordText = snapshot.passwordText
        confirmPasswordText = snapshot.confirmPasswordText
        emailText = snapshot.emailText
        monthText = snapshot.monthText
        dayText = snapshot.dayText
        yearText = snapshot.yearText
        subscriptionTierText = normalizedMembershipTierSelection(snapshot.subscriptionTierText)
        forgotRecoveryTarget = restoredRecoveryTarget
        forgotPasswordSubmissionState = restoredSubmissionState
        forgotPasswordSubmissionStartMillis = if (
            restoredSubmissionState == MockForgotPasswordSubmissionState.Loading
        ) {
            SystemClock.uptimeMillis()
        } else {
            null
        }

        mockCreateAvatarIndex = snapshot.createAvatarIndex.coerceIn(0, MOCK_CREATE_AVATAR_COUNT - 1)
        mockCreateAvatarCarouselStartMillis = null
        mockCreateAvatarCarouselFromIndex = mockCreateAvatarIndex
        mockCreateAvatarCarouselToIndex = mockCreateAvatarIndex
        mockCreateAvatarCarouselDirection = MockAvatarCarouselDirection.Next
        mockCreateAvatarCarouselStartProgress = 0f
        mockDropdownScrollOffsets.keys.forEach { dropdown ->
            mockDropdownScrollOffsets[dropdown] = 0
        }

        restartMockFormLabelAnimation()
        restartMockFormFocusAnimation()
        restartMockLandscapeInputLiftAnimation()
        restartMockInputDimAnimation()
        appliedIntroSnapshot = currentIntroSnapshot()
        postInvalidateOnAnimation()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        pressedAvatarProfile = null
        clickAvatarProfile = null
        pressedCreateAccountPrompt = false
        pressedSignInPrompt = false
        pressedForgotPasswordPrompt = false
        pressedMockField = null
        pressedRememberMe = false
        pressedBackChevron = false
        pressedMockCreateAvatarNav = null
        activeMockCreateAvatarDrag = false
        mockCreateAvatarDragMoved = false
        pressedMockDropdown = null
        pressedMockDropdownOption = null
        pressedForgotRecoveryTarget = null
        pressedForgotPasswordSubmit = false
        activeMockDropdownScroll = null
        mockDropdownScrollMoved = false
        mockDragReturnInProgress = false
        postInvalidateOnAnimation()
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        postInvalidateOnAnimation()
        return super.onApplyWindowInsets(insets)
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
        finishMockSignInTransitionIfNeeded()
        finishMockCreateAvatarCarouselIfNeeded()
        finishMockForgotPasswordSubmissionIfNeeded()
        val mockProgress = mockSignInProgress()
        updateMockFormFocusAnimation()
        updateMockLandscapeInputLiftAnimation()
        updateMockInputDimAnimation()
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
        if (mockSignInStartMillis != null) {
            drawMockSignInForm(canvas, stage.left, stage.top, stage.scale, mockMotion, formFocusMotion)
        }

        if (
            progress < 1f ||
            isMockSignInAnimating() ||
            isMockFormLabelAnimating() ||
            isMockFormFocusAnimating() ||
            isMockLandscapeInputLiftAnimating() ||
            isMockCreateAvatarCarouselAnimating() ||
            isMockForgotPasswordSubmissionAnimating() ||
            isMockInputDimAnimating()
        ) {
            postInvalidateOnAnimation()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (mockSignInStartMillis != null) {
                    mockTouchDownX = event.x
                    mockTouchDownY = event.y
                    mockDragReturnInProgress = false
                    mockDropdownScrollMoved = false
                    val touchedDropdownOptions = mockDropdownOptionsHit(event.x, event.y)
                    activeMockDropdownScroll = touchedDropdownOptions?.takeIf { mockDropdownCanScroll(it) }
                    mockDropdownScrollStartY = event.y
                    mockDropdownScrollStartOffset = activeMockDropdownScroll?.let { mockDropdownScrollOffset(it) } ?: 0
                    pressedMockDropdownOption = if (touchedDropdownOptions != null) {
                        mockDropdownOptionHit(event.x, event.y)
                    } else {
                        null
                    }
                    pressedMockDropdown = if (pressedMockDropdownOption == null && touchedDropdownOptions == null) {
                        mockDropdownHit(event.x, event.y)
                    } else {
                        null
                    }
                    pressedForgotRecoveryTarget = if (
                        touchedDropdownOptions == null &&
                        pressedMockDropdownOption == null &&
                        pressedMockDropdown == null
                    ) {
                        mockForgotRecoveryTargetHit(event.x, event.y)
                    } else {
                        null
                    }
                    pressedForgotPasswordSubmit = touchedDropdownOptions == null &&
                        pressedMockDropdownOption == null &&
                        pressedMockDropdown == null &&
                        pressedForgotRecoveryTarget == null &&
                        mockForgotPasswordSubmitHit(event.x, event.y)
                    val touchedCreateAvatarCarousel = touchedDropdownOptions == null &&
                        pressedMockDropdownOption == null &&
                        pressedMockDropdown == null &&
                        pressedForgotRecoveryTarget == null &&
                        !pressedForgotPasswordSubmit &&
                        mockCreateAvatarCarouselHit(event.x, event.y) &&
                        !isMockCreateAvatarCarouselAnimating()
                    activeMockCreateAvatarDrag = touchedCreateAvatarCarousel
                    mockCreateAvatarDragMoved = false
                    mockCreateAvatarDragDeltaX = 0f
                    pressedMockCreateAvatarNav = if (
                        touchedDropdownOptions == null &&
                        pressedMockDropdownOption == null &&
                        pressedMockDropdown == null &&
                        pressedForgotRecoveryTarget == null &&
                        !pressedForgotPasswordSubmit &&
                        touchedCreateAvatarCarousel
                    ) {
                        mockCreateAvatarNavHit(event.x, event.y)
                    } else {
                        null
                    }
                    pressedMockField = if (
                        touchedDropdownOptions == null &&
                        pressedMockDropdownOption == null &&
                        pressedMockDropdown == null &&
                        pressedForgotRecoveryTarget == null &&
                        pressedMockCreateAvatarNav == null &&
                        !pressedForgotPasswordSubmit &&
                        !touchedCreateAvatarCarousel
                    ) {
                        mockSignInFieldHit(event.x, event.y)
                    } else {
                        null
                    }
                    pressedRememberMe = touchedDropdownOptions == null &&
                        !touchedCreateAvatarCarousel &&
                        !pressedForgotPasswordSubmit &&
                        pressedMockField == null &&
                        mockRememberMeHit(event.x, event.y)
                    pressedBackChevron = pressedMockField == null &&
                        touchedDropdownOptions == null &&
                        !touchedCreateAvatarCarousel &&
                        !pressedRememberMe &&
                        pressedMockDropdown == null &&
                        pressedMockDropdownOption == null &&
                        pressedForgotRecoveryTarget == null &&
                        pressedMockCreateAvatarNav == null &&
                        !pressedForgotPasswordSubmit &&
                        mockBackChevronHit(event.x, event.y)
                    postInvalidateOnAnimation()
                    return true
                }
                pressedCreateAccountPrompt = settledCreateAccountPromptHit(event.x, event.y)
                pressedSignInPrompt = !pressedCreateAccountPrompt && settledSignInPromptHit(event.x, event.y)
                pressedForgotPasswordPrompt = !pressedCreateAccountPrompt &&
                    !pressedSignInPrompt &&
                    settledForgotPasswordPromptHit(event.x, event.y)
                pressedAvatarProfile = if (
                    pressedCreateAccountPrompt ||
                    pressedSignInPrompt ||
                    pressedForgotPasswordPrompt
                ) {
                    null
                } else {
                    settledAvatarHitProfile(event.x, event.y)
                }
                pressedCreateAccountPrompt ||
                    pressedSignInPrompt ||
                    pressedForgotPasswordPrompt ||
                    pressedAvatarProfile != null
            }
            MotionEvent.ACTION_MOVE -> {
                if (mockSignInStartMillis != null) {
                    if (updateMockCreateAvatarDrag(event)) {
                        return true
                    }
                    if (updateMockDropdownScroll(event)) {
                        return true
                    }
                    if (mockDropdownOptionsHit(mockTouchDownX, mockTouchDownY) == null && shouldStartMockDragReturn(event)) {
                        mockDragReturnInProgress = true
                        closeMockSignInScreen()
                    }
                    return true
                }
                pressedCreateAccountPrompt ||
                    pressedSignInPrompt ||
                    pressedForgotPasswordPrompt ||
                    pressedAvatarProfile != null
            }
            MotionEvent.ACTION_UP -> {
                if (mockDragReturnInProgress) {
                    mockDragReturnInProgress = false
                    pressedMockField = null
                    pressedRememberMe = false
                    pressedBackChevron = false
                    pressedMockCreateAvatarNav = null
                    activeMockCreateAvatarDrag = false
                    mockCreateAvatarDragMoved = false
                    mockCreateAvatarDragDeltaX = 0f
                    pressedMockDropdown = null
                    pressedMockDropdownOption = null
                    pressedForgotRecoveryTarget = null
                    pressedForgotPasswordSubmit = false
                    activeMockDropdownScroll = null
                    mockDropdownScrollMoved = false
                    return true
                }
                if (mockSignInStartMillis != null) {
                    val releasedDropdownOption = mockDropdownOptionHit(event.x, event.y)
                    val releasedDropdown = mockDropdownHit(event.x, event.y)
                    val releasedField = mockSignInFieldHit(event.x, event.y)
                    val releasedRememberMe = mockRememberMeHit(event.x, event.y)
                    val releasedBackChevron = mockBackChevronHit(event.x, event.y)
                    val releasedCreateAvatarNav = mockCreateAvatarNavHit(event.x, event.y)
                    val releasedForgotRecoveryTarget = mockForgotRecoveryTargetHit(event.x, event.y)
                    val releasedForgotPasswordSubmit = mockForgotPasswordSubmitHit(event.x, event.y)
                    val wasTouchingDropdownOptions = activeMockDropdownScroll != null ||
                        mockDropdownOptionsHit(mockTouchDownX, mockTouchDownY) != null
                    val wasScrollingDropdown = mockDropdownScrollMoved
                    val wasPressingRememberMe = pressedRememberMe
                    val wasPressingBackChevron = pressedBackChevron
                    val wasPressingForgotRecoveryTarget = pressedForgotRecoveryTarget != null
                    val wasPressingForgotPasswordSubmit = pressedForgotPasswordSubmit
                    val wasDraggingCreateAvatar = activeMockCreateAvatarDrag
                    val createAvatarDragMoved = mockCreateAvatarDragMoved
                    val createAvatarDragDirection = mockCreateAvatarDragDirection()
                    val createAvatarDragProgress = mockCreateAvatarDragProgress()
                    val pressedDropdown = pressedMockDropdown
                    val pressedOption = pressedMockDropdownOption
                    val pressedCreateAvatarNav = pressedMockCreateAvatarNav
                    val pressedForgotRecoverySelection = pressedForgotRecoveryTarget
                    val shouldSelectDropdownOption = !wasScrollingDropdown &&
                        pressedOption != null &&
                        pressedOption == releasedDropdownOption
                    val shouldSelectForgotRecoveryTarget = pressedForgotRecoverySelection != null &&
                        pressedForgotRecoverySelection == releasedForgotRecoveryTarget
                    val shouldToggleDropdown = pressedDropdown != null && pressedDropdown == releasedDropdown
                    val shouldToggleRememberMe = wasPressingRememberMe && releasedRememberMe
                    val shouldCloseMockSignIn = wasPressingBackChevron && releasedBackChevron
                    val shouldSubmitForgotPassword = wasPressingForgotPasswordSubmit && releasedForgotPasswordSubmit
                    val shouldNavigateCreateAvatar = pressedCreateAvatarNav != null &&
                        !createAvatarDragMoved &&
                        pressedCreateAvatarNav == releasedCreateAvatarNav
                    val shouldSwipeCreateAvatar = wasDraggingCreateAvatar &&
                        createAvatarDragMoved &&
                        createAvatarDragDirection != null &&
                        createAvatarDragProgress >= MOCK_CREATE_AVATAR_DRAG_COMMIT_PROGRESS
                    val shouldFocusField = releasedField != null &&
                        !wasTouchingDropdownOptions &&
                        !wasPressingRememberMe &&
                        !wasPressingBackChevron &&
                        !wasPressingForgotRecoveryTarget &&
                        !wasPressingForgotPasswordSubmit &&
                        !wasDraggingCreateAvatar &&
                        pressedDropdown == null &&
                        pressedOption == null &&
                        pressedCreateAvatarNav == null &&
                        (pressedMockField == null || pressedMockField == releasedField)
                    pressedMockField = null
                    pressedRememberMe = false
                    pressedBackChevron = false
                    pressedMockCreateAvatarNav = null
                    activeMockCreateAvatarDrag = false
                    mockCreateAvatarDragMoved = false
                    mockCreateAvatarDragDeltaX = 0f
                    pressedMockDropdown = null
                    pressedMockDropdownOption = null
                    this.pressedForgotRecoveryTarget = null
                    pressedForgotPasswordSubmit = false
                    activeMockDropdownScroll = null
                    mockDropdownScrollMoved = false
                    super.performClick()
                    if (shouldCloseMockSignIn) {
                        closeMockSignInScreen()
                    } else if (shouldSwipeCreateAvatar && createAvatarDragDirection != null) {
                        expandedMockDropdown = null
                        clearMockSignInFieldFocus()
                        startMockCreateAvatarCarousel(createAvatarDragDirection, createAvatarDragProgress)
                    } else if (shouldNavigateCreateAvatar && pressedCreateAvatarNav != null) {
                        expandedMockDropdown = null
                        clearMockSignInFieldFocus()
                        startMockCreateAvatarCarousel(pressedCreateAvatarNav)
                    } else if (wasDraggingCreateAvatar) {
                        postInvalidateOnAnimation()
                    } else if (shouldSelectDropdownOption && pressedOption != null) {
                        setMockDropdownValue(pressedOption)
                        expandedMockDropdown = null
                        restartMockLandscapeInputLiftAnimation()
                        restartMockInputDimAnimation()
                        clearMockSignInFieldFocus()
                        postInvalidateOnAnimation()
                    } else if (shouldToggleDropdown && pressedDropdown != null) {
                        expandedMockDropdown = if (expandedMockDropdown == pressedDropdown) null else pressedDropdown
                        expandedMockDropdown?.let { prepareMockDropdownScroll(it) }
                        restartMockLandscapeInputLiftAnimation()
                        restartMockInputDimAnimation()
                        clearMockSignInFieldFocus()
                        postInvalidateOnAnimation()
                    } else if (shouldToggleRememberMe) {
                        rememberMeChecked = !rememberMeChecked
                        notifyIntroSnapshotChanged()
                        postInvalidateOnAnimation()
                    } else if (shouldSelectForgotRecoveryTarget && pressedForgotRecoverySelection != null) {
                        setMockForgotRecoveryTarget(pressedForgotRecoverySelection)
                    } else if (shouldSubmitForgotPassword) {
                        startMockForgotPasswordSubmission()
                    } else if (shouldFocusField && releasedField != null) {
                        expandedMockDropdown = null
                        restartMockLandscapeInputLiftAnimation()
                        restartMockInputDimAnimation()
                        focusMockSignInField(releasedField)
                    } else if (wasTouchingDropdownOptions || wasScrollingDropdown) {
                        postInvalidateOnAnimation()
                    } else if (!wasPressingRememberMe && !wasPressingBackChevron && !wasPressingForgotRecoveryTarget) {
                        if (expandedMockDropdown != null && activeMockFlow == MockAccountFlow.CreateAccount) {
                            expandedMockDropdown = null
                            restartMockLandscapeInputLiftAnimation()
                            restartMockInputDimAnimation()
                        }
                        clearMockSignInFieldFocus()
                    } else {
                        postInvalidateOnAnimation()
                    }
                    return true
                }
                val shouldOpenMockCreateAccount = pressedCreateAccountPrompt &&
                    settledCreateAccountPromptHit(event.x, event.y)
                val shouldOpenMockSignIn = pressedSignInPrompt && settledSignInPromptHit(event.x, event.y)
                val shouldOpenMockForgotPassword = pressedForgotPasswordPrompt &&
                    settledForgotPasswordPromptHit(event.x, event.y)
                val releasedAvatarProfile = settledAvatarHitProfile(event.x, event.y)
                val shouldNavigate = pressedAvatarProfile != null && pressedAvatarProfile == releasedAvatarProfile
                clickAvatarProfile = pressedAvatarProfile
                pressedAvatarProfile = null
                pressedCreateAccountPrompt = false
                pressedSignInPrompt = false
                pressedForgotPasswordPrompt = false
                if (shouldOpenMockCreateAccount) {
                    super.performClick()
                    openMockCreateAccountScreen()
                    true
                } else if (shouldOpenMockSignIn) {
                    super.performClick()
                    openMockSignInScreen()
                    true
                } else if (shouldOpenMockForgotPassword) {
                    super.performClick()
                    openMockForgotPasswordScreen()
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
                pressedCreateAccountPrompt = false
                pressedSignInPrompt = false
                pressedForgotPasswordPrompt = false
                pressedMockField = null
                pressedRememberMe = false
                pressedBackChevron = false
                pressedMockCreateAvatarNav = null
                activeMockCreateAvatarDrag = false
                mockCreateAvatarDragMoved = false
                mockCreateAvatarDragDeltaX = 0f
                pressedMockDropdown = null
                pressedMockDropdownOption = null
                pressedForgotRecoveryTarget = null
                pressedForgotPasswordSubmit = false
                activeMockDropdownScroll = null
                mockDropdownScrollMoved = false
                mockDragReturnInProgress = false
                false
            }
            else -> pressedCreateAccountPrompt ||
                pressedSignInPrompt ||
                pressedForgotPasswordPrompt ||
                pressedAvatarProfile != null ||
                mockSignInStartMillis != null
        }
    }

    override fun onCheckIsTextEditor(): Boolean {
        return mockSignInStartMillis != null && !isMockSignInClosing()
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val field = focusedMockField ?: return null
        outAttrs.inputType = when (field) {
            MockSignInField.Password,
            MockSignInField.ConfirmPassword -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            MockSignInField.Email -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            MockSignInField.Month,
            MockSignInField.Day,
            MockSignInField.Year -> InputType.TYPE_CLASS_NUMBER
            else -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        }
        outAttrs.imeOptions = if (field == activeMockFields().lastOrNull()) {
            EditorInfo.IME_ACTION_DONE
        } else {
            EditorInfo.IME_ACTION_NEXT
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
        val paintAlpha = (alpha.coerceIn(0f, 1f) * 255f).roundToInt()
        accountPromptPaint.textSize = textSize
        formTitlePaint.textSize = textSize
        accountPromptPaint.alpha = paintAlpha
        formTitlePaint.alpha = paintAlpha

        val accountPromptShiftY = if (isLandscape) SIGN_IN_LANDSCAPE_ACCOUNT_PROMPT_EXTRA_SHIFT_Y else 0f
        val centerY = stageTop +
            (ACCOUNT_PROMPT_CENTER_Y + signInStackShiftY() + accountPromptShiftY + yOffset) * stageScale
        val secondaryMetrics = accountPromptPaint.fontMetrics
        val primaryMetrics = formTitlePaint.fontMetrics
        val secondaryBaselineY = centerY - (secondaryMetrics.ascent + secondaryMetrics.descent) / 2f
        val primaryBaselineY = centerY - (primaryMetrics.ascent + primaryMetrics.descent) / 2f
        if (activeMockFlow != MockAccountFlow.CreateAccount) {
            accountPromptPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(
                ACCOUNT_PROMPT_CREATE_TEXT,
                stageLeft + accountPromptCreateCenterX() * stageScale,
                secondaryBaselineY,
                accountPromptPaint
            )
        }
        if (activeMockFlow != MockAccountFlow.SignIn) {
            formTitlePaint.textAlign = Paint.Align.CENTER
            canvas.drawText(
                ACCOUNT_PROMPT_SIGN_IN_TEXT,
                stageLeft + ACCOUNT_PROMPT_SIGN_IN_CENTER_X * stageScale,
                primaryBaselineY,
                formTitlePaint
            )
        }
        if (activeMockFlow != MockAccountFlow.ForgotPassword) {
            accountPromptPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(
                ACCOUNT_PROMPT_FORGOT_TEXT,
                stageLeft + accountPromptForgotCenterX() * stageScale,
                secondaryBaselineY,
                accountPromptPaint
            )
        }
        accountPromptPaint.alpha = 255
        accountPromptPaint.textAlign = Paint.Align.CENTER
        formTitlePaint.alpha = 255
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
        if (activeMockFlow == MockAccountFlow.CreateAccount) {
            drawMockCreateAccountForm(canvas, stageLeft, stageTop, stageScale, alpha, formFocusMotion)
            return
        }
        if (activeMockFlow == MockAccountFlow.ForgotPassword) {
            drawMockForgotPasswordForm(canvas, stageLeft, stageTop, stageScale, alpha, formFocusMotion)
            return
        }

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
        val rememberMeY = passwordY + MOCK_FORM_FIELD_HEIGHT + MOCK_REMEMBER_ME_TOP_GAP + focusShiftY
        drawMockSignInTitle(canvas, usernameY + focusShiftY, stageLeft, stageTop, stageScale, alpha)
        drawMockSignInField(canvas, MockSignInField.Username, fieldX, usernameY + focusShiftY, stageLeft, stageTop, stageScale, alpha)
        drawMockSignInField(canvas, MockSignInField.Password, fieldX, passwordY + focusShiftY, stageLeft, stageTop, stageScale, alpha)
        drawRememberMeCheckbox(
            canvas,
            fieldX,
            rememberMeY,
            stageLeft,
            stageTop,
            stageScale,
            alpha
        )
        drawBackChevron(
            canvas,
            fieldX + MOCK_FORM_WIDTH / 2f,
            rememberMeY + MOCK_REMEMBER_ME_BOX_SIZE + MOCK_BACK_CHEVRON_TOP_GAP,
            stageLeft,
            stageTop,
            stageScale,
            alpha
        )
        drawMockInputDimOverlayAndActiveControl(
            canvas,
            stageLeft,
            stageTop,
            stageScale,
            alpha,
            yOffset,
            focusShiftY
        )

        formLabelPaint.alpha = 255
        formInputPaint.alpha = 255
        formTitlePaint.alpha = 255
        formCaretPaint.alpha = 255
    }

    private fun drawMockCreateAccountForm(
        canvas: Canvas,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float,
        formFocusMotion: Float
    ) {
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - alpha)
        val fieldFillAlpha = (alpha * 26f).roundToInt()
        val fieldStrokeAlpha = (alpha * 185f).roundToInt()
        val fieldX = mockCreateFormX()
        val focusShiftY = mockFormFocusShiftY(formFocusMotion)

        formFieldFillPaint.color = Color.argb(fieldFillAlpha, 255, 255, 255)
        formFieldStrokePaint.color = Color.argb(fieldStrokeAlpha, 255, 255, 255)
        formFieldStrokePaint.strokeWidth = MOCK_FORM_FIELD_STROKE_WIDTH * stageScale
        formCaretPaint.strokeWidth = 1.45f * stageScale

        val firstFieldY = MOCK_FORM_Y + yOffset + focusShiftY
        drawMockCreateAccountTitle(canvas, firstFieldY, stageLeft, stageTop, stageScale, alpha)
        activeMockFields().forEach { field ->
            drawMockSignInField(
                canvas,
                field,
                mockActiveFieldX(field),
                mockActiveFieldY(field, yOffset, focusShiftY),
                stageLeft,
                stageTop,
                stageScale,
                alpha,
                mockActiveFieldWidth(field)
            )
        }
        drawMockDropdownControl(
            canvas,
            MockDropdown.SubscriptionTier,
            mockDropdownX(MockDropdown.SubscriptionTier),
            mockDropdownY(MockDropdown.SubscriptionTier, yOffset, focusShiftY),
            stageLeft,
            stageTop,
            stageScale,
            alpha
        )
        listOf(MockDropdown.Month, MockDropdown.Day, MockDropdown.Year).forEach { dropdown ->
            drawMockDropdownControl(
                canvas,
                dropdown,
                mockDropdownX(dropdown),
                mockDropdownY(dropdown, yOffset, focusShiftY),
                stageLeft,
                stageTop,
                stageScale,
                alpha,
                mockDropdownWidth(dropdown)
            )
        }
        drawMockCreateAccountAvatarText(
            canvas,
            fieldX + (MOCK_CREATE_FORM_COLUMNS - 1) * (MOCK_FORM_WIDTH + MOCK_CREATE_FORM_COLUMN_GAP),
            firstFieldY,
            stageLeft,
            stageTop,
            stageScale,
            alpha
        )
        drawMockDropdownOptions(canvas, stageLeft, stageTop, stageScale, alpha)
        drawBackChevron(
            canvas,
            fieldX + mockCreateFormWidth() / 2f,
            firstFieldY + mockCreateFormHeight() + MOCK_BACK_CHEVRON_TOP_GAP,
            stageLeft,
            stageTop,
            stageScale,
            alpha
        )
        drawMockInputDimOverlayAndActiveControl(
            canvas,
            stageLeft,
            stageTop,
            stageScale,
            alpha,
            yOffset,
            focusShiftY
        )

        formLabelPaint.alpha = 255
        formInputPaint.alpha = 255
        formTitlePaint.alpha = 255
        formCaretPaint.alpha = 255
    }

    private fun drawMockForgotPasswordForm(
        canvas: Canvas,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float,
        formFocusMotion: Float
    ) {
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - alpha)
        val fieldFillAlpha = (alpha * 26f).roundToInt()
        val fieldStrokeAlpha = (alpha * 185f).roundToInt()
        val fieldX = mockForgotPasswordFormX()
        val copyX = mockForgotPasswordCopyX()
        val formWidth = mockForgotPasswordFormWidth()
        val focusShiftY = mockFormFocusShiftY(formFocusMotion)
        val formTopY = MOCK_FORM_Y + yOffset + focusShiftY
        val emailY = mockForgotPasswordFieldY(yOffset, focusShiftY)
        val buttonY = mockForgotPasswordButtonY(yOffset, focusShiftY)
        val helpTopY = buttonY + MOCK_FORGOT_PASSWORD_BUTTON_HEIGHT + MOCK_FORGOT_PASSWORD_HELP_TOP_GAP

        formFieldFillPaint.color = Color.argb(fieldFillAlpha, 255, 255, 255)
        formFieldStrokePaint.color = Color.argb(fieldStrokeAlpha, 255, 255, 255)
        formFieldStrokePaint.strokeWidth = MOCK_FORM_FIELD_STROKE_WIDTH * stageScale
        formCaretPaint.strokeWidth = 1.45f * stageScale

        drawMockForgotPasswordTitle(canvas, formTopY, stageLeft, stageTop, stageScale, alpha)
        drawMockForgotPasswordBodyParagraph(
            canvas,
            mockForgotPasswordBodyText(),
            copyX + MOCK_FORGOT_PASSWORD_COPY_WIDTH / 2f,
            formTopY + MOCK_FORGOT_PASSWORD_BODY_TOP_GAP,
            MOCK_FORGOT_PASSWORD_COPY_WIDTH,
            stageLeft,
            stageTop,
            stageScale,
            alpha
        )
        drawMockForgotRecoverySelector(
            canvas,
            fieldX,
            mockForgotRecoverySelectorY(yOffset, focusShiftY),
            stageLeft,
            stageTop,
            stageScale,
            alpha,
            formWidth
        )
        drawMockSignInField(
            canvas,
            MockSignInField.Email,
            fieldX,
            emailY,
            stageLeft,
            stageTop,
            stageScale,
            alpha,
            formWidth
        )
        drawMockForgotPasswordButton(
            canvas,
            fieldX,
            buttonY,
            stageLeft,
            stageTop,
            stageScale,
            alpha,
            formWidth
        )
        drawMockFormParagraph(
            canvas,
            mockForgotPasswordHelpText(),
            fieldX + formWidth / 2f,
            helpTopY,
            formWidth,
            stageLeft,
            stageTop,
            stageScale,
            alpha,
            MOCK_FORGOT_PASSWORD_HELP_TEXT_SIZE,
            MOCK_FORGOT_PASSWORD_HELP_LINE_HEIGHT
        )
        drawBackChevron(
            canvas,
            mockActiveFormCenterX(),
            mockForgotPasswordBackChevronTopY(yOffset, focusShiftY),
            stageLeft,
            stageTop,
            stageScale,
            alpha
        )
        drawMockInputDimOverlayAndActiveControl(
            canvas,
            stageLeft,
            stageTop,
            stageScale,
            alpha,
            yOffset,
            focusShiftY
        )

        formLabelPaint.alpha = 255
        formInputPaint.alpha = 255
        formTitlePaint.alpha = 255
        formCaretPaint.alpha = 255
    }

    private fun drawMockInputDimOverlayAndActiveControl(
        canvas: Canvas,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float,
        yOffset: Float,
        focusShiftY: Float
    ) {
        val dimProgress = mockInputDimProgress.coerceIn(0f, 1f)
        if (dimProgress <= 0.01f) return

        inputDimPaint.alpha = (dimProgress * MOCK_INPUT_DIM_ALPHA * 255f).roundToInt()
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), inputDimPaint)
        inputDimPaint.alpha = 255

        formFieldFillPaint.color = Color.argb((alpha * 26f).roundToInt(), 255, 255, 255)
        formFieldStrokePaint.color = Color.argb((alpha * 185f).roundToInt(), 255, 255, 255)
        formFieldStrokePaint.strokeWidth = MOCK_FORM_FIELD_STROKE_WIDTH * stageScale
        formCaretPaint.strokeWidth = 1.45f * stageScale

        val activeDropdown = expandedMockDropdown ?: mockInputDimActiveDropdown
        if (activeDropdown != null && activeMockFlow == MockAccountFlow.CreateAccount) {
            drawMockDropdownControl(
                canvas,
                activeDropdown,
                mockDropdownX(activeDropdown),
                mockDropdownY(activeDropdown, yOffset, focusShiftY),
                stageLeft,
                stageTop,
                stageScale,
                alpha,
                mockDropdownWidth(activeDropdown)
            )
            if (expandedMockDropdown == activeDropdown) {
                drawMockDropdownOptions(canvas, stageLeft, stageTop, stageScale, alpha)
            }
            return
        }

        val activeField = focusedMockField ?: mockInputDimActiveField ?: return
        drawMockSignInField(
            canvas,
            activeField,
            mockActiveFieldX(activeField),
            mockActiveFieldY(activeField, yOffset, focusShiftY),
            stageLeft,
            stageTop,
            stageScale,
            alpha,
            mockActiveFieldWidth(activeField)
        )
    }

    private fun drawMockSignInTitle(
        canvas: Canvas,
        fieldTopY: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float
    ) {
        val motion = alpha.coerceIn(0f, 1f)
        val sourceTextSize = signInAccountPromptTextSize() * stageScale
        val targetTextSize = MOCK_FORM_TITLE_TEXT_SIZE * stageScale
        val sourceCenterX = stageLeft + ACCOUNT_PROMPT_SIGN_IN_CENTER_X * stageScale
        val targetCenterX = stageLeft + ACCOUNT_PROMPT_SIGN_IN_CENTER_X * stageScale
        val sourceCenterY = stageTop +
            (ACCOUNT_PROMPT_CENTER_Y + signInStackShiftY() + signInAccountPromptExtraShiftY()) * stageScale
        formTitlePaint.textSize = sourceTextSize
        val sourceMetrics = formTitlePaint.fontMetrics
        val sourceBaselineY = sourceCenterY - (sourceMetrics.ascent + sourceMetrics.descent) / 2f
        val targetBaselineY = stageTop + (fieldTopY - MOCK_FORM_TITLE_BASELINE_GAP) * stageScale

        formTitlePaint.textSize = lerpFloat(sourceTextSize, targetTextSize, motion)
        formTitlePaint.alpha = 255
        formTitlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            ACCOUNT_PROMPT_SIGN_IN_TEXT,
            lerpFloat(sourceCenterX, targetCenterX, motion),
            lerpFloat(sourceBaselineY, targetBaselineY, motion),
            formTitlePaint
        )
    }

    private fun drawMockCreateAccountTitle(
        canvas: Canvas,
        fieldTopY: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float
    ) {
        val motion = alpha.coerceIn(0f, 1f)
        val sourceTextSize = signInAccountPromptTextSize() * stageScale
        val targetTextSize = MOCK_FORM_TITLE_TEXT_SIZE * stageScale
        val sourceCenterX = stageLeft + accountPromptCreateCenterX() * stageScale
        val sourceCenterY = stageTop +
            (ACCOUNT_PROMPT_CENTER_Y + signInStackShiftY() + signInAccountPromptExtraShiftY()) * stageScale
        formTitlePaint.textSize = sourceTextSize
        val sourceMetrics = formTitlePaint.fontMetrics
        val sourceBaselineY = sourceCenterY - (sourceMetrics.ascent + sourceMetrics.descent) / 2f
        val targetBaselineY = stageTop + (fieldTopY - MOCK_FORM_TITLE_BASELINE_GAP) * stageScale
        val targetCenterX = stageLeft + ACCOUNT_PROMPT_SIGN_IN_CENTER_X * stageScale

        formTitlePaint.textSize = lerpFloat(sourceTextSize, targetTextSize, motion)
        formTitlePaint.alpha = 255
        formTitlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            ACCOUNT_PROMPT_CREATE_TEXT,
            lerpFloat(sourceCenterX, targetCenterX, motion),
            lerpFloat(sourceBaselineY, targetBaselineY, motion),
            formTitlePaint
        )
    }

    private fun drawMockForgotPasswordTitle(
        canvas: Canvas,
        formTopY: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float
    ) {
        val motion = alpha.coerceIn(0f, 1f)
        val sourceTextSize = signInAccountPromptTextSize() * stageScale
        val targetTextSize = MOCK_FORM_TITLE_TEXT_SIZE * stageScale
        val sourceCenterX = stageLeft + accountPromptForgotCenterX() * stageScale
        val sourceCenterY = stageTop +
            (ACCOUNT_PROMPT_CENTER_Y + signInStackShiftY() + signInAccountPromptExtraShiftY()) * stageScale
        formTitlePaint.textSize = sourceTextSize
        val sourceMetrics = formTitlePaint.fontMetrics
        val sourceBaselineY = sourceCenterY - (sourceMetrics.ascent + sourceMetrics.descent) / 2f
        val targetBaselineY = stageTop + (formTopY - MOCK_FORM_TITLE_BASELINE_GAP) * stageScale
        val targetCenterX = stageLeft + ACCOUNT_PROMPT_SIGN_IN_CENTER_X * stageScale

        formTitlePaint.textSize = lerpFloat(sourceTextSize, targetTextSize, motion)
        formTitlePaint.alpha = 255
        formTitlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            ACCOUNT_PROMPT_FORGOT_TEXT,
            lerpFloat(sourceCenterX, targetCenterX, motion),
            lerpFloat(sourceBaselineY, targetBaselineY, motion),
            formTitlePaint
        )
    }

    private fun drawMockForgotPasswordButton(
        canvas: Canvas,
        x: Float,
        y: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float,
        buttonWidth: Float = MOCK_FORM_WIDTH
    ) {
        tempRect.set(
            stageLeft + x * stageScale,
            stageTop + y * stageScale,
            stageLeft + (x + buttonWidth) * stageScale,
            stageTop + (y + MOCK_FORGOT_PASSWORD_BUTTON_HEIGHT) * stageScale
        )
        val radius = MOCK_FORGOT_PASSWORD_BUTTON_RADIUS * stageScale
        val submissionState = forgotPasswordSubmissionState
        val enabled = mockForgotPasswordCanSubmit()
        val pressed = pressedForgotPasswordSubmit && enabled
        val fillAlpha = when {
            submissionState == MockForgotPasswordSubmissionState.Sent -> 86f
            submissionState == MockForgotPasswordSubmissionState.Loading -> 72f
            pressed -> 92f
            enabled -> 68f
            else -> 18f
        }
        val strokeAlpha = when {
            submissionState == MockForgotPasswordSubmissionState.Sent -> 245f
            submissionState == MockForgotPasswordSubmissionState.Loading -> 228f
            pressed -> 238f
            enabled -> 210f
            else -> 118f
        }
        formFieldFillPaint.shader = null
        formFieldFillPaint.color = Color.argb((alpha * fillAlpha).roundToInt(), 255, 255, 255)
        formFieldStrokePaint.color = Color.argb((alpha * strokeAlpha).roundToInt(), 255, 255, 255)
        formFieldStrokePaint.strokeWidth = MOCK_FORM_FIELD_STROKE_WIDTH * stageScale
        canvas.drawRoundRect(tempRect, radius, radius, formFieldFillPaint)
        canvas.drawRoundRect(tempRect, radius, radius, formFieldStrokePaint)
        if (submissionState == MockForgotPasswordSubmissionState.Loading) {
            drawMockForgotPasswordButtonShimmer(canvas, tempRect, radius, alpha)
        }

        formTitlePaint.textSize = MOCK_REMEMBER_ME_TEXT_SIZE * stageScale
        formTitlePaint.textAlign = Paint.Align.CENTER
        val metrics = formTitlePaint.fontMetrics
        val baselineY = tempRect.centerY() - (metrics.ascent + metrics.descent) / 2f
        val maxTextWidth = buttonWidth * stageScale - MOCK_FORM_LABEL_INSET_X * stageScale * 2f
        val showCheck = submissionState == MockForgotPasswordSubmissionState.Sent
        val checkSize = if (showCheck) MOCK_FORGOT_PASSWORD_SENT_CHECK_SIZE * stageScale else 0f
        val checkGap = if (showCheck) MOCK_FORGOT_PASSWORD_SENT_CHECK_GAP * stageScale else 0f
        val textMaxWidth = (maxTextWidth - checkSize - checkGap).coerceAtLeast(maxTextWidth * 0.5f)
        val buttonText = trailingFittingText(
            mockForgotPasswordButtonText(),
            if (showCheck) textMaxWidth else maxTextWidth,
            formTitlePaint
        )
        val textAlpha = (alpha.coerceIn(0f, 1f) * when {
            submissionState == MockForgotPasswordSubmissionState.Loading -> 226f
            submissionState == MockForgotPasswordSubmissionState.Sent -> 255f
            enabled -> 255f
            else -> 138f
        }).roundToInt()
        formTitlePaint.alpha = textAlpha
        val textX = if (showCheck) {
            val textWidth = formTitlePaint.measureText(buttonText)
            val groupWidth = checkSize + checkGap + textWidth
            val groupLeft = tempRect.centerX() - groupWidth / 2f
            val checkLeft = groupLeft
            val checkCenterY = tempRect.centerY()
            backChevronPaint.alpha = textAlpha
            backChevronPaint.strokeWidth = 2.1f * stageScale
            canvas.drawLine(
                checkLeft + checkSize * 0.18f,
                checkCenterY + checkSize * 0.03f,
                checkLeft + checkSize * 0.42f,
                checkCenterY + checkSize * 0.26f,
                backChevronPaint
            )
            canvas.drawLine(
                checkLeft + checkSize * 0.42f,
                checkCenterY + checkSize * 0.26f,
                checkLeft + checkSize * 0.84f,
                checkCenterY - checkSize * 0.28f,
                backChevronPaint
            )
            groupLeft + checkSize + checkGap + textWidth / 2f
        } else {
            tempRect.centerX()
        }
        canvas.drawText(
            buttonText,
            textX,
            baselineY,
            formTitlePaint
        )
    }

    private fun drawMockForgotPasswordButtonShimmer(
        canvas: Canvas,
        buttonRect: RectF,
        radius: Float,
        alpha: Float
    ) {
        val progress = mockForgotPasswordSubmissionProgress()
        val sweepWidth = buttonRect.width() * 0.44f
        val sweepCenterX = buttonRect.left + buttonRect.width() * (progress * 1.35f - 0.18f)
        val sweepLeft = sweepCenterX - sweepWidth / 2f
        val sweepRight = sweepCenterX + sweepWidth / 2f
        formFieldFillPaint.shader = LinearGradient(
            sweepLeft,
            buttonRect.top,
            sweepRight,
            buttonRect.top,
            intArrayOf(
                Color.argb(0, 255, 255, 255),
                Color.argb((alpha.coerceIn(0f, 1f) * 54f).roundToInt(), 255, 255, 255),
                Color.argb(0, 255, 255, 255)
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.save()
        tempPath.reset()
        tempPath.addRoundRect(buttonRect, radius, radius, Path.Direction.CW)
        canvas.clipPath(tempPath)
        canvas.drawRect(sweepLeft, buttonRect.top, sweepRight, buttonRect.bottom, formFieldFillPaint)
        canvas.restore()
        formFieldFillPaint.shader = null
    }

    private fun drawMockForgotRecoverySelector(
        canvas: Canvas,
        x: Float,
        y: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float,
        formWidth: Float = MOCK_FORM_WIDTH
    ) {
        formLabelPaint.textSize = MOCK_FORGOT_PASSWORD_SELECTOR_LABEL_TEXT_SIZE * stageScale
        formLabelPaint.alpha = (alpha.coerceIn(0f, 1f) * 255f).roundToInt()
        formLabelPaint.textAlign = Paint.Align.LEFT

        val labelWidth = formLabelPaint.measureText(MOCK_FORGOT_PASSWORD_SELECTOR_TEXT)
        formInputPaint.textSize = MOCK_FORGOT_PASSWORD_SELECTOR_OPTION_TEXT_SIZE * stageScale
        val optionBoxWidth = mockForgotRecoverySelectorBoxWidth(
            usernameTextWidth = formInputPaint.measureText(MockForgotRecoveryTarget.Username.displayText),
            passwordTextWidth = formInputPaint.measureText(MockForgotRecoveryTarget.Password.displayText),
            baseBoxWidth = MOCK_FORGOT_PASSWORD_SELECTOR_BOX_WIDTH * stageScale
        )
        val layout = mockForgotRecoverySelectorLayout(
            left = stageLeft + x * stageScale,
            width = formWidth * stageScale,
            labelWidth = labelWidth,
            boxWidth = optionBoxWidth
        )
        val rowTop = stageTop + y * stageScale
        val rowCenterY = rowTop + MOCK_FORGOT_PASSWORD_SELECTOR_BOX_HEIGHT * stageScale / 2f
        val labelMetrics = formLabelPaint.fontMetrics
        canvas.drawText(
            MOCK_FORGOT_PASSWORD_SELECTOR_TEXT,
            layout.labelX,
            rowCenterY - (labelMetrics.ascent + labelMetrics.descent) / 2f,
            formLabelPaint
        )

        drawMockForgotRecoveryOption(
            canvas,
            MockForgotRecoveryTarget.Username,
            layout.usernameBoxX,
            rowTop,
            layout.boxWidth,
            stageScale,
            alpha
        )
        drawMockForgotRecoveryOption(
            canvas,
            MockForgotRecoveryTarget.Password,
            layout.passwordBoxX,
            rowTop,
            layout.boxWidth,
            stageScale,
            alpha
        )
    }

    private fun drawMockForgotRecoveryOption(
        canvas: Canvas,
        target: MockForgotRecoveryTarget,
        left: Float,
        top: Float,
        boxWidth: Float,
        stageScale: Float,
        alpha: Float
    ) {
        val right = left + boxWidth
        val bottom = top + MOCK_FORGOT_PASSWORD_SELECTOR_BOX_HEIGHT * stageScale
        tempRect.set(left, top, right, bottom)
        val selected = forgotRecoveryTarget == target
        val pressed = pressedForgotRecoveryTarget == target
        val fillAlpha = (alpha * when {
            pressed -> 82f
            selected -> 64f
            else -> 14f
        }).roundToInt()
        val strokeAlpha = (alpha * when {
            pressed -> 245f
            selected -> 235f
            else -> 142f
        }).roundToInt()
        val radius = MOCK_FORM_FIELD_RADIUS * stageScale

        formFieldFillPaint.color = Color.argb(fillAlpha, 255, 255, 255)
        formFieldStrokePaint.color = Color.argb(strokeAlpha, 255, 255, 255)
        formFieldStrokePaint.strokeWidth = MOCK_FORM_FIELD_STROKE_WIDTH * stageScale
        canvas.drawRoundRect(tempRect, radius, radius, formFieldFillPaint)
        canvas.drawRoundRect(tempRect, radius, radius, formFieldStrokePaint)

        formInputPaint.textSize = MOCK_FORGOT_PASSWORD_SELECTOR_OPTION_TEXT_SIZE * stageScale
        formInputPaint.alpha = (alpha.coerceIn(0f, 1f) * if (selected || pressed) 255f else 178f).roundToInt()
        formInputPaint.textAlign = Paint.Align.CENTER
        val metrics = formInputPaint.fontMetrics
        canvas.drawText(
            target.displayText,
            tempRect.centerX(),
            tempRect.centerY() - (metrics.ascent + metrics.descent) / 2f,
            formInputPaint
        )
    }

    private fun drawMockForgotPasswordBodyParagraph(
        canvas: Canvas,
        text: String,
        centerX: Float,
        topY: Float,
        maxWidth: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float
    ): Float {
        val paragraphs = text.split(Regex("\\n\\s*\\n"))
        val leadBottomY = drawMockFormParagraph(
            canvas,
            paragraphs.firstOrNull().orEmpty(),
            centerX,
            topY,
            maxWidth,
            stageLeft,
            stageTop,
            stageScale,
            alpha,
            MOCK_FORGOT_PASSWORD_BODY_LEAD_TEXT_SIZE,
            MOCK_FORGOT_PASSWORD_BODY_LEAD_LINE_HEIGHT
        )
        val middleText = paragraphs.getOrNull(1) ?: return leadBottomY
        val middleBottomY = drawMockFormParagraph(
            canvas,
            middleText,
            centerX,
            leadBottomY + MOCK_FORGOT_PASSWORD_BODY_LINE_HEIGHT,
            maxWidth,
            stageLeft,
            stageTop,
            stageScale,
            alpha,
            MOCK_FORGOT_PASSWORD_BODY_MIDDLE_TEXT_SIZE,
            MOCK_FORGOT_PASSWORD_BODY_MIDDLE_LINE_HEIGHT,
            Paint.Align.LEFT
        )
        val bottomText = paragraphs.drop(2).joinToString("\n\n")
        if (bottomText.isEmpty()) return middleBottomY
        return drawMockFormParagraph(
            canvas,
            bottomText,
            centerX,
            middleBottomY + MOCK_FORGOT_PASSWORD_BODY_LINE_HEIGHT,
            maxWidth,
            stageLeft,
            stageTop,
            stageScale,
            alpha,
            MOCK_FORGOT_PASSWORD_BODY_MIDDLE_TEXT_SIZE,
            MOCK_FORGOT_PASSWORD_BODY_MIDDLE_LINE_HEIGHT,
            Paint.Align.LEFT
        )
    }

    private fun drawMockFormParagraph(
        canvas: Canvas,
        text: String,
        centerX: Float,
        topY: Float,
        maxWidth: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float,
        textSize: Float,
        lineHeight: Float,
        textAlign: Paint.Align = Paint.Align.CENTER
    ): Float {
        formInputPaint.textSize = textSize * stageScale
        formInputPaint.alpha = (alpha.coerceIn(0f, 1f) * 218f).roundToInt()
        formInputPaint.textAlign = textAlign

        val lines = wrappedTextLines(text, maxWidth * stageScale, formInputPaint)
        val metrics = formInputPaint.fontMetrics
        val x = when (textAlign) {
            Paint.Align.LEFT -> stageLeft + (centerX - maxWidth / 2f) * stageScale
            Paint.Align.RIGHT -> stageLeft + (centerX + maxWidth / 2f) * stageScale
            Paint.Align.CENTER -> stageLeft + centerX * stageScale
        }
        var baselineY = stageTop + topY * stageScale - metrics.ascent
        lines.forEach { line ->
            canvas.drawText(line, x, baselineY, formInputPaint)
            baselineY += lineHeight * stageScale
        }
        return topY + lines.size * lineHeight
    }

    private fun drawMockCreateAccountAvatarText(
        canvas: Canvas,
        columnX: Float,
        firstFieldY: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float
    ) {
        val avatarScale = MOCK_CREATE_AVATAR_STACK_SCALE
        val scaledAvatarSize = MOCK_CREATE_AVATAR_SIZE * avatarScale
        val labelBaselineFromTop = MOCK_CREATE_AVATAR_LABEL_BASELINE_FROM_TOP * avatarScale
        val avatarTopGap = MOCK_CREATE_AVATAR_TOP_GAP * avatarScale
        val centerX = stageLeft + (columnX + MOCK_FORM_WIDTH / 2f) * stageScale
        val baselineY = stageTop + (firstFieldY + labelBaselineFromTop) * stageScale
        formLabelPaint.textSize = MOCK_FORM_LABEL_TEXT_SIZE * avatarScale * stageScale
        formLabelPaint.alpha = (alpha.coerceIn(0f, 1f) * 255f).roundToInt()
        formLabelPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            MOCK_FORM_AVATAR_TEXT,
            centerX,
            baselineY,
            formLabelPaint
        )

        val avatarLeftX = columnX + (MOCK_FORM_WIDTH - scaledAvatarSize) / 2f
        val avatarTopY = firstFieldY + labelBaselineFromTop + avatarTopGap
        val avatarBounds = Bounds(
            avatarLeftX,
            avatarTopY,
            scaledAvatarSize,
            scaledAvatarSize
        )
        val carouselProgress = mockCreateAvatarCarouselProgress()
        val slideDirection = mockCreateAvatarCarouselDirection.stageDirection
        val slideDistance = mockCreateAvatarSlideDistance(scaledAvatarSize)
        val dragDirection = mockCreateAvatarDragDirection()
        val dragProgress = mockCreateAvatarDragProgress(slideDistance, stageScale)

        drawMockCreateAvatarBubbleShell(canvas, avatarBounds, stageLeft, stageTop, stageScale, alpha)

        canvas.save()
        clipMockCreateAvatarBubbleInterior(canvas, avatarBounds, stageLeft, stageTop, stageScale)
        if (
            activeMockCreateAvatarDrag &&
            mockCreateAvatarDragMoved &&
            dragDirection != null &&
            !isMockCreateAvatarCarouselAnimating()
        ) {
            val dragSlideDirection = dragDirection.stageDirection
            drawMockCreateAvatarBitmap(
                canvas,
                mockCreateAvatarIndex,
                avatarBounds,
                -dragSlideDirection * dragProgress * slideDistance,
                stageLeft,
                stageTop,
                stageScale,
                alpha
            )
            drawMockCreateAvatarBitmap(
                canvas,
                mockCreateAvatarIndexForDirection(dragDirection),
                avatarBounds,
                dragSlideDirection * (1f - dragProgress) * slideDistance,
                stageLeft,
                stageTop,
                stageScale,
                alpha
            )
        } else if (isMockCreateAvatarCarouselAnimating()) {
            drawMockCreateAvatarBitmap(
                canvas,
                mockCreateAvatarCarouselFromIndex,
                avatarBounds,
                -slideDirection * carouselProgress * slideDistance,
                stageLeft,
                stageTop,
                stageScale,
                alpha
            )
            drawMockCreateAvatarBitmap(
                canvas,
                mockCreateAvatarCarouselToIndex,
                avatarBounds,
                slideDirection * (1f - carouselProgress) * slideDistance,
                stageLeft,
                stageTop,
                stageScale,
                alpha
            )
        } else {
            drawMockCreateAvatarBitmap(
                canvas,
                mockCreateAvatarIndex,
                avatarBounds,
                0f,
                stageLeft,
                stageTop,
                stageScale,
                alpha
            )
        }
        canvas.restore()
        drawMockCreateAvatarChevron(
            canvas,
            MockAvatarCarouselDirection.Previous,
            avatarBounds,
            stageLeft,
            stageTop,
            stageScale,
            alpha
        )
        drawMockCreateAvatarChevron(
            canvas,
            MockAvatarCarouselDirection.Next,
            avatarBounds,
            stageLeft,
            stageTop,
            stageScale,
            alpha
        )
    }

    private fun drawMockCreateAvatarBitmap(
        canvas: Canvas,
        avatarIndex: Int,
        bounds: Bounds,
        offsetX: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float
    ) {
        drawFigmaBitmap(
            canvas,
            mockCreateAvatarBitmap(avatarIndex),
            Bounds(bounds.x + offsetX, bounds.y, bounds.w, bounds.h),
            stageLeft,
            stageTop,
            stageScale,
            alpha
        )
    }

    private fun drawMockCreateAvatarBubbleShell(
        canvas: Canvas,
        avatarBounds: Bounds,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float
    ) {
        drawFigmaBitmap(
            canvas,
            createAvatarBubbleShell,
            avatarBounds,
            stageLeft,
            stageTop,
            stageScale,
            alpha
        )
    }

    private fun clipMockCreateAvatarBubbleInterior(
        canvas: Canvas,
        avatarBounds: Bounds,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float
    ) {
        tempPath.reset()
        tempPath.addCircle(
            stageLeft + (avatarBounds.x + avatarBounds.w / 2f) * stageScale,
            stageTop + (avatarBounds.y + avatarBounds.h / 2f) * stageScale,
            mockCreateAvatarBubbleInnerRadius(avatarBounds, stageScale),
            Path.Direction.CW
        )
        canvas.clipPath(tempPath)
    }

    private fun mockCreateAvatarBubbleInnerRadius(avatarBounds: Bounds, stageScale: Float): Float {
        return (avatarBounds.w / 2f - avatarBounds.w * MOCK_CREATE_AVATAR_BUBBLE_INNER_INSET_RATIO) * stageScale
    }

    private fun drawMockCreateAvatarChevron(
        canvas: Canvas,
        direction: MockAvatarCarouselDirection,
        avatarBounds: Bounds,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float
    ) {
        val centerX = stageLeft + mockCreateAvatarChevronCenterX(direction, avatarBounds) * stageScale
        val centerY = stageTop + mockCreateAvatarChevronCenterY(avatarBounds) * stageScale
        val halfWidth = MOCK_CREATE_AVATAR_CHEVRON_WIDTH * stageScale / 2f
        val halfHeight = MOCK_CREATE_AVATAR_CHEVRON_HEIGHT * stageScale / 2f
        val pressedAlpha = if (pressedMockCreateAvatarNav == direction) 0.62f else 0.9f
        backChevronPaint.alpha = (alpha.coerceIn(0f, 1f) * pressedAlpha * 255f).roundToInt()
        backChevronPaint.strokeWidth = MOCK_CREATE_AVATAR_CHEVRON_STROKE_WIDTH * stageScale

        if (direction == MockAvatarCarouselDirection.Previous) {
            canvas.drawLine(centerX + halfWidth, centerY - halfHeight, centerX - halfWidth, centerY, backChevronPaint)
            canvas.drawLine(centerX - halfWidth, centerY, centerX + halfWidth, centerY + halfHeight, backChevronPaint)
        } else {
            canvas.drawLine(centerX - halfWidth, centerY - halfHeight, centerX + halfWidth, centerY, backChevronPaint)
            canvas.drawLine(centerX + halfWidth, centerY, centerX - halfWidth, centerY + halfHeight, backChevronPaint)
        }
        backChevronPaint.alpha = 255
    }

    private fun drawMockDropdownControl(
        canvas: Canvas,
        dropdown: MockDropdown,
        x: Float,
        y: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float,
        dropdownWidth: Float = MOCK_FORM_WIDTH
    ) {
        tempRect.set(
            stageLeft + x * stageScale,
            stageTop + y * stageScale,
            stageLeft + (x + dropdownWidth) * stageScale,
            stageTop + (y + MOCK_FORM_FIELD_HEIGHT) * stageScale
        )
        val radius = MOCK_FORM_FIELD_RADIUS * stageScale
        canvas.drawRoundRect(tempRect, radius, radius, formFieldFillPaint)
        canvas.drawRoundRect(tempRect, radius, radius, formFieldStrokePaint)

        val textAlpha = (alpha.coerceIn(0f, 1f) * 255f).roundToInt()
        formInputPaint.textSize = mockDropdownTextSize(dropdown) * stageScale
        formInputPaint.alpha = textAlpha
        formInputPaint.textAlign = Paint.Align.LEFT
        val textInset = mockDropdownTextInsetX(dropdown) * stageScale
        val chevronInset = mockDropdownChevronInsetX(dropdown) * stageScale
        val displayText = mockDropdownDisplayText(dropdown)
        val membershipIcon = membershipTierArtworkIconForOption(displayText)
            ?.takeIf { dropdown == MockDropdown.SubscriptionTier }
        val iconLaneWidth = if (membershipIcon != null) {
            (MOCK_MEMBERSHIP_TIER_CONTROL_ICON_SIZE + MOCK_MEMBERSHIP_TIER_ICON_TEXT_GAP) * stageScale
        } else {
            0f
        }
        val maxTextWidth = (dropdownWidth * stageScale - textInset - iconLaneWidth - chevronInset * 2f)
            .coerceAtLeast(0f)
        if (membershipIcon != null) {
            drawMembershipTierIcon(
                canvas = canvas,
                bitmap = membershipIcon,
                left = tempRect.left + textInset,
                centerY = tempRect.centerY(),
                iconSize = MOCK_MEMBERSHIP_TIER_CONTROL_ICON_SIZE * stageScale,
                alpha = textAlpha
            )
        }
        canvas.drawText(
            trailingFittingText(displayText, maxTextWidth, formInputPaint),
            tempRect.left + textInset + iconLaneWidth,
            tempRect.centerY() - (formInputPaint.fontMetrics.ascent + formInputPaint.fontMetrics.descent) / 2f,
            formInputPaint
        )

        val centerX = tempRect.right - chevronInset
        val centerY = tempRect.centerY() + 2f * stageScale
        val halfSize = mockDropdownChevronSize(dropdown) * stageScale / 2f
        backChevronPaint.alpha = textAlpha
        backChevronPaint.strokeWidth = 1.65f * stageScale
        canvas.drawLine(centerX - halfSize, centerY - halfSize / 2f, centerX, centerY + halfSize / 2f, backChevronPaint)
        canvas.drawLine(centerX, centerY + halfSize / 2f, centerX + halfSize, centerY - halfSize / 2f, backChevronPaint)
        backChevronPaint.alpha = 255
    }

    private fun drawMockDropdownOptions(
        canvas: Canvas,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float
    ) {
        val dropdown = expandedMockDropdown ?: return
        if (activeMockFlow != MockAccountFlow.CreateAccount) return

        val x = mockDropdownX(dropdown)
        val y = mockDropdownOptionsY(
            dropdown,
            MOCK_FORM_ENTRY_Y * (1f - alpha),
            mockFormFocusShiftY(FastOutSlowInEasing.transform(mockFormFocusProgress))
        )
        val left = stageLeft + x * stageScale
        val top = stageTop + y * stageScale
        val right = stageLeft + (x + mockDropdownWidth(dropdown)) * stageScale
        val optionHeight = mockDropdownOptionHeight(dropdown) * stageScale
        val optionAlpha = 255
        val optionTextInset = mockDropdownOptionTextInsetX(dropdown) * stageScale
        val scrollbarReservedWidth = if (mockDropdownCanScroll(dropdown)) {
            (MOCK_DATE_DROPDOWN_SCROLLBAR_WIDTH + MOCK_DATE_DROPDOWN_SCROLLBAR_INSET_X * 2f) * stageScale
        } else {
            0f
        }

        val visibleOptions = mockDropdownVisibleOptions(dropdown)
        visibleOptions.forEachIndexed { index, option ->
            val optionTop = top + index * optionHeight
            val optionBottom = optionTop + optionHeight
            val fillColor = if (option == mockDropdownSelectedValue(dropdown)) {
                Color.argb(optionAlpha, 76, 12, 94)
            } else {
                Color.argb(optionAlpha, 14, 2, 18)
            }
            tempRect.set(left, optionTop, right, optionBottom)
            formFieldFillPaint.color = fillColor
            formFieldStrokePaint.color = Color.argb(optionAlpha, 255, 255, 255)
            formFieldStrokePaint.strokeWidth = 1.2f * stageScale
            canvas.drawRect(tempRect, formFieldFillPaint)
            canvas.drawRect(tempRect, formFieldStrokePaint)

            formInputPaint.textSize = mockDropdownOptionTextSize(dropdown) * stageScale
            formInputPaint.alpha = optionAlpha
            formInputPaint.textAlign = Paint.Align.LEFT
            val metrics = formInputPaint.fontMetrics
            val membershipIcon = membershipTierArtworkIconForOption(option)
                ?.takeIf { dropdown == MockDropdown.SubscriptionTier }
            val iconLaneWidth = if (membershipIcon != null) {
                (MOCK_MEMBERSHIP_TIER_OPTION_ICON_SIZE + MOCK_MEMBERSHIP_TIER_ICON_TEXT_GAP) * stageScale
            } else {
                0f
            }
            if (membershipIcon != null) {
                drawMembershipTierIcon(
                    canvas = canvas,
                    bitmap = membershipIcon,
                    left = left + optionTextInset,
                    centerY = (optionTop + optionBottom) / 2f,
                    iconSize = MOCK_MEMBERSHIP_TIER_OPTION_ICON_SIZE * stageScale,
                    alpha = optionAlpha
                )
            }
            val maxTextWidth = (right - left - optionTextInset - iconLaneWidth - scrollbarReservedWidth)
                .coerceAtLeast(0f)
            canvas.drawText(
                trailingFittingText(option, maxTextWidth, formInputPaint),
                left + optionTextInset + iconLaneWidth,
                (optionTop + optionBottom) / 2f - (metrics.ascent + metrics.descent) / 2f,
                formInputPaint
            )
        }

        if (mockDropdownCanScroll(dropdown)) {
            drawMockDropdownScrollbar(
                canvas,
                dropdown,
                top,
                right,
                optionHeight * visibleOptions.size,
                stageScale,
                optionAlpha
            )
        }
    }

    private fun drawMembershipTierIcon(
        canvas: Canvas,
        bitmap: Bitmap,
        left: Float,
        centerY: Float,
        iconSize: Float,
        alpha: Int
    ) {
        val bitmapAspect = bitmap.width.toFloat() / bitmap.height.toFloat()
        val width: Float
        val height: Float
        if (bitmapAspect >= 1f) {
            width = iconSize
            height = iconSize / bitmapAspect
        } else {
            width = iconSize * bitmapAspect
            height = iconSize
        }
        val top = centerY - height / 2f
        tempIconRect.set(left, top, left + width, top + height)
        imagePaint.alpha = alpha.coerceIn(0, 255)
        canvas.drawBitmap(bitmap, null, tempIconRect, imagePaint)
        imagePaint.alpha = 255
    }

    private fun drawMockDropdownScrollbar(
        canvas: Canvas,
        dropdown: MockDropdown,
        top: Float,
        right: Float,
        dropdownHeight: Float,
        stageScale: Float,
        alpha: Int
    ) {
        val options = mockDropdownOptions(dropdown)
        val visibleCount = mockDropdownVisibleOptionCount(dropdown)
        val maxOffset = mockDropdownMaxScrollOffset(dropdown)
        if (options.isEmpty() || maxOffset <= 0) return

        val trackRight = right - MOCK_DATE_DROPDOWN_SCROLLBAR_INSET_X * stageScale
        val trackLeft = trackRight - MOCK_DATE_DROPDOWN_SCROLLBAR_WIDTH * stageScale
        val trackTop = top + MOCK_DATE_DROPDOWN_SCROLLBAR_INSET_Y * stageScale
        val trackBottom = top + dropdownHeight - MOCK_DATE_DROPDOWN_SCROLLBAR_INSET_Y * stageScale
        val trackHeight = (trackBottom - trackTop).coerceAtLeast(0f)
        val thumbHeight = maxOf(14f * stageScale, trackHeight * visibleCount / options.size)
            .coerceAtMost(trackHeight)
        val thumbProgress = mockDropdownScrollOffset(dropdown) / maxOffset.toFloat()
        val thumbTop = trackTop + (trackHeight - thumbHeight) * thumbProgress
        val radius = MOCK_DATE_DROPDOWN_SCROLLBAR_WIDTH * stageScale / 2f

        tempRect.set(trackLeft, trackTop, trackRight, trackBottom)
        formFieldFillPaint.color = Color.argb((alpha * 0.28f).roundToInt(), 255, 255, 255)
        canvas.drawRoundRect(tempRect, radius, radius, formFieldFillPaint)

        tempRect.set(trackLeft, thumbTop, trackRight, thumbTop + thumbHeight)
        formFieldFillPaint.color = Color.argb((alpha * 0.78f).roundToInt(), 255, 255, 255)
        canvas.drawRoundRect(tempRect, radius, radius, formFieldFillPaint)
    }

    private fun drawMockSignInField(
        canvas: Canvas,
        field: MockSignInField,
        x: Float,
        y: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float,
        fieldWidth: Float = MOCK_FORM_WIDTH
    ) {
        tempRect.set(
            stageLeft + x * stageScale,
            stageTop + y * stageScale,
            stageLeft + (x + fieldWidth) * stageScale,
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
            formInputPaint.textAlign = Paint.Align.LEFT
            formCaretPaint.alpha = inputAlpha

            val inputX = tempRect.left + MOCK_FORM_LABEL_INSET_X * stageScale
            val inputBaselineY = tempRect.top + MOCK_FORM_INPUT_BASELINE_FROM_TOP * stageScale
            val maxInputWidth = fieldWidth * stageScale - MOCK_FORM_LABEL_INSET_X * stageScale * 2f
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

    private fun drawRememberMeCheckbox(
        canvas: Canvas,
        x: Float,
        y: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float
    ) {
        val paintAlpha = (alpha.coerceIn(0f, 1f) * 255f).roundToInt()
        val boxLeft = stageLeft + x * stageScale
        val boxTop = stageTop + y * stageScale
        val boxRight = boxLeft + MOCK_REMEMBER_ME_BOX_SIZE * stageScale
        val boxBottom = boxTop + MOCK_REMEMBER_ME_BOX_SIZE * stageScale
        val fillAlpha = (alpha * if (rememberMeChecked || pressedRememberMe) 46f else 18f).roundToInt()
        val strokeAlpha = (alpha * if (rememberMeChecked) 220f else 168f).roundToInt()

        rememberMeBoxFillPaint.color = Color.argb(fillAlpha, 255, 255, 255)
        rememberMeBoxStrokePaint.color = Color.argb(strokeAlpha, 255, 255, 255)
        rememberMeBoxStrokePaint.strokeWidth = 1.7f * stageScale
        tempRect.set(boxLeft, boxTop, boxRight, boxBottom)
        val radius = MOCK_REMEMBER_ME_BOX_RADIUS * stageScale
        canvas.drawRoundRect(tempRect, radius, radius, rememberMeBoxFillPaint)
        canvas.drawRoundRect(tempRect, radius, radius, rememberMeBoxStrokePaint)

        if (rememberMeChecked) {
            rememberMeCheckPaint.alpha = paintAlpha
            rememberMeCheckPaint.strokeWidth = 2.45f * stageScale
            canvas.drawLine(
                boxLeft + 5.3f * stageScale,
                boxTop + 11.8f * stageScale,
                boxLeft + 9.1f * stageScale,
                boxTop + 15.8f * stageScale,
                rememberMeCheckPaint
            )
            canvas.drawLine(
                boxLeft + 9.1f * stageScale,
                boxTop + 15.8f * stageScale,
                boxLeft + 17.2f * stageScale,
                boxTop + 6.9f * stageScale,
                rememberMeCheckPaint
            )
            rememberMeCheckPaint.alpha = 255
        }

        rememberMeTextPaint.textSize = MOCK_REMEMBER_ME_TEXT_SIZE * stageScale
        rememberMeTextPaint.alpha = paintAlpha
        rememberMeTextPaint.textAlign = Paint.Align.LEFT
        val metrics = rememberMeTextPaint.fontMetrics
        val baselineY = (boxTop + boxBottom) / 2f - (metrics.ascent + metrics.descent) / 2f
        canvas.drawText(
            MOCK_REMEMBER_ME_TEXT,
            boxRight + MOCK_REMEMBER_ME_LABEL_GAP * stageScale,
            baselineY,
            rememberMeTextPaint
        )
        rememberMeTextPaint.alpha = 255
    }

    private fun drawBackChevron(
        canvas: Canvas,
        centerX: Float,
        topY: Float,
        stageLeft: Float,
        stageTop: Float,
        stageScale: Float,
        alpha: Float
    ) {
        val chevronAlpha = alpha.coerceIn(0f, 1f) * if (pressedBackChevron) 0.68f else 0.92f
        val center = stageLeft + centerX * stageScale
        val top = stageTop + topY * stageScale
        val bottom = top + MOCK_BACK_CHEVRON_HEIGHT * stageScale
        val halfWidth = MOCK_BACK_CHEVRON_WIDTH * stageScale / 2f

        backChevronPaint.alpha = (chevronAlpha * 255f).roundToInt()
        backChevronPaint.strokeWidth = MOCK_BACK_CHEVRON_STROKE_WIDTH * stageScale
        canvas.drawLine(center - halfWidth, top, center, bottom, backChevronPaint)
        canvas.drawLine(center, bottom, center + halfWidth, top, backChevronPaint)
        backChevronPaint.alpha = 255
    }

    private fun decode(resId: Int): Bitmap {
        return BitmapFactory.decodeResource(resources, resId, bitmapOptions)
    }

    private fun createAvatarCharacterBitmap(
        source: Bitmap,
        includeDarkSaturatedPixels: Boolean = false
    ): Bitmap {
        val bitmapWidth = source.width
        val bitmapHeight = source.height
        val pixels = IntArray(bitmapWidth * bitmapHeight)
        source.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight)

        val mask = createAvatarCharacterMask(pixels, bitmapWidth, bitmapHeight, includeDarkSaturatedPixels)
        val outputPixels = IntArray(pixels.size) { index ->
            if (mask[index]) pixels[index] else Color.TRANSPARENT
        }
        return Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888).apply {
            setPixels(outputPixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight)
        }
    }

    private fun createAvatarBubbleShellBitmap(source: Bitmap): Bitmap {
        val bitmapWidth = source.width
        val bitmapHeight = source.height
        val pixels = IntArray(bitmapWidth * bitmapHeight)
        source.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight)

        val centerX = (bitmapWidth - 1f) / 2f
        val centerY = (bitmapHeight - 1f) / 2f
        val innerRadius = min(bitmapWidth, bitmapHeight) * (0.5f - MOCK_CREATE_AVATAR_BUBBLE_INNER_INSET_RATIO)
        val innerRadiusSquared = innerRadius * innerRadius

        val outputPixels = IntArray(pixels.size) { index ->
            val x = index % bitmapWidth
            val y = index / bitmapWidth
            val dx = x - centerX
            val dy = y - centerY
            if (dx * dx + dy * dy < innerRadiusSquared) {
                Color.TRANSPARENT
            } else {
                pixels[index]
            }
        }
        return Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888).apply {
            setPixels(outputPixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight)
        }
    }

    private fun createAvatarCharacterMask(
        pixels: IntArray,
        bitmapWidth: Int,
        bitmapHeight: Int,
        includeDarkSaturatedPixels: Boolean
    ): BooleanArray {
        val coloredPixels = BooleanArray(pixels.size)
        pixels.forEachIndexed { index, pixel ->
            coloredPixels[index] = isColorfulAvatarPixel(pixel, includeDarkSaturatedPixels)
        }

        val mask = BooleanArray(pixels.size)
        val outlineRadius = 7
        for (y in 0 until bitmapHeight) {
            for (x in 0 until bitmapWidth) {
                val index = y * bitmapWidth + x
                if (!coloredPixels[index]) continue

                mask[index] = true
                for (dy in -outlineRadius..outlineRadius) {
                    val sampleY = y + dy
                    if (sampleY !in 0 until bitmapHeight) continue
                    for (dx in -outlineRadius..outlineRadius) {
                        val sampleX = x + dx
                        if (sampleX !in 0 until bitmapWidth) continue
                        val sampleIndex = sampleY * bitmapWidth + sampleX
                        val samplePixel = pixels[sampleIndex]
                        val sampleAlpha = Color.alpha(samplePixel)
                        val sampleMax = maxOf(
                            Color.red(samplePixel),
                            Color.green(samplePixel),
                            Color.blue(samplePixel)
                        )
                        if (sampleAlpha > 16 && sampleMax <= 44) {
                            mask[sampleIndex] = true
                        }
                    }
                }
            }
        }
        return mask
    }

    private fun isColorfulAvatarPixel(
        pixel: Int,
        includeDarkSaturatedPixels: Boolean
    ): Boolean {
        val alpha = Color.alpha(pixel)
        val red = Color.red(pixel)
        val green = Color.green(pixel)
        val blue = Color.blue(pixel)
        val channelMax = maxOf(red, green, blue)
        val channelMin = minOf(red, green, blue)
        if (alpha <= 16) return false

        val chroma = channelMax - channelMin
        return (channelMax > 48 && chroma > 22) ||
            (includeDarkSaturatedPixels && channelMax > 36 && chroma > 14)
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

    private fun settledCreateAccountPromptHit(x: Float, y: Float): Boolean {
        if (mockSignInStartMillis != null || !isFinalFrameSettled()) return false

        val stage = currentStageMetrics() ?: return false
        val centerX = stage.left + accountPromptCreateCenterX() * stage.scale
        val centerY = stage.top +
            (ACCOUNT_PROMPT_CENTER_Y + signInStackShiftY() + signInAccountPromptExtraShiftY()) * stage.scale
        val halfWidth = ACCOUNT_PROMPT_CREATE_HIT_WIDTH * stage.scale / 2f
        val halfHeight = ACCOUNT_PROMPT_CREATE_HIT_HEIGHT * stage.scale / 2f
        return x in (centerX - halfWidth)..(centerX + halfWidth) &&
            y in (centerY - halfHeight)..(centerY + halfHeight)
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

    private fun settledForgotPasswordPromptHit(x: Float, y: Float): Boolean {
        if (mockSignInStartMillis != null || !isFinalFrameSettled()) return false

        val stage = currentStageMetrics() ?: return false
        val centerX = stage.left + accountPromptForgotCenterX() * stage.scale
        val centerY = stage.top +
            (ACCOUNT_PROMPT_CENTER_Y + signInStackShiftY() + signInAccountPromptExtraShiftY()) * stage.scale
        val halfWidth = ACCOUNT_PROMPT_FORGOT_HIT_WIDTH * stage.scale / 2f
        val halfHeight = ACCOUNT_PROMPT_FORGOT_HIT_HEIGHT * stage.scale / 2f
        return x in (centerX - halfWidth)..(centerX + halfWidth) &&
            y in (centerY - halfHeight)..(centerY + halfHeight)
    }

    private fun mockSignInFieldHit(x: Float, y: Float): MockSignInField? {
        if (mockSignInStartMillis == null) return null

        val stage = currentStageMetrics() ?: return null
        val progress = FastOutSlowInEasing.transform(mockSignInProgress())
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - progress)
        val focusShiftY = mockFormFocusShiftY(FastOutSlowInEasing.transform(mockFormFocusProgress))
        return activeMockFields().firstOrNull { field ->
            val fieldX = mockActiveFieldX(field)
            val fieldY = mockActiveFieldY(field, yOffset, focusShiftY)
            mockSignInFieldContains(x, y, fieldX, fieldY, stage, mockActiveFieldWidth(field))
        }
    }

    private fun mockForgotRecoveryTargetHit(x: Float, y: Float): MockForgotRecoveryTarget? {
        if (mockSignInStartMillis == null || activeMockFlow != MockAccountFlow.ForgotPassword) return null

        val stage = currentStageMetrics() ?: return null
        val progress = FastOutSlowInEasing.transform(mockSignInProgress())
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - progress)
        val focusShiftY = mockFormFocusShiftY(FastOutSlowInEasing.transform(mockFormFocusProgress))
        val selectorY = mockForgotRecoverySelectorY(yOffset, focusShiftY)
        return MockForgotRecoveryTarget.values().firstOrNull { target ->
            mockForgotRecoveryTargetContains(x, y, target, selectorY, stage)
        }
    }

    private fun mockForgotPasswordSubmitHit(x: Float, y: Float): Boolean {
        if (
            mockSignInStartMillis == null ||
            activeMockFlow != MockAccountFlow.ForgotPassword ||
            isMockSignInClosing() ||
            !mockForgotPasswordCanSubmit()
        ) {
            return false
        }

        val stage = currentStageMetrics() ?: return false
        val progress = FastOutSlowInEasing.transform(mockSignInProgress())
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - progress)
        val focusShiftY = mockFormFocusShiftY(FastOutSlowInEasing.transform(mockFormFocusProgress))
        return mockForgotPasswordButtonContains(
            x,
            y,
            mockForgotPasswordFormX(),
            mockForgotPasswordButtonY(yOffset, focusShiftY),
            stage,
            mockForgotPasswordFormWidth()
        )
    }

    private fun mockRememberMeHit(x: Float, y: Float): Boolean {
        if (mockSignInStartMillis == null || activeMockFlow != MockAccountFlow.SignIn) return false

        val stage = currentStageMetrics() ?: return false
        val progress = FastOutSlowInEasing.transform(mockSignInProgress())
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - progress)
        val focusShiftY = mockFormFocusShiftY(FastOutSlowInEasing.transform(mockFormFocusProgress))
        val fieldX = mockFormX()
        val usernameY = MOCK_FORM_Y + yOffset + focusShiftY
        val passwordY = usernameY + MOCK_FORM_FIELD_HEIGHT + MOCK_FORM_FIELD_GAP
        val rememberMeY = passwordY + MOCK_FORM_FIELD_HEIGHT + MOCK_REMEMBER_ME_TOP_GAP
        return mockRememberMeContains(x, y, fieldX, rememberMeY, stage)
    }

    private fun mockDropdownHit(x: Float, y: Float): MockDropdown? {
        if (mockSignInStartMillis == null || activeMockFlow != MockAccountFlow.CreateAccount) return null

        val stage = currentStageMetrics() ?: return null
        val progress = FastOutSlowInEasing.transform(mockSignInProgress())
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - progress)
        val focusShiftY = mockFormFocusShiftY(FastOutSlowInEasing.transform(mockFormFocusProgress))
        return MockDropdown.values().firstOrNull { dropdown ->
            mockSignInFieldContains(
                x,
                y,
                mockDropdownX(dropdown),
                mockDropdownY(dropdown, yOffset, focusShiftY),
                stage,
                mockDropdownWidth(dropdown)
            )
        }
    }

    private fun mockCreateAvatarNavHit(x: Float, y: Float): MockAvatarCarouselDirection? {
        if (mockSignInStartMillis == null || activeMockFlow != MockAccountFlow.CreateAccount) return null

        val stage = currentStageMetrics() ?: return null
        val progress = FastOutSlowInEasing.transform(mockSignInProgress())
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - progress)
        val focusShiftY = mockFormFocusShiftY(FastOutSlowInEasing.transform(mockFormFocusProgress))
        val columnX = mockCreateFormX() + (MOCK_CREATE_FORM_COLUMNS - 1) * (MOCK_FORM_WIDTH + MOCK_CREATE_FORM_COLUMN_GAP)
        val firstFieldY = MOCK_FORM_Y + yOffset + focusShiftY
        val avatarBounds = mockCreateAvatarBounds(columnX, firstFieldY)
        return MockAvatarCarouselDirection.values().firstOrNull { direction ->
            mockCreateAvatarChevronContains(x, y, direction, avatarBounds, stage)
        }
    }

    private fun mockCreateAvatarCarouselHit(x: Float, y: Float): Boolean {
        if (mockSignInStartMillis == null || activeMockFlow != MockAccountFlow.CreateAccount) return false

        val stage = currentStageMetrics() ?: return false
        val progress = FastOutSlowInEasing.transform(mockSignInProgress())
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - progress)
        val focusShiftY = mockFormFocusShiftY(FastOutSlowInEasing.transform(mockFormFocusProgress))
        val columnX = mockCreateFormX() + (MOCK_CREATE_FORM_COLUMNS - 1) * (MOCK_FORM_WIDTH + MOCK_CREATE_FORM_COLUMN_GAP)
        val firstFieldY = MOCK_FORM_Y + yOffset + focusShiftY
        val avatarBounds = mockCreateAvatarBounds(columnX, firstFieldY)
        val carouselBounds = mockCreateAvatarCarouselBounds(columnX, firstFieldY, avatarBounds)
        val left = stage.left + carouselBounds.x * stage.scale
        val top = stage.top + carouselBounds.y * stage.scale
        val right = stage.left + (carouselBounds.x + carouselBounds.w) * stage.scale
        val bottom = stage.top + (carouselBounds.y + carouselBounds.h) * stage.scale
        return x in left..right && y in top..bottom
    }

    private fun updateMockCreateAvatarDrag(event: MotionEvent): Boolean {
        if (!activeMockCreateAvatarDrag) return false

        mockCreateAvatarDragDeltaX = event.x - mockTouchDownX
        val dragDeltaY = event.y - mockTouchDownY
        if (
            !mockCreateAvatarDragMoved &&
            abs(mockCreateAvatarDragDeltaX) > touchSlop &&
            abs(mockCreateAvatarDragDeltaX) > abs(dragDeltaY)
        ) {
            mockCreateAvatarDragMoved = true
            pressedMockCreateAvatarNav = null
        }

        if (mockCreateAvatarDragMoved) {
            postInvalidateOnAnimation()
        }
        return true
    }

    private fun mockDropdownOptionHit(pointerX: Float, pointerY: Float): MockDropdownOption? {
        if (
            mockSignInStartMillis == null ||
            activeMockFlow != MockAccountFlow.CreateAccount ||
            expandedMockDropdown == null
        ) {
            return null
        }

        val dropdown = expandedMockDropdown ?: return null
        val stage = currentStageMetrics() ?: return null
        val progress = FastOutSlowInEasing.transform(mockSignInProgress())
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - progress)
        val focusShiftY = mockFormFocusShiftY(FastOutSlowInEasing.transform(mockFormFocusProgress))
        val dropdownX = mockDropdownX(dropdown)
        val yStart = mockDropdownOptionsY(dropdown, yOffset, focusShiftY)
        val left = stage.left + dropdownX * stage.scale
        val right = stage.left + (dropdownX + mockDropdownWidth(dropdown)) * stage.scale
        if (pointerX !in left..right) return null
        if (mockDropdownCanScroll(dropdown)) {
            val scrollbarLeft = right -
                (MOCK_DATE_DROPDOWN_SCROLLBAR_WIDTH + MOCK_DATE_DROPDOWN_SCROLLBAR_INSET_X * 2f) * stage.scale
            if (pointerX >= scrollbarLeft) return null
        }

        mockDropdownVisibleOptions(dropdown).forEachIndexed { optionIndex, option ->
            val optionHeight = mockDropdownOptionHeight(dropdown)
            val top = stage.top + (yStart + optionIndex * optionHeight) * stage.scale
            val bottom = top + optionHeight * stage.scale
            if (pointerY in top..bottom) return MockDropdownOption(dropdown, option)
        }
        return null
    }

    private fun mockDropdownOptionsHit(pointerX: Float, pointerY: Float): MockDropdown? {
        if (
            mockSignInStartMillis == null ||
            activeMockFlow != MockAccountFlow.CreateAccount ||
            expandedMockDropdown == null
        ) {
            return null
        }

        val dropdown = expandedMockDropdown ?: return null
        val stage = currentStageMetrics() ?: return null
        val progress = FastOutSlowInEasing.transform(mockSignInProgress())
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - progress)
        val focusShiftY = mockFormFocusShiftY(FastOutSlowInEasing.transform(mockFormFocusProgress))
        val dropdownX = mockDropdownX(dropdown)
        val yStart = mockDropdownOptionsY(dropdown, yOffset, focusShiftY)
        val left = stage.left + dropdownX * stage.scale
        val right = stage.left + (dropdownX + mockDropdownWidth(dropdown)) * stage.scale
        val top = stage.top + yStart * stage.scale
        val bottom = top + mockDropdownVisibleOptionCount(dropdown) * mockDropdownOptionHeight(dropdown) * stage.scale
        return if (pointerX in left..right && pointerY in top..bottom) dropdown else null
    }

    private fun updateMockDropdownScroll(event: MotionEvent): Boolean {
        val dropdown = activeMockDropdownScroll ?: return false
        if (expandedMockDropdown != dropdown || !mockDropdownCanScroll(dropdown)) return false

        val stage = currentStageMetrics() ?: return true
        val optionHeightPx = (mockDropdownOptionHeight(dropdown) * stage.scale).coerceAtLeast(1f)
        val dragDeltaY = event.y - mockDropdownScrollStartY
        if (abs(dragDeltaY) > touchSlop) {
            mockDropdownScrollMoved = true
            pressedMockDropdownOption = null
        }

        if (mockDropdownScrollMoved) {
            val rowDelta = ((mockDropdownScrollStartY - event.y) / optionHeightPx).roundToInt()
            setMockDropdownScrollOffset(dropdown, mockDropdownScrollStartOffset + rowDelta)
            postInvalidateOnAnimation()
        }
        return true
    }

    private fun mockBackChevronHit(x: Float, y: Float): Boolean {
        if (mockSignInStartMillis == null || isMockSignInClosing()) return false

        val stage = currentStageMetrics() ?: return false
        val progress = FastOutSlowInEasing.transform(mockSignInProgress())
        val yOffset = MOCK_FORM_ENTRY_Y * (1f - progress)
        val focusShiftY = mockFormFocusShiftY(FastOutSlowInEasing.transform(mockFormFocusProgress))
        val chevronTopY = when (activeMockFlow) {
            MockAccountFlow.SignIn -> {
                val lastField = activeMockFields().lastOrNull() ?: return false
                val lastFieldY = mockActiveFieldY(lastField, yOffset, focusShiftY)
                lastFieldY + MOCK_FORM_FIELD_HEIGHT + MOCK_REMEMBER_ME_TOP_GAP + MOCK_REMEMBER_ME_BOX_SIZE +
                    MOCK_BACK_CHEVRON_TOP_GAP
            }
            MockAccountFlow.CreateAccount -> {
                MOCK_FORM_Y + yOffset + focusShiftY + mockCreateFormHeight() + MOCK_BACK_CHEVRON_TOP_GAP
            }
            MockAccountFlow.ForgotPassword -> mockForgotPasswordBackChevronTopY(yOffset, focusShiftY)
            null -> return false
        }
        return mockBackChevronContains(x, y, mockActiveFormCenterX(), chevronTopY, stage)
    }

    private fun shouldStartMockDragReturn(event: MotionEvent): Boolean {
        if (mockDragReturnInProgress || isMockSignInClosing()) return false

        val upwardDragY = mockTouchDownY - event.y
        if (upwardDragY <= mockBackDragThresholdPx()) return false

        val dragX = event.x - mockTouchDownX
        return upwardDragY > abs(dragX)
    }

    private fun mockBackDragThresholdPx(): Float {
        val stageThreshold = currentStageMetrics()?.let { stage ->
            MOCK_BACK_DRAG_THRESHOLD * stage.scale
        } ?: MOCK_BACK_DRAG_THRESHOLD
        return maxOf(stageThreshold, touchSlop * 3f)
    }

    private fun mockSignInFieldContains(
        pointerX: Float,
        pointerY: Float,
        fieldX: Float,
        fieldY: Float,
        stage: StageMetrics,
        fieldWidth: Float = MOCK_FORM_WIDTH
    ): Boolean {
        val left = stage.left + fieldX * stage.scale
        val top = stage.top + fieldY * stage.scale
        val right = stage.left + (fieldX + fieldWidth) * stage.scale
        val bottom = stage.top + (fieldY + MOCK_FORM_FIELD_HEIGHT) * stage.scale
        return pointerX in left..right && pointerY in top..bottom
    }

    private fun mockRememberMeContains(
        pointerX: Float,
        pointerY: Float,
        x: Float,
        y: Float,
        stage: StageMetrics
    ): Boolean {
        val left = stage.left + x * stage.scale
        val top = stage.top + (y - MOCK_REMEMBER_ME_HIT_TOP_PADDING) * stage.scale
        val right = stage.left + (x + MOCK_FORM_WIDTH) * stage.scale
        val bottom = stage.top + (y - MOCK_REMEMBER_ME_HIT_TOP_PADDING + MOCK_REMEMBER_ME_HIT_HEIGHT) * stage.scale
        return pointerX in left..right && pointerY in top..bottom
    }

    private fun mockForgotRecoveryTargetContains(
        pointerX: Float,
        pointerY: Float,
        target: MockForgotRecoveryTarget,
        selectorY: Float,
        stage: StageMetrics
    ): Boolean {
        formLabelPaint.textSize = MOCK_FORGOT_PASSWORD_SELECTOR_LABEL_TEXT_SIZE
        val labelWidth = formLabelPaint.measureText(MOCK_FORGOT_PASSWORD_SELECTOR_TEXT)
        formInputPaint.textSize = MOCK_FORGOT_PASSWORD_SELECTOR_OPTION_TEXT_SIZE
        val optionBoxWidth = mockForgotRecoverySelectorBoxWidth(
            usernameTextWidth = formInputPaint.measureText(MockForgotRecoveryTarget.Username.displayText),
            passwordTextWidth = formInputPaint.measureText(MockForgotRecoveryTarget.Password.displayText)
        )
        val layout = mockForgotRecoverySelectorLayout(
            left = mockForgotPasswordFormX(),
            width = mockForgotPasswordFormWidth(),
            labelWidth = labelWidth,
            boxWidth = optionBoxWidth
        )
        val boxX = when (target) {
            MockForgotRecoveryTarget.Username -> layout.usernameBoxX
            MockForgotRecoveryTarget.Password -> layout.passwordBoxX
        }
        val left = stage.left + boxX * stage.scale
        val top = stage.top + selectorY * stage.scale
        val right = stage.left + (boxX + layout.boxWidth) * stage.scale
        val bottom = stage.top + (selectorY + MOCK_FORGOT_PASSWORD_SELECTOR_BOX_HEIGHT) * stage.scale
        return pointerX in left..right && pointerY in top..bottom
    }

    private fun mockForgotPasswordButtonContains(
        pointerX: Float,
        pointerY: Float,
        buttonX: Float,
        buttonY: Float,
        stage: StageMetrics,
        buttonWidth: Float = MOCK_FORM_WIDTH
    ): Boolean {
        val left = stage.left + buttonX * stage.scale
        val top = stage.top + buttonY * stage.scale
        val right = stage.left + (buttonX + buttonWidth) * stage.scale
        val bottom = stage.top + (buttonY + MOCK_FORGOT_PASSWORD_BUTTON_HEIGHT) * stage.scale
        return pointerX in left..right && pointerY in top..bottom
    }

    private fun mockBackChevronContains(
        pointerX: Float,
        pointerY: Float,
        centerX: Float,
        topY: Float,
        stage: StageMetrics
    ): Boolean {
        val center = stage.left + centerX * stage.scale
        val hitHalfWidth = MOCK_BACK_CHEVRON_HIT_WIDTH * stage.scale / 2f
        val left = center - hitHalfWidth
        val right = center + hitHalfWidth
        val top = stage.top + (topY - (MOCK_BACK_CHEVRON_HIT_HEIGHT - MOCK_BACK_CHEVRON_HEIGHT) / 2f) * stage.scale
        val bottom = top + MOCK_BACK_CHEVRON_HIT_HEIGHT * stage.scale
        return pointerX in left..right && pointerY in top..bottom
    }

    private fun mockCreateAvatarBounds(columnX: Float, firstFieldY: Float): Bounds {
        val scaledAvatarSize = MOCK_CREATE_AVATAR_SIZE * MOCK_CREATE_AVATAR_STACK_SCALE
        return Bounds(
            columnX + (MOCK_FORM_WIDTH - scaledAvatarSize) / 2f,
            firstFieldY +
                MOCK_CREATE_AVATAR_LABEL_BASELINE_FROM_TOP * MOCK_CREATE_AVATAR_STACK_SCALE +
                MOCK_CREATE_AVATAR_TOP_GAP * MOCK_CREATE_AVATAR_STACK_SCALE,
            scaledAvatarSize,
            scaledAvatarSize
        )
    }

    private fun mockCreateAvatarCarouselBounds(
        columnX: Float,
        firstFieldY: Float,
        avatarBounds: Bounds
    ): Bounds {
        val centerX = columnX + MOCK_FORM_WIDTH / 2f
        val contentTop = firstFieldY
        val contentBottom = avatarBounds.y + avatarBounds.h
        val contentCenterY = (contentTop + contentBottom) / 2f
        val chevronSpan = avatarBounds.w +
            (MOCK_CREATE_AVATAR_CHEVRON_SIDE_GAP + MOCK_CREATE_AVATAR_CHEVRON_HIT_WIDTH / 2f) * 2f
        val side = maxOf(chevronSpan, contentBottom - contentTop)
        return Bounds(
            centerX - side / 2f,
            contentCenterY - side / 2f,
            side,
            side
        )
    }

    private fun mockCreateAvatarSlideDistance(scaledAvatarSize: Float): Float {
        return scaledAvatarSize + MOCK_CREATE_AVATAR_CHEVRON_SIDE_GAP * 2f
    }

    private fun mockCreateAvatarChevronCenterX(
        direction: MockAvatarCarouselDirection,
        avatarBounds: Bounds
    ): Float {
        return if (direction == MockAvatarCarouselDirection.Previous) {
            avatarBounds.x - MOCK_CREATE_AVATAR_CHEVRON_SIDE_GAP
        } else {
            avatarBounds.x + avatarBounds.w + MOCK_CREATE_AVATAR_CHEVRON_SIDE_GAP
        }
    }

    private fun mockCreateAvatarChevronCenterY(avatarBounds: Bounds): Float {
        return avatarBounds.y + avatarBounds.h / 2f
    }

    private fun mockCreateAvatarChevronContains(
        pointerX: Float,
        pointerY: Float,
        direction: MockAvatarCarouselDirection,
        avatarBounds: Bounds,
        stage: StageMetrics
    ): Boolean {
        val centerX = stage.left + mockCreateAvatarChevronCenterX(direction, avatarBounds) * stage.scale
        val centerY = stage.top + mockCreateAvatarChevronCenterY(avatarBounds) * stage.scale
        val halfWidth = MOCK_CREATE_AVATAR_CHEVRON_HIT_WIDTH * stage.scale / 2f
        val halfHeight = MOCK_CREATE_AVATAR_CHEVRON_HIT_HEIGHT * stage.scale / 2f
        return pointerX in (centerX - halfWidth)..(centerX + halfWidth) &&
            pointerY in (centerY - halfHeight)..(centerY + halfHeight)
    }

    private fun focusMockSignInField(field: MockSignInField) {
        mockInputDimAwaitingField = field
        mockInputDimActiveField = field
        mockInputDimActiveDropdown = null
        restartMockLandscapeInputLiftAnimation()
        restartMockInputDimAnimation()
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
        mockInputDimAwaitingField = null
        restartMockFormLabelAnimation()
        restartMockFormFocusAnimation()
        restartMockLandscapeInputLiftAnimation()
        restartMockInputDimAnimation()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
        clearFocus()
        postInvalidateOnAnimation()
    }

    private fun mockFormX(): Float {
        return (FIGMA_FRAME_WIDTH - MOCK_FORM_WIDTH) / 2f
    }

    private fun mockCreateFormWidth(): Float {
        return MOCK_CREATE_FORM_COLUMNS * MOCK_FORM_WIDTH +
            (MOCK_CREATE_FORM_COLUMNS - 1) * MOCK_CREATE_FORM_COLUMN_GAP
    }

    private fun mockCreateFormHeight(): Float {
        return MOCK_CREATE_FORM_ROWS * MOCK_FORM_FIELD_HEIGHT +
            (MOCK_CREATE_FORM_ROWS - 1) * MOCK_FORM_FIELD_GAP
    }

    private fun mockCreateFormX(): Float {
        return (FIGMA_FRAME_WIDTH - mockCreateFormWidth()) / 2f
    }

    private fun mockForgotPasswordFormX(): Float {
        return mockForgotPasswordCopyX() + MOCK_FORGOT_PASSWORD_COPY_WIDTH + MOCK_FORGOT_PASSWORD_STACK_GAP
    }

    private fun mockForgotPasswordFormWidth(): Float {
        return MOCK_FORGOT_PASSWORD_FORM_WIDTH
    }

    private fun mockForgotPasswordCopyX(): Float {
        return (FIGMA_FRAME_WIDTH - MOCK_FORGOT_PASSWORD_USABLE_WIDTH) / 2f
    }

    private fun mockForgotPasswordFieldY(yOffset: Float, focusShiftY: Float): Float {
        return MOCK_FORM_Y + yOffset + focusShiftY + MOCK_FORGOT_PASSWORD_FIELD_OFFSET_Y
    }

    private fun mockForgotRecoverySelectorY(yOffset: Float, focusShiftY: Float): Float {
        return MOCK_FORM_Y + yOffset + focusShiftY + MOCK_FORGOT_PASSWORD_SELECTOR_TOP_GAP
    }

    private fun mockForgotPasswordButtonY(yOffset: Float, focusShiftY: Float): Float {
        return mockForgotPasswordFieldY(yOffset, focusShiftY) +
            MOCK_FORM_FIELD_HEIGHT +
            MOCK_FORGOT_PASSWORD_BUTTON_TOP_GAP
    }

    private fun mockForgotPasswordBackChevronTopY(yOffset: Float, focusShiftY: Float): Float {
        return mockForgotPasswordButtonY(yOffset, focusShiftY) +
            MOCK_FORGOT_PASSWORD_BUTTON_HEIGHT +
            MOCK_FORGOT_PASSWORD_HELP_TOP_GAP +
            MOCK_FORGOT_PASSWORD_HELP_LINE_HEIGHT * 2f +
            MOCK_FORGOT_PASSWORD_BACK_CHEVRON_TOP_GAP
    }

    private fun mockForgotRecoverySelectorLayout(
        left: Float,
        width: Float,
        labelWidth: Float,
        boxWidth: Float
    ): ForgotRecoverySelectorLayout {
        val gap = ((width - labelWidth - boxWidth * 2f) / 4f).coerceAtLeast(0f)
        val labelX = left + gap
        val usernameBoxX = labelX + labelWidth + gap
        val passwordBoxX = usernameBoxX + boxWidth + gap
        return ForgotRecoverySelectorLayout(
            labelX = labelX,
            usernameBoxX = usernameBoxX,
            passwordBoxX = passwordBoxX,
            boxWidth = boxWidth
        )
    }

    private fun mockForgotRecoverySelectorBoxWidth(
        usernameTextWidth: Float,
        passwordTextWidth: Float,
        baseBoxWidth: Float = MOCK_FORGOT_PASSWORD_SELECTOR_BOX_WIDTH
    ): Float {
        val widestTextWidth = maxOf(usernameTextWidth, passwordTextWidth)
        val currentHorizontalPadding = ((baseBoxWidth - widestTextWidth) / 2f).coerceAtLeast(0f)
        return widestTextWidth + currentHorizontalPadding * 4f
    }

    private fun activeMockFields(): List<MockSignInField> {
        return when (activeMockFlow) {
            MockAccountFlow.CreateAccount -> MOCK_CREATE_ACCOUNT_FIELDS
            MockAccountFlow.ForgotPassword -> MOCK_FORGOT_PASSWORD_FIELDS
            else -> MOCK_SIGN_IN_FIELDS
        }
    }

    private fun mockActiveFieldX(field: MockSignInField): Float {
        if (activeMockFlow == MockAccountFlow.ForgotPassword) return mockForgotPasswordFormX()
        if (activeMockFlow != MockAccountFlow.CreateAccount) return mockFormX()

        val middleColumnX = mockCreateFormX() + MOCK_FORM_WIDTH + MOCK_CREATE_FORM_COLUMN_GAP
        return when (field) {
            MockSignInField.Username,
            MockSignInField.Password,
            MockSignInField.ConfirmPassword -> mockCreateFormX()
            MockSignInField.Email -> middleColumnX
            MockSignInField.Month -> middleColumnX
            MockSignInField.Day -> middleColumnX + MOCK_DATE_MONTH_WIDTH + MOCK_DATE_FIELD_GAP
            MockSignInField.Year -> middleColumnX + MOCK_DATE_MONTH_WIDTH + MOCK_DATE_DAY_WIDTH + MOCK_DATE_FIELD_GAP * 2f
        }
    }

    private fun mockActiveFieldY(field: MockSignInField, yOffset: Float, focusShiftY: Float): Float {
        if (activeMockFlow == MockAccountFlow.ForgotPassword) {
            return mockForgotPasswordFieldY(yOffset, focusShiftY)
        }

        val rowIndex = if (activeMockFlow == MockAccountFlow.CreateAccount) {
            when (field) {
                MockSignInField.Username,
                MockSignInField.Email -> 0
                MockSignInField.Password -> 1
                MockSignInField.ConfirmPassword,
                MockSignInField.Month,
                MockSignInField.Day,
                MockSignInField.Year -> 2
            }
        } else {
            activeMockFields().indexOf(field).coerceAtLeast(0)
        }
        return MOCK_FORM_Y + yOffset + focusShiftY + rowIndex * (MOCK_FORM_FIELD_HEIGHT + MOCK_FORM_FIELD_GAP)
    }

    private fun mockActiveFieldWidth(field: MockSignInField): Float {
        if (activeMockFlow == MockAccountFlow.ForgotPassword) return mockForgotPasswordFormWidth()

        return when (field) {
            MockSignInField.Month -> MOCK_DATE_MONTH_WIDTH
            MockSignInField.Day -> MOCK_DATE_DAY_WIDTH
            MockSignInField.Year -> MOCK_DATE_YEAR_WIDTH
            else -> MOCK_FORM_WIDTH
        }
    }

    private fun mockActiveFormCenterX(): Float {
        return when (activeMockFlow) {
            MockAccountFlow.CreateAccount -> mockCreateFormX() + mockCreateFormWidth() / 2f
            MockAccountFlow.ForgotPassword -> mockCreateFormX() + mockCreateFormWidth() / 2f
            else -> mockFormX() + MOCK_FORM_WIDTH / 2f
        }
    }

    private fun mockCreateFormColumnForIndex(index: Int): Int {
        return index / MOCK_CREATE_FORM_ROWS
    }

    private fun mockCreateFormRowForIndex(index: Int): Int {
        return index % MOCK_CREATE_FORM_ROWS
    }

    private fun mockDropdownX(dropdown: MockDropdown): Float {
        val middleColumnX = mockCreateFormX() + MOCK_FORM_WIDTH + MOCK_CREATE_FORM_COLUMN_GAP
        return when (dropdown) {
            MockDropdown.SubscriptionTier -> middleColumnX
            MockDropdown.Month -> middleColumnX
            MockDropdown.Day -> middleColumnX + MOCK_DATE_MONTH_WIDTH + MOCK_DATE_FIELD_GAP
            MockDropdown.Year -> middleColumnX + MOCK_DATE_MONTH_WIDTH + MOCK_DATE_DAY_WIDTH + MOCK_DATE_FIELD_GAP * 2f
        }
    }

    private fun mockDropdownY(dropdown: MockDropdown, yOffset: Float, focusShiftY: Float): Float {
        val rowIndex = when (dropdown) {
            MockDropdown.SubscriptionTier -> 1
            MockDropdown.Month,
            MockDropdown.Day,
            MockDropdown.Year -> 2
        }
        return MOCK_FORM_Y + yOffset + focusShiftY + rowIndex * (MOCK_FORM_FIELD_HEIGHT + MOCK_FORM_FIELD_GAP)
    }

    private fun mockDropdownWidth(dropdown: MockDropdown): Float {
        return when (dropdown) {
            MockDropdown.SubscriptionTier -> MOCK_FORM_WIDTH
            MockDropdown.Month -> MOCK_DATE_MONTH_WIDTH
            MockDropdown.Day -> MOCK_DATE_DAY_WIDTH
            MockDropdown.Year -> MOCK_DATE_YEAR_WIDTH
        }
    }

    private fun mockDropdownOptionsY(dropdown: MockDropdown, yOffset: Float, focusShiftY: Float): Float {
        val controlY = mockDropdownY(dropdown, yOffset, focusShiftY)
        return if (mockDropdownUsesDateViewport(dropdown)) {
            mockActiveFieldY(MockSignInField.Email, yOffset, focusShiftY)
        } else {
            controlY + MOCK_FORM_FIELD_HEIGHT
        }
    }

    private fun mockDropdownOpensUp(dropdown: MockDropdown): Boolean {
        return dropdown != MockDropdown.SubscriptionTier
    }

    private fun mockDropdownTextSize(dropdown: MockDropdown): Float {
        return if (mockDropdownOpensUp(dropdown)) MOCK_DATE_DROPDOWN_TEXT_SIZE else MOCK_FORM_LABEL_TEXT_SIZE
    }

    private fun mockDropdownTextInsetX(dropdown: MockDropdown): Float {
        return if (mockDropdownOpensUp(dropdown)) MOCK_DATE_DROPDOWN_TEXT_INSET_X else MOCK_FORM_LABEL_INSET_X
    }

    private fun mockDropdownChevronInsetX(dropdown: MockDropdown): Float {
        return if (mockDropdownOpensUp(dropdown)) MOCK_DATE_DROPDOWN_CHEVRON_INSET_X else 20f
    }

    private fun mockDropdownChevronSize(dropdown: MockDropdown): Float {
        return if (mockDropdownOpensUp(dropdown)) MOCK_DATE_DROPDOWN_CHEVRON_SIZE else MOCK_DROPDOWN_CHEVRON_SIZE
    }

    private fun mockDropdownOptionHeight(dropdown: MockDropdown): Float {
        return if (mockDropdownOpensUp(dropdown)) MOCK_DATE_DROPDOWN_OPTION_HEIGHT else MOCK_DROPDOWN_OPTION_HEIGHT
    }

    private fun mockDropdownOptionTextSize(dropdown: MockDropdown): Float {
        return if (mockDropdownOpensUp(dropdown)) MOCK_DATE_DROPDOWN_OPTION_TEXT_SIZE else 18f
    }

    private fun mockDropdownOptionTextInsetX(dropdown: MockDropdown): Float {
        return if (mockDropdownUsesDateViewport(dropdown)) MOCK_DATE_DROPDOWN_TEXT_INSET_X else MOCK_FORM_LABEL_INSET_X
    }

    private fun mockDropdownUsesDateViewport(dropdown: MockDropdown): Boolean {
        return dropdown != MockDropdown.SubscriptionTier
    }

    private fun mockDropdownVisibleOptionCount(dropdown: MockDropdown): Int {
        val optionCount = mockDropdownOptions(dropdown).size
        val viewportCount = if (mockDropdownUsesDateViewport(dropdown)) {
            MOCK_DATE_DROPDOWN_VISIBLE_OPTION_COUNT
        } else {
            optionCount
        }
        return min(optionCount, viewportCount)
    }

    private fun mockDropdownCanScroll(dropdown: MockDropdown): Boolean {
        return mockDropdownOptions(dropdown).size > mockDropdownVisibleOptionCount(dropdown)
    }

    private fun mockDropdownScrollOffset(dropdown: MockDropdown): Int {
        return (mockDropdownScrollOffsets[dropdown] ?: 0).coerceIn(0, mockDropdownMaxScrollOffset(dropdown))
    }

    private fun mockDropdownMaxScrollOffset(dropdown: MockDropdown): Int {
        return (mockDropdownOptions(dropdown).size - mockDropdownVisibleOptionCount(dropdown)).coerceAtLeast(0)
    }

    private fun setMockDropdownScrollOffset(dropdown: MockDropdown, offset: Int) {
        mockDropdownScrollOffsets[dropdown] = offset.coerceIn(0, mockDropdownMaxScrollOffset(dropdown))
    }

    private fun mockDropdownVisibleOptions(dropdown: MockDropdown): List<String> {
        val options = mockDropdownOptions(dropdown)
        val offset = mockDropdownScrollOffset(dropdown)
        return options.drop(offset).take(mockDropdownVisibleOptionCount(dropdown))
    }

    private fun prepareMockDropdownScroll(dropdown: MockDropdown) {
        if (!mockDropdownCanScroll(dropdown)) {
            setMockDropdownScrollOffset(dropdown, 0)
            return
        }

        val selectedIndex = mockDropdownOptions(dropdown).indexOf(mockDropdownSelectedValue(dropdown))
        setMockDropdownScrollOffset(dropdown, if (selectedIndex >= 0) selectedIndex else 0)
    }

    private fun mockDropdownDisplayText(dropdown: MockDropdown): String {
        return when (dropdown) {
            MockDropdown.SubscriptionTier -> normalizedMembershipTierSelection(subscriptionTierText)
            else -> mockDropdownSelectedValue(dropdown)
        }
    }

    private fun mockDropdownSelectedValue(dropdown: MockDropdown): String {
        return when (dropdown) {
            MockDropdown.SubscriptionTier -> normalizedMembershipTierSelection(subscriptionTierText)
            MockDropdown.Month -> monthText
            MockDropdown.Day -> dayText
            MockDropdown.Year -> yearText
        }
    }

    private fun mockDropdownOptions(dropdown: MockDropdown): List<String> {
        return when (dropdown) {
            MockDropdown.SubscriptionTier -> MOCK_MEMBERSHIP_OPTIONS
            MockDropdown.Month -> MOCK_MONTH_OPTIONS
            MockDropdown.Day -> MOCK_DAY_OPTIONS
            MockDropdown.Year -> MOCK_YEAR_OPTIONS
        }
    }

    private fun setMockDropdownValue(selection: MockDropdownOption) {
        mockInputDimAwaitingField = null
        mockInputDimActiveDropdown = selection.dropdown
        mockInputDimActiveField = null
        when (selection.dropdown) {
            MockDropdown.SubscriptionTier -> subscriptionTierText = normalizedMembershipTierSelection(selection.option)
            MockDropdown.Month -> monthText = selection.option
            MockDropdown.Day -> dayText = selection.option
            MockDropdown.Year -> yearText = selection.option
        }
        notifyIntroSnapshotChanged()
        restartMockLandscapeInputLiftAnimation()
        restartMockInputDimAnimation()
    }

    private fun membershipTierIconForOption(option: String): Bitmap? {
        return when (normalizedMembershipTierSelection(option)) {
            MOCK_MEMBERSHIP_BASIC_TEXT -> membershipTierBasicIcon
            MOCK_MEMBERSHIP_PRO_TEXT -> membershipTierProIcon
            MOCK_MEMBERSHIP_ROCK_STAR_TEXT -> membershipTierRockStarIcon
            else -> null
        }
    }

    private fun membershipTierArtworkIconForOption(option: String): Bitmap? {
        return when (normalizedMembershipTierSelection(option)) {
            MOCK_MEMBERSHIP_BASIC_TEXT -> membershipTierBasicArtworkIcon
            MOCK_MEMBERSHIP_PRO_TEXT -> membershipTierProArtworkIcon
            MOCK_MEMBERSHIP_ROCK_STAR_TEXT -> membershipTierRockStarArtworkIcon
            else -> null
        }
    }

    private fun normalizedMembershipTierSelection(selection: String): String {
        return when (selection) {
            MOCK_MEMBERSHIP_LEGACY_OPTION_1_TEXT -> MOCK_MEMBERSHIP_BASIC_TEXT
            MOCK_MEMBERSHIP_LEGACY_OPTION_2_TEXT -> MOCK_MEMBERSHIP_PRO_TEXT
            MOCK_MEMBERSHIP_LEGACY_OPTION_3_TEXT -> MOCK_MEMBERSHIP_ROCK_STAR_TEXT
            else -> selection
        }
    }

    private fun setMockForgotRecoveryTarget(target: MockForgotRecoveryTarget) {
        if (activeMockFlow != MockAccountFlow.ForgotPassword) return
        if (forgotRecoveryTarget == target) {
            postInvalidateOnAnimation()
            return
        }

        forgotRecoveryTarget = target
        resetMockForgotPasswordSubmission()
        activeComposingText = ""
        mockInputDimAwaitingField = null
        notifyIntroSnapshotChanged()
        if (focusedMockField != null) {
            clearMockSignInFieldFocus()
        } else {
            restartMockFormLabelAnimation()
            restartMockFormFocusAnimation()
            restartMockLandscapeInputLiftAnimation()
            restartMockInputDimAnimation()
            postInvalidateOnAnimation()
        }
    }

    private fun mockForgotPasswordBodyText(): String {
        return forgotRecoveryTarget.bodyText
    }

    private fun mockForgotPasswordHelpText(): String {
        return when (forgotPasswordSubmissionState) {
            MockForgotPasswordSubmissionState.Loading -> MOCK_FORGOT_PASSWORD_LOADING_HELP_TEXT
            MockForgotPasswordSubmissionState.Sent -> forgotRecoveryTarget.sentHelpText
            MockForgotPasswordSubmissionState.Idle -> {
                if (emailText.trim().isEmpty()) {
                    MOCK_FORGOT_PASSWORD_EMPTY_HELP_TEXT
                } else {
                    forgotRecoveryTarget.idleHelpText
                }
            }
        }
    }

    private fun mockForgotPasswordButtonText(): String {
        return when (forgotPasswordSubmissionState) {
            MockForgotPasswordSubmissionState.Loading -> MOCK_FORGOT_PASSWORD_LOADING_BUTTON_TEXT
            MockForgotPasswordSubmissionState.Sent -> forgotRecoveryTarget.sentButtonText
            MockForgotPasswordSubmissionState.Idle -> forgotRecoveryTarget.submitButtonText
        }
    }

    private fun mockForgotPasswordCanSubmit(): Boolean {
        return activeMockFlow == MockAccountFlow.ForgotPassword &&
            forgotPasswordSubmissionState == MockForgotPasswordSubmissionState.Idle &&
            emailText.trim().isNotEmpty()
    }

    private fun startMockForgotPasswordSubmission() {
        if (!mockForgotPasswordCanSubmit()) {
            postInvalidateOnAnimation()
            return
        }

        clearMockSignInFieldFocus()
        forgotPasswordSubmissionState = MockForgotPasswordSubmissionState.Loading
        forgotPasswordSubmissionStartMillis = SystemClock.uptimeMillis()
        notifyIntroSnapshotChanged()
        postInvalidateOnAnimation()
    }

    private fun mockForgotPasswordSubmissionProgress(): Float {
        val startMillis = forgotPasswordSubmissionStartMillis ?: return if (
            forgotPasswordSubmissionState == MockForgotPasswordSubmissionState.Sent
        ) {
            1f
        } else {
            0f
        }
        val elapsed = SystemClock.uptimeMillis() - startMillis
        return (elapsed / MOCK_FORGOT_PASSWORD_SUBMIT_MS.toFloat()).coerceIn(0f, 1f)
    }

    private fun isMockForgotPasswordSubmissionAnimating(): Boolean {
        return forgotPasswordSubmissionState == MockForgotPasswordSubmissionState.Loading
    }

    private fun finishMockForgotPasswordSubmissionIfNeeded() {
        val startMillis = forgotPasswordSubmissionStartMillis ?: return
        if (forgotPasswordSubmissionState != MockForgotPasswordSubmissionState.Loading) {
            forgotPasswordSubmissionStartMillis = null
            return
        }
        if (SystemClock.uptimeMillis() - startMillis < MOCK_FORGOT_PASSWORD_SUBMIT_MS) return

        forgotPasswordSubmissionState = MockForgotPasswordSubmissionState.Sent
        forgotPasswordSubmissionStartMillis = null
        notifyIntroSnapshotChanged()
    }

    private fun resetMockForgotPasswordSubmission() {
        pressedForgotPasswordSubmit = false
        forgotPasswordSubmissionState = MockForgotPasswordSubmissionState.Idle
        forgotPasswordSubmissionStartMillis = null
        notifyIntroSnapshotChanged()
    }

    private fun startMockCreateAvatarCarousel(
        direction: MockAvatarCarouselDirection,
        startProgress: Float = 0f
    ) {
        if (activeMockFlow != MockAccountFlow.CreateAccount || isMockCreateAvatarCarouselAnimating()) return

        val nextIndex = mockCreateAvatarIndexForDirection(direction)
        if (nextIndex == mockCreateAvatarIndex) return

        pressedMockCreateAvatarNav = null
        mockCreateAvatarCarouselFromIndex = mockCreateAvatarIndex
        mockCreateAvatarCarouselToIndex = nextIndex
        mockCreateAvatarCarouselDirection = direction
        mockCreateAvatarCarouselStartProgress = startProgress.coerceIn(0f, 0.96f)
        mockCreateAvatarIndex = nextIndex
        mockCreateAvatarCarouselStartMillis = SystemClock.uptimeMillis()
        notifyIntroSnapshotChanged()
        postInvalidateOnAnimation()
    }

    private fun mockCreateAvatarIndexForDirection(direction: MockAvatarCarouselDirection): Int {
        return (mockCreateAvatarIndex + direction.indexDelta + MOCK_CREATE_AVATAR_COUNT) % MOCK_CREATE_AVATAR_COUNT
    }

    private fun mockCreateAvatarDragDirection(): MockAvatarCarouselDirection? {
        return when {
            mockCreateAvatarDragDeltaX < 0f -> MockAvatarCarouselDirection.Next
            mockCreateAvatarDragDeltaX > 0f -> MockAvatarCarouselDirection.Previous
            else -> null
        }
    }

    private fun mockCreateAvatarDragProgress(): Float {
        val stage = currentStageMetrics() ?: return 0f
        val scaledAvatarSize = MOCK_CREATE_AVATAR_SIZE * MOCK_CREATE_AVATAR_STACK_SCALE
        return mockCreateAvatarDragProgress(mockCreateAvatarSlideDistance(scaledAvatarSize), stage.scale)
    }

    private fun mockCreateAvatarDragProgress(slideDistance: Float, stageScale: Float): Float {
        val slideDistancePx = (slideDistance * stageScale).coerceAtLeast(1f)
        return (abs(mockCreateAvatarDragDeltaX) / slideDistancePx).coerceIn(0f, 1f)
    }

    private fun mockCreateAvatarBitmap(index: Int): Bitmap {
        return when (index.coerceIn(0, MOCK_CREATE_AVATAR_COUNT - 1)) {
            0 -> steveCreateAvatarCharacter
            1 -> martinCreateAvatarCharacter
            else -> jannyCreateAvatarCharacter
        }
    }

    private fun mockCreateAvatarCarouselProgress(): Float {
        val startMillis = mockCreateAvatarCarouselStartMillis ?: return 1f
        val elapsed = SystemClock.uptimeMillis() - startMillis
        val elapsedProgress = (elapsed / MOCK_CREATE_AVATAR_CAROUSEL_MS.toFloat()).coerceIn(0f, 1f)
        return lerpFloat(mockCreateAvatarCarouselStartProgress, 1f, elapsedProgress).coerceIn(0f, 1f)
    }

    private fun isMockCreateAvatarCarouselAnimating(): Boolean {
        return mockCreateAvatarCarouselStartMillis != null
    }

    private fun finishMockCreateAvatarCarouselIfNeeded() {
        val startMillis = mockCreateAvatarCarouselStartMillis ?: return
        if (SystemClock.uptimeMillis() - startMillis < MOCK_CREATE_AVATAR_CAROUSEL_MS) return

        mockCreateAvatarCarouselStartMillis = null
        mockCreateAvatarCarouselFromIndex = mockCreateAvatarIndex
        mockCreateAvatarCarouselToIndex = mockCreateAvatarIndex
        mockCreateAvatarCarouselStartProgress = 0f
    }

    private fun resetMockCreateAvatarCarousel() {
        pressedMockCreateAvatarNav = null
        activeMockCreateAvatarDrag = false
        mockCreateAvatarDragMoved = false
        mockCreateAvatarDragDeltaX = 0f
        mockCreateAvatarIndex = 0
        mockCreateAvatarCarouselStartMillis = null
        mockCreateAvatarCarouselFromIndex = 0
        mockCreateAvatarCarouselToIndex = 0
        mockCreateAvatarCarouselDirection = MockAvatarCarouselDirection.Next
        mockCreateAvatarCarouselStartProgress = 0f
    }

    private fun updateMockFormLabelAnimation() {
        val now = SystemClock.uptimeMillis()
        val elapsed = (now - lastFormLabelAnimationMillis).coerceIn(0L, 48L)
        lastFormLabelAnimationMillis = now
        val step = elapsed / MOCK_FORM_LABEL_FLOAT_ANIMATION_MS.toFloat()
        mockFieldLabelFloatProgress.keys.forEach { field ->
            mockFieldLabelFloatProgress[field] = moveToward(
                mockFieldLabelFloatProgress[field] ?: 0f,
                mockSignInFieldLabelTarget(field),
                step
            )
        }
    }

    private fun updateMockFormFocusAnimation() {
        val now = SystemClock.uptimeMillis()
        val elapsed = (now - lastFormFocusAnimationMillis).coerceIn(0L, 48L)
        lastFormFocusAnimationMillis = now
        val step = elapsed / MOCK_FORM_FOCUS_TRANSITION_MS.toFloat()
        mockFormFocusProgress = moveToward(mockFormFocusProgress, mockFormFocusTarget(), step)
    }

    private fun updateMockLandscapeInputLiftAnimation() {
        val now = SystemClock.uptimeMillis()
        val elapsed = (now - lastMockLandscapeInputLiftAnimationMillis).coerceIn(0L, 48L)
        lastMockLandscapeInputLiftAnimationMillis = now
        val step = elapsed / MOCK_LANDSCAPE_INPUT_LIFT_TRANSITION_MS.toFloat()
        mockLandscapeInputLiftProgress = moveToward(mockLandscapeInputLiftProgress, mockLandscapeInputLiftTarget(), step)
    }

    private fun updateMockInputDimAnimation() {
        val target = mockInputDimTarget()
        if (target > 0f) {
            mockInputDimActiveDropdown = expandedMockDropdown
            mockInputDimActiveField = if (expandedMockDropdown == null) focusedMockField else null
        }

        val now = SystemClock.uptimeMillis()
        val elapsed = (now - lastMockInputDimAnimationMillis).coerceIn(0L, 48L)
        lastMockInputDimAnimationMillis = now
        val step = elapsed / MOCK_INPUT_DIM_TRANSITION_MS.toFloat()
        mockInputDimProgress = moveToward(mockInputDimProgress, target, step)

        if (mockInputDimProgress == 0f && target == 0f) {
            mockInputDimActiveField = null
            mockInputDimActiveDropdown = null
        }
    }

    private fun isMockFormLabelAnimating(): Boolean {
        return mockFieldLabelFloatProgress.any { (field, progress) ->
            progress != mockSignInFieldLabelTarget(field)
        }
    }

    private fun isMockFormFocusAnimating(): Boolean {
        return mockFormFocusProgress != mockFormFocusTarget()
    }

    private fun isMockLandscapeInputLiftAnimating(): Boolean {
        return mockLandscapeInputLiftProgress != mockLandscapeInputLiftTarget()
    }

    private fun isMockInputDimAnimating(): Boolean {
        return mockInputDimProgress != mockInputDimTarget()
    }

    private fun restartMockFormLabelAnimation() {
        lastFormLabelAnimationMillis = SystemClock.uptimeMillis() - 16L
    }

    private fun restartMockFormFocusAnimation() {
        lastFormFocusAnimationMillis = SystemClock.uptimeMillis() - 16L
    }

    private fun restartMockLandscapeInputLiftAnimation() {
        lastMockLandscapeInputLiftAnimationMillis = SystemClock.uptimeMillis() - 16L
    }

    private fun restartMockInputDimAnimation() {
        lastMockInputDimAnimationMillis = SystemClock.uptimeMillis() - 16L
    }

    private fun mockLandscapeInputLiftTarget(): Float {
        if (width <= height) return 0f
        if (mockSignInStartMillis == null || isMockSignInClosing()) return 0f
        return if (
            focusedMockField != null ||
            expandedMockDropdown != null ||
            isSoftKeyboardVisible()
        ) {
            1f
        } else {
            0f
        }
    }

    private fun mockLandscapeInputStageLiftY(stageScale: Float): Float {
        if (width <= height || mockSignInStartMillis == null) return 0f
        val liftMotion = FastOutSlowInEasing.transform(mockLandscapeInputLiftProgress)
        return MOCK_FORM_LANDSCAPE_FOCUS_SHIFT_Y * stageScale * liftMotion
    }

    private fun isSoftKeyboardVisible(): Boolean {
        val rootInsets = ViewCompat.getRootWindowInsets(this)
        if (rootInsets != null) {
            val imeInsets = rootInsets.getInsets(WindowInsetsCompat.Type.ime())
            if (rootInsets.isVisible(WindowInsetsCompat.Type.ime()) || imeInsets.bottom > 0) return true
        }

        val rootHeight = rootView?.height ?: 0
        if (rootHeight <= 0) return false
        getWindowVisibleDisplayFrame(tempWindowRect)
        val obscuredHeight = rootHeight - tempWindowRect.height()
        return obscuredHeight > rootHeight * 0.18f
    }

    private fun mockInputDimTarget(): Float {
        if (mockSignInStartMillis == null || isMockSignInClosing()) return 0f
        if (expandedMockDropdown != null) return 1f
        val awaitingField = mockInputDimAwaitingField ?: return 0f
        return if (focusedMockField == awaitingField) 1f else 0f
    }

    private fun mockFormFocusTarget(): Float {
        if (mockSignInStartMillis == null || isMockSignInClosing()) return 0f
        if (width > height) {
            return if (focusedMockField != null || expandedMockDropdown != null) 1f else 0f
        }
        if (activeMockFlow == MockAccountFlow.CreateAccount) return 0f
        return if (
            focusedMockField != null ||
            activeMockFields().any { mockSignInFieldText(it).isNotEmpty() }
        ) {
            1f
        } else {
            0f
        }
    }

    private fun mockFormFocusShiftY(formFocusMotion: Float): Float {
        if (width > height) return 0f
        return MOCK_FORM_FOCUS_SHIFT_Y * formFocusMotion.coerceIn(0f, 1f)
    }

    private fun mockLogoFocusShiftY(formFocusMotion: Float): Float {
        return if (width > height) {
            val liftMotion = FastOutSlowInEasing.transform(mockLandscapeInputLiftProgress)
            -(LOGO_FINAL_TOP + LOGO_FINAL_HEIGHT + 64f) * liftMotion
        } else {
            mockFormFocusShiftY(formFocusMotion)
        }
    }

    private fun mockSignInFieldLabelTarget(field: MockSignInField): Float {
        return if (focusedMockField == field || mockSignInFieldText(field).isNotEmpty()) 1f else 0f
    }

    private fun mockSignInFieldLabelProgress(field: MockSignInField): Float {
        return mockFieldLabelFloatProgress[field] ?: 0f
    }

    private fun mockSignInFieldLabel(field: MockSignInField): String {
        if (activeMockFlow == MockAccountFlow.ForgotPassword && field == MockSignInField.Email) {
            return forgotRecoveryTarget.inputLabel
        }

        return when (field) {
            MockSignInField.Username -> MOCK_FORM_USERNAME_TEXT
            MockSignInField.Password -> MOCK_FORM_PASSWORD_TEXT
            MockSignInField.ConfirmPassword -> MOCK_FORM_CONFIRM_PASSWORD_TEXT
            MockSignInField.Email -> MOCK_FORM_EMAIL_TEXT
            MockSignInField.Month -> MOCK_FORM_MONTH_TEXT
            MockSignInField.Day -> MOCK_FORM_DAY_TEXT
            MockSignInField.Year -> MOCK_FORM_YEAR_TEXT
        }
    }

    private fun mockSignInFieldText(field: MockSignInField): String {
        return when (field) {
            MockSignInField.Username -> usernameText
            MockSignInField.Password -> passwordText
            MockSignInField.ConfirmPassword -> confirmPasswordText
            MockSignInField.Email -> emailText
            MockSignInField.Month -> monthText
            MockSignInField.Day -> dayText
            MockSignInField.Year -> yearText
        }
    }

    private fun mockSignInFieldDisplayText(field: MockSignInField): String {
        return when (field) {
            MockSignInField.Username -> usernameText
            MockSignInField.Password -> "*".repeat(passwordText.length)
            MockSignInField.ConfirmPassword -> "*".repeat(confirmPasswordText.length)
            MockSignInField.Email -> emailText
            MockSignInField.Month -> monthText
            MockSignInField.Day -> dayText
            MockSignInField.Year -> yearText
        }
    }

    private fun setMockSignInFieldText(field: MockSignInField, text: String) {
        val limitedText = text.take(MOCK_FORM_INPUT_MAX_CHARS)
        when (field) {
            MockSignInField.Username -> usernameText = limitedText
            MockSignInField.Password -> passwordText = limitedText
            MockSignInField.ConfirmPassword -> confirmPasswordText = limitedText
            MockSignInField.Email -> emailText = limitedText
            MockSignInField.Month -> monthText = limitedText
            MockSignInField.Day -> dayText = limitedText
            MockSignInField.Year -> yearText = limitedText
        }
        if (activeMockFlow == MockAccountFlow.ForgotPassword && field == MockSignInField.Email) {
            resetMockForgotPasswordSubmission()
        }
        notifyIntroSnapshotChanged()
        if (mockInputDimAwaitingField == field) {
            mockInputDimAwaitingField = null
        }
        restartMockFormLabelAnimation()
        restartMockFormFocusAnimation()
        restartMockInputDimAnimation()
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
        val fields = activeMockFields()
        val fieldIndex = fields.indexOf(focusedMockField)
        if (fieldIndex >= 0 && fieldIndex < fields.lastIndex && actionCode != EditorInfo.IME_ACTION_DONE) {
            focusMockSignInField(fields[fieldIndex + 1])
        } else if (
            activeMockFlow == MockAccountFlow.ForgotPassword &&
            focusedMockField == MockSignInField.Email &&
            mockForgotPasswordCanSubmit()
        ) {
            startMockForgotPasswordSubmission()
        } else {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
            clearMockSignInFieldFocus()
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

    private fun wrappedTextLines(text: String, maxWidth: Float, paint: Paint): List<String> {
        val lines = mutableListOf<String>()
        text.split(Regex("\\n\\s*\\n")).forEachIndexed { paragraphIndex, paragraph ->
            if (paragraphIndex > 0) {
                lines += ""
            }

            var currentLine = ""
            paragraph.replace('\n', ' ')
                .split(Regex("\\s+"))
                .filter { it.isNotEmpty() }
                .forEach { word ->
                    val candidateLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    if (currentLine.isEmpty() || paint.measureText(candidateLine) <= maxWidth) {
                        currentLine = candidateLine
                    } else {
                        lines += currentLine
                        currentLine = word
                    }
                }
            if (currentLine.isNotEmpty()) {
                lines += currentLine
            }
        }
        return lines
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
            top = (height - FIGMA_FRAME_HEIGHT * stageScale) / 2f + mockLandscapeInputStageLiftY(stageScale),
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

    private fun accountPromptCreateCenterX(): Float {
        return mockCreateFormX() + MOCK_FORM_WIDTH / 2f
    }

    private fun accountPromptForgotCenterX(): Float {
        return (FIGMA_FRAME_WIDTH - MOCK_FORGOT_PASSWORD_USABLE_WIDTH) / 2f +
            MOCK_FORGOT_PASSWORD_USABLE_WIDTH * 0.8f
    }

    private fun currentIntroSnapshot(): CinerificIntroSnapshot {
        val activeFlowName = activeMockFlow
            ?.takeIf { mockSignInStartMillis != null && mockSignInTransitionTargetProgress > 0f }
            ?.name
            .orEmpty()
        return CinerificIntroSnapshot(
            activeFlowName = activeFlowName,
            usernameText = usernameText,
            passwordText = passwordText,
            confirmPasswordText = confirmPasswordText,
            emailText = emailText,
            monthText = monthText,
            dayText = dayText,
            yearText = yearText,
            subscriptionTierText = normalizedMembershipTierSelection(subscriptionTierText),
            forgotRecoveryTargetName = forgotRecoveryTarget.name,
            forgotPasswordSubmissionStateName = forgotPasswordSubmissionState.name,
            rememberMeChecked = rememberMeChecked,
            createAvatarIndex = mockCreateAvatarIndex
        )
    }

    private fun notifyIntroSnapshotChanged() {
        val snapshot = currentIntroSnapshot()
        if (appliedIntroSnapshot == snapshot) return

        appliedIntroSnapshot = snapshot
        onIntroSnapshotChanged?.invoke(snapshot)
    }

    private fun openMockSignInScreen() {
        if (mockSignInStartMillis != null && mockSignInTransitionTargetProgress == 1f) return
        val currentProgress = mockSignInProgress()
        activeMockFlow = MockAccountFlow.SignIn
        mockSignInStartMillis = SystemClock.uptimeMillis()
        mockSignInTransitionStartProgress = currentProgress
        mockSignInTransitionTargetProgress = 1f
        notifyIntroSnapshotChanged()
        postInvalidateOnAnimation()
    }

    private fun openMockCreateAccountScreen() {
        if (mockSignInStartMillis != null && mockSignInTransitionTargetProgress == 1f) return
        val currentProgress = mockSignInProgress()
        activeMockFlow = MockAccountFlow.CreateAccount
        mockSignInStartMillis = SystemClock.uptimeMillis()
        mockSignInTransitionStartProgress = currentProgress
        mockSignInTransitionTargetProgress = 1f
        notifyIntroSnapshotChanged()
        postInvalidateOnAnimation()
    }

    private fun openMockForgotPasswordScreen() {
        if (mockSignInStartMillis != null && mockSignInTransitionTargetProgress == 1f) return
        val currentProgress = mockSignInProgress()
        activeMockFlow = MockAccountFlow.ForgotPassword
        forgotRecoveryTarget = MockForgotRecoveryTarget.Password
        emailText = ""
        resetMockForgotPasswordSubmission()
        mockSignInStartMillis = SystemClock.uptimeMillis()
        mockSignInTransitionStartProgress = currentProgress
        mockSignInTransitionTargetProgress = 1f
        notifyIntroSnapshotChanged()
        postInvalidateOnAnimation()
    }

    private fun closeMockSignInScreen() {
        if (mockSignInStartMillis == null) return
        val currentProgress = mockSignInProgress()
        pressedMockField = null
        pressedRememberMe = false
        pressedBackChevron = false
        pressedMockCreateAvatarNav = null
        pressedMockDropdown = null
        pressedMockDropdownOption = null
        pressedForgotRecoveryTarget = null
        pressedForgotPasswordSubmit = false
        activeMockDropdownScroll = null
        mockDropdownScrollMoved = false
        activeComposingText = ""
        mockInputDimAwaitingField = null
        usernameText = ""
        passwordText = ""
        confirmPasswordText = ""
        emailText = ""
        monthText = MOCK_FORM_MONTH_TEXT
        dayText = MOCK_FORM_DAY_TEXT
        yearText = MOCK_FORM_YEAR_TEXT
        subscriptionTierText = MOCK_FORM_SUBSCRIPTION_TIER_TEXT
        forgotRecoveryTarget = MockForgotRecoveryTarget.Password
        resetMockForgotPasswordSubmission()
        resetMockCreateAvatarCarousel()
        expandedMockDropdown = null
        mockDropdownScrollOffsets.keys.forEach { dropdown ->
            mockDropdownScrollOffsets[dropdown] = 0
        }
        focusedMockField = null
        restartMockFormLabelAnimation()
        restartMockFormFocusAnimation()
        restartMockLandscapeInputLiftAnimation()
        restartMockInputDimAnimation()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
        clearFocus()
        mockSignInStartMillis = SystemClock.uptimeMillis()
        mockSignInTransitionStartProgress = currentProgress
        mockSignInTransitionTargetProgress = 0f
        notifyIntroSnapshotChanged()
        postInvalidateOnAnimation()
    }

    private fun mockSignInProgress(): Float {
        val startMillis = mockSignInStartMillis ?: return 0f
        val elapsed = SystemClock.uptimeMillis() - startMillis
        val progress = (elapsed / mockSignInTransitionDurationMs().toFloat()).coerceIn(0f, 1f)
        return lerpFloat(
            mockSignInTransitionStartProgress,
            mockSignInTransitionTargetProgress,
            progress
        )
    }

    private fun isMockSignInAnimating(): Boolean {
        val startMillis = mockSignInStartMillis ?: return false
        return SystemClock.uptimeMillis() - startMillis < mockSignInTransitionDurationMs()
    }

    private fun isMockSignInClosing(): Boolean {
        return mockSignInStartMillis != null && mockSignInTransitionTargetProgress == 0f
    }

    private fun mockSignInTransitionDurationMs(): Long {
        val distance = abs(mockSignInTransitionTargetProgress - mockSignInTransitionStartProgress)
        return (MOCK_SIGN_IN_TRANSITION_MS * distance)
            .roundToInt()
            .coerceAtLeast(1)
            .toLong()
    }

    private fun finishMockSignInTransitionIfNeeded() {
        val startMillis = mockSignInStartMillis ?: return
        if (SystemClock.uptimeMillis() - startMillis < mockSignInTransitionDurationMs()) return

        if (mockSignInTransitionTargetProgress == 0f) {
            mockSignInStartMillis = null
            activeMockFlow = null
            mockSignInTransitionStartProgress = 0f
            mockSignInTransitionTargetProgress = 0f
            pressedMockField = null
            pressedRememberMe = false
            pressedBackChevron = false
            pressedMockCreateAvatarNav = null
            pressedMockDropdown = null
            pressedMockDropdownOption = null
            pressedForgotRecoveryTarget = null
            pressedForgotPasswordSubmit = false
            activeMockDropdownScroll = null
            mockDropdownScrollMoved = false
            mockDragReturnInProgress = false
            resetMockForgotPasswordSubmission()
            resetMockCreateAvatarCarousel()
            notifyIntroSnapshotChanged()
            return
        }

        mockSignInTransitionStartProgress = 1f
        mockSignInTransitionTargetProgress = 1f
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

internal data class CinerificIntroSnapshot(
    val activeFlowName: String = "",
    val usernameText: String = "",
    val passwordText: String = "",
    val confirmPasswordText: String = "",
    val emailText: String = "",
    val monthText: String = MOCK_FORM_MONTH_TEXT,
    val dayText: String = MOCK_FORM_DAY_TEXT,
    val yearText: String = MOCK_FORM_YEAR_TEXT,
    val subscriptionTierText: String = MOCK_FORM_SUBSCRIPTION_TIER_TEXT,
    val forgotRecoveryTargetName: String = "Password",
    val forgotPasswordSubmissionStateName: String = "Idle",
    val rememberMeChecked: Boolean = false,
    val createAvatarIndex: Int = 0
)

private enum class MockAccountFlow {
    SignIn,
    CreateAccount,
    ForgotPassword
}

private enum class MockSignInField {
    Username,
    Password,
    ConfirmPassword,
    Email,
    Month,
    Day,
    Year
}

private enum class MockDropdown {
    SubscriptionTier,
    Month,
    Day,
    Year
}

private enum class MockForgotRecoveryTarget(
    val displayText: String,
    val inputLabel: String,
    val bodyText: String,
    val submitButtonText: String,
    val sentButtonText: String,
    val idleHelpText: String,
    val sentHelpText: String
) {
    Username(
        MOCK_FORM_USERNAME_TEXT,
        MOCK_FORM_USERNAME_TEXT,
        MOCK_FORGOT_PASSWORD_USERNAME_BODY_TEXT,
        MOCK_FORGOT_PASSWORD_USERNAME_BUTTON_TEXT,
        MOCK_FORGOT_PASSWORD_USERNAME_SENT_BUTTON_TEXT,
        MOCK_FORGOT_PASSWORD_USERNAME_HELP_TEXT,
        MOCK_FORGOT_PASSWORD_USERNAME_SENT_HELP_TEXT
    ),
    Password(
        MOCK_FORM_PASSWORD_TEXT,
        MOCK_FORM_EMAIL_TEXT,
        MOCK_FORGOT_PASSWORD_PASSWORD_BODY_TEXT,
        MOCK_FORGOT_PASSWORD_PASSWORD_BUTTON_TEXT,
        MOCK_FORGOT_PASSWORD_PASSWORD_SENT_BUTTON_TEXT,
        MOCK_FORGOT_PASSWORD_PASSWORD_HELP_TEXT,
        MOCK_FORGOT_PASSWORD_PASSWORD_SENT_HELP_TEXT
    )
}

private enum class MockForgotPasswordSubmissionState {
    Idle,
    Loading,
    Sent
}

private enum class MockAvatarCarouselDirection(
    val indexDelta: Int,
    val stageDirection: Float
) {
    Previous(indexDelta = -1, stageDirection = -1f),
    Next(indexDelta = 1, stageDirection = 1f)
}

private data class MockDropdownOption(
    val dropdown: MockDropdown,
    val option: String
)

private data class ForgotRecoverySelectorLayout(
    val labelX: Float,
    val usernameBoxX: Float,
    val passwordBoxX: Float,
    val boxWidth: Float
)

private val MOCK_SIGN_IN_FIELDS = listOf(
    MockSignInField.Username,
    MockSignInField.Password
)

private val MOCK_CREATE_ACCOUNT_FIELDS = listOf(
    MockSignInField.Username,
    MockSignInField.Password,
    MockSignInField.ConfirmPassword,
    MockSignInField.Email
)

private val MOCK_FORGOT_PASSWORD_FIELDS = listOf(
    MockSignInField.Email
)

private val MOCK_MEMBERSHIP_OPTIONS = listOf(
    MOCK_MEMBERSHIP_BASIC_TEXT,
    MOCK_MEMBERSHIP_PRO_TEXT,
    MOCK_MEMBERSHIP_ROCK_STAR_TEXT
)

private val MOCK_MONTH_OPTIONS = listOf(
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"
)

private val MOCK_DAY_OPTIONS = (1..31).map { it.toString() }

private val MOCK_YEAR_OPTIONS = (1900..2026).map { it.toString() }

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

private inline fun <reified T : Enum<T>> enumValueOrNull(name: String): T? {
    return runCatching { enumValueOf<T>(name) }.getOrNull()
}

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
