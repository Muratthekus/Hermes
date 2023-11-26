package me.thekusch.hermes.core.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

internal object HermesLocalDataSource {

    private const val USERNAME = "USERNAME"
    private const val PASSWORD = "PASSWORD"
    private const val EMAIL = "EMAIL"
    private const val ISSIGNUP_FINISHED = "ISSIGNUP_FINISHED"

    private lateinit var sharedPreferences: SharedPreferences

    internal var username: String?
        get() {
            return sharedPreferences.getString(USERNAME, null)
        }
        set(value) {
            sharedPreferences.edit {
                putString(USERNAME, value)
            }
        }

    internal var password: String?
        get() {
            return sharedPreferences.getString(PASSWORD, null)
        }
        set(value) {
            sharedPreferences.edit {
                putString(PASSWORD, value)
            }
        }

    internal var email: String?
        get() {
            return sharedPreferences.getString(PASSWORD, null)
        }
        set(value) {
            sharedPreferences.edit {
                putString(PASSWORD, value)
            }
        }

    internal var isSignUpProcessFinished: Boolean
        get() {
            return sharedPreferences.getBoolean(ISSIGNUP_FINISHED, false)
        }
        set(value) {
            sharedPreferences.edit {
                putBoolean(ISSIGNUP_FINISHED,value)
            }
        }

    fun init(context: Context) {

        if (::sharedPreferences.isInitialized)
            return

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        // Initialize/open an instance of EncryptedSharedPreferences on below line.
        sharedPreferences = EncryptedSharedPreferences.create(
            // passing a file name to share a preferences
            context.packageName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

}