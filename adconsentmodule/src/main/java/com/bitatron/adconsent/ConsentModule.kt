package com.bitatron.adconsent

import android.content.res.Resources
import com.bitatron.adconsent.data.*
import com.bitatron.adconsent.domain.GetConsentStatusUseCase
import com.bitatron.adconsent.domain.GetIfConsentSetUseCase
import com.bitatron.adconsent.domain.SetUserConsentUseCase
import com.bitatron.adconsent.presentation.ConsentViewModel
import com.bitatron.adconsent.presentation.GoogleConsentFormCreator
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import java.net.URL

val consentModule = module {
    // Data
    factory<ConsentStatusSource> {
        ConsentStatusSourceImpl(get(),
                get<Resources>().getString(R.string.consent_lib_publisher_id),
                get())
    }
    factory<ConsentStorage> { ConsentStorageImpl(get()) }
    factory<ConsentRepository> { ConsentRepositoryImpl(get(), get()) }

    // Domain
    factory { SetUserConsentUseCase(get()) }
    factory { GetConsentStatusUseCase(get(), get()) }
    factory { GetIfConsentSetUseCase(get()) }

    // Presentation
    factory {
        GoogleConsentFormCreator(get(),
                URL(get<Resources>().getString(R.string.consent_lib_url_privacy_policy)),
                get<Resources>().getString(R.string.consent_lib_paid_app_package_name),
                get())
    }
    factory { GoogleConsentStatusToConsentStatusMapper() }
    viewModel { ConsentViewModel(get(), get(), get(), get(), get()) }
}