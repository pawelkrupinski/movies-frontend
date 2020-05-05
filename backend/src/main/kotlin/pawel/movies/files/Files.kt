package pawel.movies.files

import pawel.movies.db.DB
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.toList

interface ScanDirectory {
    fun scan(): List<Path>;
}

val Vuze_Directory = "/Users/pawel/Documents/Vuze Downloads"

class ScanForMovies(val directory: String = Vuze_Directory) : ScanDirectory {
    private val movieExtensions = setOf("mkv", "avi", "mp4")

    private fun isMovieFile(path: Path) = movieExtensions.contains(path.toFile().extension)

    override fun scan(): List<Path> {
        try {
            val files = Files.walk(Paths.get(directory))
                .filter { path -> Files.isRegularFile(path) }.toList()

            return files.filter(::isMovieFile)
        } catch (e: Exception) {
            return emptyList()
        }
    }
}