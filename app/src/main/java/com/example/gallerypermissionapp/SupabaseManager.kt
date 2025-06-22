package com.example.gallerypermissionapp

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseManager {

    private const val SUPABASE_URL = "https://cmjjgicgyubhvdddvnsb.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNtampnaWNneXViaGV2ZGR2bnNiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTA1NTAxMTgsImV4cCI6MjA2NjEyNjExOH0.xvVDVQ8ibEBD2pmcY5bPLiM0QiB1JbuMRYHMA82iUdU"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }
} 