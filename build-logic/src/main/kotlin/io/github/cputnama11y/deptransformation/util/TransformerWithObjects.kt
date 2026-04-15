package io.github.cputnama11y.deptransformation.util

import org.gradle.api.Transformer
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class TransformerWithObjects<OUT, IN : Any>(val f: (IN, ObjectFactory) -> OUT) : Transformer<OUT, IN> {
    @get:Inject
    abstract val objects: ObjectFactory

    override fun transform(`in`: IN): OUT {
        return f(`in`, objects)
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <OUT, IN : Any> create(objects: ObjectFactory, f: (IN, ObjectFactory) -> OUT): Transformer<OUT, IN> =
            objects.newInstance(TransformerWithObjects::class.java, f) as Transformer<OUT, IN>
    }
}