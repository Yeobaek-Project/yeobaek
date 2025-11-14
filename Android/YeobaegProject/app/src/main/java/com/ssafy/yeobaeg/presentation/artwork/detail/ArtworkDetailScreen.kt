package com.ssafy.yeobaeg.presentation.artwork.detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.art.ArtistSummary
import com.ssafy.yeobaeg.domain.model.art.ArtworkDetail
import com.ssafy.yeobaeg.domain.model.art.ExhibitionSpot
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.presentation.common.util.formatPrice
import com.ssafy.yeobaeg.presentation.common.util.removeTrailingZero
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGray
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkRed
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray

@Composable
fun ArtworkDetailScreen(
    artworkId: Long,
    viewModel: ArtworkDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(artworkId) { viewModel.load(artworkId) }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.loading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = YeobaegBrown
            )
        }
    }

    state.artwork?.let {
        ArtworkDetailScreen(
            artwork = it,
            userRole = state.userRole,
            onBackClick = viewModel::onBackClick,
            toggleBookmark = viewModel::toggleBookmark,
            onArtistClick = viewModel::navigateToArtist
        )
    }
}

@Composable
fun ArtworkDetailScreen(
    modifier: Modifier = Modifier,
    artwork: ArtworkDetail,
    userRole: UserRole? = null,
    onBackClick: () -> Unit = {},
    toggleBookmark: () -> Unit = {},
    onArtistClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "작품 상세",
                onBackClick = onBackClick,
                actionIcon = if (artwork.isBookmarked) R.drawable.ic_heart_fill else R.drawable.ic_heart,
                onActionClick = toggleBookmark,
                iconTint = YeobaegDarkRed
            )
        },
        bottomBar = {
            if (userRole != UserRole.ARTIST) {
                YeobaegButton(
                    text = "문의",
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                )
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // 1) 이미지 페이저
            ArtworkImagePager(artwork.images)

            // 2) 타이틀/카운트
            Column(
                Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        artwork.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        painter = painterResource(R.drawable.ic_heart),
                        contentDescription = null,
                        tint = YeobaegDarkRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "${artwork.bookmarkCount}",
                        color = YeobaegDarkGray,
                        fontWeight = FontWeight.Light
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_view),
                        contentDescription = null,
                        tint = YeobaegDarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "${artwork.viewCount}",
                        color = YeobaegDarkGray,
                        fontWeight = FontWeight.Light
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(artwork.category, color = YeobaegBrown)
                Spacer(Modifier.height(8.dp))
            }

            // 3) 작가 카드
            ArtistCard(
                artist = artwork.artist,
                onArtistClick = onArtistClick
            )

            // 4) 작품 정보
            InfoCard(
                price = artwork.price,
                sizeText = "${artwork.widthCm}cm x ${artwork.heightCm}cm",
                year = artwork.productionYear,
                weight = artwork.weightKg
            )
            HorizontalDivider(thickness = 0.5.dp, color = YeobaegLightGray)
            // 5) 작품 설명
            DescriptionCard(artwork.description)
            HorizontalDivider(thickness = 0.5.dp, color = YeobaegLightGray)
            // 6) 전시 장소
            artwork.exhibition?.let { ExhibitionCard(it) }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ArtworkImagePager(images: List<String>) {
    val pagerState = rememberPagerState { images.size.coerceAtLeast(1) }

    Box(
        Modifier
            .fillMaxWidth()
            .height(360.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val url = images.getOrNull(page)
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // 인디케이터
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(images.size) { idx ->
                    val active = idx == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .size(if (active) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (active) YeobaegDarkBrown else YeobaegLightGray)
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistCard(
    artist: ArtistSummary,
    onArtistClick: () -> Unit = {}
) {
    Surface(
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = onArtistClick
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF9FAFB))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = artist.profileImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(artist.artistName, fontWeight = FontWeight.SemiBold)
                Text("작가", color = YeobaegTextGray)
            }
        }
    }
}

@Composable
private fun InfoCard(
    price: Int,
    sizeText: String,
    year: Int,
    weight: Double
) {
    Column(
        Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("작품 정보", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        InfoRow("가격", price.formatPrice())
        InfoRow("크기", sizeText)
        InfoRow("제작년도", "$year")
        InfoRow("무게", "${weight.removeTrailingZero()}kg")
    }
}

@Composable
private fun DescriptionCard(text: String) {
    Column(Modifier.padding(24.dp)) {
        Text("작품 설명", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(text, color = YeobaegDarkGray, lineHeight = 20.sp)
    }
}

@Composable
private fun ExhibitionCard(ex: ExhibitionSpot) {
    Column(Modifier.padding(24.dp)) {
        Text("전시 장소", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ex.cafeImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(ex.cafeName, fontWeight = FontWeight.SemiBold)
                Text(
                    ex.cafeAddress,
                    color = YeobaegTextGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = YeobaegDarkGray)
        Text(value, color = YeobaegBlack, textAlign = TextAlign.End, fontWeight = FontWeight.Light)
    }
}

@Preview(showBackground = true, name = "작품 상세 - 전시중")
@Composable
private fun PreviewArtworkDetailScreen_Exhibiting() {
    MaterialTheme {
        ArtworkDetailScreen(
            artwork = ArtworkDetail(
                artworkId = 1L,
                title = "푸른 밤의 꿈",
                category = "현대미술",
                images = listOf(
                    "https://imgnews.pstatic.net/image/657/2025/11/02/0000044371_002_20251102115108734.jpg?type=w860",
                    "https://picsum.photos/seed/art2/600/800",
                    "https://picsum.photos/seed/art3/600/800"
                ),
                artist = ArtistSummary(
                    artistId = 1L,
                    artistName = "김예술",
                    profileImageUrl = "https://imgnews.pstatic.net/image/657/2025/11/02/0000044371_002_20251102115108734.jpg?type=w860",
                    introduction = "현대미술 작가",
                    artworkCount = 24
                ),
                price = 5000000,
                widthCm = 120,
                heightCm = 80,
                productionYear = 2023,
                weightKg = 3.5,
                description = "이 작품은 밤하늘의 신비로움과 꿈의 세계를 표현한 작품입니다. 푸른색 계열의 색채를 통해 몽환적인 분위기를 연출하였습니다.",
                bookmarkCount = 128,
                viewCount = 1543,
                isBookmarked = true,
                exhibition = ExhibitionSpot(
                    cafeName = "아트카페 여백",
                    cafeAddress = "서울시 강남구 신사동 123-45",
                    cafeImageUrl = "https://picsum.photos/seed/cafe1/200",
                    cafeId = 1L,
                    slotName = "",
                    startDate = "",
                    endDate = ""
                )
            )
        )
    }
}

@Preview(showBackground = true, name = "작품 상세 - 전시 안함")
@Composable
private fun PreviewArtworkDetailScreen_NotExhibiting() {
    MaterialTheme {
        ArtworkDetailScreen(
            artwork = ArtworkDetail(
                artworkId = 2L,
                title = "도시의 리듬",
                category = "서양화",
                images = listOf("https://picsum.photos/seed/art4/600/800"),
                artist = ArtistSummary(
                    artistId = 2L,
                    artistName = "박작가",
                    profileImageUrl = "https://picsum.photos/seed/artist2/200",
                    introduction = "추상화 전문 작가",
                    artworkCount = 42
                ),
                price = 3500000,
                widthCm = 90,
                heightCm = 120,
                productionYear = 2024,
                weightKg = 2.8,
                description = "도시의 역동적인 에너지와 리듬을 추상적으로 표현한 작품입니다.",
                bookmarkCount = 67,
                viewCount = 892,
                isBookmarked = false,
                exhibition = null
            )
        )
    }
}
