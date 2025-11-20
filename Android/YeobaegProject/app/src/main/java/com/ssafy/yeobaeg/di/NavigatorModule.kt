package com.ssafy.yeobaeg.di

import com.ssafy.yeobaeg.navigation.navigator.InternalNavigator
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.navigation.navigator.NavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
internal abstract class NavigatorModule {

    @Binds
    @ActivityRetainedScoped
    abstract fun provideNavigator(navigator: NavigatorImpl): Navigator

    @Binds
    @ActivityRetainedScoped
    abstract fun provideInternalNavigator(navigator: NavigatorImpl): InternalNavigator
}
