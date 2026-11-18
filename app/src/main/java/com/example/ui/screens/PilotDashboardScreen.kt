package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.model.DroneType
import com.example.data.model.OrderStatus
import com.example.data.model.OrderWithDetails
import com.example.data.model.PaymentMethod
import com.example.data.model.PilotProfileEntity
import com.example.data.model.UserEntity
import com.example.domain.GeoPolygonCalculator
import com.example.ui.components.MiniPolygonPreview
import com.example.ui.components.formatInr
import com.example.ui.theme.AlertRust
import com.example.ui.theme.AlertRustBg
import com.example.ui.theme.EmeraldPulse
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.HarvestGoldBg
import com.example.ui.theme.SoftSage
import com.example.ui.theme.VerifiedTeal
import com.example.ui.theme.VerifiedTealBg
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilotDashboardScreen(
    user: UserEntity,
    profile: PilotProfileEntity?,
    queueOrders: List<OrderWithDetails>,
    assignedOrders: List<OrderWithDetails>,
    onToggleOnline: (Boolean) -> Unit,
    onAcceptOrder: (String) -> Unit,
    onRejectOrder: (String) -> Unit,
    onStartSpraying: (String) -> Unit,
    onCompleteOrder: (String) -> Unit,
    onTopUpWallet: (Double) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    onLocationPermissionResult: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        onLocationPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            onLocationPermissionResult(true)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val activeMissions = assignedOrders.filter {
        it.statusEnum == OrderStatus.ACCEPTED || it.statusEnum == OrderStatus.IN_PROGRESS
    }
    val completedMissions = assignedOrders.filter { it.statusEnum == OrderStatus.COMPLETED }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (profile?.isLicenseVerified == true) {
                                Icon(
                                    imageVector = Icons.Rounded.Verified,
                                    contentDescription = "Verified License",
                                    tint = VerifiedTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        val droneLabel = profile?.let {
                            "${DroneType.fromApi(it.droneType).label} • ${it.tankCapacity.toInt()}L"
                        } ?: "Drone Pilot"
                        Text(
                            text = droneLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = HarvestGoldBg,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "Pilot rating",
                                tint = HarvestAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f", profile?.ratingAverage ?: 5.0),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = HarvestAmber
                            )
                        }
                    }
                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier.testTag("pilot_sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Logout,
                            contentDescription = "Sign Out",
                            tint = ForestGreen
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Rounded.FlightTakeoff, contentDescription = "Dispatch Queue") },
                    label = { Text("Dispatch (${queueOrders.size + activeMissions.size})") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestGreen,
                        selectedTextColor = ForestGreen,
                        indicatorColor = SoftSage
                    ),
                    modifier = Modifier.testTag("pilot_tab_queue")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.History, contentDescription = "Earnings") },
                    label = { Text("Earnings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestGreen,
                        selectedTextColor = ForestGreen,
                        indicatorColor = SoftSage
                    ),
                    modifier = Modifier.testTag("pilot_tab_earnings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (selectedTab == 0) {
                PilotDispatchContent(
                    profile = profile,
                    queueOrders = queueOrders,
                    activeMissions = activeMissions,
                    onToggleOnline = onToggleOnline,
                    onAcceptOrder = onAcceptOrder,
                    onRejectOrder = onRejectOrder,
                    onStartSpraying = onStartSpraying,
                    onCompleteOrder = onCompleteOrder,
                    onTopUpWallet = onTopUpWallet
                )
            } else {
                PilotEarningsContent(
                    profile = profile,
                    completedMissions = completedMissions,
                    onTopUpWallet = onTopUpWallet
                )
            }
        }
    }
}

