package com.ssafy.yeobaeg.di

import com.ssafy.yeobaeg.data.session.AuthSessionManagerImpl
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthSessionManager(
        authSessionManagerImpl: AuthSessionManagerImpl
    ): AuthSessionManager
}

