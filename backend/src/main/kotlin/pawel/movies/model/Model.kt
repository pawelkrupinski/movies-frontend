package pawel.movies.model

import com.google.gson.*
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.id.WrappedObjectId
import org.litote.kmongo.newId

data class Movie(
    @BsonId val id: Id<Movie> = newId(),
    val title: String,
    val added: Long = 0,
    val path: String? = null,
    val year: Int? = null,
    val imdbId: String? = null,
    val poster: String? = null,
    val missing: Boolean = false,
    val edited: Boolean = false,
    val removed: Boolean = false,
    val customPoster: Boolean = false
)

data class Token(val token: String, val disabled: Boolean = false)

data class Poster(
        @BsonId val id: Id<Movie> = newId(),
        val type: String,
        val content: ByteArray
)

fun createGson(): Gson {
    val builder = GsonBuilder()

    builder.registerTypeAdapter(Id::class.java,
        JsonSerializer<Id<Any>> { id, _, _ -> JsonPrimitive(id?.toString()) })

    builder.registerTypeAdapter(Id::class.java,
        JsonDeserializer<Id<Any>> { id, _, _ -> WrappedObjectId(id.asString) })

    return builder.create()
}

