package com.bitatron.adconsent.presentation

import com.bitatron.adconsent.domain.GetConsentStatusUseCase
import com.bitatron.adconsent.domain.GetIfConsentSetUseCase
import com.bitatron.adconsent.domain.SetUserConsentUseCase
import com.bitatron.statestream.logger.Logger
import com.bitatron.statestream.presentation.StateViewModel
import com.bitatron.statestream.schedulers.SchedulersProvider

class ConsentViewModel(private val logger: Logger,
                       schedulersProvider: SchedulersProvider,
                       getConsentStatusUseCase: GetConsentStatusUseCase,
                       getIfConsentSetUseCase: GetIfConsentSetUseCase,
                       setUserConsentUseCase: SetUserConsentUseCase)
    : StateViewModel<ConsentUiModel>(ConsentUiModel(),
        logger,
        schedulersProvider) {

    init {
        subscriptions.addAll(
                viewModelAction().filter { it == CheckConsentStatus }
                        .flatMapSingle {
                            getConsentStatusUseCase.execute()
                                    .doOnError { logger.e(this, it) }
                        }
                        .subscribe { input().onNext(CheckConsentStatusSuccessInput(it)) },

                viewModelAction().filter { it == CheckIfConsentAlreadySet }
                        .flatMapSingle {
                            getIfConsentSetUseCase.execute()
                                    .doOnError { logger.e(this, it) }
                        }
                        .subscribe { input().onNext(OnIsConsentSetInput(it)) },

                viewModelAction().filter { it == SetUserConsentAsSet }
                        .flatMapCompletable {
                            setUserConsentUseCase.execute()
                                    .doOnError { logger.e(this, it) }
                                    .onErrorComplete()
                                    .doOnComplete { input().onNext(OnUserConsentSetInput) }
                        }.subscribe()
        )
    }

    fun checkConsentStatus(ignoreIfConsentSet: Boolean) {
        input().onNext(CheckConsentStatusInput(ignoreIfConsentSet))
    }

    fun onConsentDialogClosed(consentForm: ConsentForm) {
        input().onNext(OnConsentDialogClosedInput(consentForm))
    }

    fun onConsentDialogError(throwable: Throwable) {
        logger.e(this, throwable)
        input().onNext(OnConsentDialogErrorInput)
    }

    fun onActivityFinished() {
        input().onNext(OnActivityFinished)
    }
}