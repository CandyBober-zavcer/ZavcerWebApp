package ru.yarsu.web.handlers.spot

import org.http4k.core.*
import org.http4k.lens.Query
import org.http4k.lens.int
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.INPAGE
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.spot.SpotsVM
import ru.yarsu.web.templates.ContextAwareViewRender

class SpotsListHandler(
    private val htmlView: ContextAwareViewRender,
    private val databaseController: DatabaseController,
) : HttpHandler {
    private val pageLens = Query.int().required("page")
    private val haveDrumsLens = Query.int().optional("hasDrums") //BOOLEAN
    private val guitarAmpsLens = Query.int().optional("guitarAmps") //BOOLEAN
    private val bassAmpsLens = Query.int().optional("bassAmps") //BOOLEAN
    private val distsLens = Query.int().multi.optional("districts") //ENUM (LIST)
    private val minGoldLens = Query.int().optional("minGold") //INT
    private val maxGoldLens = Query.int().optional("maxGold") //INT
    private val nearestLens = Query.int().optional("ifNear") //BOOLEAN

    override fun invoke(request: Request): Response {
        val uri = request.uri
        val page = lensOrDefault(pageLens, request) {0}.takeIf { it > -1 } ?: 0
        val haveDrums = (lensOrNull(haveDrumsLens, request) ?: 0) == 1
        val guitarAmps = lensOrNull(guitarAmpsLens, request) ?: 0
        val bassAmps = lensOrNull(bassAmpsLens, request) ?: 0
        val districts = lensOrNull(distsLens, request)
        val minGold = lensOrNull(minGoldLens, request) ?: 0
        val maxGold = lensOrNull(maxGoldLens, request) ?: Int.MAX_VALUE
        val nearest = (lensOrNull(nearestLens, request) ?: 0) == 1

        val params = prepareParams(districts)
        val getResult = databaseController.getSpotsByPage(page, INPAGE, haveDrums, guitarAmps, bassAmps, params, minGold, maxGold, nearest)
        val paginator = Paginator(page, getResult.second, uri.removeQueries("page"))

        val viewModel = SpotsVM(getResult.first, DistrictEnums.entries)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}

fun prepareParams(
    districts: List<Int>?,
): List<Int> {
    var districtsParams = mutableListOf<Int>()

    districts?.forEach { districtsParams.add(it) }
    if (districtsParams.isEmpty())
        districtsParams = DistrictEnums.entries.map { it.id }.toMutableList()

    return districtsParams
}