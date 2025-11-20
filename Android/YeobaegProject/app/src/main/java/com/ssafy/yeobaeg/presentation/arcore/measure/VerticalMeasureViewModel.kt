package com.ssafy.yeobaeg.presentation.arcore.measure

import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerticalMeasureViewModel @Inject constructor(
    navigator: Navigator
) : MeasureBaseViewModel(navigator) {

}
