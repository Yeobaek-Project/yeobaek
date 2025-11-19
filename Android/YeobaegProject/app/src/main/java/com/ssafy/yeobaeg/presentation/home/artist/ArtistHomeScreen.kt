package com.ssafy.yeobaeg.presentation.home.artist

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite

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
                title = null,
                titleIcon = R.drawable.logo_title,
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
            .background(YeobaegWhite)
            .padding(top = innerPadding.calculateTopPadding()),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    ArtistProfileHeader(artist = artHome.artist)

                    Spacer(Modifier.height(20.dp))
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }

        // Action Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = R.drawable.register_cafe,
                    title = "작품 업로드",
                    description = "새로운 작품을\n등록하세요",
                    onClick = onUploadClick
                )
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = R.drawable.matching,
                    title = "카페 매칭",
                    description = "전시할 카페를\n찾아보세요",
                    onClick = onMatchingClick
                )
            }
        }


        item { Spacer(Modifier.height(32.dp)) }

        // Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "내 인기 작품 현황",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        // Empty state
        item {
            if (artHome.artworks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp, bottom = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "아직 등록한 작품이 없어요",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF222222)
                    )
                    Text(
                        text = "작품을 업로드하고 내 작품 현황을 확인하세요!",
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
        }

        // Artwork items
        items(artHome.artworks, key = { it.artworkId }) { artwork ->
            ArtistArtworkItem(
                artwork = artwork,
                onClick = { onWorkClick(artwork.artworkId) }
            )
            Spacer(Modifier.height(12.dp))
        }

        item { Spacer(Modifier.height(20.dp)) }
    }
}

/* ───────────── Header ───────────── */

@Composable
private fun ArtistProfileHeader(artist: ArtistHome) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image - Left Side
        AsyncImage(
            model = artist.profileImageUrl.ifBlank { R.drawable.ic_art },
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F0F0)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(24.dp))

        // Info - Right Side
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Name
            Text(
                text = artist.artistName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "작가님",
                fontSize = 13.sp,
                color = Color(0xFF999999)
            )

            Spacer(Modifier.height(16.dp))

            // Stats Grid - Compact
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatRow(
                    label = "작품",
                    value = "${artist.artworkCount}"
                )

                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )

                StatRow(
                    label = "전시 중",
                    value = "${artist.currentExhibitionCount}"
                )

                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
private fun ActionCard(
    modifier: Modifier = Modifier,
    icon: Int,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = title,
                modifier = Modifier.size(70.dp),
            )

            Spacer(Modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF999999),
                    lineHeight = 16.sp
                )
            }
            Spacer(Modifier.width(10.dp))

        }

    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF666666),
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222)
        )
    }
}

/* ───────────── Artwork item ───────────── */

@Composable
private fun ArtistArtworkItem(
    artwork: ArtworkHome,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Artwork Image
            AsyncImage(
                model = artwork.thumbnailUrl ?: R.drawable.ic_art,
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(YeobaegWhite),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            // Artwork Info
            Column(modifier = Modifier.weight(1f)) {
                // Title row with badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = artwork.title,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(Modifier.width(8.dp))

                        ArtworkStatusChip(artwork.isExhibiting)
                    }

                    // Likes
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 3.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_heart_fill),
                            contentDescription = null,
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = artwork.bookmarkCount.toString(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF222222)
                        )
                    }
                }

                // Cafe name
                Spacer(Modifier.height(6.dp))
                Text(
                    text = artwork.cafeName ?: "",
                    fontSize = 13.sp,
                    color = Color(0xFF999999),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )


                Spacer(Modifier.height(8.dp))

                // Price
                Text(
                    text = artwork.price.toWonString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF222222)
                )
            }
        }
    }
}

@Composable
private fun ArtworkStatusChip(isExhibition: Boolean) {
    if (!isExhibition) return

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFE8F5E9)
    ) {
        Text(
            text = "전시 중",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4CAF50),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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