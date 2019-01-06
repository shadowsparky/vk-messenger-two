package ru.shadowsparky.messenger.response_utils.responses

data class YandexTranslateResult(
    val code: Int,
    val lang: String,
    val text: List<String>
)