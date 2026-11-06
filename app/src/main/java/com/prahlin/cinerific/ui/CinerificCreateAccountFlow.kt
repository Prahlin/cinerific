package com.prahlin.cinerific.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal data class CinerificCreateAccountDraft(
    val username: String,
    val email: String,
    val birthday: String,
    val membershipTier: String,
    val profile: CinerificProfile
)

private enum class CreateAccountContinuationStep {
    Terms,
    Payment,
    Review
}

private enum class CreateAccountPaymentChoice(val title: String, val detail: String) {
    GooglePlay(
        title = "Google Play",
        detail = "Secure subscription checkout through Google Play Billing"
    ),
    PayPal(
        title = "PayPal",
        detail = "Continue through a secure PayPal checkout session"
    ),
    CreditCard(
        title = "Credit Card",
        detail = "Use a secure hosted checkout for major credit cards"
    ),
    ConfigureLater(
        title = "Set up later",
        detail = "Create the account now and finish billing before protected playback"
    )
}

private val CreateFlowPurple = Color(0xFF600878)
private val CreateFlowPurpleLight = Color(0xFFB66BD0)
private val CreateFlowSurface = Color(0xFF170B1C)
private val CreateFlowSurfaceRaised = Color(0xFF26142D)
private val CreateFlowTextMuted = Color(0xFFC7B9CB)

