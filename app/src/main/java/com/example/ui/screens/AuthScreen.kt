package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Agriculture
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.DroneType
import com.example.data.model.UserRole
import com.example.ui.components.KrishivantriBrandMark
import com.example.ui.theme.AlertRust
import com.example.ui.theme.AlertRustBg
import com.example.ui.theme.EmeraldPulse
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.SoftSage
import com.example.ui.viewmodel.AuthUiState

/**
 * Unified Login / Signup Page (First Page / Entry Point).
 * Public users see ONLY two roles: Farmer & Drone Pilot.
 * Zero insider metrics or admin panels are visible on this screen.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AuthScreen(
    state: AuthUiState,
    onSelectRole: (UserRole) -> Unit,
    onToggleSignUp: (Boolean) -> Unit,
    onSubmitFarmer: (name: String, phone: String, farmerId: String) -> Unit,
    onSubmitPilot: (
        name: String,
        phone: String,
        droneType: DroneType,
        tankCapacity: String,
        licenseUri: String,
        bankUpiId: String
    ) -> Unit,
    onSubmitAdmin: (phone: String, passcode: String, onError: (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var farmerId by rememberSaveable { mutableStateOf("") }
    var selectedDroneType by rememberSaveable { mutableStateOf(DroneType.QUADCOPTER) }
    var tankCapacity by rememberSaveable { mutableStateOf("10") }
    var bankUpiId by rememberSaveable { mutableStateOf("") }
    var licenseUri by rememberSaveable { mutableStateOf("") }

    var showHiddenAdminDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            licenseUri = uri.toString()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Top Brand Header
            KrishivantriBrandMark()

            // Editorial Hero Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.height(172.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.img_krishivantri_hero_1794983437632),
                        contentDescription = "Autonomous agricultural spraying drone over green terraced farmland",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        ForestGreen.copy(alpha = 0.88f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Precision Aerial Crop Care",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Book verified drone spraying or fly missions in your district.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftSage
                        )
                    }
                }
            }

            // Strictly Two Public Role Options: Farmer & Drone Pilot
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Continue as",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RoleOptionCard(
                        title = "Farmer",
                        subtitle = "Book & map fields",
                        icon = Icons.Rounded.Agriculture,
                        selected = state.selectedRole == UserRole.FARMER,
                        onClick = { onSelectRole(UserRole.FARMER) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("role_farmer_card")
                    )
                    RoleOptionCard(
                        title = "Drone Pilot",
                        subtitle = "Fly & earn 90%",
                        icon = Icons.Rounded.FlightTakeoff,
                        selected = state.selectedRole == UserRole.PILOT,
                        onClick = { onSelectRole(UserRole.PILOT) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("role_pilot_card")
                    )
                }
            }

            // Auth Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Sign Up / Sign In Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(4.dp)
                    ) {
                        AuthModeTab(
                            label = "New Registration",
                            selected = state.isSignUpMode,
                            onClick = { onToggleSignUp(true) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("auth_tab_signup")
                        )
                        AuthModeTab(
                            label = "Existing Login",
                            selected = !state.isSignUpMode,
                            onClick = { onToggleSignUp(false) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("auth_tab_login")
                        )
                    }

                    AnimatedVisibility(visible = state.isSignUpMode) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_name_input")
                        )
                    }

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 15) phone = it },
                        label = {
                            Text(
                                if (state.selectedRole == UserRole.PILOT) "Contact Number"
                                else "Mobile Number"
                            )
                        },
                        leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_phone_input")
                    )

                    if (state.isSignUpMode && state.selectedRole == UserRole.FARMER) {
                        OutlinedTextField(
                            value = farmerId,
                            onValueChange = { farmerId = it },
                            label = { Text("Farmer ID (for verification)") },
                            placeholder = { Text("e.g. KISAN-MH-4082") },
                            leadingIcon = { Icon(Icons.Rounded.Badge, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_farmer_id_input")
                        )
                    }

                    if (state.isSignUpMode && state.selectedRole == UserRole.PILOT) {
                        Text(
                            text = "Drone Configuration",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DroneType.entries.forEach { type ->
                                val active = selectedDroneType == type
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            selectedDroneType = type
                                            tankCapacity = if (type == DroneType.HEXACOPTER) "16" else "10"
                                        }
                                        .border(
                                            width = if (active) 1.5.dp else 1.dp,
                                            color = if (active) ForestGreen else MaterialTheme.colorScheme.outline,
                                            shape = RoundedCornerShape(14.dp)
                                        ),
                                    color = if (active) SoftSage else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = type.label,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (active) ForestGreen else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = tankCapacity,
                            onValueChange = { tankCapacity = it },
                            label = { Text("Tank Capacity (Liters)") },
                            leadingIcon = { Icon(Icons.Rounded.WaterDrop, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_tank_capacity_input")
                        )

                        OutlinedTextField(
                            value = bankUpiId,
                            onValueChange = { bankUpiId = it },
                            label = { Text("UPI ID / Bank Account for 90% Payouts") },
                            placeholder = { Text("e.g. pilotname@upi") },
                            leadingIcon = {
                                Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_upi_input")
                        )

                        // Drone Pilot License Upload using zero-permission Android Photo Picker
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .padding(12.dp)
                                    .testTag("upload_license_button"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (licenseUri.isNotBlank()) {
                                    AsyncImage(
                                        model = licenseUri,
                                        contentDescription = "Uploaded pilot license preview",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SoftSage),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.UploadFile,
                                            contentDescription = "Upload pilot license",
                                            tint = ForestGreen
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (licenseUri.isNotBlank()) "Drone Pilot License Attached"
                                        else "Upload Drone Pilot License",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = ForestGreen
                                    )
                                    Text(
                                        text = if (licenseUri.isNotBlank()) "Ready for admin verification • Tap to change"
                                        else "DGCA / RPC certificate image required",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (licenseUri.isNotBlank()) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = EmeraldPulse
                                    )
                                }
                            }
                        }
                    }

                    if (!state.errorMessage.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AlertRustBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = AlertRust,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (state.selectedRole == UserRole.FARMER) {
                                onSubmitFarmer(name, phone, farmerId)
                            } else {
                                onSubmitPilot(
                                    name,
                                    phone,
                                    selectedDroneType,
                                    tankCapacity,
                                    licenseUri,
                                    bankUpiId
                                )
                            }
                        },
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("auth_submit_button")
                    ) {
                        val actionText = if (state.isSignUpMode) {
                            if (state.selectedRole == UserRole.FARMER) "Verify & Enter Farmer Portal"
                            else "Submit Verification & Join as Pilot"
                        } else {
                            if (state.selectedRole == UserRole.FARMER) "Sign In as Farmer"
                            else "Sign In as Drone Pilot"
                        }
                        Text(text = actionText, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            // Discreet footer (long-press opens hidden Admin credential prompt; no stats/admin label shown publicly)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { showHiddenAdminDialog = true }
                    )
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Krishivantri Agritech Network • Secure Field Dispatch",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }

    if (showHiddenAdminDialog) {
        HiddenAdminLoginDialog(
            onSubmit = { adminPhone, passcode, onError ->
                onSubmitAdmin(adminPhone, passcode, onError)
            },
            onDismiss = { showHiddenAdminDialog = false }
        )
    }
}

@Composable
private fun RoleOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) ForestGreen else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(18.dp)
            ),
        color = if (selected) SoftSage else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) ForestGreen else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (selected) SoftSage else ForestGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AuthModeTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
        tonalElevation = if (selected) 1.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) ForestGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HiddenAdminLoginDialog(
    onSubmit: (phone: String, passcode: String, onError: (String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    var adminPhone by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Restricted Operations Access",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = adminPhone,
                    onValueChange = { adminPhone = it },
                    label = { Text("Admin Identifier") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = passcode,
                    onValueChange = { passcode = it },
                    label = { Text("Security Passcode") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (!errorText.isNullOrBlank()) {
                    Text(
                        text = errorText!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = AlertRust
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(adminPhone, passcode) { msg -> errorText = msg }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text("Authenticate")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
