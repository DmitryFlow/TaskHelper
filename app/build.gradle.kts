// app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) // Hilt
    alias(libs.plugins.hilt) // Hilt
    jacoco
}

android {
    namespace = "com.example.taskhelper"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.taskhelper"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            buildConfigField("String", "API_BASE_URL", "\"https://api.tu-dominio.com/\"")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            buildConfigField("boolean", "ENABLE_HTTP_LOGS", "true")
            buildConfigField("String", "API_BASE_URL", "\"https://dev.api.tu-dominio.com/\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.all {
            it.jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
        }
    }
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    description = "Genera reporte de cobertura para testDebugUnitTest"
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter =
        listOf(
            // Android/BuildConfig/Manifiestos
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            // Hilt/DI generados
            "**/*_Factory*.*",
            "**/*_Hilt*.*",
            "**/hilt_*/**",
            "**/*_MembersInjector*.*",
            // Room/KSP generados
            "**/*_Impl*.*",
            "**/database/**",
            "**/databinding/**",
            // AndroidX framework (no lo controlamos)
            "**/androidx/**",
        )

    val buildDirFile = layout.buildDirectory.get().asFile

    val javaClasses =
        fileTree("$buildDirFile/intermediates/javac/debug/classes") {
            exclude(fileFilter)
        }

    val kotlinClasses =
        fileTree("$buildDirFile/tmp/kotlin-classes/debug") {
            exclude(fileFilter)
        }
    classDirectories.setFrom(files(javaClasses, kotlinClasses))

    val srcDirs = files("src/main/java", "src/main/kotlin")
    sourceDirectories.setFrom(srcDirs)

    executionData.setFrom(
        fileTree(buildDirFile) {
            include(
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
                "jacoco/testDebugUnitTest.exec",
            )
        },
    )
}

// (Opcional) umbral mínimo inicial 60% para fallar el build
tasks.register<JacocoCoverageVerification>("jacocoCoverageVerification") {
    dependsOn("jacocoTestReport")
    violationRules {
        rule {
            limit {
                minimum = "0.60".toBigDecimal()
            }
        }
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    // Para previews en el editor
    implementation(libs.compose.ui.tooling.preview)
    // Para el inspector en runtime (solo debug)
    debugImplementation(libs.compose.ui.tooling)

    // Navigation
    implementation(libs.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Networking
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.ksp)
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.datastore.preferences)

    // Coroutines
    implementation(libs.coroutines.android)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso)
}
