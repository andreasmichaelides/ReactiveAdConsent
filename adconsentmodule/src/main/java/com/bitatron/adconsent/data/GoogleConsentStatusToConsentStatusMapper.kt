package com.bitatron.adconsent.data

import com.bitatron.adconsent.presentation.Status


class GoogleConsentStatusToConsentStatusMapper {

    fun map(consentStatus: com.google.ads.consent.ConsentStatus): ConsentStatus = when (consentStatus) {
        com.google.ads.consent.ConsentStatus.UNKNOWN -> ConsentStatus(Status.UNKNOWN)
        com.google.ads.consent.ConsentStatus.NON_PERSONALIZED -> ConsentStatus(Status.NON_PERSONALIZED)
        com.google.ads.consent.ConsentStatus.PERSONALIZED -> ConsentStatus(Status.PERSONALIZED)
        else -> {
            throw Exception("Unknown Google Consent Status")
        }
    }

}
