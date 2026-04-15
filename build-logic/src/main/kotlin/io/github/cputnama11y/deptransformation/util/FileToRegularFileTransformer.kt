package io.github.cputnama11y.deptransformation.util

import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import java.io.File

abstract class FileToRegularFileTransformer: TransformerWithObjects<RegularFile, File>({ file, objects ->
    objects.fileProperty().apply { set(file) }.get()
}) {
    companion object {
        fun create(objects: ObjectFactory) = objects.newInstance(FileToRegularFileTransformer::class.java)
    }
}