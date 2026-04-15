package io.github.cputnama11y.deptransformation.wiring

import io.github.cputnama11y.deptransformation.ConfiguredTransformer
import io.github.cputnama11y.deptransformation.transformer.Transformer
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.artifacts.*
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Nested
import com.google.common.base.Suppliers
import org.gradle.kotlin.dsl.newInstance
import java.util.*
import java.util.function.Supplier
import javax.inject.Inject

abstract class TransformationSpec {
    @get:Inject
    protected abstract val configurations: ConfigurationContainer

    @get:Inject
    protected abstract val objects: ObjectFactory

    protected val transformers: MutableList<ConfiguredTransformer<*>> = mutableListOf()

    @get:Nested
    abstract val dependencies: TransformationDependencies

    private val collectorConfig = configurations.dependencyScope("testScope" + UUID.randomUUID().toString()) {
        this.fromDependencyCollector(this@TransformationSpec.dependencies.transform)
    }

    inline fun <reified T : Transformer> use(configure: Action<T> = Action({})) {
        use(T::class.java, configure)
    }

    fun <T : Transformer> use(transformer: Class<T>, configure: Action<T> = Action({})) {
        transformers.add(objects.newInstance<ConfiguredTransformer<T>>(transformer, configure))
    }

    fun dependencies(action: Action<in TransformationDependencies>) = action.execute(dependencies)

    val deps: Supplier<List<Dependency>> = Suppliers.memoize {
        collectorConfig.get().dependencies.flatMap {
            when (it) {
                is ModuleDependency -> objects.newInstance<TransformedModuleDependency>(it, transformers)
                    .getActualDependencies()

                is FileCollectionDependency -> TODO() // TODO specialize for FileCollectionDependency
                else -> throw IllegalStateException("Expected Regular Dependency")
            }
        }
    }

    fun addTo(conf: NamedDomainObjectProvider<Configuration>) {
        deps.get()
        conf.configure {
            this.dependencies.addAll(deps.get())
        }
    }
}