package ru.falseteam.control.server.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import ru.falseteam.control.server.domain.records.RecordsInteractor

class RecordsApiImpl(
    private val recordsInteractor: RecordsInteractor,
) : RecordsApi {
    override fun install(routing: Routing) = routing.apply {
        get("/api/v1/record_video/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val file = recordsInteractor.getRecord(id)
            if (file.exists()) {
                call.respondFile(file)
            } else call.respond(HttpStatusCode.NotFound)
        }
    }
}