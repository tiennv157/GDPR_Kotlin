package "YOUR_PACKAGE"

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

interface GDPRRequestable {
    val requestingActivity: Activity
    val context: Context
        get() = requestingActivity
    fun onRequestGDPRCompleted(error: FormError?)
}

private lateinit var consentInformation: ConsentInformation
private lateinit var consentForm: ConsentForm

val GDPRRequestable.isDebugging: Boolean
    get() = false

fun GDPRRequestable.requestGDPR() {

    val consentDebugSettingsBuilder = ConsentDebugSettings
        .Builder(context)

    if (isDebugging) {
        consentDebugSettingsBuilder
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId("YOUR_TEST_DEVICE_ID")
    }

    val consentDebugSettings = consentDebugSettingsBuilder.build()

    val params = ConsentRequestParameters
        .Builder()
        .setConsentDebugSettings(consentDebugSettings)
        .setTagForUnderAgeOfConsent(false)
        .build()

    consentInformation = UserMessagingPlatform.getConsentInformation(requestingActivity)
    consentInformation.requestConsentInfoUpdate(
        requestingActivity,
        params,
        {
            if (consentInformation.isConsentFormAvailable) {
                loadForm()
            } else {
                onConsentFormNotAvailable()
            }
        },
        {
            onRequestGDPRCompleted(it)
        }
    )
}

fun GDPRRequestable.onConsentFormNotAvailable() {
    onRequestGDPRCompleted(null)
}


fun GDPRRequestable.loadForm() {
    UserMessagingPlatform.loadConsentForm(
        requestingActivity,
        {
            consentForm = it
            if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                consentForm.show(
                    requestingActivity
                ) {
                    if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.OBTAINED) {
                        onRequestGDPRCompleted(null)
                    }
                    loadForm()
                }
            }
        },
        {
            onRequestGDPRCompleted(it)
        }
    )
}
