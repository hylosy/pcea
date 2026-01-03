package hylosy.pcea.script

import hylosy.pcea.config.ConfigLoader
import hylosy.pcea.db.DatabaseManager
import hylosy.pcea.di.ServiceModule
import hylosy.pcea.service.PokemonCardOfficialSiteClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File

fun main(args: Array<String>) {
    System.out.printf("Start to fetch deck images task\n")
    DatabaseManager.initialize(
        ConfigLoader.loadDatabaseConfig()
    )
    runBlocking {
        fetchDeckImages()
    }
}

suspend fun fetchDeckImages() {
    val taskConfig = ConfigLoader.loadTaskConfig()
    val holdingEventService = ServiceModule.holdingEventService
    val deckCodes = if (taskConfig.holdingEventIds.isEmpty()) {
        holdingEventService.getDeckCodes()
    } else {
        holdingEventService.getDeckCodesByHoldingEventIds(taskConfig.holdingEventIds)
    }
    val deckCodesInImage = fetchStoredImageNames(taskConfig.inputImagePath)
    deckCodes.filterNot { deckCodesInImage.contains(it) }.forEach { deckCode ->
        downloadImage(deckCode, "${taskConfig.outputImagePath}${deckCode}.png")
        println(deckCode)
        delay(1000)
    }
}

/**
 * @param deckCode
 * @param outputPath e.g: "/path/to/images/deck_${deckCode}.png"）
 */
suspend fun downloadImage(deckCode: String, outputPath: String): Result<Unit> {
    val officialSiteClient = PokemonCardOfficialSiteClient()
    return officialSiteClient.fetchImage(deckCode).fold(
        onSuccess = { imageBytes ->
            try {
                File(outputPath).apply {
                    parentFile?.mkdirs()
                    writeBytes(imageBytes)
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        },
        onFailure = { error ->
            Result.failure(error)
        }
    )
}

fun fetchStoredImageNames(inputImagePath: String): Set<String> {
    return try {
        File(inputImagePath).apply {
            require(exists() && isDirectory) { "Directory does not exist: $inputImagePath" }
        }.let { directory ->
            directory.listFiles()
                ?.filter { it.isFile && it.name.endsWith(".png") }
                ?.map { it.nameWithoutExtension }
                ?: emptyList()
        }.toSet()
    } catch (e: Exception) {
       throw e
    }
}
