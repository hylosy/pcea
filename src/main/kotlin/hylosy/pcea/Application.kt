package hylosy.pcea

import hylosy.pcea.dao.EventDao
import hylosy.pcea.dao.HoldingEventRecordDao
import hylosy.pcea.dao.HoldingEventResultDao
import hylosy.pcea.dao.ShopsDao
import hylosy.pcea.db.DatabaseManager
import hylosy.pcea.routing.configureEventRoutes
import hylosy.pcea.service.event.HoldingEventService
import hylosy.pcea.service.event.result.EventResultService
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.plugins.di.resolve

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain
        .main(args)

suspend fun Application.module() {
    dependencies {
        provide<HoldingEventRecordDao> { HoldingEventRecordDao() }
        provide<HoldingEventResultDao> { HoldingEventResultDao() }
        provide<ShopsDao> { ShopsDao() }
        provide<EventDao> { EventDao() }

        provide<EventResultService> {
            EventResultService(
                this.resolve<EventDao>(),
                this.resolve<HoldingEventRecordDao>(),
                this.resolve<HoldingEventResultDao>(),
                this.resolve<ShopsDao>(),
            )
        }
        provide<HoldingEventService> {
            HoldingEventService(
                this.resolve<HoldingEventRecordDao>(),
                this.resolve<HoldingEventResultDao>(),
            )
        }
    }

    configureCORS()
    configureRouting()
    configureSerialization()
    configureDatabase()
}

fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(io.ktor.http.HttpHeaders.ContentType)
        allowHeader(io.ktor.http.HttpHeaders.Authorization)
        allowHost("localhost:5173")
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

suspend fun Application.configureRouting() {
    configureEventRoutes()
}

fun Application.configureDatabase() {
    DatabaseManager.initialize(
        hylosy.pcea.config.ConfigLoader
            .loadDatabaseConfig(),
    )
}
