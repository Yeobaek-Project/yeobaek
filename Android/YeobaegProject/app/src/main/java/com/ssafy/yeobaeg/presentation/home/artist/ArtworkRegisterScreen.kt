package com.ssafy.yeobaeg.presentation.home.artist

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.presentation.common.util.formatPriceInput
import com.ssafy.yeobaeg.presentation.home.artist.component.ArtworkBasicInfoContent
import com.ssafy.yeobaeg.presentation.home.artist.component.ArtworkDescriptionContent
import com.ssafy.yeobaeg.presentation.home.artist.component.ArtworkDetailInfoContent
import com.ssafy.yeobaeg.presentation.home.artist.component.ArtworkImageUploadContent
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite

@Composable
fun ArtworkRegisterScreen(
    viewModel: ArtworkRegisterViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.resetSteps() }

    val step = viewModel.currentStep.collectAsStateWithLifecycle().value
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val data = uiState.artworkData
    val context = LocalContext.current

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "작품 등록",
                onBackClick = {
                    if (step == 1) viewModel.navigateBack() else viewModel.prevStep()
                },
                actionIcon = null,
                onActionClick = {}
            )
        },
        containerColor = YeobaegWhite
    ) { padding ->
        when (step) {
            1 -> ArtworkImageUploadContent(
                imageUris = uiState.imageUris,
                onAddImages = { uris -> viewModel.addImages(context, uris) },
                onRemoveImage = viewModel::removeImage,
                onNext = viewModel::nextStep,
                modifier = Modifier.padding(padding)
            )

            2 -> ArtworkBasicInfoContent(
                title = data.title,
                category = data.category,
                price = if (data.price == 0) "" else formatPriceInput(
                    data.price.toString()
                ),
                onTitleChange = { viewModel.updateArtworkData(title = it) },
                onCategorySelect = { viewModel.updateArtworkData(category = it) },
                onPriceChange = { formatted ->
                    val digitsOnly = formatted.filter { it.isDigit() }
                    viewModel.updateArtworkData(price = digitsOnly.toIntOrNull() ?: 0)
                },
                onNext = viewModel::nextStep,
                modifier = Modifier.padding(padding)
            )

            3 -> ArtworkDetailInfoContent(
                width = if (data.widthCm == 0) "" else data.widthCm.toString(),
                height = if (data.heightCm == 0) "" else data.heightCm.toString(),
                weight = if (data.weightKg == 0.0) "" else data.weightKg.toString(),
                year = data.productionYear,
                onWidthChange = { viewModel.updateArtworkData(widthCm = it.toIntOrNull() ?: 0) },
                onHeightChange = { viewModel.updateArtworkData(heightCm = it.toIntOrNull() ?: 0) },
                onWeightChange = {
                    viewModel.updateArtworkData(
                        weightKg = it.toDoubleOrNull() ?: 0.0
                    )
                },
                onYearChange = { viewModel.updateArtworkData(productionYear = it) },
                onNext = viewModel::nextStep,
                modifier = Modifier.padding(padding)
            )

            4 -> {
                val priceText = if (data.price > 0) formatPriceInput(data.price.toString()) else ""
                val sizeText = "${data.widthCm}cm × ${data.heightCm}cm"
                val weightText = "${data.weightKg}kg"
                val yearText = data.productionYear

                ArtworkDescriptionContent(
                    description = data.description,
                    onDescriptionChange = { viewModel.updateArtworkData(description = it) },
                    onRegister = { viewModel.registerArtwork(context) },
                    // 요약 카드용 props
                    title = data.title,
                    categoryLabel = data.category.label,
                    priceText = priceText,
                    sizeText = sizeText,
                    weightText = weightText,
                    yearText = yearText,
                    imageCount = uiState.imageUris.size,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}
