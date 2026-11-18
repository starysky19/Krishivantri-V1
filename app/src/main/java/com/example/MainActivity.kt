package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.components.InsufficientWalletDialog
import com.example.ui.components.RazorpayCheckoutSheet
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.FarmerDashboardScreen
import com.example.ui.screens.PilotDashboardScreen
import com.example.ui.theme.KrishivantriTheme
import com.example.ui.viewmodel.KrishivantriViewModel
import org.maplibre.android.MapLibre

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching { MapLibre.getInstance(applicationContext) }
        enableEdgeToEdge()
        setContent {
            KrishivantriTheme {
                KrishivantriApp()
            }
        }
    }
}

@Composable
fun KrishivantriApp(viewModel: KrishivantriViewModel = viewModel()) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val mapState by viewModel.mapBookingState.collectAsStateWithLifecycle()
    val farmerProfile by viewModel.farmerProfile.collectAsStateWithLifecycle()
    val farmerOrders by viewModel.farmerOrders.collectAsStateWithLifecycle()
    val savedFarms by viewModel.savedFarms.collectAsStateWithLifecycle()
    val bookingOrdersForMap by viewModel.allBookingOrdersForMap.collectAsStateWithLifecycle()
    val realtimeOperators by viewModel.realtimeOperators.collectAsStateWithLifecycle()
    val pilotProfile by viewModel.pilotProfile.collectAsStateWithLifecycle()
    val pilotQueueOrders by viewModel.pilotQueueOrders.collectAsStateWithLifecycle()
    val pilotAssignedOrders by viewModel.pilotAssignedOrders.collectAsStateWithLifecycle()
    val adminOverview by viewModel.adminOverview.collectAsStateWithLifecycle()
    val razorpaySheet by viewModel.razorpaySheet.collectAsStateWithLifecycle()
    val insufficientWalletAlert by viewModel.insufficientWalletAlert.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.consumeSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val user = currentUser
            if (user == null) {
                AuthScreen(
                    state = authState,
                    onSelectRole = viewModel::selectAuthRole,
                    onToggleSignUp = viewModel::toggleAuthMode,
                    onSubmitFarmer = viewModel::submitFarmerAuth,
                    onSubmitPilot = viewModel::submitPilotAuth,
                    onSubmitAdmin = viewModel::submitAdminLogin
                )
            } else {
                when (UserRole.fromApi(user.role)) {
                    UserRole.FARMER -> FarmerDashboardScreen(
                        user = user,
                        profile = farmerProfile,
                        mapRepository = viewModel.mapRepository,
                        mapState = mapState,
                        savedFarms = savedFarms,
                        bookingOrdersForMap = bookingOrdersForMap,
                        realtimeOperators = realtimeOperators,
                        orders = farmerOrders,
                        onLocationPermissionResult = viewModel::onLocationPermissionResult,
                        onResumeGpsUpdates = viewModel::startBatteryEfficientGpsUpdates,
                        onPauseGpsUpdates = viewModel::stopBatteryEfficientGpsUpdates,
                        onRefreshCurrentLocation = viewModel::refreshCurrentLocation,
                        onSelectMapStyle = viewModel::selectMapStyle,
                        onSearchQueryChange = viewModel::updateSearchQuery,
                        onCenterMap = { viewModel.searchAndCenterMap(context) },
                        onSelectLocationSuggestion = viewModel::selectLocationSuggestion,
                        onMapTapPoint = viewModel::addMapBoundaryPoint,
                        onUpdateBoundaryPoint = viewModel::updateMapBoundaryPoint,
                        onMapCameraMoved = viewModel::onMapCameraMoved,
                        onMapLoadingChanged = viewModel::onMapLoadingChanged,
                        onMapError = viewModel::onMapError,
                        onRetryMapLoad = viewModel::retryMapLoad,
                        onUndoPolygonPoint = viewModel::undoLastPolygonPoint,
                        onClearPolygon = viewModel::clearPolygon,
                        onZoomMap = viewModel::zoomMap,
                        onResetCompass = viewModel::resetCompass,
                        onFarmNameChange = viewModel::updateFarmName,
                        onSaveFarmToSupabase = viewModel::saveFarmBoundaryToSupabase,
                        onFocusSavedFarm = viewModel::focusSavedFarm,
                        onCacheCurrentRegionOffline = viewModel::cacheCurrentRegionOffline,
                        onSelectPaymentMethod = viewModel::selectPaymentMethod,
                        onConfirmOrder = viewModel::initiateFarmerOrderPlacement,
                        onConsumeBookingSuccess = viewModel::consumeBookingSuccess,
                        onRatePilot = viewModel::ratePilot,
                        onSignOut = viewModel::signOut
                    )

                    UserRole.PILOT -> PilotDashboardScreen(
                        user = user,
                        profile = pilotProfile,
                        queueOrders = pilotQueueOrders,
                        assignedOrders = pilotAssignedOrders,
                        onToggleOnline = viewModel::togglePilotOnline,
                        onAcceptOrder = viewModel::acceptOrder,
                        onRejectOrder = viewModel::rejectOrder,
                        onStartSpraying = viewModel::startSpraying,
                        onCompleteOrder = viewModel::completeOrder,
                        onTopUpWallet = viewModel::openWalletTopUp,
                        onSignOut = viewModel::signOut,
                        onLocationPermissionResult = viewModel::onLocationPermissionResult
                    )

                    UserRole.ADMIN -> AdminDashboardScreen(
                        overview = adminOverview,
                        onVerifyPilotLicense = viewModel::verifyPilotLicense,
                        onSignOut = viewModel::signOut
                    )
                }
            }
        }
    }

    razorpaySheet?.let { sheet ->
        RazorpayCheckoutSheet(
            state = sheet,
            onConfirmPayment = viewModel::confirmRazorpayPayment,
            onDismiss = viewModel::dismissRazorpaySheet
        )
    }

    insufficientWalletAlert?.let { alert ->
        InsufficientWalletDialog(
            alert = alert,
            onTopUpNow = viewModel::openWalletTopUp,
            onDismiss = viewModel::dismissInsufficientWalletAlert
        )
    }
}
