package com.ssafy.yeobaeg.presentation.artwork

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.art.ArtistSummary
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTextField
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.presentation.main.LocalUserRole
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGray
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkRed
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegYellow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ArtExploreScreen(
    viewModel: ArtExploreViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val userRole = LocalUserRole.current
    val focus = LocalFocusManager.current
    var showFilter by remember { mutableStateOf(false) }

    BackHandler(enabled = state.mode == SearchMode.RESULT) {
        focus.clearFocus(true)
        viewModel.selectTabSearch()
    }
    BackHandler(enabled = state.mode == SearchMode.RECENT) {
        focus.clearFocus(true)
        viewModel.selectTabBrowse()
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                YeobaegTopAppBar(
                    title = state.mode.title,
                    onBackClick = {
                        when (state.mode) {
                            SearchMode.BROWSE -> {}
                            SearchMode.RECENT -> viewModel.selectTabBrowse()
                            SearchMode.RESULT -> viewModel.selectTabSearch()
                        }
                        focus.clearFocus(true)
                    },
                    showBackButton = state.mode != SearchMode.BROWSE,
                    actionIcon = if (state.mode != SearchMode.RECENT) R.drawable.ic_filter else null,
                    onActionClick = if (userRole == UserRole.CAFE_OWNER) {
                        { showFilter = true }
                    } else null
                )
                Spacer(Modifier.height(12.dp))
                SearchBar(
                    value = state.query,
                    onValueChange = viewModel::setQuery,
                    onSearch = { viewModel.search() },
                    onFocus = { viewModel.selectTabSearch() }
                )
            }
        },
        containerColor = Color.White
    ) { inner ->
        when (state.mode) {
            SearchMode.BROWSE -> BrowseContent(
                modifier = Modifier
                    .padding(top = inner.calculateTopPadding())
                    .fillMaxSize(),
                categories = state.categories,
                selected = state.selectedCategory,
                onCategory = viewModel::selectCategory,
                onClickArtwork = viewModel::navigateToDetail,
                artworks = state.artworks,
                hasNext = state.paginationInfo?.hasNext ?: false,
                isLoading = state.isSearchLoading,
                onLoadNextPage = viewModel::loadNextPage
            )

            SearchMode.RECENT -> RecentSearchContent(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                recent = state.recent,
                onSearchClick = {
                    viewModel.setQuery(it)
                    viewModel.search()
                    focus.clearFocus()
                },
                onRemoveRecent = viewModel::removeRecent,
                onClearAll = viewModel::clearRecent
            )

            SearchMode.RESULT -> SearchResultContent(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                artists = state.resultArtists,
                artworks = state.resultArtworks,
                onClickArtwork = viewModel::navigateToDetail,
                onLoadNextPage = viewModel::loadNextPage,
                hasNext = state.paginationInfo?.hasNext ?: true,
                isLoading = state.isSearchLoading
            )
        }
    }

    ArtworkFilterSheet(
        visible = showFilter,
        maxWidthCm = state.maxWidthCm,
        maxHeightCm = state.maxHeightCm,
        onApply = { w, h -> viewModel.setSizeFilter(w, h) },
        onDismiss = { showFilter = false }
    )
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    onSearch: () -> Unit
) {
    YeobaegTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = "작품, 작가를 검색해 보세요",
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .onFocusChanged { state ->
                if (state.isFocused) onFocus()
            },
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                modifier = Modifier.clickable { onSearch() }
            )
        }
    )
}

@Composable
private fun BrowseContent(
    modifier: Modifier,
    categories: List<ArtworkCategory>,
    selected: ArtworkCategory?,
    onCategory: (ArtworkCategory) -> Unit,
    onClickArtwork: (Long) -> Unit = {},
    artworks: List<ArtworkSummary>,
    hasNext: Boolean = false,
    isLoading: Boolean = false,
    onLoadNextPage: () -> Unit = {}
) {
    val listState = rememberLazyGridState()

    // 무한 스크롤 로직
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

    Column(modifier = modifier) {
        Spacer(Modifier.height(4.dp))
        CategoryChips(
            modifier = Modifier.padding(horizontal = 20.dp),
            categories = categories.map { it.label },
            selected = selected?.label ?: "",
            onClick = { label ->
                categories.firstOrNull { it.label == label }?.let(onCategory)
            }
        )
        Spacer(Modifier.height(4.dp))
        ArtworkGrid(
            items = artworks,
            onClickArtwork = onClickArtwork,
            listState = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
        )
    }
}

