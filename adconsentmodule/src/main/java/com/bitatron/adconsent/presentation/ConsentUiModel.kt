package com.bitatron.adconsent.presentation

import com.bitatron.adconsent.data.ConsentStatus
import com.bitatron.statestream.presentation.*
import java.util.*

data class ConsentUiModel(
        val isLoading: Boolean = false,
        val ignoreIfConsentSet: Boolean = false,
        val isDialogShown: Boolean = false
) : UiModel(Stack(), Stack(), Stack())

object CheckConsentStatus : ViewModelAction
object CheckIfConsentAlreadySet : ViewModelAction
object SetUserConsentAsSet : ViewModelAction

object ShowConsentDialog : ActivityAction
data class ShowPaidApp(val paidAppPackageName: String) : ActivityAction
object FinishActivity : ActivityAction

data class CheckConsentStatusInput(private val ignoreIfConsentSet: Boolean) : Input<ConsentUiModel> {
    override fun transformState(uiModel: ConsentUiModel): ConsentUiModel =
            uiModel.copy(isLoading = true, ignoreIfConsentSet = ignoreIfConsentSet)
                    .push(CheckConsentStatus)
}

data class CheckConsentStatusSuccessInput(private val consentStatus: ConsentStatus) : Input<ConsentUiModel> {
    override fun transformState(uiModel: ConsentUiModel): ConsentUiModel {
        return if (consentStatus.status == Status.UNKNOWN || uiModel.ignoreIfConsentSet) {
            uiModel.push(CheckIfConsentAlreadySet)
        } else {
            uiModel.push(FinishActivity)
        }
    }
}

data class OnIsConsentSetInput(private val isConsentSet: Boolean) : Input<ConsentUiModel> {
    override fun transformState(uiModel: ConsentUiModel): ConsentUiModel =
            when (isConsentSet && !uiModel.ignoreIfConsentSet) {
                true -> uiModel.copy(isLoading = false).push(FinishActivity)
                else -> if (!uiModel.isDialogShown) {
                    uiModel.copy(isLoading = false, isDialogShown = true).push(ShowConsentDialog)
                } else {
                    uiModel.copy(isLoading = false)
                }
            }
}

data class OnConsentDialogClosedInput(private val consentForm: ConsentForm) : Input<ConsentUiModel> {
    override fun transformState(uiModel: ConsentUiModel): ConsentUiModel {
        val consentUiModel = uiModel.copy(isDialogShown = false)

        return if (consentForm.selectedPaidVersion) {
            consentUiModel
                    .push(ShowPaidApp(consentForm.paidPackageName))
        } else {
            when (consentForm.consentStatus) {
                Status.PERSONALIZED -> consentUiModel.push(SetUserConsentAsSet)
                Status.NON_PERSONALIZED -> consentUiModel.push(SetUserConsentAsSet)
                Status.UNKNOWN -> consentUiModel.push(FinishActivity)
            }
        }
    }
}

object OnActivityFinished : Input<ConsentUiModel> {
    override fun transformState(uiModel: ConsentUiModel): ConsentUiModel = uiModel.copy(isDialogShown = false)
}

object OnConsentDialogErrorInput : Input<ConsentUiModel> {
    override fun transformState(uiModel: ConsentUiModel): ConsentUiModel = uiModel.push(FinishActivity)
}

object OnUserConsentSetInput : Input<ConsentUiModel> {
    override fun transformState(uiModel: ConsentUiModel): ConsentUiModel = uiModel.push(FinishActivity)
}

