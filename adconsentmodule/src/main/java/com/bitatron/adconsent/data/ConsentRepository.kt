package com.bitatron.adconsent.data

import io.reactivex.Single

interface ConsentRepository {

    fun getConsentStatus(): Single<ConsentStatus>


}