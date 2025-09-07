// build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false     // Hilt/Room KSP
    alias(libs.plugins.hilt) apply false    // Hilt

    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    // ——— KTLINT ———
    plugins.withId("org.jetbrains.kotlin.android") {
        apply(plugin = "org.jlleitschuh.gradle.ktlint")

        configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
            ignoreFailures.set(false)
            reporters {
                reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
                reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
                reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.SARIF)
            }
        }
        tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask>().configureEach {
            workerMaxHeapSize.set("512m")
        }
    }

    // Si algún día añades módulos JVM puros:
    plugins.withId("org.jetbrains.kotlin.jvm") {
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
    }

    // ——— DETEKT ———
    plugins.withId("org.jetbrains.kotlin.android") {
        apply(plugin = "io.gitlab.arturbosch.detekt")

        // Reglas extra para Compose (opcional, recomendado)
        dependencies {
            add("detektPlugins", "io.nlopez.compose.rules:detekt:${libs.versions.composeRules.get()}")
        }

        val detektConfig = rootProject.file("config/detekt/detekt.yml")
        val detektBaseline = rootProject.file("config/detekt/baseline.xml")

        configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
            buildUponDefaultConfig = true
            allRules = false
            config.setFrom(detektConfig)
            parallel = true
            if (detektBaseline.exists()) baseline = detektBaseline
        }

        tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
            jvmTarget = "17"
            reports {
                html.required.set(true)
                xml.required.set(true)
                sarif.required.set(true)
                txt.required.set(false)
                md.required.set(false)
            }
        }
    }

    // Si algún día añades módulos JVM puros:
    plugins.withId("org.jetbrains.kotlin.jvm") {
        apply(plugin = "io.gitlab.arturbosch.detekt")
    }
}

// ——— TAREAS DE COMODIDAD ———
tasks.register("detektAll") {
    group = "verification"
    description = "Ejecuta Detekt en todos los módulos"
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("detekt") })
}

tasks.register("checkQuality") {
    group = "verification"
    description = "ktlintCheck + detekt"
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("ktlintCheck") })
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("detekt") })
}