package io.github.cputnama11y.deptransformation.wiring

import io.github.cputnama11y.deptransformation.transformer.Transformer
import io.github.cputnama11y.deptransformation.wiring.TransformedModuleDependency.Companion.nextID
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.newInstance
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.inject.Inject

//@CacheableTask
abstract class TransformationTask : DefaultTask() {
    @get:Inject
    protected abstract val objects: ObjectFactory

    @get:InputFile
    abstract val inputArtifact: RegularFileProperty

    @get:InputFiles
    @get:Classpath
    abstract val classpathArtifacts: ConfigurableFileCollection

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

//    @get:Input
    @get:Nested
    abstract val transformers: ListProperty<Transformer>

    @get:Internal
    private var nextFileID = 0L

    @TaskAction
    fun transform() {
        val context = objects.newInstance<Context>()
        context.inputPath.set(inputArtifact)
        context.outputPath.set(getTempFile())
        context.classpath.from(classpathArtifacts)
        for (transformer in transformers.get()) {
            transformer.transform(context)
            context.inputPath.set(context.outputPath.get())
            context.outputPath.set(getTempFile())
        }
        Files.copy(
            context.inputPath.get().asFile.toPath(),
            context.outputPath.get().asFile.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
        Files.copy(
            context.outputPath.get().asFile.toPath(),
            outputFile.get().asFile.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
    }
    @Internal
    fun getTempFile() = temporaryDir.resolve("temp-${nextFileID++}")

    interface Context : Transformer.Context {
        override val outputPath: RegularFileProperty
        override val inputPath: RegularFileProperty
        override val classpath: ConfigurableFileCollection
    }


}