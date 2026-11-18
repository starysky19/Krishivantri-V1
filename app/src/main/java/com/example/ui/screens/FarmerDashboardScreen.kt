package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.GpsOff
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.OfflinePin
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.Toll
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.DroneType
import com.example.data.model.FarmBoundaryEntity
import com.example.data.model.FarmerProfileEntity
import com.example.data.model.LatLngPoint
import com.example.data.model.MapStyleType
import com.example.data.model.OperatorLocationMarker
import com.example.data.model.OperatorRoleType
import com.example.data.model.OrderStatus
import com.example.data.model.OrderWithDetails
import com.example.data.model.PaymentMethod
import com.example.data.model.UserEntity
import com.example.data.repository.MapRepository
import com.example.domain.GeoPolygonCalculator
import com.example.domain.LocationMatch
import com.example.ui.components.MapLibreFarmMap
import com.example.ui.components.MiniPolygonPreview
import com.example.ui.components.formatInr
import com.example.ui.theme.AlertRust
import com.example.ui.theme.AlertRustBg
import com.example.ui.theme.EmeraldPulse
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.HarvestGoldBg
import com.example.ui.theme.SoftSage
import com.example.ui.viewmodel.MapBookingState
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerDashboardScreen(
    user: UserEntity,
    profile: FarmerProfileEntity?,
    mapRepository: MapRepository,
    mapState: MapBookingState,
    savedFarms: List<FarmBoundaryEntity>,
    bookingOrdersForMap: List<OrderWithDetails>,
    realtimeOperators: List<OperatorLocationMarker>,
    orders: List<OrderWithDetails>,
    onLocationPermissionResult: (Boolean) -> Unit,
    onResumeGpsUpdates: (Boolean) -> Unit,
    onPauseGpsUpdates: () -> Unit,
    onRefreshCurrentLocation: () -> Unit,
    onSelectMapStyle: (MapStyleType) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCenterMap: () -> Unit,
    onSelectLocationSuggestion: (LocationMatch) -> Unit,
    onMapTapPoint: (LatLngPoint) -> Unit,
    onUpdateBoundaryPoint: (Int, LatLngPoint) -> Unit,
    onMapCameraMoved: (LatLngPoint) -> Unit,
    onMapLoadingChanged: (Boolean) -> Unit,
    onMapError: (String?) -> Unit,
    onRetryMapLoad: () -> Unit,
    onUndoPolygonPoint: () -> Unit,
    onClearPolygon: () -> Unit,
    onZoomMap: (Boolean) -> Unit,
    onResetCompass: () -> Unit,
    onFarmNameChange: (String) -> Unit,
    onSaveFarmToSupabase: () -> Unit,
    onFocusSavedFarm: (FarmBoundaryEntity) -> Unit,
    onCacheCurrentRegionOffline: () -> Unit,
    onSelectPaymentMethod: (PaymentMethod) -> Unit,
    onConfirmOrder: () -> Unit,
    onConsumeBookingSuccess: () -> Unit,
    onRatePilot: (orderId: String, stars: Int) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        onLocationPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        if (!mapState.hasLocationPermission && !mapState.locationPermissionDenied) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Battery-efficient GPS updates aligned with Activity/Composable lifecycle
    DisposableEffect(lifecycleOwner, mapState.hasLocationPermission) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (mapState.hasLocationPermission) {
                        onResumeGpsUpdates(selectedTab == 0)
                    }
                }
                Lifecycle.Event.ON_PAUSE -> onPauseGpsUpdates()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(mapState.bookingSuccessOrderId) {
        if (mapState.bookingSuccessOrderId != null) {
            selectedTab = 1
            onConsumeBookingSuccess()
        }
    }

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
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "ID: ${profile?.farmerIdNumber ?: user.id.take(8)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = HarvestGoldBg,
                        border = BorderStroke(1.dp, HarvestAmber.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("farmer_coins_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Toll,
                                contentDescription = "Harvest Coins",
                                tint = HarvestAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${profile?.coins ?: 0} Coins",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = HarvestAmber
                            )
                        }
                    }
                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier.testTag("farmer_sign_out_button")
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
                    icon = { Icon(Icons.Rounded.Map, contentDescription = "Book & Map") },
                    label = { Text("Book & Map") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestGreen,
                        selectedTextColor = ForestGreen,
                        indicatorColor = SoftSage
                    ),
                    modifier = Modifier.testTag("farmer_tab_book")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.LocalShipping, contentDescription = "Live Orders") },
                    label = {
                        val activeCount = orders.count {
                            it.statusEnum != OrderStatus.COMPLETED && it.statusEnum != OrderStatus.CANCELLED
                        }
                        Text(if (activeCount > 0) "Orders ($activeCount)" else "Orders")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestGreen,
                        selectedTextColor = ForestGreen,
                        indicatorColor = SoftSage
                    ),
                    modifier = Modifier.testTag("farmer_tab_orders")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedTab == 0) {
                FullScreenMapBookingContent(
                    farmerIdLabel = profile?.farmerIdNumber ?: user.id,
                    mapRepository = mapRepository,
                    mapState = mapState,
                    savedFarms = savedFarms,
                    bookingOrders = bookingOrdersForMap,
                    operators = realtimeOperators,
                    onRequestLocationPermission = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    onRefreshCurrentLocation = onRefreshCurrentLocation,
                    onSelectMapStyle = onSelectMapStyle,
                    onSearchQueryChange = onSearchQueryChange,
                    onCenterMap = onCenterMap,
                    onSelectLocationSuggestion = onSelectLocationSuggestion,
                    onMapTapPoint = onMapTapPoint,
                    onUpdateBoundaryPoint = onUpdateBoundaryPoint,
                    onMapCameraMoved = onMapCameraMoved,
                    onMapLoadingChanged = onMapLoadingChanged,
                    onMapError = onMapError,
                    onRetryMapLoad = onRetryMapLoad,
                    onUndoPolygonPoint = onUndoPolygonPoint,
                    onClearPolygon = onClearPolygon,
                    onZoomMap = onZoomMap,
                    onResetCompass = onResetCompass,
                    onFarmNameChange = onFarmNameChange,
                    onSaveFarmToSupabase = onSaveFarmToSupabase,
                    onFocusSavedFarm = onFocusSavedFarm,
                    onCacheCurrentRegionOffline = onCacheCurrentRegionOffline,
                    onSelectPaymentMethod = onSelectPaymentMethod,
                    onConfirmOrder = onConfirmOrder
                )
            } else {
                FarmerOrdersTrackingContent(
                    orders = orders,
                    realtimeOperators = realtimeOperators,
                    onRatePilot = onRatePilot
                )
            }
        }
    }
}

