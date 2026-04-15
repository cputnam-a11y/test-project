plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"

}

dependencyResolutionManagement {
    repositories {
        repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
        mavenCentral()
    }
}
