package pawel.movies.db

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import org.litote.kmongo.KMongo

class DB {
    val client = KMongo.createClient()

    fun createTest(): MongoDatabase = create(testDatabase())

    fun tearDownTest() {
        testDatabase().drop()
    }

    private fun testDatabase(): MongoDatabase {
        return client.getDatabase("test")
    }
    
    fun createProd(): MongoDatabase = create(prodDatabase())

    private fun prodDatabase(): MongoDatabase {
        val database = client.getDatabase("movies")
        return database
    }

    private fun create(database: MongoDatabase): MongoDatabase {
        val movies = database.getCollection("movies")
        movies.createIndex(Indexes.ascending("path"), IndexOptions().unique(true).sparse(true))
        movies.createIndex(Indexes.ascending("imdbId"), IndexOptions().unique(true).sparse(true))

        val tokens = database.getCollection("tokens")
        tokens.createIndex(Indexes.ascending("token"), IndexOptions().unique(true))

        return database
    }
}