package io.github.cputnama11y.deptransformation

import io.github.cputnama11y.deptransformation.transformer.Transformer
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class ConfiguredTransformer<T : Transformer> @Inject constructor(
    val transformer: Class<T>,
    val configure: Action<T>
) {
    @get:Inject
    protected abstract val objects: ObjectFactory

    fun create() = objects.newInstance(transformer).apply {
        configure.execute(this)
    }
}