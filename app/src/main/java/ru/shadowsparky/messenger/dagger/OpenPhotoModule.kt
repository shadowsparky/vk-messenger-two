package ru.shadowsparky.messenger.dagger

import dagger.Module
import dagger.Provides
import ru.shadowsparky.messenger.open_photo.OpenPhoto
import ru.shadowsparky.messenger.open_photo.OpenPhotoModel
import ru.shadowsparky.messenger.open_photo.OpenPhotoPresenter

@Module
class OpenPhotoModule {
    @Provides
    fun provideOpenPhotoPresenter() : OpenPhotoPresenter = OpenPhotoPresenter()

    @Provides
    fun provideOpenPhotoModel() : OpenPhotoModel = OpenPhotoModel()
}