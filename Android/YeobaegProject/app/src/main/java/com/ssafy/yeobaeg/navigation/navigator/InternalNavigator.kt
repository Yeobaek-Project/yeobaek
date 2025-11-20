package com.ssafy.yeobaeg.navigation.navigator

import kotlinx.coroutines.channels.Channel

internal interface InternalNavigator {
    val channel: Channel<InternalRoute>
}
