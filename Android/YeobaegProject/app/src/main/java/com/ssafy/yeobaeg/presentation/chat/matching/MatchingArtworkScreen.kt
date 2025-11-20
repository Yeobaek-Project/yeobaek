package com.ssafy.yeobaeg.presentation.chat.matching

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.exhibition.ArtworkSummaryInfo
import com.ssafy.yeobaeg.domain.model.exhibition.PossibleArtworkSummary
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegStepBar
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingArtworkScreen(
    viewModel: MatchingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(uiState.proposal.startDate, uiState.proposal.endDate) {
        val startDate = uiState.proposal.startDate
        val endDate = uiState.proposal.endDate

        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            viewModel.loadPossibleArtworks()
        }
    }
    Scaffold(
        topBar = { YeobaegStepBar(3, 5) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YeobaegWhite)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                YeobaegButton(
                    text = "다음",
                    onClick = {
                        selectedId?.let {
                            viewModel.setArtwork(it)
                        }
                    },
                    enabled = selectedId != null && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        MatchingArtworkContent(
            modifier = Modifier.padding(innerPadding),
            artworkList = uiState.possibleArtworks,
            selectedId = selectedId,
            onSelect = { selectedId = it }
        )
    }
}

@Composable
fun MatchingArtworkContent(
    modifier: Modifier = Modifier,
    artworkList: List<PossibleArtworkSummary>,
    selectedId: Long?,
    onSelect: (Long) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(YeobaegWhite)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = "전시에 참여할 작품을 선택해주세요.",
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = jansilFont
        )

        Spacer(Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(artworkList) { art ->
                ArtworkCard(
                    artwork = art,
                    selected = selectedId == art.artworkId,
                    onSelect = { onSelect(art.artworkId) }
                )
            }
        }
    }
}

@Composable
fun ArtworkCard(artwork: PossibleArtworkSummary, selected: Boolean, onSelect: () -> Unit) {
    Column(
        modifier = Modifier.width(160.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSelect() }
        ) {
            AsyncImage(
                model = if (artwork.thumbnailUrl.isNotEmpty()) artwork.thumbnailUrl else null,
                contentDescription = artwork.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F8F8)),
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = selected,
                    onClick = { onSelect() },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = YeobaegBrown,
                        unselectedColor = Color(0xFFE0E0E0)
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = artwork.title,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color.Black,
            fontFamily = jansilFont
        )

        // ✅ 작품 정보
        Text(
            text = "${artwork.widthCm}cm x ${artwork.heightCm}cm, ${artwork.weightKg}kg",
            fontSize = 13.sp,
            color = YeobaegTextGray,
            fontFamily = jansilFont
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MatchingArtworkPreview() {
    MatchingArtworkContent(
        artworkList = listOf(
            PossibleArtworkSummary(
                artworkId = 1L,
                title = "꿈의 조각",
                thumbnailUrl = "",
                widthCm = 60.0,
                heightCm = 80.0,
                weightKg = 2.3
            ),
            PossibleArtworkSummary(
                artworkId = 2L,
                title = "고요의 능선",
                thumbnailUrl = "",
                widthCm = 70.0,
                heightCm = 50.0,
                weightKg = 1.8
            ),
            PossibleArtworkSummary(
                artworkId = 3L,
                title = "선의 미학",
                thumbnailUrl = "",
                widthCm = 40.0,
                heightCm = 60.0,
                weightKg = 1.1
            ),
            PossibleArtworkSummary(
                artworkId = 4L,
                title = "시간의 흔적",
                thumbnailUrl = "",
                widthCm = 90.0,
                heightCm = 70.0,
                weightKg = 2.8
            )
        ),
        selectedId = 2,
        onSelect = {}
    )
}
