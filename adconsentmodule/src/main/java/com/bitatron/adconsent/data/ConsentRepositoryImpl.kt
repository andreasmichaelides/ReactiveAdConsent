package com.bitatron.adconsent.data

import android.content.Context
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.DebugGeography
import io.reactivex.Single

class ConsentRepositoryImpl(context: Context,
                            private val consentStatusSource: ConsentStatusSource) : ConsentRepository {

    init {
        ConsentInformation.getInstance(context.applicationContext).debugGeography = DebugGeography.DEBUG_GEOGRAPHY_DISABLED
    }

    override fun getConsentStatus(): Single<ConsentStatus> {
        return consentStatusSource.getConsentStatus()
    }

}