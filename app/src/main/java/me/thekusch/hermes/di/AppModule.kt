package me.thekusch.hermes.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
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
}