package me.thekusch.messager.wifi.permission

import android.app.Activity
import android.content.IntentSender
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task

class LocationRequestBuilder {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /**
     * launch ActivityResultContract with sendRequest provided by this lambda
     * */
    var onRegisterActivityToEnableLocation: ((senderRequest: IntentSenderRequest) -> Unit)? = null

    fun init(
        hostActivity: Activity,
    ) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(hostActivity)
    }

    /**
     * Request the enable GPS, if disabled
     * [onRegisterActivityToEnableLocation] lambda will invoked, if approve dialog should shown the user
     * */
    fun requestToEnableLocation(
        hostActivity: Activity,
        onError: (() -> Unit)
    ) = initLocationRequest(hostActivity, onError)

    private fun initLocationRequest(
        hostActivity: Activity,
        onError: (() -> Unit)
    ) {
        val locationRequest = LocationRequest.create().apply {
            interval = INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(hostActivity)
        val locationReqTask = client.checkLocationSettings(builder.build())

        listenLocationResponse(locationReqTask, onError)
    }

    private fun listenLocationResponse(
        locationReqTask: Task<LocationSettingsResponse>,
        onError: (() -> Unit)
    ) {
        locationReqTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    val intentSenderRequest = IntentSenderRequest
                        .Builder(exception.resolution).build()

                    onRegisterActivityToEnableLocation?.invoke(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            } else {
                onError()
            }
        }
    }

    companion object {
        private const val INTERVAL = 10000L
    }
}