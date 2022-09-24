package org.kenis

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


open class MangaUpDates(mangaName: String) {

    private val client = OkHttpClient()
    private val BASE_URL = "https://api.mangaupdates.com/v1/series/"
    private val json = Json
    private val manga = mangaDetailRequest(mangaIdRequest(mangaName))

    private val description = manga.jsonObject["description"]!!.jsonPrimitive.content
    private val title = manga.jsonObject["title"]!!.jsonPrimitive.content
    private val alternativeName =
        manga.jsonObject["associated"]!!.jsonArray.joinToString(", ") { it.jsonObject["title"]!!.jsonPrimitive.content }
    private val genres =
        manga.jsonObject["genres"]!!.jsonArray.joinToString(", ") { it.jsonObject["genre"]!!.jsonPrimitive.content }
    private val categories = manga.jsonObject["categories"]!!.jsonArray
    private val publishers = manga.jsonObject["publishers"]!!.jsonArray
    private val authors = manga.jsonObject["authors"]!!.jsonArray
    private val thumbnailUrl =
        manga.jsonObject["image"]!!.jsonObject["url"]!!.jsonObject["original"]!!.jsonPrimitive.content
    private val type = manga.jsonObject["type"]!!.jsonPrimitive.content
    private val latestChapter = manga.jsonObject["latest_chapter"]!!.jsonPrimitive.content
    private val status = manga.jsonObject["status"]!!.jsonPrimitive.content
    private val completed = manga.jsonObject["completed"]!!.jsonPrimitive.boolean
    private val year = manga.jsonObject["year"]!!.jsonPrimitive.content


    //getter
    open fun getTitle(): String {
        return title
    }

    open fun getDescription(): String {
        return descriptionParser(description)
    }

    open fun getType(): String {
        return type
    }

    open fun getIsCompleted(): Boolean {
        return completed
    }

    open fun getCategory(): String {
        return categoryParser(categories)
    }

    open fun getAuthor(): String {
        return authorsParser(authors)
    }

    open fun getArtist(): String {
        return artistParser(authors)
    }

    open fun getAlternativeName(): String {
        return alternativeName
    }

    open fun getStatus(isForTachiyomi: Boolean = true): Any {
        return if (isForTachiyomi) {

            statusParser(status, true).toInt()
        } else {
            statusParser(status, false)
        }
    }

    open fun getGenres(isMixedWithCategoryAndType: Boolean = false): String {
        return if (isMixedWithCategoryAndType) {
            listOfNotNull(genres, getCategory(), type).joinToString(", ")
        } else {
            genres
        }
    }

    open fun getYears(): String {
        return year
    }

    open fun getThumbnailUrl(): String {
        return thumbnailUrl
    }

    open fun getPublishers(): String {
        return publishersParser(publishers)
    }

    open fun getLastChapter(): String {
        return latestChapter
    }

    private fun mangaIdRequest(name: String): String {
        val search = JsonObject(
            mapOf(
                "search" to JsonPrimitive(name)
            )
        )

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = search.toString().toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(BASE_URL + "search")
            .post(body)
            .build()
        val responseBody = client.newCall(request).execute().body
        val data =
            json.decodeFromString<JsonObject>(responseBody!!.string())["results"]!!.jsonArray[0].jsonObject["record"]!!
        return data.jsonObject["series_id"].toString()
    }

    private fun mangaDetailRequest(mangaId: String): JsonObject {
        val request = Request.Builder()
            .url(BASE_URL + mangaId)
            .build()
        val responseBody = client.newCall(request).execute().body
        return json.decodeFromString<JsonObject>(responseBody!!.string()).jsonObject
    }

    private fun descriptionParser(description: String): String {
        val stringList = description.split("<")
        return stringList[0]
    }

    private fun categoryParser(category: JsonArray): String {
        var totalVote = 0
        for (i in 0 until category.size) {
            val vote = category[i].jsonObject["votes"].toString().toInt()
            totalVote += vote
        }
        val averageVote = (totalVote / category.size).toInt()
        val parsedCategory = buildJsonArray {
            for (i in 0 until category.size) {
                val j = category[i].jsonObject["votes"].toString().toInt()
                if (j > averageVote) {
                    category[i].jsonObject["category"]?.let { add(it) }
                }
            }
        }
        return parsedCategory.joinToString(", ") { it.jsonPrimitive.content }
    }

    private fun authorsParser(authors: JsonArray): String {
        return buildJsonArray {
            for (i in 0 until authors.size) {
                val type = authors[i].jsonObject["type"]!!.jsonPrimitive.content
                if (type == "Author") {
                    authors[i].jsonObject["name"]?.let { add(it) }
                }
            }
        }.joinToString(", ") { it.jsonPrimitive.content }

    }

    private fun publishersParser(publishers: JsonArray): String {
        return buildJsonArray {
            for (i in 0 until publishers.size) {
                val type = publishers[i].jsonObject["type"]!!.jsonPrimitive.content
                if (type == "Original") {
                    publishers[i].jsonObject["publisher_name"]?.let { add(it) }
                }
            }
        }.joinToString(", ") { it.jsonPrimitive.content }

    }

    private fun artistParser(authors: JsonArray): String {
        return buildJsonArray {
            for (i in 0 until authors.size) {
                val type = authors[i].jsonObject["type"]!!.jsonPrimitive.content
                if (type == "Artist") {
                    authors[i].jsonObject["name"]?.let { add(it) }
                }
            }
        }.joinToString(", ") { it.jsonPrimitive.content }

    }

    private fun statusParser(status: String, isForTachiyomi: Boolean = true): String {
        return if (isForTachiyomi) {
            when {
                completed -> "2"
                status.contains("Ongoing") -> "1"
                status.contains("Complete") -> "2"
                status.contains("Hiatus") -> "6"
                else -> "0"
            }
        } else {
            when {
                completed -> "Completed"
                status.contains("Ongoing") -> "Ongoing"
                status.contains("Complete") -> "Completed"
                status.contains("Hiatus") -> "Hiatus"
                else -> "UNKNOWN"
            }
        }

    }

}