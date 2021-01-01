package ru.falseteam.control.di

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import org.kodein.di.*
import ru.falseteam.control.api.CamsApi
import ru.falseteam.control.api.CamsApiImpl
import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.control.domain.cams.CamsInteractor
import ru.falseteam.control.domain.cams.CamsInteractorImpl
import ru.falseteam.rsub.client.RSubClient
import ru.falseteam.rsub.connector.ktorwebsocket.client.RSubConnectorKtorWebSocket

val Kodein = DI.lazy {
    // Domain
    bind<CamsInteractor>() with singleton { CamsInteractorImpl(instance()) }

    // Api
    bind<CamsApi>() with singleton { CamsApiImpl(instance()) }

    // rSub
    bind<HttpClient>() with singleton {
        HttpClient {
            install(WebSockets)
        }
    }
    bind<RSubClient>() with singleton {
        RSubClient(
            RSubConnectorKtorWebSocket(
                instance(),
                host = "10.0.0.56"
            )
        )
    }
    bind<CamsRSub>() with singleton { instance<RSubClient>().getProxy(CamsRSub::class) }
}
