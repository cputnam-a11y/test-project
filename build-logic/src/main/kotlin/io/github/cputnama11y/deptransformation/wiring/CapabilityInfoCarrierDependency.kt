package io.github.cputnama11y.deptransformation.wiring

import org.gradle.api.artifacts.FileCollectionDependency
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.artifacts.dependencies.SelfResolvingDependencyInternal
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class CapabilityInfoCarrierDependency @Inject constructor(vararg val capabilities: Capability) :
    FileCollectionDependency, SelfResolvingDependencyInternal {
    @get:Inject
    protected abstract val objects: ObjectFactory

    override fun getGroup(): String? = null

    override fun getTargetComponentId(): ComponentIdentifier? = null

    override fun getName(): String = "CapabilityInfoCarrierDependency"

    override fun getVersion(): String? = null

    override fun copy(): CapabilityInfoCarrierDependency =
        objects.newInstance(CapabilityInfoCarrierDependency::class.java)

    override fun getReason(): String? = null

    override fun because(reason: String?) = Unit

    override fun getFiles(): FileCollection {
        return objects.fileCollection()
    }
}