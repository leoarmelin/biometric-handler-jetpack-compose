package com.leoarmelin.biometricfingerprint

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

class BiometricHandler(
    private val activity: FragmentActivity,
    private val authListener: OnAuthListener,
    private val availabilityListener: OnAvailabilityListener,
) {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var biometricManager: BiometricManager

    interface OnAuthListener {
        fun onAuthError(errorString: CharSequence)
        fun onAuthFailed()
        fun onAuthSuccess()
        fun onNegativeButtonClick()
    }

    interface OnAvailabilityListener {
        fun onAvailableSuccess()
        fun onAvailableError(errorCode: Int)
        fun onAvailableUnknown()
    }

    fun verifyAvailability() {
        biometricManager = BiometricManager.from(activity)

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("BiometricHandler", "App can authenticate using biometrics.")
                availabilityListener.onAvailableSuccess()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("BiometricHandler", "No biometric features available on this device.")
                availabilityListener.onAvailableError(BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("BiometricHandler", "Biometric features are currently unavailable.")
                availabilityListener.onAvailableError(BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e("BiometricHandler", "Biometric features are not set, please create.")
                availabilityListener.onAvailableError(BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED)
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                Log.e(
                    "BiometricHandler",
                    "A security vulnerability has been discovered with one or more hardware sensors."
                )
                availabilityListener.onAvailableError(BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED)
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                Log.e(
                    "BiometricHandler",
                    "The specified options are incompatible with the current Android version."
                )
                availabilityListener.onAvailableError(BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED)
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                Log.e("BiometricHandler", "Unable to determine whether the user can authenticate.")
                availabilityListener.onAvailableUnknown()
            }
        }
    }

    fun configureBiometricHandler(
        dialogTitle: String = "Biometric login for my app",
        dialogSubtitle: String = "Log in using your biometric credential",
        dialogNegativeButtonText: String = "Use account password"
    ) {
        executor = ContextCompat.getMainExecutor(activity)

        biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errString == dialogNegativeButtonText) {
                        authListener.onNegativeButtonClick()
                        return
                    }
                    authListener.onAuthError(errString)
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    authListener.onAuthSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    authListener.onAuthFailed()
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(dialogTitle)
            .setSubtitle(dialogSubtitle)
            .setNegativeButtonText(dialogNegativeButtonText)
            .build()
    }

    fun requestFingerprint() {
        if (!::biometricPrompt.isInitialized) throw UninitializedPropertyAccessException("You cannot request fingerprint. Please, verify if you used 'verifyAvailability' function correctly or if some error occurred, using 'BiometricHandler' tag in the Logcat.")
        biometricPrompt.authenticate(promptInfo)
    }
}