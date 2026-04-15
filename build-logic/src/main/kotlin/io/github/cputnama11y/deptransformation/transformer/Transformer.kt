package io.github.cputnama11y.deptransformation.transformer

import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

interface Transformer {
    fun transform(context: Context)

    interface Context {
        val outputPath: Provider<RegularFile>
        val inputPath: Provider<RegularFile>
        val classpath: FileCollection
    }
}