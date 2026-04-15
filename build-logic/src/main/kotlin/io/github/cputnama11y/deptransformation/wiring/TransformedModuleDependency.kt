package io.github.cputnama11y.deptransformation.wiring

import io.github.cputnama11y.deptransformation.ConfiguredTransformer
import io.github.cputnama11y.deptransformation.util.FileToRegularFileTransformer
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.artifacts.dsl.DependencyFactory
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.newInstance
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

abstract class TransformedModuleDependency constructor(
    private val dep: ModuleDependency,
    private val baseCapability: Capability,
    private val transformers: List<ConfiguredTransformer<*>>,
    vararg val otherCaps: Capability
) {
    @Inject
    constructor(dep: ModuleDependency, transformers: List<ConfiguredTransformer<*>>) : this(
        dep,
        Capability(dep.group!!, dep.name),
        transformers,
        *dep.requestedCapabilities.map { Capability.from(it) }.toTypedArray()
    )

    @get:Inject
    protected abstract val dependencies: DependencyHandler

    @get:Inject
    protected abstract val objects: ObjectFactory

    @get:Inject
    protected abstract val tasks: TaskContainer

    @get:Inject
    protected abstract val layout: ProjectLayout

    @get:Inject
    protected abstract val configurations: ConfigurationContainer

    @get:Inject
    protected abstract val projectArtifacts: ArtifactHandler

    @get:Inject
    protected abstract val dependencyFactory: DependencyFactory

    private val inputConfiguration = configurations.register(
        "transformationInputFor${baseCapability.name}#${nextID.getAndIncrement()}"
    ) {
        isCanBeDeclared = true
    }
    private val outputConfiguration = configurations.consumable(
        "transformationOutputFor${baseCapability.name}#${nextID.getAndIncrement()}"
    )
    private val transformationTask = tasks.register(
        "transform${baseCapability.name}#${nextID.getAndIncrement()}",
        TransformationTask::class.java,
    ) {
        inputArtifact.set(inputConfiguration.flatMap {
            return@flatMap it.incoming.artifacts.resolvedArtifacts.map {
                it.find { artifact ->
                    artifact.variant.capabilities.any {
                        Capability.from(it).equals(baseCapability)
                    } && otherCaps.all { otherCap ->
                        artifact.variant.capabilities.any {
                            Capability.from(it).equals(otherCap)
                        }
                    }
                }?.file
            }
        }.map(FileToRegularFileTransformer.create(objects)))
        classpathArtifacts.from(inputConfiguration.flatMap {
            return@flatMap it.incoming.artifacts.resolvedArtifacts.map {
                it.filter { artifact ->
                    !(artifact.variant.capabilities.any {
                        Capability.from(it).equals(baseCapability)
                    } && otherCaps.all { otherCap ->
                        artifact.variant.capabilities.any {
                            Capability.from(it).equals(otherCap)
                        }
                    })
                }.map { it.file }
            }
        })

        outputFile.set(layout.projectDirectory.file(".transformers").asFile.resolve(name))
        transformers.set(this@TransformedModuleDependency.transformers.map {
            it.create()
        })
    }

    init {
        outputConfiguration.configure {
            this.outgoing.capability("${baseCapability.group}:${baseCapability.name}:${dep.version}")
            otherCaps.forEach {
                this.outgoing.capability("${it.group}:${it.name}:${dep.version}")
            }
            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
                attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            }
            outgoing {
                artifact(transformationTask) {
                }
            }
        }

        inputConfiguration.configure {
            dependencies.add(dep)
        }

//        outputConfiguration.configure {
//
//        }
//        dependencies.add(outputConfiguration.name, transformationTask)
//        dependencies.add(outputConfiguration.name, dep.copy().apply {
//            capabilities {
//                requireCapability("io.github.cputnama11y.dep-transformer.spoofed.${dep.group}:${dep.version}")
//            }
//        })

    }

    fun getActualDependencies(): Set<Dependency> = setOf(
        dep.copy(),
//        dependencyFactory.create(objects.fileCollection().from(layout.projectDirectory.file("./gradle/wrapper/gradle-wrapper.jar")))
        objects.newInstance<CapabilityInfoCarrierDependency>(buildList {
            add(baseCapability)
            addAll(otherCaps)
        }.toTypedArray())

    )

    companion object {
        val nextID = AtomicLong(0)
    }
}