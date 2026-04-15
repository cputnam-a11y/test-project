import io.github.cputnama11y.deptransformation.transformer.TestTransform


plugins {
    application
    id("test-plugin")
}

transformations {
    create {
        dependencies {
            transform(libs.guava)
        }
        use<TestTransform>()
        addTo(configurations.implementation)
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

println(configurations.runtimeClasspath.get().elements.get())

application {
    mainClass = "org.example.App"
}