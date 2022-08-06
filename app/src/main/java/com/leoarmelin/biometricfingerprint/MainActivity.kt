package com.leoarmelin.biometricfingerprint

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity(), BiometricHandler.OnAuthListener,
    BiometricHandler.OnAvailabilityListener {

    private val biometricHandler = BiometricHandler(this, this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        biometricHandler.verifyAvailability()

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
            ) {
                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = { biometricHandler.requestFingerprint() }
                ) {
                    Text(text = "Open biometric auth")
                }
            }
        }
    }

    override fun onAuthError(errorString: CharSequence) {
        Log.d("BiometricHandler", "onAuthError $errorString")
    }

    override fun onAuthFailed() {
        Log.d("BiometricHandler", "onAuthFailed")
    }

    override fun onAuthSuccess() {
        Log.d("BiometricHandler", "onAuthSuccess")
    }

    override fun onAvailableSuccess() {
        Log.d("BiometricHandler", "onAvailableSuccess")
        biometricHandler.configureBiometricHandler(
            "My biometric title",
            "A good description",
            "Not today pal"
        )
    }

    override fun onAvailableError(errorCode: Int) {
        Log.d("BiometricHandler", "onAvailableError")
    }

    override fun onAvailableUnknown() {
        Log.d("BiometricHandler", "onAvailableUnknown")
    }

    override fun onNegativeButtonClick() {
        Log.d("BiometricHandler", "onNegativeButtonClick")
    }
}