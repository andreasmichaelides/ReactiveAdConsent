package com.bitatron.adconsent.domain

import com.bitatron.adconsent.data.ConsentRepository
import com.bitatron.adconsent.data.ConsentStatus
import com.bitatron.statestream.schedulers.SchedulersProvider
import io.reactivex.Single

class GetConsentStatusUseCase (private val consentRepository: ConsentRepository,
                               private val schedulersProvider: SchedulersProvider) {

    fun execute(): Single<ConsentStatus> = consentRepository.getConsentStatus()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.mainThread())

}