@Composable
private fun PilotDispatchContent(
    profile: PilotProfileEntity?,
    queueOrders: List<OrderWithDetails>,
    activeMissions: List<OrderWithDetails>,
    onToggleOnline: (Boolean) -> Unit,
    onAcceptOrder: (String) -> Unit,
    onRejectOrder: (String) -> Unit,
    onStartSpraying: (String) -> Unit,
    onCompleteOrder: (String) -> Unit,
    onTopUpWallet: (Double) -> Unit
) {
    val isOnline = profile?.isOnline ?: false
    val walletBalance = profile?.walletBalance ?: 0.0
    val priorityScore = profile?.let { GeoPolygonCalculator.calculateDispatchPriorityScore(it) } ?: 0.0

    LazyColumn(
        modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Uber-style Online / Offline Availability Card
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = if (isOnline) ForestGreen else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isOnline) "You are Online" else "You are Offline",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isOnline) Color.White else ForestGreen
                            )
                            Text(
                                text = if (isOnline) "Receiving priority dispatch spray requests"
                                else "Go online to receive incoming farmer orders",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isOnline) SoftSage else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isOnline,
                            onCheckedChange = onToggleOnline,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ForestGreen,
                                checkedTrackColor = SoftSage
                            ),
                            modifier = Modifier.testTag("pilot_online_switch")
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isOnline) Color.White.copy(alpha = 0.16f) else SoftSage
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Bolt,
                                    contentDescription = null,
                                    tint = if (isOnline) SoftSage else ForestGreen,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Priority Score: ${priorityScore.toInt()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOnline) SoftSage else ForestGreen
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (profile?.isLicenseVerified == true) VerifiedTealBg else HarvestGoldBg
                        ) {
                            Text(
                                text = if (profile?.isLicenseVerified == true) "License Verified"
                                else "License Pending Review",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (profile?.isLicenseVerified == true) VerifiedTeal else HarvestAmber,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // Wallet Balance Card (Cash Order 10% Commission Rule)
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SoftSage),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AccountBalanceWallet,
                                contentDescription = null,
                                tint = ForestGreen
                            )
                        }
                        Column {
                            Text(
                                text = "Dispatch Wallet",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatInr(walletBalance),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen,
                                modifier = Modifier.testTag("pilot_wallet_balance")
                            )
                            Text(
                                text = "10% commission reserve for Cash orders",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = { onTopUpWallet(500.0) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("pilot_topup_button")
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Top Up")
                    }
                }
            }
        }

        // Active Missions
        if (activeMissions.isNotEmpty()) {
            item {
                Text(
                    text = "Active Spray Mission",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(activeMissions, key = { "active_${it.order.id}" }) { mission ->
                PilotActiveMissionCard(
                    item = mission,
                    onStartSpraying = { onStartSpraying(mission.order.id) },
                    onCompleteOrder = { onCompleteOrder(mission.order.id) }
                )
            }
        }

        // Incoming Job Queue
        item {
            Text(
                text = "Incoming Dispatch Queue",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (!isOnline) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Toggle Online above to receive live farmer spray bookings in your region.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        } else if (queueOrders.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No pending spray requests right now. Stay online — priority jobs appear here automatically.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        } else {
            items(queueOrders, key = { it.order.id }) { item ->
                PilotQueueJobCard(
                    item = item,
                    walletBalance = walletBalance,
                    onAccept = { onAcceptOrder(item.order.id) },
                    onReject = { onRejectOrder(item.order.id) },
                    onTopUp = { onTopUpWallet(500.0) }
                )
            }
        }
    }
}

@Composable
private fun PilotActiveMissionCard(
    item: OrderWithDetails,
    onStartSpraying: () -> Unit,
    onCompleteOrder: () -> Unit
) {
    val isInProgress = item.statusEnum == OrderStatus.IN_PROGRESS
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SoftSage.copy(alpha = 0.55f),
        border = BorderStroke(1.5.dp, ForestGreen),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val distanceSuffix = item.liveDistanceKm?.let {
                    " • ${GeoPolygonCalculator.formatDistanceLabel(it)}"
                }.orEmpty()
                Text(
                    text = if (isInProgress) {
                        "Spraying In Progress"
                    } else {
                        "En Route$distanceSuffix • ETA ${item.order.pilotEtaMinutes}m"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
                Text(
                    text = "Payout: ${formatInr(item.order.pilotEarnings)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MiniPolygonPreview(points = item.polygonPoints)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${item.farmerName} • ${item.farmerPhone}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${item.order.locationAddress} • ${String.format(Locale.US, "%.2f", item.order.acresToSpray)} acres",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.paymentMethodEnum.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldPulse
                    )
                }
            }

            Button(
                onClick = if (isInProgress) onCompleteOrder else onStartSpraying,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag(if (isInProgress) "complete_order_button" else "start_spraying_button")
            ) {
                Icon(
                    imageVector = if (isInProgress) Icons.Rounded.Check else Icons.Rounded.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isInProgress) "Complete Spray Job" else "Arrived • Start Spraying",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun PilotQueueJobCard(
    item: OrderWithDetails,
    walletBalance: Double,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onTopUp: () -> Unit
) {
    val isCash = item.paymentMethodEnum == PaymentMethod.CASH
    val hasEnoughForCash = !isCash || walletBalance >= item.order.adminCommission

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isCash) HarvestGoldBg else SoftSage
                ) {
                    Text(
                        text = item.paymentMethodEnum.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isCash) HarvestAmber else ForestGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Your 90% Payout",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatInr(item.order.pilotEarnings),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MiniPolygonPreview(points = item.polygonPoints)
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = null,
                            tint = EmeraldPulse,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = item.order.locationAddress,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "${String.format(Locale.US, "%.2f", item.order.acresToSpray)} acres • Total ${formatInr(item.order.totalAmount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val distPart = item.liveDistanceKm?.let {
                        " • ${GeoPolygonCalculator.formatDistanceLabel(it)}"
                    }.orEmpty()
                    Text(
                        text = "Farmer: ${item.farmerName}$distPart",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!hasEnoughForCash) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AlertRustBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.WarningAmber,
                                contentDescription = null,
                                tint = AlertRust,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Requires ${formatInr(item.order.adminCommission)} wallet balance (10% fee)",
                                style = MaterialTheme.typography.labelSmall,
                                color = AlertRust
                            )
                        }
                        OutlinedButton(
                            onClick = onTopUp,
                            border = BorderStroke(1.dp, AlertRust),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Add Funds", style = MaterialTheme.typography.labelSmall, color = AlertRust)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("reject_order_${item.order.id}")
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Reject")
                }
                Button(
                    onClick = onAccept,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(48.dp)
                        .testTag("accept_order_${item.order.id}")
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Accept Job")
                }
            }
        }
    }
}

