import io.github.cputnama11y.deptransformation.transformer.Transformer
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream

plugins {
    application
    id("test-plugin")
}
open class TestTransform : Transformer {
    override fun transform(context: Transformer.Context) {
        try {
            JarInputStream(Files.newInputStream(context.inputPath.get().asFile.toPath())).use { `in` ->
                JarOutputStream(Files.newOutputStream(context.outputPath.get().asFile.toPath())).use { `out` ->
                    var entry: JarEntry? = `in`.nextJarEntry
                    while (true) {
                        if (entry == null) break

                        out.putNextEntry(entry)
                        `in`.transferTo(out)
                        entry = `in`.nextJarEntry
                    }
                    out.putNextEntry(JarEntry("test.txt"))
                    out.write("Hello, World!".toByteArray(Charsets.UTF_8))
                }
            }
        } catch (ignored: IOException) {
            Files.copy(context.inputPath.get().asFile.toPath(), context.outputPath.get().asFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }
    companion object {
        init {
            println("Arrived")
        }
    }

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

application {
    mainClass = "org.example.App"
}