package io.github.cputnama11y.deptransformation.wiring

data class Capability(val group: String, val name: String) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other) || other is Capability && group == other.group && name == other.name
    }

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + name.hashCode()
        return result * 31 + super.hashCode()
    }
    companion object {
        fun from(other: org.gradle.api.capabilities.Capability): Capability {
            return Capability(other.group, other.name)
        }
    }
}
