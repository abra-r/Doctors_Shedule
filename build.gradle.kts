// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google() // Google's Maven repository
        mavenCentral() // Maven Central repository
    }
    dependencies {
        // Firebase plugin for Google services
        classpath(libs.google.services)
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
}