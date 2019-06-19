package com.bitatron.adconsent.domain

import com.bitatron.adconsent.data.ConsentStorage
import io.reactivex.Single

class GetIfConsentSetUseCase(private val consentStorage: ConsentStorage) {

    fun execute(): Single<Boolean> {
        return Single.just(consentStorage.getHasUserSelectedConsent())
    }

}