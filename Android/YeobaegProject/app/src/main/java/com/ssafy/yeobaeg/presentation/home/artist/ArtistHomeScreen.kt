package com.ssafy.yeobaeg.presentation.home.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.ssafy.yeobaeg.domain.model.artist.ArtHome
import com.ssafy.yeobaeg.domain.model.artist.ArtistHome
import com.ssafy.yeobaeg.domain.model.artist.ArtworkHome
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import com.ssafy.yeobaeg.ui.theme.YeobaegProjectTheme

@Composable
fun ArtistHomeScreen(
    viewModel: ArtistHomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadArtistData()
    }

    ArtistHomeContent(
        state = state,
        onUploadClick = viewModel::navigateToRegisterArtwork,
        onMatchingClick = viewModel::navigateToMatching,
        onWorkClick = viewModel::navigateToArtworkDetail
    )
}

@Composable
fun ArtistHomeContent(
    state: ArtistHomeUiState,
    onUploadClick: () -> Unit = {},
    onMatchingClick: () -> Unit = {},
    onWorkClick: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "작가 홈",
                onBackClick = {},
                showBackButton = false,
            )
        }
    ) { inner ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.artHome != null -> {
                ArtistHomeContent(
                    artHome = state.artHome,
                    innerPadding = inner,
                    onUploadClick = onUploadClick,
                    onMatchingClick = onMatchingClick,
                    onWorkClick = onWorkClick
                )
            }

            state.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "데이터를 불러올 수 없습니다\n${state.errorMessage}",
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistHomeContent(
    artHome: ArtHome,
    innerPadding: PaddingValues,
    onUploadClick: () -> Unit,
    onMatchingClick: () -> Unit,
    onWorkClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = innerPadding.calculateTopPadding()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier.height(20.dp))
            ArtistProfileHeader(artist = artHome.artist)
        }

        item {
            ArtistQuickActions(
                onUploadClick = onUploadClick,
                onMatchingClick = onMatchingClick
            )
            Spacer(Modifier.height(20.dp))
        }

        item {
            Text(
                text = "내 인기 작품 현황",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            if(artHome.artworks.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "아직 등록한 작품이 없어요",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = "작품을 업로드하고 내 작품 현황을 확인하세요!",
                        fontSize = 14.sp,
                        color = YeobaegGray
                    )
                }
            }
        }

        items(artHome.artworks, key = { it.artworkId }) { artwork ->
            ArtistArtworkItem(
                artwork = artwork,
                onClick = { onWorkClick(artwork.artworkId) }
            )
        }

        item {
            Spacer(Modifier.height(16.dp))
        }
    }
}

/* ───────────── Header ───────────── */

@Composable
private fun ArtistProfileHeader(artist: ArtistHome) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = artist.profileImageUrl.ifBlank { R.drawable.ic_art },
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F2F5)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(12.dp))
        Text(text = artist.artistName, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatCell(top = artist.artworkCount.toString(), bottom = "작품")
            StatCell(
                top = artist.currentExhibitionCount.toString(),
                bottom = "전시 중",
                emphasis = true
            )
        }
    }
}

@Composable
private fun StatCell(top: String, bottom: String, emphasis: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = top,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (emphasis) Color(0xFF3B82F6) else Color.Black
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = bottom,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF7A7F87)
        )
    }
}

/* ───────────── Quick actions ───────────── */

@Composable
private fun ArtistQuickActions(
    onUploadClick: () -> Unit,
    onMatchingClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        YeobaegOutlinedButton(
            modifier = Modifier.weight(1f),
            text = "작품 업로드",
            icon = R.drawable.ic_cafe_add,
            onClick = onUploadClick
        )
        YeobaegOutlinedButton(
            modifier = Modifier.weight(1f),
            text = "카페 매칭",
            icon = R.drawable.ic_search,
            onClick = onMatchingClick
        )
    }
}

/* ───────────── Artwork item ───────────── */

@Composable
private fun ArtistArtworkItem(
    artwork: ArtworkHome,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = artwork.thumbnailUrl ?: R.drawable.ic_art,
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF2F3F5)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = artwork.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(8.dp))
                if(artwork.isExhibiting) ArtworkStatusChip()
            }

            if (artwork.cafeName != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = artwork.cafeName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF7A7F87),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = artwork.price.toWonString(), fontSize = 13.sp)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fill_heart),
                        contentDescription = null,
                        tint = Color(0xFFE74C3C),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = artwork.bookmarkCount.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtworkStatusChip() {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color(0xFFDCFCE7)
    ) {
        Text(
            text = "전시 중",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF16A34A),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Preview
@Composable
fun Preview_ArtistHome() {
    YeobaegProjectTheme {
        ArtistHomeContent(
            state = ArtistHomeUiState(
                artHome = ArtHome(
                    artist = ArtistHome(
                        artistName = "김아티스트",
                        artworkCount = 12,
                        currentExhibitionCount = 3,
                        profileImageUrl = "",
                        userId = 1
                    ),
                    artworks = listOf(
                        ArtworkHome(
                            artworkId = 1,
                            bookmarkCount = 45,
                            cafeName = "Brew & Canvas",
                            category = "현대미술",
                            isExhibiting = true,
                            price = 450000,
                            thumbnailUrl = "https://picsum.photos/seed/a1/300/200",
                            title = "Urban Dreams"
                        ),
                        ArtworkHome(
                            artworkId = 2,
                            bookmarkCount = 28,
                            cafeName = "",
                            category = "추상화",
                            isExhibiting = false,
                            price = 320000,
                            thumbnailUrl = "https://picsum.photos/seed/a2/300/200",
                            title = "Morning Light"
                        )
                    )
                )
            ),
            onUploadClick = {},
            onMatchingClick = {},
            onWorkClick = {}
        )
    }
}