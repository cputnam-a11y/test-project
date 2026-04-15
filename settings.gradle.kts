import org.gradle.kotlin.dsl.mavenCentral
import org.gradle.kotlin.dsl.repositories

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("com.gradle.develocity") version("4.4.0")
}

dependencyResolutionManagement {
    repositories {
        repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
        mavenCentral()
    }
}

rootProject.name = "test-project"
include("app")
includeBuild("build-logic")