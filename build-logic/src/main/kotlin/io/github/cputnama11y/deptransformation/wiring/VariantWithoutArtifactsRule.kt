package io.github.cputnama11y.deptransformation.wiring

import org.gradle.api.artifacts.*
import org.gradle.api.capabilities.Capability
import java.util.*

class VariantWithoutArtifactsRule : ComponentMetadataRule {
    override fun execute(context: ComponentMetadataContext) {
        mutableListOf<VariantMetadata>().let {
            context.details.allVariants(it::add)
            it.forEach { variant ->
                context.details.addVariant(UUID.randomUUID().toString()) {
                    mutableListOf<Capability>().let {
                        variant.withCapabilities {
                            it.addAll(capabilities)
                        }
                        withCapabilities {
                            it.forEach {
                                addCapability(
                                    "io.github.cputnama11y.dep-transformer.spoofed." + it.group,
                                    it.name,
                                    it.version
                                )
                            }
                        }
                    }

                    mutableListOf<DirectDependencyMetadata>().let {
                        variant.withDependencies(it::addAll)
                        withDependencies {
                            it.forEach {
                                add(it)
                            }
                        }
                    }

                    mutableListOf<DependencyConstraintMetadata>().let {
                        variant.withDependencyConstraints(it::addAll)
                        withDependencyConstraints {
                            it.forEach {
                                this.add(it)
                            }
                        }
                    }
                }
            }
        }
    }
}