package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.enums.RoleEnums
import ru.yarsu.web.models.teacher.TeachersVM
import ru.yarsu.web.templates.ContextAwareViewRender
import org.http4k.lens.Query
import org.http4k.lens.WebForm
import org.http4k.lens.boolean
import org.http4k.lens.int
import ru.yarsu.web.domain.INPAGE
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.domain.enums.ExpEnums
import ru.yarsu.web.funs.*

class TeachersGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val databaseController: DatabaseController,
) : HttpHandler {
    private val pageLens = Query.int().required("page")
    private val expLens = Query.int().optional("experience") //ENUM
    private val absLens = Query.int().multi.optional("abilities") //ENUM (LIST)
    private val distsLens = Query.int().multi.optional("districts") //ENUM (LIST)
    private val minGoldLens = Query.int().optional("minGold") //INT
    private val maxGoldLens = Query.int().optional("maxGold") //INT
    private val nearestLens = Query.int().optional("ifNear") //BOOLEAN

    override fun invoke(request: Request): Response {
        val uri = request.uri
        val page = lensOrDefault(pageLens, request) {0}.takeIf { it > -1 } ?: 0
        val exp = lensOrNull(expLens, request) ?: 0
        val abilities = lensOrNull(absLens, request)
        val districts = lensOrNull(distsLens, request)
        val minGold = lensOrNull(minGoldLens, request) ?: 0
        val maxGold = lensOrNull(maxGoldLens, request) ?: Int.MAX_VALUE
        val nearest = (lensOrNull(nearestLens, request) ?: 0) == 1

        val params = prepareParams(abilities, districts)
        val getResult = databaseController.getTeachersByPage(page, INPAGE, params.first, params.second, minGold, maxGold, exp, nearest)

        val viewModel = TeachersVM(getResult.first, ExpEnums.entries, AbilityEnums.entries, DistrictEnums.entries.dropLast(1),
            TechersWebParams(exp, params, Pair(minGold, maxGold), nearest))
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}

data class TechersWebParams(
    val exp: Int,
    val enumsParams: Pair<List<Int>, List<Int>>,
    val goldRange: Pair<Int, Int>,
    val ifNear: Boolean
)

fun prepareParams(
    abilities: List<Int>?,
    districts: List<Int>?,
): Pair<List<Int>, List<Int>> {
    var abilitiesParams = mutableListOf<Int>()
    var districtsParams = mutableListOf<Int>()

    abilities?.forEach { abilitiesParams.add(it) }
    districts?.forEach { districtsParams.add(it) }

    if (abilitiesParams.isEmpty())
        abilitiesParams = AbilityEnums.entries.map { it.id }.toMutableList()
    if (districtsParams.isEmpty())
        districtsParams = DistrictEnums.entries.map { it.id }.toMutableList()

    return Pair(abilitiesParams, districtsParams)
}
