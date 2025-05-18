package ru.yarsu.web.funs

abstract class EnumFinder<V, E>(private val valueMap: Map<V, E>) {
    fun from(value: V) = valueMap[value]
}
