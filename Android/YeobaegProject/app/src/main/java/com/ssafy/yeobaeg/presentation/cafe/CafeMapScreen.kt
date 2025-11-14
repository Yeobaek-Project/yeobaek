package com.ssafy.yeobaeg.presentation.cafe

import android.Manifest
import android.R.attr.onClick
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.google.maps.android.compose.*
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import androidx.core.graphics.createBitmap
import com.ssafy.yeobaeg.presentation.cafe.components.CafeFilterDialog
import com.ssafy.yeobaeg.presentation.cafe.components.CafeListBottomSheetContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeMapScreen(
    viewModel: CafeMapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // 바텀 시트 상태
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true  // Hidden 상태를 건너뛰어 항상 화면에 표시
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )

    // 위치 권한 요청
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.updateLocationPermission(granted)
    }

    // 초기 권한 요청
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            CafeListBottomSheetContent(
                cafes = uiState.cafes,
                onCafeClick = { cafeId ->
                    viewModel.onCafeListItemClick(cafeId)
                    // 바텀시트를 PartiallyExpanded 상태로 내리기
                    coroutineScope.launch {
                        bottomSheetState.partialExpand()
                    }
                }
            )
        },
        sheetPeekHeight = 56.dp,
        sheetSwipeEnabled = true,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContainerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 지도 (전체 화면)
            GoogleMapView(
                cafes = uiState.cafes,
                currentLocation = uiState.currentLocation,
                cameraPosition = uiState.cameraPosition,
                currentZoomLevel = uiState.currentZoomLevel,
                selectedCafeId = uiState.selectedCafeId,
                shouldAnimateCamera = uiState.shouldAnimateCamera,
                onCameraPositionChanged = viewModel::onCameraPositionChanged,
                onCameraAnimationCompleted = viewModel::onCameraAnimationCompleted,
                onCafeInfoWindowClick = { viewModel.navigateToCafeDetail(it) }
            )

            // 검색 바 (지도 위에 배치)
            CafeSearchBar(
                searchKeyword = uiState.searchKeyword,
                onSearchKeywordChanged = viewModel::onSearchKeywordChanged,
                onSearch = {
                    viewModel.onSearch()
                    coroutineScope.launch {
                        bottomSheetState.expand()
                    }
                },
                showFilterButton = uiState.userRole == UserRole.ARTIST,
                onFilterClick = viewModel::toggleFilterDialog,
                isFilterActive = uiState.filter.isActive(),
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // 현재 위치로 이동 버튼
            FloatingActionButton(
                onClick = { viewModel.loadCurrentLocation() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .padding(bottom = 72.dp),
                containerColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                ),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "현재 위치",
                    tint = YeobaegDarkBrown
                )
            }

            if (uiState.isFilterDialogVisible) {
                CafeFilterDialog(
                    currentFilter = uiState.filter,
                    onDismiss = viewModel::toggleFilterDialog,
                    onApply = viewModel::applyFilter,
                    onClear = viewModel::clearFilter
                )
            }

            if (uiState.isInitialLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .padding(bottom = 200.dp),
                    action = {
                        TextButton(onClick = viewModel::clearError) {
                            Text("확인")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeSearchBar(
    searchKeyword: String,
    onSearchKeywordChanged: (String) -> Unit,
    onSearch: () -> Unit,
    showFilterButton: Boolean,
    onFilterClick: () -> Unit,
    isFilterActive: Boolean,
    modifier: Modifier = Modifier
) {
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 2.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "카페 찾기",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = YeobaegDarkBrown
                )

                if (showFilterButton) {
                    Surface(
                        onClick = onFilterClick,
                        modifier = Modifier.size(42.dp),
                        shape = CircleShape,
                        color = if (isFilterActive) YeobaegDarkBrown else Color(0xFFF5F5F5),
                        shadowElevation = if (isFilterActive) 4.dp else 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "필터",
                                tint = if (isFilterActive) Color.White else YeobaegDarkBrown,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF8F8F8),
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "검색",
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchKeyword,
                        onValueChange = onSearchKeywordChanged,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF212121)
                        ),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Search
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                                onSearch()
                            }
                        ),
                        decorationBox = { inner ->
                            Box {
                                if (searchKeyword.isEmpty()) {
                                    Text(
                                        "카페나 전시를 검색하세요",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color(0xFFBDBDBD)
                                    )
                                }
                                inner()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GoogleMapView(
    cafes: List<CafeSummary>,
    currentLocation: LatLng?,
    cameraPosition: LatLng?,
    currentZoomLevel: Float,
    selectedCafeId: Long?,
    shouldAnimateCamera: Boolean,
    onCameraPositionChanged: (LatLng, Float, CameraMoveStartedReason) -> Unit,
    onCameraAnimationCompleted: () -> Unit,
    onCafeInfoWindowClick: (Long) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val defaultPosition = remember { LatLng(36.10722517564397, 128.41605298051869) } // 서울 시청
    val initialPosition = cameraPosition ?: currentLocation ?: defaultPosition

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, currentZoomLevel)
    }

    // 단일 마커 기본 아이콘(갈색 계열)
    var singleMarkerIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    val markerIconCache = remember { mutableMapOf<Int, BitmapDescriptor>() }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val reason = cameraPositionState.cameraMoveStartedReason
            onCameraPositionChanged(
                cameraPositionState.position.target,
                cameraPositionState.position.zoom,
                reason
            )
        }
    }

    // shouldAnimateCamera가 true일 때만 카메라 애니메이션 실행
    LaunchedEffect(shouldAnimateCamera) {
        if (shouldAnimateCamera && cameraPosition != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(cameraPosition, currentZoomLevel),
                durationMs = 600
            )
            onCameraAnimationCompleted()
        }
    }

    // 클러스터 재계산: cafes와 줌 레벨 변경시에만 재계산
    val clusters = remember(cafes, (cameraPositionState.position.zoom * 2).toInt()) {
        clusterCafes(cafes, cameraPositionState.position.zoom)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = currentLocation != null
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false
        )
    ) {
        clusters.forEach { cluster ->
            if (cluster.cafes.size == 1) {
                val cafe = cluster.cafes.first()
                val lat = cafe.latitude ?: return@forEach
                val lng = cafe.longitude ?: return@forEach
                val pos = LatLng(lat, lng)

                if (singleMarkerIcon == null) {
                    singleMarkerIcon = BitmapDescriptorFactory.defaultMarker(15f)
                }

                // MarkerState를 remember로 관리하여 InfoWindow 상태를 제어
                val markerState = remember(cafe.cafeId) {
                    MarkerState(position = pos)
                }

                // selectedCafeId가 변경되면 해당 마커의 InfoWindow 표시
                LaunchedEffect(selectedCafeId, cafe.cafeId) {
                    if (selectedCafeId == cafe.cafeId) {
                        markerState.showInfoWindow()
                    } else {
                        markerState.hideInfoWindow()
                    }
                }

                // MarkerInfoWindow 사용
                MarkerInfoWindow(
                    state = markerState,
                    icon = singleMarkerIcon,
                    onInfoWindowClick = { onCafeInfoWindowClick(cafe.cafeId) }
                ) {
                    CafeInfoWindow(
                        cafe = cafe
                    )
                }
            } else {
                // 클러스터 마커: 임시 객체이므로 자체 MarkerState를 생성해서 사용
                val count = cluster.cafes.size
                val clusterIcon = markerIconCache.getOrPut(count) {
                    try { createClusterMarkerIcon(count) }
                    catch (_: Exception) { BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE) }
                }

                Marker(
                    state = MarkerState(position = cluster.position),
                    title = "${count}개 카페",
                    snippet = "탭하여 확대",
                    icon = clusterIcon,
                    onClick = {
                        // 프로그램적 애니메이션 → VM에서 reason으로 필터되어 API 호출 안 됨
                        val builder = LatLngBounds.Builder()
                        cluster.cafes.forEach { c ->
                            val la = c.latitude ?: return@forEach
                            val lo = c.longitude ?: return@forEach
                            builder.include(LatLng(la, lo))
                        }
                        val bounds = builder.build()
                        val padding = 150
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngBounds(bounds, padding),
                                durationMs = 500
                            )
                        }
                        true
                    }
                )
            }
        }
    }
}

