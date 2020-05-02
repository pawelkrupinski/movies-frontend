package pawel.movies.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import org.litote.kmongo.toId

data class Movie(
    @BsonId val id: Id<Movie> = newId(),
    val path: String,
    val title: String,
    val year: Int? = null,
    val imdbId: String? = null,
    val poster: String? = null,
    val missing: Boolean = false,
    val edited: Boolean = false,
    val removed: Boolean = false
)

data class Token(val token: String, val disabled: Boolean = false)

fun createGson(): Gson {
    val builder = GsonBuilder()

    builder.registerTypeAdapter(Id::class.java,
        JsonSerializer<Id<Any>> { id, _, _ -> JsonPrimitive(id?.toString()) })

    builder.registerTypeAdapter(Id::class.java,
        JsonDeserializer<Id<Any>> { id, _, _ -> id.asString.toId() })

    return builder.create()
}

