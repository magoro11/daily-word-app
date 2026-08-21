package com.dailyword.nativeapp.domain

import com.dailyword.nativeapp.data.BibleVerse

/** Keeps public-domain bundled data separate from future licensed/API translations. */
interface BibleTranslationProvider {
    val translationCode: String
    suspend fun verses(): List<BibleVerse>
}
