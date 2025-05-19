package ru.yarsu.web.domain.enums

abstract class EnumFinder<V, E>(
    private val valueMap: Map<V, E>,
) {
    fun from(value: V) = valueMap[value]
}
