package io.github.cputnama11y.deptransformation.wiring

import org.gradle.api.artifacts.dsl.Dependencies
import org.gradle.api.artifacts.dsl.DependencyCollector

interface TransformationDependencies : Dependencies {
    val transform: DependencyCollector
}