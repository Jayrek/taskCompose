package com.fs.jayrek.taskcompose.model.module

import com.fs.jayrek.taskcompose.model.repository.AuthRepositoryImpl
import com.fs.jayrek.taskcompose.model.repository.IAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModules {

    @Binds
    @Singleton
    abstract fun providesAuthRepositoryImpl(repository: AuthRepositoryImpl): IAuthRepository


}