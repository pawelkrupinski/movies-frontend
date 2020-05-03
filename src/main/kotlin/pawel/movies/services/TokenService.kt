package pawel.movies.services

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import pawel.movies.model.Movie
import pawel.movies.model.Token

interface TokenService {
    fun tokens(): Set<String>
    fun addTokens(tokens: List<String>)
    fun createTokensForEditing(movie: Movie, newTitle: String)
    fun delete(params: String)
}

class DbTokenService(database: MongoDatabase) : TokenService {
    
    private val collection = database.getCollection<Token>("tokens")
    private val movieClean = MovieClean()

    override fun tokens() = collection.find(Token::disabled eq false).map { it.token }.toSet()

    override fun addTokens(tokens: List<String>) {
        if (tokens.isNotEmpty()) {
            collection.insertMany(tokens.map { token -> Token(token) })
        }
    }

    override fun createTokensForEditing(movie: Movie, newTitle: String) {
        val cleanTitle = movieClean.clean(movie, tokens()).title
        if (cleanTitle.contains(newTitle)) {
            val tokens = cleanTitle
                .replace(newTitle, "")
                .trim()
                .split(Regex(" +"))
                .filterNot { it.matches(Regex("\\d+")) }
                .filterNot { it == "" }
            addTokens(tokens)
        }
    }

    override fun delete(token: String) {
        collection.updateOne(
            Token::token eq token,
            set(Token::disabled setTo true)
        )
    }
}