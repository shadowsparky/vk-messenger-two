package ru.shadowsparky.messenger.response_utils

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import ru.shadowsparky.messenger.response_utils.pojos.YandexTranslateArgs
import ru.shadowsparky.messenger.response_utils.responses.YandexTranslateResult

interface YandexApi {
    @GET("api/v1.5/tr.json/translate")
    fun translate(
        @Query("text") text: String,
        @Query("lang") lang: String = "en-ru",
        @Query("key") key: String = "trnsl.1.1.20190106T080057Z.0a4ff4fc589b8919.8ad89a049b3adbb977a5d137b5f4beab54e93ee9"
    ) : Single<YandexTranslateResult>
}