@Composable
private fun FullScreenMapBookingContent(
    farmerIdLabel: String,
    mapRepository: MapRepository,
    mapState: MapBookingState,
    savedFarms: List<FarmBoundaryEntity>,
    bookingOrders: List<OrderWithDetails>,
    operators: List<OperatorLocationMarker>,
    onRequestLocationPermission: () -> Unit,
    onRefreshCurrentLocation: () -> Unit,
    onSelectMapStyle: (MapStyleType) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCenterMap: () -> Unit,
    onSelectLocationSuggestion: (LocationMatch) -> Unit,
    onMapTapPoint: (LatLngPoint) -> Unit,
    onUpdateBoundaryPoint: (Int, LatLngPoint) -> Unit,
    onMapCameraMoved: (LatLngPoint) -> Unit,
    onMapLoadingChanged: (Boolean) -> Unit,
    onMapError: (String?) -> Unit,
    onRetryMapLoad: () -> Unit,
    onUndoPolygonPoint: () -> Unit,
    onClearPolygon: () -> Unit,
    onZoomMap: (Boolean) -> Unit,
    onResetCompass: () -> Unit,
    onFarmNameChange: (String) -> Unit,
    onSaveFarmToSupabase: () -> Unit,
    onFocusSavedFarm: (FarmBoundaryEntity) -> Unit,
    onCacheCurrentRegionOffline: () -> Unit,
    onSelectPaymentMethod: (PaymentMethod) -> Unit,
    onConfirmOrder: () -> Unit
) {
    var sheetExpanded by rememberSaveable { mutableStateOf(false) }

    // Auto-expand bottom sheet slightly when farmer finishes drawing a valid 3+ point field boundary
    LaunchedEffect(mapState.polygonPoints.size) {
        if (mapState.polygonPoints.size == 3) {
            sheetExpanded = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Full-Screen Interactive MapLibre Open-Source Map
        MapLibreFarmMap(
            mapRepository = mapRepository,
            styleType = mapState.mapStyleType,
            cameraTarget = mapState.mapCenter,
            cameraFlyNonce = mapState.cameraFlyNonce,
            zoomInNonce = mapState.zoomInNonce,
            zoomOutNonce = mapState.zoomOutNonce,
            resetCompassNonce = mapState.resetCompassNonce,
            styleRetryNonce = mapState.styleRetryNonce,
            hasLocationPermission = mapState.hasLocationPermission,
            drawingPoints = mapState.polygonPoints,
            computedAcres = mapState.computedAcres,
            computedHectares = mapState.computedHectares,
            savedFarms = savedFarms,
            bookingOrders = bookingOrders,
            operators = operators,
            onMapTapPoint = onMapTapPoint,
            onUpdateBoundaryPoint = onUpdateBoundaryPoint,
            onMapLoadingChanged = onMapLoadingChanged,
            onMapError = onMapError,
            onCameraMoved = onMapCameraMoved,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Top Overlay Stack: Search bar, Location Suggestions, Style Selector, Offline Cache, GPS/Error banners
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search Location Bar
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = mapState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search village, taluka, or farm...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search location",
                                tint = ForestGreen
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onCenterMap() }),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("location_search_input")
                    )
                    Button(
                        onClick = onCenterMap,
                        enabled = !mapState.isSearchingLocation,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("center_map_button")
                    ) {
                        Text(if (mapState.isSearchingLocation) "..." else "Locate")
                    }
                }
            }

            // Location Suggestions Dropdown
            if (mapState.locationSuggestions.size > 1) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
                    shadowElevation = 3.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        mapState.locationSuggestions.take(3).forEach { match ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSelectLocationSuggestion(match) }
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.LocationOn,
                                    contentDescription = null,
                                    tint = EmeraldPulse,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = match.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Map Style Selector (Standard | Satellite | Hybrid) + Offline Download + Live Operators pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(MapStyleType.HYBRID, MapStyleType.SATELLITE, MapStyleType.STANDARD).forEach { styleOption ->
                    val isSelected = mapState.mapStyleType == styleOption
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) ForestGreen else MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) ForestGreen else MaterialTheme.colorScheme.outline
                        ),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectMapStyle(styleOption) }
                            .testTag("map_style_${styleOption.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Layers,
                                contentDescription = "${styleOption.label} map style",
                                tint = if (isSelected) SoftSage else ForestGreen,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = styleOption.label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else ForestGreen
                            )
                        }
                    }
                }

                // Offline Region Cache Action Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = mapState.offlineDownloadProgress == null) {
                            onCacheCurrentRegionOffline()
                        }
                        .testTag("offline_cache_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        val progress = mapState.offlineDownloadProgress
                        if (progress != null) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = EmeraldPulse
                            )
                            Text(
                                text = "Caching $progress%",
                                style = MaterialTheme.typography.labelMedium,
                                color = ForestGreen
                            )
                        } else {
                            Icon(
                                imageVector = if (mapState.offlineRegions.isNotEmpty()) {
                                    Icons.Rounded.OfflinePin
                                } else {
                                    Icons.Rounded.CloudDownload
                                },
                                contentDescription = "Save map area offline",
                                tint = EmeraldPulse,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (mapState.offlineRegions.isNotEmpty()) {
                                    "Offline (${mapState.offlineRegions.size})"
                                } else {
                                    "Save Offline"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = ForestGreen
                            )
                        }
                    }
                }
            }

            // Live Operators & Drawing HUD Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (mapState.polygonPoints.size >= 3) {
                                Icons.Rounded.CheckCircle
                            } else {
                                Icons.Rounded.TouchApp
                            },
                            contentDescription = null,
                            tint = EmeraldPulse,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = when {
                                mapState.polygonPoints.isEmpty() -> "Tap corners on map to select field"
                                mapState.polygonPoints.size < 3 -> "${mapState.polygonPoints.size}/3 corners • Tap more"
                                else -> String.format(
                                    Locale.US,
                                    "%.2f ac • %.2f ha (Drag corners)",
                                    mapState.computedAcres,
                                    mapState.computedHectares
                                )
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ForestGreen,
                            modifier = Modifier.testTag("map_live_acres_label")
                        )
                    }
                }

                val pilotCount = operators.count { it.roleType == OperatorRoleType.DRONE_OPERATOR }
                val mitraCount = operators.count { it.roleType == OperatorRoleType.DRONE_MITRA }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ForestGreen.copy(alpha = 0.92f)
                ) {
                    Text(
                        text = "$pilotCount Pilots • $mitraCount Mitra Live",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SoftSage,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("realtime_operators_badge")
                    )
                }
            }

            // Graceful GPS Permission-Denied Banner
            if (mapState.locationPermissionDenied && !mapState.hasLocationPermission) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AlertRustBg,
                    border = BorderStroke(1.dp, AlertRust.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gps_permission_denied_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GpsOff,
                            contentDescription = null,
                            tint = AlertRust,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "GPS permission denied. You can still search & draw boundaries manually.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AlertRust,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = onRequestLocationPermission,
                            modifier = Modifier.testTag("grant_gps_retry_button")
                        ) {
                            Text("Enable", color = ForestGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Map Error + Retry Banner
            if (mapState.mapErrorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AlertRustBg,
                    border = BorderStroke(1.dp, AlertRust.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("map_error_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WarningAmber,
                            contentDescription = null,
                            tint = AlertRust,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = mapState.mapErrorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = AlertRust,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        FilledTonalIconButton(
                            onClick = onRetryMapLoad,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("map_retry_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Retry loading map",
                                tint = ForestGreen
                            )
                        }
                    }
                }
            }
        }

        // 3. Center Map Loading Progress Indicator
        if (mapState.isMapLoading) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag("map_loading_indicator")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp,
                        color = ForestGreen
                    )
                    Text(
                        text = "Loading ${mapState.mapStyleType.label} map...",
                        style = MaterialTheme.typography.labelLarge,
                        color = ForestGreen
                    )
                }
            }
        }

        // 4. Right-Side Floating Map Controls (Zoom, Compass, Boundary Undo/Clear, Floating Current-Location FAB)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (mapState.polygonPoints.isNotEmpty()) {
                FilledTonalIconButton(
                    onClick = onUndoPolygonPoint,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("undo_polygon_point_button"),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        contentColor = ForestGreen
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Undo,
                        contentDescription = "Undo last boundary point"
                    )
                }
                FilledTonalIconButton(
                    onClick = onClearPolygon,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("clear_polygon_button"),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        contentColor = AlertRust
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Clear boundary"
                    )
                }
            }

            FilledTonalIconButton(
                onClick = onResetCompass,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("map_compass_reset_button"),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    contentColor = ForestGreen
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Explore,
                    contentDescription = "Reset map rotation north"
                )
            }

            FilledTonalIconButton(
                onClick = { onZoomMap(true) },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("map_zoom_in_button"),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    contentColor = ForestGreen
                )
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Zoom in map")
            }

            FilledTonalIconButton(
                onClick = { onZoomMap(false) },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("map_zoom_out_button"),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    contentColor = ForestGreen
                )
            ) {
                Icon(imageVector = Icons.Rounded.Remove, contentDescription = "Zoom out map")
            }

            FloatingActionButton(
                onClick = {
                    if (mapState.hasLocationPermission) {
                        onRefreshCurrentLocation()
                    } else {
                        onRequestLocationPermission()
                    }
                },
                containerColor = ForestGreen,
                contentColor = SoftSage,
                modifier = Modifier.testTag("current_location_fab")
            ) {
                Icon(
                    imageVector = Icons.Rounded.MyLocation,
                    contentDescription = "Center on my GPS location"
                )
            }
        }

        // 5. Bottom Section: Visible OSM Attribution Pill + Expandable Farm Information Bottom Sheet
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .widthIn(max = 640.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Visible OpenStreetMap Attribution Pill (always legible above the bottom sheet)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                modifier = Modifier
                    .padding(start = 12.dp)
                    .testTag("osm_attribution_pill")
            ) {
                Text(
                    text = if (mapState.mapStyleType == MapStyleType.STANDARD) {
                        "© OpenStreetMap contributors • OpenFreeMap"
                    } else {
                        "© OpenStreetMap contributors • Esri Satellite Imagery"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            FarmInformationBottomSheet(
                farmerIdLabel = farmerIdLabel,
                mapState = mapState,
                savedFarms = savedFarms,
                expanded = sheetExpanded,
                onToggleExpanded = { sheetExpanded = !sheetExpanded },
                onFarmNameChange = onFarmNameChange,
                onSaveFarmToSupabase = onSaveFarmToSupabase,
                onFocusSavedFarm = onFocusSavedFarm,
                onSelectPaymentMethod = onSelectPaymentMethod,
                onConfirmOrder = onConfirmOrder
            )
        }
    }
}

