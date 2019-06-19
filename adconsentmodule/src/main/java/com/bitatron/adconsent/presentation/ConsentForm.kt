package com.bitatron.adconsent.presentation

import android.app.Activity
import com.bitatron.adconsent.data.GoogleConsentStatusToConsentStatusMapper
import com.bitatron.statestream.schedulers.SchedulersProvider
import com.google.ads.consent.ConsentFormListener
import com.google.ads.consent.ConsentStatus
import io.reactivex.Observable
import java.net.URL


class GoogleConsentFormCreator(private val googleConsentStatusToConsentStatusMapper: GoogleConsentStatusToConsentStatusMapper,
                               private val privacyPolicyURL: URL,
                               private val paidPackageName: String,
                               private val schedulersProvider: SchedulersProvider) {

    private lateinit var form: com.google.ads.consent.ConsentForm

    fun create(activity: Activity): Observable<ConsentForm> {
        return Observable.create<ConsentForm> {
            val builder = com.google.ads.consent.ConsentForm.Builder(activity, privacyPolicyURL)
                    .withListener(object : ConsentFormListener() {
                        override fun onConsentFormLoaded() {
                            it.onNext(ConsentForm(form, ConsentFormStatus.LOADED, paidPackageName = paidPackageName))
                        }

                        override fun onConsentFormOpened() {
                            it.onNext(ConsentForm(form, ConsentFormStatus.OPENED, paidPackageName = paidPackageName))
                        }

                        override fun onConsentFormClosed(consentStatus: ConsentStatus, userPrefersAdFree: Boolean) {
                            it.onNext(createClosedConsentForm(consentStatus, userPrefersAdFree))
                            it.onComplete()
                        }

                        override fun onConsentFormError(errorDescription: String) {
                            it.onNext(ConsentForm(form, ConsentFormStatus.ERROR, paidPackageName = paidPackageName))
                            it.onComplete()
                        }
                    })
                    .withPersonalizedAdsOption()
                    .withNonPersonalizedAdsOption()

            form = if (paidPackageName.isEmpty()) {
                builder
            } else {
                builder.withAdFreeOption()
            }.build()

            form.load()
        }.observeOn(schedulersProvider.mainThread())
    }

    private fun createClosedConsentForm(consentStatus: ConsentStatus, userPrefersAdFree: Boolean): ConsentForm {
        return ConsentForm(form,
                ConsentFormStatus.CLOSED, googleConsentStatusToConsentStatusMapper.map(consentStatus).status,
                userPrefersAdFree,
                paidPackageName)
    }
}

data class ConsentForm(private val consentForm: com.google.ads.consent.ConsentForm,
                       val consentFormStatus: ConsentFormStatus,
                       val consentStatus: Status = Status.UNKNOWN,
                       val selectedPaidVersion: Boolean = false,
                       val paidPackageName: String) {

    fun show() {
        consentForm.show()
    }

}

enum class ConsentFormStatus {
    LOADED,
    NOT_LOADED,
    OPENED,
    CLOSED,
    ERROR
}