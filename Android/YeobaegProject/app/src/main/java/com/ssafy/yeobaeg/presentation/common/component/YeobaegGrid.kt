package com.ssafy.yeobaeg.presentation.common.component

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun YeobaegGrid(
    artworks: List<ArtworkSummary>,
    isLoading: Boolean,
    hasNext: Boolean,
    onLoadMore: () -> Unit,
    columns: Int = 3,
    cornerRadius: Int = 3
) {
    val gridState = rememberLazyGridState()

    // 스크롤이 끝에 도달했을 때 다음 데이터 요청
    LaunchedEffect(gridState, hasNext, isLoading) {
        snapshotFlow {
            val layoutInfo = gridState.layoutInfo
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalItemsCount = layoutInfo.totalItemsCount
            totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 3
        }
            .distinctUntilChanged()
            .filter { it }
            .debounce(150)
            .collect {
                if (hasNext && !isLoading) {
                    Log.d("YeobaegGrid", "스크롤 끝 감지 → onLoadMore 호출")
                    onLoadMore()
                }
            }
    }

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(artworks) { artwork ->
            AsyncImage(
                model = artwork.thumbnailUrl,
                contentDescription = "Artwork Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(cornerRadius.dp))
            )
        }

        // 로딩 인디케이터 (다음 페이지 불러오는 중일 때)
        if (isLoading) {
            item(span = { GridItemSpan(columns) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YeobaegBrown)
                }
            }
        }
    }
}
