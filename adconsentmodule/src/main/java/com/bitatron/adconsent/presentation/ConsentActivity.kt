package com.bitatron.adconsent.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bitatron.adconsent.R
import com.bitatron.statestream.logger.Logger
import com.bitatron.statestream.presentation.openAppInPlaystore
import com.bitatron.statestream.presentation.popAll
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject

private const val KEY_IGNORE_IF_CONSENT_SET = "KEY_IGNORE_IF_CONSENT_SET"

class ConsentActivity : AppCompatActivity() {

    companion object {
        fun getIntent(activity: Activity, ignoreIfConsentSet: Boolean = false): Intent {
            val intent = Intent(activity, ConsentActivity::class.java)
            intent.putExtra(KEY_IGNORE_IF_CONSENT_SET, ignoreIfConsentSet)
            return intent
        }
    }

    private val consentViewModel: ConsentViewModel by inject()
    private val googleConsentFormCreator: GoogleConsentFormCreator by inject()
    private val logger: Logger by inject()

    private val subscriptions = CompositeDisposable()
    private val consentForm = PublishSubject.create<Observable<ConsentForm>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(0, 0)

        setContentView(R.layout.activity_consent)

        subscriptions.add(
                consentForm.switchMap { it }
                        .doOnNext {
                            it.show()
                        }
                        .filter {
                            it.consentFormStatus == ConsentFormStatus.CLOSED
                                    || it.consentFormStatus == ConsentFormStatus.ERROR
                        }
                        .doOnError { consentViewModel.onConsentDialogError(it) }
                        .onErrorResumeNext(Observable.never())
                        .subscribe {
                            consentViewModel.onConsentDialogClosed(it)
                        }
        )

        consentViewModel.activityUiModel().observe(this, Observer { onStateChanged(it) })
    }

    override fun onResume() {
        super.onResume()
        consentViewModel.checkConsentStatus(intent.getBooleanExtra(KEY_IGNORE_IF_CONSENT_SET, false))
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
        consentViewModel.onActivityFinished()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }

    private fun onStateChanged(uiModel: ConsentUiModel) {
        uiModel.activityActions.popAll { action ->
            when (action) {
                is ShowPaidApp -> openAppInPlaystore(action.paidAppPackageName, logger)
                is FinishActivity -> finish()
                is ShowConsentDialog -> {
                    consentForm.onNext(googleConsentFormCreator.create(this))
                }
            }
        }
    }
}