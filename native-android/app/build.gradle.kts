plugins { id("com.android.application"); id("org.jetbrains.kotlin.android"); id("com.google.dagger.hilt.android"); id("com.google.devtools.ksp") }

android { namespace = "com.dailyword.nativeapp"; compileSdk = 35
    defaultConfig { applicationId = "com.dailyword.nativeapp"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "1.0" }
    buildFeatures { compose = true; buildConfig = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.15" }
}
dependencies {
    implementation("androidx.core:core-ktx:1.15.0"); implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.0"); implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui"); implementation("androidx.compose.ui:ui-tooling-preview"); implementation("androidx.compose.material3:material3"); implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.datastore:datastore-preferences:1.1.2"); implementation("androidx.room:room-runtime:2.6.1"); implementation("androidx.room:room-ktx:2.6.1"); ksp("androidx.room:room-compiler:2.6.1")
    implementation("com.google.dagger:hilt-android:2.52"); ksp("com.google.dagger:hilt-compiler:2.52"); implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.work:work-runtime-ktx:2.10.0"); implementation("androidx.glance:glance-appwidget:1.1.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
