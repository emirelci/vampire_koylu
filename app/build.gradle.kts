plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.ee.vampirkoylu"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ee.vampirkoylu"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "0.6.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {

        debug {
            buildConfigField("String", "BILLING_PUBLIC_KEY", "\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArKCHhuf3b3tqx8WZSwh7SvYap3yxyUbBYljSKjw97YfPaGbVzO58BerDZ1OmErc15wRGvKdvQwlEvE/EAjzJTaOndVkOZcAqoG2hNAhOQXrmJcaGgkY7H/j6sOeyNxXDhFh7sx5JswpDD3N3h3mYORjb7huE8q6oFq7shilAoZy3lZAAv0OKG7uQZp5EtYoT0GJ14x2QKO5FGb4n+o7Y953xLIAnOm5zx4Sm25PRvjBlzbBdXcyQsECSV02J47efJU3GA8ht/K3R3vxDXwxYr6W+Fq84CksHArqoYNqo+o5x69VpOwuowQOYA5tsv44zRa61/3pvpOrEh7Q5R9ZDhwIDAQAB\"")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BILLING_PUBLIC_KEY", "\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArKCHhuf3b3tqx8WZSwh7SvYap3yxyUbBYljSKjw97YfPaGbVzO58BerDZ1OmErc15wRGvKdvQwlEvE/EAjzJTaOndVkOZcAqoG2hNAhOQXrmJcaGgkY7H/j6sOeyNxXDhFh7sx5JswpDD3N3h3mYORjb7huE8q6oFq7shilAoZy3lZAAv0OKG7uQZp5EtYoT0GJ14x2QKO5FGb4n+o7Y953xLIAnOm5zx4Sm25PRvjBlzbBdXcyQsECSV02J47efJU3GA8ht/K3R3vxDXwxYr6W+Fq84CksHArqoYNqo+o5x69VpOwuowQOYA5tsv44zRa61/3pvpOrEh7Q5R9ZDhwIDAQAB\"")
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true
        dataBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.billing.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.material)
    //Navigation
    implementation(libs.androidx.navigation.compose)
    //Coroutine
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.material3.window.size.class1)

    //Google Play Billing
    implementation(libs.billing)

    //Google Ads
    implementation("com.google.android.gms:play-services-ads:24.4.0")

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.runtime.compose)

    //Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)


}