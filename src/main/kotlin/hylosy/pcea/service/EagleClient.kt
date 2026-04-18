package hylosy.pcea.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class EagleClient {
    private val baseUrl = "http://localhost:41595"
    private val client =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

    suspend fun exists(
        name: String,
        folderId: String,
    ): Boolean =
        try {
            val response: ItemListResponse =
                client
                    .get("$baseUrl/api/item/list?keyword=$name&folders[]=$folderId&limit=1")
                    .body()
            response.data.any { it.name == name }
        } catch (e: Exception) {
            false
        }

    suspend fun addFromURL(
        url: String,
        name: String,
        folderId: String,
    ): Result<Unit> =
        try {
            val response: HttpResponse =
                client.post("$baseUrl/api/item/addFromURL") {
                    contentType(ContentType.Application.Json)
                    setBody(AddFromURLRequest(url = url, name = name, folderId = folderId))
                }
            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Eagle API error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    @Serializable
    private data class ItemListResponse(
        val data: List<EagleItem>,
    )

    @Serializable
    private data class EagleItem(
        val name: String,
    )

    @Serializable
    private data class AddFromURLRequest(
        val url: String,
        val name: String,
        val folderId: String,
    )
}
