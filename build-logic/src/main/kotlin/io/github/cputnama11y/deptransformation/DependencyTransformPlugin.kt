package io.github.cputnama11y.deptransformation

import io.github.cputnama11y.deptransformation.wiring.Capability
import io.github.cputnama11y.deptransformation.wiring.CapabilityInfoCarrierDependency
import io.github.cputnama11y.deptransformation.wiring.Transformations
import io.github.cputnama11y.deptransformation.wiring.VariantWithoutArtifactsRule
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies

class DependencyTransformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create<Transformations>("transformations")
        target.dependencies {
            components {
                all(VariantWithoutArtifactsRule::class.java)
            }
        }

        target.configurations.configureEach {
            resolutionStrategy {
                componentSelection {
                    this.all {  }
                }
                capabilitiesResolution {
                    all {
                        candidates.forEach {
                            val id = it.id;
                            val tags =
                                this@configureEach.allDependencies.filterIsInstance<CapabilityInfoCarrierDependency>()
                            if (tags.none {
                                    it.capabilities.any {
                                        Capability.from(capability) == it
                                    }
                                }) return@all

                            if (id is ProjectComponentIdentifier && id.projectName == target.name) {
                                select(it)
                            }
                        }
                    }
                }
                dependencySubstitution {
                    all {
                        val tags =
                            this@configureEach.allDependencies.filterIsInstance<CapabilityInfoCarrierDependency>()
                        if (tags.none {
                                requested.requestedCapabilities.all { cap ->
                                    it.capabilities.any {
                                        Capability.from(cap) == it
                                    }
                                } || (requested as? ModuleComponentSelector)?.run {
                                    it.capabilities.any {
                                        Capability(moduleIdentifier.group, moduleIdentifier.name) == it
                                    }
                                } ?: false
                            }) return@all
                        useTarget(variant(project(target.path)) {
                            this.capabilities {
                                requested.requestedCapabilities.forEach {
                                    this.requireCapability("${it.group}:${it.name}")
                                }
                                (requested as? ModuleComponentSelector)?.let {
                                    this.requireCapability("${it.moduleIdentifier.group}:${it.moduleIdentifier.name}")
                                }
                            }
                        })
                    }
                }
            }

        }
    }
}