@Composable
internal fun CinerificCreateAccountContinuation(
    draft: CinerificCreateAccountDraft,
    onBackToDetails: () -> Unit,
    onAccountCreated: (CinerificProfile) -> Unit
) {
    var stepName by rememberSaveable { mutableStateOf(CreateAccountContinuationStep.Terms.name) }
    var acceptedTerms by rememberSaveable { mutableStateOf(false) }
    var acceptedPrivacy by rememberSaveable { mutableStateOf(false) }
    var paymentChoiceName by rememberSaveable {
        mutableStateOf(CreateAccountPaymentChoice.GooglePlay.name)
    }
    val step = enumValueOf<CreateAccountContinuationStep>(stepName)
    val paymentChoice = enumValueOf<CreateAccountPaymentChoice>(paymentChoiceName)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black, Color(0xFF24062C), Color.Black)
                )
            )
            .systemBarsPadding()
    ) {
        val horizontalPadding = if (maxWidth < 600.dp) 22.dp else 52.dp
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding, vertical = 26.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            CreateFlowHeader(
                step = step,
                onBack = when (step) {
                    CreateAccountContinuationStep.Terms -> onBackToDetails
                    CreateAccountContinuationStep.Payment -> {
                        { stepName = CreateAccountContinuationStep.Terms.name }
                    }
                    CreateAccountContinuationStep.Review -> {
                        { stepName = CreateAccountContinuationStep.Payment.name }
                    }
                }
            )

            when (step) {
                CreateAccountContinuationStep.Terms -> {
                    CreateFlowSection(
                        title = "Terms & Privacy",
                        subtitle = "Please review the essentials before creating your Cinerific account."
                    ) {
                        Text(
                            text = "Cinerific provides personalized streaming profiles, membership features, and playback services. Your account information is used to authenticate you, manage profiles and entitlements, protect the service, and communicate important account events.",
                            color = Color.White,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                        Text(
                            text = "Memberships renew according to the billing terms shown during secure checkout. You may cancel future renewal through the payment provider. Availability and playback rights may vary by region and membership tier.",
                            color = Color.White,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                        Text(
                            text = "Draft product copy — final legal Terms of Service and Privacy Policy must replace this text before release.",
                            color = CreateFlowPurpleLight,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    CreateFlowConsentRow(
                        checked = acceptedTerms,
                        text = "I accept the Terms & Conditions",
                        onCheckedChange = { acceptedTerms = it }
                    )
                    CreateFlowConsentRow(
                        checked = acceptedPrivacy,
                        text = "I acknowledge the Privacy Policy",
                        onCheckedChange = { acceptedPrivacy = it }
                    )
                    CreateFlowPrimaryButton(
                        text = "Continue to payment",
                        enabled = acceptedTerms && acceptedPrivacy,
                        onClick = { stepName = CreateAccountContinuationStep.Payment.name }
                    )
                }

                CreateAccountContinuationStep.Payment -> {
                    CreateFlowSection(
                        title = "Payment",
                        subtitle = "Choose how to activate the ${draft.membershipTier} membership."
                    ) {
                        CreateFlowSummaryRow("Membership", draft.membershipTier)
                        CreateFlowPaymentChoice(
                            choice = CreateAccountPaymentChoice.GooglePlay,
                            selected = paymentChoice == CreateAccountPaymentChoice.GooglePlay,
                            onSelect = { paymentChoiceName = CreateAccountPaymentChoice.GooglePlay.name }
                        )
                        CreateFlowPaymentChoice(
                            choice = CreateAccountPaymentChoice.PayPal,
                            selected = paymentChoice == CreateAccountPaymentChoice.PayPal,
                            onSelect = { paymentChoiceName = CreateAccountPaymentChoice.PayPal.name }
                        )
                        CreateFlowPaymentChoice(
                            choice = CreateAccountPaymentChoice.CreditCard,
                            selected = paymentChoice == CreateAccountPaymentChoice.CreditCard,
                            onSelect = { paymentChoiceName = CreateAccountPaymentChoice.CreditCard.name }
                        )
                        CreateFlowPaymentChoice(
                            choice = CreateAccountPaymentChoice.ConfigureLater,
                            selected = paymentChoice == CreateAccountPaymentChoice.ConfigureLater,
                            onSelect = { paymentChoiceName = CreateAccountPaymentChoice.ConfigureLater.name }
                        )
                        Text(
                            text = "No card or PayPal credentials are collected by this screen. Google Play Billing or a backend-provided secure checkout session will handle payment when integration is enabled.",
                            color = CreateFlowTextMuted,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                    CreateFlowPrimaryButton(
                        text = "Continue to review",
                        enabled = true,
                        onClick = { stepName = CreateAccountContinuationStep.Review.name }
                    )
                }

                CreateAccountContinuationStep.Review -> {
                    CreateFlowSection(
                        title = "Review your account",
                        subtitle = "Confirm these details before account creation."
                    ) {
                        CreateFlowSummaryRow("Username", draft.username)
                        CreateFlowSummaryRow("Email", draft.email)
                        CreateFlowSummaryRow("Birthday", draft.birthday)
                        CreateFlowSummaryRow("Profile", draft.profile.name)
                        CreateFlowSummaryRow("Membership", draft.membershipTier)
                        CreateFlowSummaryRow("Payment", paymentChoice.title)
                        Text(
                            text = "The current prototype completes locally. A later backend handoff will create the account, verify email, establish the secure session, and return membership entitlements here.",
                            color = CreateFlowTextMuted,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                    CreateFlowPrimaryButton(
                        text = "Create Account",
                        enabled = true,
                        onClick = { onAccountCreated(draft.profile) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun CreateFlowHeader(
    step: CreateAccountContinuationStep,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                .clickable(onClick = onBack)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Back", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Create My Account",
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Step ${step.ordinal + 2} of 4",
                color = CreateFlowTextMuted,
                fontSize = 14.sp
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .height(9.dp)
                        .width(30.dp)
                        .clip(CircleShape)
                        .background(
                            if (index <= step.ordinal + 1) CreateFlowPurpleLight
                            else Color.White.copy(alpha = 0.18f)
                        )
                )
            }
        }
    }
}

@Composable
private fun CreateFlowSection(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CreateFlowSurface.copy(alpha = 0.96f))
            .border(1.dp, CreateFlowPurpleLight.copy(alpha = 0.55f), RoundedCornerShape(22.dp))
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = CreateFlowTextMuted, fontSize = 15.sp, lineHeight = 21.sp)
        content()
    }
}

@Composable
private fun CreateFlowConsentRow(
    checked: Boolean,
    text: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CreateFlowSurfaceRaised)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = CreateFlowPurpleLight,
                checkmarkColor = Color.Black,
                uncheckedColor = Color.White.copy(alpha = 0.7f)
            )
        )
        Text(text, color = Color.White, fontSize = 16.sp)
    }
}

@Composable
private fun CreateFlowPaymentChoice(
    choice: CreateAccountPaymentChoice,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(if (selected) CreateFlowPurple.copy(alpha = 0.42f) else CreateFlowSurfaceRaised)
            .border(
                1.dp,
                if (selected) CreateFlowPurpleLight else Color.White.copy(alpha = 0.2f),
                RoundedCornerShape(15.dp)
            )
            .clickable(onClick = onSelect)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .height(20.dp)
                .widthIn(min = 20.dp, max = 20.dp)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape)
                .padding(4.dp)
                .background(if (selected) Color.White else Color.Transparent, CircleShape)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(choice.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(choice.detail, color = CreateFlowTextMuted, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun CreateFlowSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = CreateFlowTextMuted,
            fontSize = 14.sp,
            modifier = Modifier.weight(0.38f)
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.62f)
        )
    }
}

@Composable
private fun CreateFlowPrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(
                if (enabled) {
                    Brush.horizontalGradient(listOf(CreateFlowPurple, Color(0xFF9A32B5)))
                } else {
                    Brush.horizontalGradient(
                        listOf(Color(0xFF3B3340), Color(0xFF3B3340))
                    )
                }
            )
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 20.dp, vertical = 17.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) Color.White else Color.White.copy(alpha = 0.45f),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
