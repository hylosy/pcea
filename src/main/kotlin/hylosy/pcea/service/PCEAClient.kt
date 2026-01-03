package hylosy.pcea.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*

class PCEAClient {
    private val mHttpClient: HttpClient

    constructor() {
        mHttpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = false })
            }
        }
    }
    fun close() {
        mHttpClient.close()
    }

    suspend fun get(url: String): HttpResponse {
        return mHttpClient.get(url)
    }
}
