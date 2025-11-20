package com.ssafy.yeobaeg.presentation.review

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.review.RatingStat
import com.ssafy.yeobaeg.domain.model.review.ReviewDetail
import com.ssafy.yeobaeg.domain.model.review.ReviewStatistics
import com.ssafy.yeobaeg.domain.model.review.Reviewer
import com.ssafy.yeobaeg.presentation.common.component.YeobaegRatingStars
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.YeobaegYellow

@Composable
fun ReviewManagementScreen(
    viewModel: ReviewManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReviewManagementContent(
        uiState = uiState,
        onBackClick = viewModel::navigateBack,
        onLoadNextPage = viewModel::loadNextPage,
        onLoadWrittenNextPage = viewModel::loadWrittenNextPage
    )
}

@Composable
fun ReviewManagementContent(
    uiState: ReviewManagementUiState,
    onBackClick: () -> Unit = {},
    onLoadNextPage: () -> Unit = {},
    onLoadWrittenNextPage: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("받은 리뷰", "보낸 리뷰")

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "리뷰",
                onBackClick = onBackClick
            )
        },
        containerColor = YeobaegWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = YeobaegWhite,
                contentColor = YeobaegBrown,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(3.dp),
                        color = YeobaegBrown
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        modifier = Modifier
                            .height(56.dp)
                            .padding(horizontal = 8.dp),
                        text = {
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) YeobaegBrown else YeobaegGray
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    if (uiState.isLoading && uiState.statistics == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = YeobaegBrown)
                        }
                    } else {
                        ReceivedReviewTab(
                            statistics = uiState.statistics,
                            reviews = uiState.reviews,
                            hasNext = uiState.hasNext,
                            isLoading = uiState.isLoading,
                            onLoadNextPage = onLoadNextPage
                        )
                    }
                }
                1 -> {
                    if (uiState.writtenIsLoading && uiState.writtenReviews.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = YeobaegBrown)
                        }
                    } else {
                        SentReviewTab(
                            writtenReviews = uiState.writtenReviews,
                            hasNext = uiState.writtenHasNext,
                            isLoading = uiState.writtenIsLoading,
                            onLoadNextPage = onLoadWrittenNextPage
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReceivedReviewTab(
    statistics: ReviewStatistics?,
    reviews: List<ReviewDetail>,
    hasNext: Boolean = true,
    isLoading: Boolean = false,
    onLoadNextPage: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, hasNext, isLoading) {
        snapshotFlow {
            val info = listState.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            last >= total - 3
        }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore && hasNext && !isLoading) {
                    onLoadNextPage()
                }
            }
    }

    if (statistics == null || (statistics.totalReviewCount == 0 && reviews.isEmpty())) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YeobaegWhite),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "아직 받은 리뷰가 없어요",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "전시를 진행하고 리뷰를 받아보세요!",
                    fontSize = 14.sp,
                    color = YeobaegGray
                )
            }
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(YeobaegWhite)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                ReviewStatisticsCard(statistics = statistics)
            }

            if (reviews.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "아직 리뷰가 없습니다",
                            fontSize = 14.sp,
                            color = YeobaegGray
                        )
                    }
                }
            } else {
                items(
                    items = reviews,
                    key = { it.reviewId }
                ) { review ->
                    ReviewCard(review = review)
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun ReviewStatisticsCard(statistics: ReviewStatistics) {
    val animatedScore = remember { Animatable(0f) }
    val animatedReviewCount = remember { Animatable(0f) }

    LaunchedEffect(statistics) {
        animatedScore.animateTo(
            targetValue = statistics.totalReviewScore.toFloat(),
            animationSpec = tween(durationMillis = 1000)
        )
    }

    LaunchedEffect(statistics) {
        animatedReviewCount.animateTo(
            targetValue = statistics.totalReviewCount.toFloat(),
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(YeobaegWhite, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFF2F2F2), shape = RoundedCornerShape(12.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = String.format("%.1f", animatedScore.value),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                YeobaegRatingStars(
                    rating = statistics.totalReviewScore,
                    animated = true
                )

                Text(
                    text = "${animatedReviewCount.value.toInt()}개의 리뷰",
                    fontSize = 14.sp,
                    color = YeobaegGray
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        statistics.ratingStats.sortedByDescending { it.rating }.forEach { stat ->
            RatingBar(
                rating = stat.rating,
                count = stat.count,
                percentage = stat.percentage
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun RatingBar(
    rating: Int,
    count: Int,
    percentage: Double
) {
    val animatedPercentage = remember { Animatable(0f) }

    LaunchedEffect(percentage) {
        animatedPercentage.animateTo(
            targetValue = percentage.toFloat() / 100f,
            animationSpec = tween(durationMillis = 800, delayMillis = (5 - rating) * 100)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${rating}점",
            fontSize = 14.sp,
            color = YeobaegGray,
            modifier = Modifier.width(40.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(5.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedPercentage.value)
                    .height(10.dp)
                    .background(YeobaegBrown, shape = RoundedCornerShape(5.dp))
            )
        }

        Text(
            text = count.toString(),
            fontSize = 14.sp,
            color = YeobaegGray,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ReviewCard(review: ReviewDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(YeobaegWhite, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFF2F2F2), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = review.reviewer.profileImageUrl ?: "",
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(YeobaegYellow),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.reviewer.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = review.createdAt,
                    fontSize = 12.sp,
                    color = YeobaegGray
                )
            }

            YeobaegRatingStars(
                rating = review.rating,
                starSize = 16
            )
        }

        if (review.comment.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = review.comment,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun SentReviewTab(
    writtenReviews: List<ReviewDetail>,
    hasNext: Boolean = true,
    isLoading: Boolean = false,
    onLoadNextPage: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, hasNext, isLoading) {
        snapshotFlow {
            val info = listState.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            last >= total - 3
        }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore && hasNext && !isLoading) {
                    onLoadNextPage()
                }
            }
    }

    if (writtenReviews.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YeobaegWhite),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "아직 작성한 리뷰가 없어요",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "전시를 경험하고 리뷰를 남겨보세요!",
                    fontSize = 14.sp,
                    color = YeobaegGray
                )
            }
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(YeobaegWhite)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(
                items = writtenReviews,
                key = { it.reviewId }
            ) { review ->
                WrittenReviewCard(review = review)
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun WrittenReviewCard(review: ReviewDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(YeobaegWhite, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFF2F2F2), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = review.reviewer.profileImageUrl ?: "",
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(YeobaegYellow),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.reviewer.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = review.createdAt,
                    fontSize = 12.sp,
                    color = YeobaegGray
                )
            }

            YeobaegRatingStars(
                rating = review.rating,
                starSize = 16
            )
        }

        if (review.comment.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = review.comment,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewManagementScreenPreview() {
    val fakeState = ReviewManagementUiState(
        isLoading = false,
        statistics = ReviewStatistics(
            totalReviewCount = 125,
            totalReviewScore = 4.8,
            ratingStats = listOf(
                RatingStat(5, 100, 80.0),
                RatingStat(4, 15, 12.0),
                RatingStat(3, 5, 4.0),
                RatingStat(2, 3, 2.4),
                RatingStat(1, 2, 1.6)
            )
        ),
        reviews = listOf(
            ReviewDetail(
                reviewId = 1,
                reviewer = Reviewer(
                    name = "김민준",
                    profileImageUrl = null,
                    role = UserRole.ARTIST
                ),
                rating = 5,
                comment = "작가님 너무 멋있었고, 또 같이 작업하고 싶어요! 멋진 작품들이 많았습니다. 다음에 또 연락드릴게요. 감사합니다!",
                exhibitionStartDate = "2023-10-26",
                exhibitionEndDate = "2023-11-26",
                createdAt = "2023.10.26"
            ),
            ReviewDetail(
                reviewId = 2,
                reviewer = Reviewer(
                    name = "이서연",
                    profileImageUrl = null,
                    role = UserRole.CAFE_OWNER
                ),
                rating = 4,
                comment = "피드백 빨리 받아서 즐겁게 작업했습니다. 다만 조금만 더 빨리 연락주셨으면 좋을 것 같아요.",
                exhibitionStartDate = "2023-10-22",
                exhibitionEndDate = "2023-11-22",
                createdAt = "2023.10.22"
            ),
            ReviewDetail(
                reviewId = 3,
                reviewer = Reviewer(
                    name = "박지훈",
                    profileImageUrl = null,
                    role = UserRole.ARTIST
                ),
                rating = 5,
                comment = "최고의 카페였습니다. 프페에서성공하고 싶네요 !",
                exhibitionStartDate = "2023-10-15",
                exhibitionEndDate = "2023-11-15",
                createdAt = "2023.10.15"
            )
        )
    )

    ReviewManagementContent(
        uiState = fakeState,
        onBackClick = {},
        onLoadNextPage = {},
        onLoadWrittenNextPage = {}
    )
}
