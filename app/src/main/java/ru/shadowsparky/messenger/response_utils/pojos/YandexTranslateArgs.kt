package ru.shadowsparky.messenger.response_utils.pojos

data class YandexTranslateArgs(
    val text: String,
    val key: String = "trnsl.1.1.20190106T080057Z.0a4ff4fc589b8919.8ad89a049b3adbb977a5d137b5f4beab54e93ee9f",
    val lang: String = "en-ru"
)