package ru.yarsu.web.domain

import org.http4k.core.Uri
import org.http4k.core.query

const val RANGE = 3

class Paginator(
    private var viewPage: Int,
    private var allPages: Int,
    private val uri: Uri,
) {
    private val nearStart = viewPage - RANGE - 2 < 0
    private val nearEnd = viewPage + RANGE + 3 > allPages

    fun getPrevPageLinks(): Map<Int, Uri> {
        val prevPages: MutableMap<Int, Uri> = mutableMapOf()
        val start = if (!nearStart) viewPage - RANGE else 0
        for (i in start until viewPage) {
            prevPages[i] = uri.query("page", i.toString())
        }
        return prevPages
    }

    fun getNextPageLinks(): Map<Int, Uri> {
        val nextPages: MutableMap<Int, Uri> = mutableMapOf()
        val end = if (!nearEnd) viewPage + RANGE + 1 else allPages
        for (i in viewPage + 1 until end) {
            nextPages[i] = uri.query("page", i.toString())
        }
        return nextPages
    }

    fun getStartPage(): Int = viewPage

    fun getAllPages(): Int = allPages

    fun getPageUri(pageNumber: Int = 0): Uri {
        val page = if (pageNumber == allPages) (pageNumber - 1) else pageNumber
        return uri.query("page", page.toString())
    }

    fun isNearStart(): Boolean = nearStart

    fun isNearEnd(): Boolean = nearEnd
}
