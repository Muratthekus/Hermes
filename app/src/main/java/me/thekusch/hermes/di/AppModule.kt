package me.thekusch.hermes.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import me.thekusch.hermes.core.datasource.local.room.HermesDataBase
import me.thekusch.hermes.core.datasource.local.room.dao.ChatDao
import me.thekusch.hermes.core.datasource.local.room.dao.MessageDao
import me.thekusch.hermes.core.datasource.local.room.dao.UserDao
import me.thekusch.hermes.core.navigator.DefaultFragmentNavigatorQualifier
import me.thekusch.hermes.core.navigator.DefaultNavigatorQualifier
import me.thekusch.hermes.core.navigator.HermesFragmentNavigator
import me.thekusch.hermes.core.navigator.HermesFragmentNavigatorImpl
import me.thekusch.hermes.core.navigator.HermesNavigator
import me.thekusch.hermes.core.navigator.HermesNavigatorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideSupaBase(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://bqszxhitbuyqkbjgjrpr.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJxc3p4aGl0YnV5cWtiamdqcnByIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTMwNTM4NTcsImV4cCI6MjAwODYyOTg1N30.mOs4OXa1W5pqKEH3XgVfu1SVpBcrEBzFE9K2-rcb-HU"
        ) {
            install(GoTrue) {

            }
        }
    }

    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): HermesDataBase {
        return Room.databaseBuilder(
            context,
            HermesDataBase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @DefaultNavigatorQualifier
    fun provideDefaultNavigator(): HermesNavigator {
        return HermesNavigatorImpl()
    }

    @Provides
    @DefaultFragmentNavigatorQualifier
    fun provideFragmentNavigator(): HermesFragmentNavigator {
        return HermesFragmentNavigatorImpl()
    }

    @Provides
    fun provideUserDao(database: HermesDataBase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideMessageDao(database: HermesDataBase): MessageDao {
        return database.messageDao()
    }

    @Provides
    fun provideChatDao(database: HermesDataBase): ChatDao {
        return database.chatDao()
    }

    @Provides
    @Singleton
    fun provideSecureSharedPref(
        @ApplicationContext context: Context
    ): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        // Initialize/open an instance of EncryptedSharedPreferences on below line.
        return EncryptedSharedPreferences.create(
            // passing a file name to share a preferences
            context.packageName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}