@Composable
private fun RecentSearchContent(
    modifier: Modifier,
    recent: List<String>,
    onSearchClick: (String) -> Unit,
    onRemoveRecent: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "최근 검색어",
                color = YeobaegDarkGray
            )
            Spacer(Modifier.weight(1f))
            if (recent.isNotEmpty()) {
                Text(
                    "전체 삭제",
                    fontSize = 13.sp,
                    color = YeobaegDarkGray,
                    modifier = Modifier.clickable { onClearAll() })
            }
        }
        Spacer(Modifier.height(12.dp))
        if (recent.isEmpty()) {
            Text("최근 검색어가 없습니다.", fontSize = 14.sp, color = YeobaegTextGray)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recent.forEach { keyword ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSearchClick(keyword) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_recent),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            keyword,
                            color = YeobaegBlack,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = null,
                            tint = YeobaegDarkRed,
                            modifier = Modifier.clickable { onRemoveRecent(keyword) }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(YeobaegYellow, shape = MaterialTheme.shapes.large)
                .padding(16.dp)
        ) {
            Column {
                Text("검색 팁", color = YeobaegDarkBrown, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "• 작가 이름이나 작품 제목으로 검색해보세요\n• 장르나 스타일로도 검색이 가능합니다\n• 여러 키워드를 조합해서 검색해보세요",
                    fontSize = 12.sp,
                    color = YeobaegDarkGray
                )
            }
        }
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
fun SearchResultContent(
    modifier: Modifier,
    artists: List<ArtistSummary>,
    artworks: List<ArtworkSummary>,
    hasNext: Boolean = true,
    isLoading: Boolean = false,
    onClickArtwork: (Long) -> Unit = {},
    onLoadNextPage: () -> Unit = {}
) {
    val listState = rememberLazyGridState()

    // 무한 스크롤 로직
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

    if (artists.isEmpty() && artworks.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(vertical = 80.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("검색 결과가 없습니다.", fontSize = 14.sp, color = YeobaegTextGray)
        }

        return
    }

    ArtworkGrid(
        items = artworks,
        onClickArtwork = onClickArtwork,
        listState = listState,
        columns = 3,
        modifier = modifier
            .fillMaxSize(),
        topContent = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (artists.isNotEmpty()) ArtistResultSection(artists = artists)
                if (artworks.isNotEmpty()) Text(
                    text = "작품",
                    modifier = Modifier.padding(8.dp),
                    color = YeobaegTextGray
                )
            }
        }
    )
}

@Composable
fun ArtistResultSection(
    artists: List<ArtistSummary>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text("작가", color = YeobaegTextGray)

        artists.forEach { artist ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = artist.profileImageUrl,
                    contentDescription = artist.artistName,
                    modifier = Modifier
                        .height(48.dp)
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.large)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(artist.artistName, fontWeight = FontWeight.SemiBold)
                    Text(
                        artist.introduction,
                        color = YeobaegTextGray,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text("작품 ${artist.artworkCount}개", fontSize = 12.sp, color = YeobaegTextGray)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "둘러보기 모드")
@Composable
private fun PreviewArtExploreScreen_Browse() {
    MaterialTheme {
        BrowseContent(
            modifier = Modifier.fillMaxSize(),
            categories = ArtworkCategory.entries,
            selected = ArtworkCategory.ALL,
            onCategory = {},
            artworks = listOf(
                ArtworkSummary(
                    artworkId = 1L,
                    title = "푸른 밤의 꿈",
                    thumbnailUrl = "https://picsum.photos/seed/art1/600",
                    price = 5000000,
                    category = "현대미술",
                    widthCm = 120,
                    heightCm = 80,
                ),
                ArtworkSummary(
                    artworkId = 2L,
                    title = "도시의 리듬",
                    thumbnailUrl = "https://picsum.photos/seed/art2/600",
                    price = 3500000,
                    category = "서양화",
                    widthCm = 90,
                    heightCm = 120,
                    bookmarkCount = 67
                )
            )
        )
    }
}

@Preview(showBackground = true, name = "최근 검색어 - 있음")
@Composable
private fun PreviewRecentSearchContent_WithData() {
    MaterialTheme {
        RecentSearchContent(
            modifier = Modifier.fillMaxSize(),
            recent = listOf("김예술", "현대미술", "서양화", "추상화"),
            onSearchClick = {},
            onRemoveRecent = {},
            onClearAll = {}
        )
    }
}

@Preview(showBackground = true, name = "최근 검색어 - 없음")
@Composable
private fun PreviewRecentSearchContent_Empty() {
    MaterialTheme {
        RecentSearchContent(
            modifier = Modifier.fillMaxSize(),
            recent = emptyList(),
            onSearchClick = {},
            onRemoveRecent = {},
            onClearAll = {}
        )
    }
}

@Preview(showBackground = true, name = "검색 결과")
@Composable
private fun PreviewSearchResultContent() {
    MaterialTheme {
        SearchResultContent(
            modifier = Modifier.fillMaxSize(),
            artists = listOf(
                ArtistSummary(
                    artistId = 1L,
                    artistName = "김예술",
                    profileImageUrl = "https://picsum.photos/seed/artist1/200",
                    introduction = "현대미술과 전통의 조화를 추구하는 작가",
                    artworkCount = 24
                ),
                ArtistSummary(
                    artistId = 2L,
                    artistName = "김작가",
                    profileImageUrl = "https://picsum.photos/seed/artist2/200",
                    introduction = "추상화 전문 작가",
                    artworkCount = 18
                )
            ),
            artworks = listOf(
                ArtworkSummary(
                    artworkId = 1L,
                    title = "푸른 밤의 꿈",
                    thumbnailUrl = "https://picsum.photos/seed/art1/600",
                    price = 5000000,
                    category = "현대미술",
                    widthCm = 120,
                    heightCm = 80,
                    bookmarkCount = 128
                ),
                ArtworkSummary(
                    artworkId = 2L,
                    title = "도시의 리듬",
                    thumbnailUrl = "https://picsum.photos/seed/art2/600",
                    price = 3500000,
                    category = "서양화",
                    widthCm = 90,
                    heightCm = 120,
                    bookmarkCount = 67
                )
            )
        )
    }
}
