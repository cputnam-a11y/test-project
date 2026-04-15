package io.github.cputnama11y.deptransformation.wiring

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

abstract class Transformations {
    @get:Inject
    protected abstract val objects: ObjectFactory

    fun create(configure: Action<in TransformationSpec>) {
        val spec = objects.newInstance<TransformationSpec>()
        configure.execute(spec)
    }
}