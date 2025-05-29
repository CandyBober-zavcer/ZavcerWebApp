package ru.yarsu.web.lenses

import org.http4k.lens.Query
import org.http4k.lens.int

val pageLens = Query.int().required("page")