@Composable
private fun FarmInformationBottomSheet(
    farmerIdLabel: String,
    mapState: MapBookingState,
    savedFarms: List<FarmBoundaryEntity>,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onFarmNameChange: (String) -> Unit,
    onSaveFarmToSupabase: () -> Unit,
    onFocusSavedFarm: (FarmBoundaryEntity) -> Unit,
    onSelectPaymentMethod: (PaymentMethod) -> Unit,
    onConfirmOrder: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("farm_info_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Handle & Expand Header with Acres, Hectares, and Amount
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onToggleExpanded)
                    .padding(vertical = 4.dp)
                    .testTag("farm_sheet_toggle"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Land Selected",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.2f acres", mapState.computedAcres),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            modifier = Modifier.testTag("final_acres_text")
                        )
                        Text(
                            text = String.format(Locale.US, "%.2f ha", mapState.computedHectares),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPulse,
                            modifier = Modifier.testTag("final_hectares_text")
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Spraying Price",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatInr(mapState.computedAmount),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            modifier = Modifier.testTag("final_amount_text")
                        )
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.ExpandMore else Icons.Rounded.ExpandLess,
                        contentDescription = if (expanded) "Collapse farm details" else "Expand farm details",
                        tint = ForestGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Saved Farms Selector Chips
                    if (savedFarms.isNotEmpty()) {
                        Text(
                            text = "Saved Farms (${savedFarms.size})",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            savedFarms.forEach { farm ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SoftSage.copy(alpha = 0.65f),
                                    border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.3f)),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onFocusSavedFarm(farm) }
                                        .testTag("saved_farm_chip_${farm.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (farm.isSyncedToSupabase) {
                                                Icons.Rounded.CloudDone
                                            } else {
                                                Icons.Rounded.LocationOn
                                            },
                                            contentDescription = null,
                                            tint = ForestGreen,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = "${farm.farmName} (${String.format(Locale.US, "%.2f", farm.areaAcres)} ac / ${String.format(Locale.US, "%.2f", farm.areaHectares)} ha)",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ForestGreen
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Farm Name + Farmer ID + Save to Supabase
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = mapState.farmNameInput,
                            onValueChange = onFarmNameChange,
                            label = { Text("Farm Name (ID: $farmerIdLabel)") },
                            placeholder = { Text("e.g. North Sugarcane Plot") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("farm_name_input")
                        )
                        OutlinedButton(
                            onClick = onSaveFarmToSupabase,
                            enabled = mapState.polygonPoints.size >= 3 && !mapState.isSavingFarm,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, ForestGreen),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("save_farm_supabase_button")
                        ) {
                            if (mapState.isSavingFarm) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = ForestGreen
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Save,
                                    contentDescription = "Save farm boundary to Supabase",
                                    tint = ForestGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("Save", color = ForestGreen)
                            }
                        }
                    }

                    // Payment Method Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PaymentOptionCard(
                            title = "Razorpay UPI",
                            subtitle = "Instant online",
                            icon = Icons.Rounded.AccountBalanceWallet,
                            selected = mapState.selectedPaymentMethod == PaymentMethod.ONLINE,
                            onClick = { onSelectPaymentMethod(PaymentMethod.ONLINE) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("payment_online_card")
                        )
                        PaymentOptionCard(
                            title = "Cash on Field",
                            subtitle = "Pay pilot on arrival",
                            icon = Icons.Rounded.Payments,
                            selected = mapState.selectedPaymentMethod == PaymentMethod.CASH,
                            onClick = { onSelectPaymentMethod(PaymentMethod.CASH) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("payment_cash_card")
                        )
                    }
                }
            }

            // Primary Confirm CTA always visible inside the sheet
            Button(
                onClick = onConfirmOrder,
                enabled = mapState.polygonPoints.size >= 3 && mapState.computedAcres > 0.0,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_spray_order_button")
            ) {
                Text(
                    text = if (mapState.polygonPoints.size < 3) {
                        "Tap 3+ Farm Corners on Map"
                    } else if (mapState.selectedPaymentMethod == PaymentMethod.ONLINE) {
                        "Pay ${formatInr(mapState.computedAmount)} & Book Spray"
                    } else {
                        "Confirm Cash Order (${formatInr(mapState.computedAmount)})"
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun PaymentOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) ForestGreen else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(14.dp)
            ),
        color = if (selected) SoftSage else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = ForestGreen,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
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
private fun FarmerOrdersTrackingContent(
    orders: List<OrderWithDetails>,
    realtimeOperators: List<OperatorLocationMarker>,
    onRatePilot: (orderId: String, stars: Int) -> Unit
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SoftSage),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FlightTakeoff,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "No Spray Orders Yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Draw your farmland boundary on the map to dispatch a priority drone pilot.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(orders, key = { it.order.id }) { item ->
            FarmerOrderTrackingCard(
                item = item,
                realtimeOperators = realtimeOperators,
                onRatePilot = { stars -> onRatePilot(item.order.id, stars) }
            )
        }
    }
}

