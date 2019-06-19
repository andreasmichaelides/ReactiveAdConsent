package com.bitatron.adconsent.data

interface ConsentStorage {

    fun setHasUserSelectedConsent(hasUserSelectedConsent: Boolean)

    fun getHasUserSelectedConsent(): Boolean

}