// ArtComponents.kt
package com.ssafy.yeobaeg.presentation.artwork

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite

@Composable
fun ArtworkGrid(
    items: List<ArtworkSummary>,
    onClickArtwork: (Long) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 3,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    listState: LazyGridState = rememberLazyGridState(),
    topContent: @Composable () -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = contentPadding,
        modifier = modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            topContent()
        }

        items(
            items = items,
        ) { artwork ->
            AsyncImage(
                model = artwork.thumbnailUrl,
                contentDescription = artwork.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onClickArtwork(artwork.artworkId) },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun CategoryChips(
    categories: List<String>,
    selected: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories.size) { i ->
            val c = categories[i]
            val isSelected = c == selected
            FilterChip(
                selected = isSelected,
                onClick = { onClick(c) },
                label = { Text(c) },
                shape = MaterialTheme.shapes.large,
                border = null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFFF3F4F6),
                    selectedContainerColor = YeobaegBrown,
                    selectedLabelColor = YeobaegWhite,
                    labelColor = YeobaegDarkGray
                )
            )
        }
    }
}
