package ru.yarsu.web.handlers.upgrade

import org.http4k.core.*
import ru.yarsu.db.UserData
import ru.yarsu.web.models.upgrade.UpgradeOwnerListVM
import ru.yarsu.web.templates.ContextAwareViewRender

class UpgradeOwnerListGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val users: UserData,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val viewModel = UpgradeOwnerListVM(users.getPendingOwner())
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
