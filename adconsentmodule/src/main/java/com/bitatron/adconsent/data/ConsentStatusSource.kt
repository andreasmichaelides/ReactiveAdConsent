package com.bitatron.adconsent.data

import io.reactivex.Single

interface ConsentStatusSource {

    fun getConsentStatus(): Single<ConsentStatus>

}