package ru.yarsu.web.handlers.upgrade

import org.http4k.core.*
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.enums.RoleEnums
import ru.yarsu.web.models.upgrade.UpgradeOwnerListVM
import ru.yarsu.web.templates.ContextAwareViewRender

class UpgradeOwnerListGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val databaseController: DatabaseController,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val viewModel = UpgradeOwnerListVM(databaseController.getPendingOwners())
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
