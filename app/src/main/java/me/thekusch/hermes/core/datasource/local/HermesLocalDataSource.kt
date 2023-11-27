package me.thekusch.hermes.core.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HermesLocalDataSource @Inject constructor() {

    private val NAME = "NAME"
    private val PASSWORD = "PASSWORD"
    private val EMAIL = "EMAIL"
    private val ISSIGNUP_FINISHED = "ISSIGNUP_FINISHED"

    private lateinit var sharedPreferences: SharedPreferences

    internal var name: String?
        get() {
            return sharedPreferences.getString(NAME, null)
        }
        set(value) {
            sharedPreferences.edit {
                putString(NAME, value)
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
                putBoolean(ISSIGNUP_FINISHED, value)
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