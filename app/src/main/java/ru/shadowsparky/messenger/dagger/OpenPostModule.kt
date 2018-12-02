package ru.shadowsparky.messenger.dagger

import dagger.Module
import dagger.Provides
import ru.shadowsparky.messenger.open_post.OpenPostModel
import ru.shadowsparky.messenger.open_post.OpenPostPresenter

@Module
class OpenPostModule {
    @Provides
    fun providePostPresenter() : OpenPostPresenter = OpenPostPresenter()

    @Provides
    fun providePostModel() : OpenPostModel = OpenPostModel()
}