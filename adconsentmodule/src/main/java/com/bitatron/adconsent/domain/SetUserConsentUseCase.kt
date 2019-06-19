package com.bitatron.adconsent.domain

import com.bitatron.adconsent.data.ConsentStorage
import io.reactivex.Completable

class SetUserConsentUseCase constructor(private val consentStorage: ConsentStorage){

    fun execute(): Completable {
        return Completable.fromAction { consentStorage.setHasUserSelectedConsent(true) }
    }

}