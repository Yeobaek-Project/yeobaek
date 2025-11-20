package com.ssafy.yeobaeg.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkJson

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StoreJson
