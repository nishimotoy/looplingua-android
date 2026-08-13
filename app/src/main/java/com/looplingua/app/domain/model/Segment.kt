package com.looplingua.app.domain.model

data class Segment(

    val id: Long,

    val originalStartMs: Long,
    val originalEndMs: Long,

    val translationStartMs: Long = 0,
    val translationEndMs: Long = 0,

    val memoStartMs: Long = 0,
    val memoEndMs: Long = 0,

    val originalText: String,
    val translationText: String? = null,

    val memoAuto: String = "",
    val memoUser: String = "",

    val flagged: Boolean = false
) {

    val memoText: String?
        get() = when {
            memoUser.isNotBlank() && memoAuto.isNotBlank() ->
                "$memoUser\n[Auto] $memoAuto"

            memoUser.isNotBlank() ->
                memoUser

            memoAuto.isNotBlank() ->
                "[Auto] $memoAuto"

            else ->
                null
        }
}