@Composable
private fun FarmerOrderTrackingCard(
    item: OrderWithDetails,
    realtimeOperators: List<OperatorLocationMarker>,
    onRatePilot: (Int) -> Unit
) {
    val status = item.statusEnum
    val fieldCenter = remember(item.order.fieldCenterLat, item.order.fieldCenterLng, item.polygonPoints) {
        when {
            item.order.fieldCenterLat != 0.0 || item.order.fieldCenterLng != 0.0 ->
                LatLngPoint(item.order.fieldCenterLat, item.order.fieldCenterLng)
            else -> GeoPolygonCalculator.calculatePolygonCenter(item.polygonPoints)
                ?: item.polygonPoints.firstOrNull()
        }
    }
    val liveOperator = realtimeOperators.firstOrNull { it.id == item.order.assignedPilotId }
    val pilotPoint = remember(item.pilotLat, item.pilotLng, liveOperator) {
        when {
            item.pilotLat != null && item.pilotLng != null ->
                LatLngPoint(item.pilotLat, item.pilotLng)
            liveOperator != null ->
                LatLngPoint(liveOperator.lat, liveOperator.lng)
            else -> null
        }
    }
    val liveDistanceKm = remember(pilotPoint, fieldCenter, item.liveDistanceKm, item.order.distanceKm) {
        when {
            pilotPoint != null && fieldCenter != null ->
                GeoPolygonCalculator.calculateDistanceKm(pilotPoint, fieldCenter)
            item.liveDistanceKm != null -> item.liveDistanceKm
            else -> item.order.distanceKm
        }
    }
    val droneType = remember(item.pilotDroneType) {
        item.pilotDroneType?.let { DroneType.fromApi(it) } ?: DroneType.QUADCOPTER
    }
    val targetEtaMinutes = remember(liveDistanceKm, droneType, item.order.pilotEtaMinutes) {
        if (liveDistanceKm != null) {
            GeoPolygonCalculator.estimateArrivalMinutes(liveDistanceKm, droneType)
        } else {
            item.order.pilotEtaMinutes.coerceAtLeast(1)
        }
    }

    var remainingSeconds by remember(item.order.id) {
        mutableIntStateOf((targetEtaMinutes * 60).coerceAtLeast(0))
    }

    LaunchedEffect(item.order.id, status, targetEtaMinutes, item.order.acceptedAt) {
        if (status == OrderStatus.ACCEPTED) {
            val initialSeconds = if (
                targetEtaMinutes == item.order.initialEtaMinutes &&
                item.order.acceptedAt != null
            ) {
                val elapsedSec = ((System.currentTimeMillis() - item.order.acceptedAt) / 1000L)
                    .toInt()
                    .coerceAtLeast(0)
                ((targetEtaMinutes * 60) - elapsedSec).coerceAtLeast(0)
            } else {
                (targetEtaMinutes * 60).coerceAtLeast(0)
            }
            remainingSeconds = initialSeconds
            while (remainingSeconds > 0) {
                delay(1000L)
                remainingSeconds = (remainingSeconds - 1).coerceAtLeast(0)
            }
        }
    }

    val displayEtaMinutes = ((remainingSeconds + 59) / 60).coerceAtLeast(0)
    val countdownClock = String.format(
        Locale.US,
        "%02d:%02d",
        remainingSeconds / 60,
        remainingSeconds % 60
    )

    val targetProgress = when (status) {
        OrderStatus.PENDING -> 0.25f
        OrderStatus.ACCEPTED -> {
            val totalSecs = (item.order.initialEtaMinutes.coerceAtLeast(1) * 60f)
            val fractionElapsed = 1f - (remainingSeconds.toFloat() / totalSecs)
            (0.35f + (fractionElapsed.coerceIn(0f, 1f) * 0.45f)).coerceIn(0.35f, 0.80f)
        }
        OrderStatus.IN_PROGRESS -> 0.90f
        OrderStatus.COMPLETED -> 1.0f
        OrderStatus.CANCELLED -> 0.0f
    }
    val progress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 500),
        label = "etaProgress"
    )
    val hectares = GeoPolygonCalculator.acresToHectares(item.order.acresToSpray)

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (status == OrderStatus.COMPLETED) SoftSage else ForestGreen
                    ) {
                        Text(
                            text = when (status) {
                                OrderStatus.PENDING -> "Matching Pilot"
                                OrderStatus.ACCEPTED -> if (remainingSeconds == 0) {
                                    "Arriving Now"
                                } else {
                                    "Arriving in $displayEtaMinutes min${if (displayEtaMinutes == 1) "" else "s"}"
                                }
                                OrderStatus.IN_PROGRESS -> "Spraying Live"
                                OrderStatus.COMPLETED -> "Completed"
                                OrderStatus.CANCELLED -> "Cancelled"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (status == OrderStatus.COMPLETED) ForestGreen else Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    Text(
                        text = item.paymentMethodEnum.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = formatInr(item.order.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            }

            // Blinkit-style Real-Time Pilot Arrival & Distance Card when Pilot accepts job
            if (status == OrderStatus.ACCEPTED) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SoftSage.copy(alpha = 0.72f),
                    border = BorderStroke(1.dp, EmeraldPulse.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("blinkit_eta_card_${item.order.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(ForestGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.FlightTakeoff,
                                        contentDescription = "Pilot en route",
                                        tint = SoftSage,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = if (remainingSeconds == 0) {
                                            "Drone Pilot at your field"
                                        } else {
                                            "Pilot arriving in $displayEtaMinutes min${if (displayEtaMinutes == 1) "" else "s"}"
                                        },
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.NearMe,
                                            contentDescription = "Live distance to field",
                                            tint = EmeraldPulse,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (liveDistanceKm != null) {
                                                "${GeoPolygonCalculator.formatDistanceLabel(liveDistanceKm)} • Live GPS"
                                            } else {
                                                "Pilot on the way • Live GPS"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ForestGreen,
                                            modifier = Modifier.testTag("live_distance_label_${item.order.id}")
                                        )
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = ForestGreen
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Schedule,
                                        contentDescription = "Real-time arrival countdown",
                                        tint = SoftSage,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = countdownClock,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.testTag("live_eta_countdown_${item.order.id}")
                                    )
                                }
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            color = ForestGreen,
                            trackColor = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Accepted",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = ForestGreen
                            )
                            Text(
                                text = "En Route",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPulse
                            )
                            Text(
                                text = "At Field",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                LinearProgressIndicator(
                    progress = { progress },
                    color = EmeraldPulse,
                    trackColor = SoftSage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = String.format(Locale.US, "%.2f acres (%.2f ha)", item.order.acresToSpray, hectares),
                        style = MaterialTheme.typography.bodyMedium,
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.pilotName != null) {
                        Text(
                            text = "Pilot: ${item.pilotName} (${item.pilotDroneType?.replaceFirstChar { it.uppercase() } ?: "Drone"})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Dispatching to top-rated nearby drone pilots...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (status == OrderStatus.COMPLETED) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (item.order.pilotRating != null) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldPulse,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "You rated this pilot ${item.order.pilotRating}/5 ★",
                                style = MaterialTheme.typography.labelLarge,
                                color = ForestGreen
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Rate Your Drone Pilot",
                                style = MaterialTheme.typography.labelLarge,
                                color = ForestGreen
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                (1..5).forEach { stars ->
                                    IconButton(
                                        onClick = { onRatePilot(stars) },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .testTag("rate_star_${stars}_${item.order.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.StarOutline,
                                            contentDescription = "Rate $stars stars",
                                            tint = HarvestAmber,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
