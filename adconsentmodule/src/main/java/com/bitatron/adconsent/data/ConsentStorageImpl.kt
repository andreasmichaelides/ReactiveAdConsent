package com.bitatron.adconsent.data

import android.content.Context
import android.content.SharedPreferences

private const val COM_BITATRON_AD_CONSENT_PREFERENCES = "COM_BITATRON_AD_CONSENT_PREFERENCES"
private const val USER_CONSENT = "USER_CONSENT"

class ConsentStorageImpl(context: Context) : ConsentStorage {

    private val sharedPreferences: SharedPreferences = context.applicationContext.getSharedPreferences(COM_BITATRON_AD_CONSENT_PREFERENCES, Context.MODE_PRIVATE)

    override fun setHasUserSelectedConsent(hasUserSelectedConsent: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(USER_CONSENT, hasUserSelectedConsent).apply()
    }

    override fun getHasUserSelectedConsent(): Boolean = sharedPreferences.getBoolean(USER_CONSENT, false)
}