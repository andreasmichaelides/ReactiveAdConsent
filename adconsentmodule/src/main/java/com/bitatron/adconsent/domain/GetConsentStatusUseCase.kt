package com.bitatron.adconsent.domain

import com.bitatron.adconsent.data.ConsentRepository
import com.bitatron.adconsent.data.ConsentStatus
import com.bitatron.adconsent.presentation.Status
import com.bitatron.statestream.logger.Logger
import com.bitatron.statestream.schedulers.SchedulersProvider
import io.reactivex.Single

class GetConsentStatusUseCase(
    private val consentRepository: ConsentRepository,
    private val schedulersProvider: SchedulersProvider,
    private val logger: Logger
) {

    fun execute(): Single<ConsentStatus> = consentRepository.getConsentStatus()
        .doOnError { logger.e(this, it) }
        .onErrorReturnItem(ConsentStatus(Status.UNKNOWN))
        .subscribeOn(schedulersProvider.io())
        .observeOn(schedulersProvider.mainThread())

}