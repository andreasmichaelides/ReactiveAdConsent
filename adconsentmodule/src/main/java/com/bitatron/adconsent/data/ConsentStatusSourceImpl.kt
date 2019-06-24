package com.bitatron.adconsent.data

import android.content.Context
import com.google.ads.consent.ConsentInfoUpdateListener
import com.google.ads.consent.ConsentInformation
import io.reactivex.Single

class ConsentStatusSourceImpl(private val context: Context,
                              private val publisherId: String,
                              private val googleConsentStatusToConsentStatusMapper: GoogleConsentStatusToConsentStatusMapper) : ConsentStatusSource {

    override fun getConsentStatus(): Single<ConsentStatus> {
        return Single.just(context)
                .map { ConsentInformation.getInstance(it) }
                .flatMap { consentInformation ->
                    Single.create<com.google.ads.consent.ConsentStatus> {
                        consentInformation.requestConsentInfoUpdate(listOf(publisherId).toTypedArray(), object : ConsentInfoUpdateListener {
                            override fun onConsentInfoUpdated(consentStatus: com.google.ads.consent.ConsentStatus) {
                                // User's consent status successfully updated.
                                it.onSuccess(consentStatus)
                            }

                            override fun onFailedToUpdateConsentInfo(errorDescription: String) {
                                // User's consent status failed to update.
                                it.tryOnError(Exception(errorDescription))
                            }
                        })
                    }
                }.map { googleConsentStatusToConsentStatusMapper.map(it) }
    }

}