/** 카페 정보 윈도우 (마커 클릭 시 표시) */
@Composable
fun CafeInfoWindow(
    cafe: CafeSummary,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier
            .width(280.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 썸네일 이미지 (왼쪽)
            cafe.primaryImageUrl?.let { imageUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .allowHardware(false)
                        .build(),
                    contentDescription = cafe.cafeName,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentScale = ContentScale.Crop
                )
            }

            // 카페 정보 (오른쪽)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = cafe.cafeName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = YeobaegDarkBrown,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = cafe.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(shape = RoundedCornerShape(6.dp), color = YeobaegDarkBrown.copy(alpha = 0.1f)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "슬롯",
                            style = MaterialTheme.typography.labelSmall,
                            color = YeobaegDarkBrown,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${cafe.availableSlotCount}/${cafe.totalSlotCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = YeobaegDarkBrown,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                cafe.distance?.let { distance ->
                    Text(
                        text = "${String.format("%.1f", distance)}km",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF616161),
                        fontWeight = FontWeight.Medium
                    )
                }

                if (cafe.categories.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        cafe.categories.take(2).forEach { category ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFF5F5F5),
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF757575),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 카페 클러스터 데이터 클래스 */
data class CafeCluster(
    val position: LatLng,
    val cafes: List<CafeSummary>
)

/** 줌 레벨에 따라 카페를 단순 클러스터링 */
fun clusterCafes(
    cafes: List<CafeSummary>,
    zoomLevel: Float
): List<CafeCluster> {
    if (zoomLevel >= 15f) {
        return cafes.mapNotNull { cafe ->
            val lat = cafe.latitude ?: return@mapNotNull null
            val lng = cafe.longitude ?: return@mapNotNull null
            CafeCluster(LatLng(lat, lng), listOf(cafe))
        }
    }

    val clusterDistance = when {
        zoomLevel >= 13f -> 0.01  // ~1km
        zoomLevel >= 11f -> 0.05  // ~5km
        zoomLevel >= 9f  -> 0.1   // ~10km
        else -> 0.2               // ~20km
    }

    val clusters = mutableListOf<CafeCluster>()
    val processed = mutableSetOf<CafeSummary>()

    cafes.forEach { cafe ->
        if (cafe in processed) return@forEach
        val lat = cafe.latitude ?: return@forEach
        val lng = cafe.longitude ?: return@forEach

        val nearby = mutableListOf(cafe)
        processed.add(cafe)

        cafes.forEach { other ->
            val la = other.latitude
            val lo = other.longitude
            if (other !in processed && la != null && lo != null) {
                val d = kotlin.math.sqrt((lat - la) * (lat - la) + (lng - lo) * (lng - lo))
                if (d < clusterDistance) {
                    nearby.add(other); processed.add(other)
                }
            }
        }

        val centerLat = nearby.mapNotNull { it.latitude }.average()
        val centerLng = nearby.mapNotNull { it.longitude }.average()
        clusters.add(CafeCluster(LatLng(centerLat, centerLng), nearby))
    }

    return clusters
}

/** 클러스터 마커 아이콘 생성 (갈색 원 + 숫자) */
fun createClusterMarkerIcon(count: Int): BitmapDescriptor {
    val baseSize = 180
    val size = when {
        count >= 50 -> (baseSize * 1.5f).toInt()
        count >= 20 -> (baseSize * 1.3f).toInt()
        count >= 10 -> (baseSize * 1.1f).toInt()
        else -> baseSize
    }

    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)
    val cx = size / 2f
    val cy = size / 2f

    val brown = android.graphics.Color.rgb(101, 67, 33)
    val white = android.graphics.Color.WHITE

    val shadow = Paint().apply {
        color = android.graphics.Color.argb(70, 0, 0, 0)
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    canvas.drawCircle(cx, cy + 3f, size / 2.4f, shadow)

    val circle = Paint().apply {
        color = brown
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    canvas.drawCircle(cx, cy, size / 2.5f, circle)

    val text = if (count > 999) "999+" else count.toString()
    val fontSize = when {
        count >= 100 -> size / 3.5f
        count >= 10 -> size / 3.0f
        else -> size / 2.5f
    }

    val tp = Paint().apply {
        color = white
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = fontSize
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val y = cy - ((tp.descent() + tp.ascent()) / 2)
    canvas.drawText(text, cx, y, tp)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}