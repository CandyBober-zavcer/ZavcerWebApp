package ru.yarsu.web.funs

open class EnumFinder<K, E : WithId<K>>(
    private val map: Map<K, E>,
) {
    operator fun get(key: K): E? = map[key]
}
