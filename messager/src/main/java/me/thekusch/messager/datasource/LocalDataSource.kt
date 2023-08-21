package me.thekusch.messager.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

internal object LocalDataSource {

    private const val USERNAME = "USERNAME"

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