@Composable
private fun PilotEarningsContent(
    profile: PilotProfileEntity?,
    completedMissions: List<OrderWithDetails>,
    onTopUpWallet: (Double) -> Unit
) {
    val totalEarnings = completedMissions.sumOf { it.order.pilotEarnings }
    LazyColumn(
        modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    label = "Completed Jobs",
                    value = "${completedMissions.size}",
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = "90% Direct Earnings",
                    value = formatInr(totalEarnings),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Linked Payout UPI / Bank",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = profile?.bankUpiId ?: "-",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "Wallet Balance: ${formatInr(profile?.walletBalance ?: 0.0)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldPulse
                        )
                    }
                    OutlinedButton(onClick = { onTopUpWallet(500.0) }) {
                        Text("Top Up")
                    }
                }
            }
        }

        item {
            Text(
                text = "Completed Job Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (completedMissions.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Your completed spray jobs and 90% direct payout breakdowns will appear here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        } else {
            items(completedMissions, key = { "done_${it.order.id}" }) { mission ->
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${mission.order.locationAddress} (${String.format(Locale.US, "%.2f", mission.order.acresToSpray)} ac)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "+${formatInr(mission.order.pilotEarnings)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPulse
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total: ${formatInr(mission.order.totalAmount)} • Fee (10%): ${formatInr(mission.order.adminCommission)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = mission.order.pilotRating?.let { "${it}★ Rated" } ?: "Awaiting Rating",
                                style = MaterialTheme.typography.labelSmall,
                                color = HarvestAmber
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
        }
    }
}
