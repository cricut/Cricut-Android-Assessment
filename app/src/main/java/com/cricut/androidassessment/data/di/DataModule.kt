package com.cricut.androidassessment.data.di

import com.cricut.androidassessment.data.LocalQuizRepository
import com.cricut.androidassessment.data.QuizRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/** Qualifier for the dispatcher used for simulated I/O work. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    fun bindQuizRepository(impl: LocalQuizRepository): QuizRepository

    companion object {

        @Provides
        @IoDispatcher
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    }
}
