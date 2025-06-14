package ru.yarsu.web.funs

import org.http4k.lens.Lens
import org.http4k.lens.LensFailure
import org.http4k.lens.MultipartForm
import org.http4k.lens.MultipartFormField
import ru.yarsu.web.domain.enums.AbilityEnums

fun <IN : Any, OUT> lensOrNull(
    lens: Lens<IN, OUT?>,
    value: IN,
): OUT? =
    try {
        lens.invoke(value)
    } catch (_: LensFailure) {
        null
    }

fun <IN : Any, OUT> lensOrDefault(
    lens: Lens<IN, OUT>,
    value: IN,
    default: () -> OUT,
): OUT =
    try {
        lens.invoke(value)
    } catch (_: LensFailure) {
        default()
    }

fun lensOrDefaultAbilities(
    lens: Lens<MultipartForm, List<MultipartFormField>>,
    value: MultipartForm,
    default: () -> MutableSet<AbilityEnums>,
): MutableSet<AbilityEnums> =
    try {
        val rawValues = lens.invoke(value)
        if (rawValues.isEmpty()) {
            default()
        } else {
            rawValues.mapNotNull { field -> AbilityEnums.entries.find { it.name == field.value } }
                .toMutableSet()
        }
    } catch (_: LensFailure) {
        default()
    }
