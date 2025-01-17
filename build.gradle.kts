// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
buildscript {
    repositories {
        // Check that you have the following line (if not, add it):
        google()  // Google's Maven repository

    }
    dependencies {
        classpath ("com.google.gms:google-services:4.4.2")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.0")

    }
}