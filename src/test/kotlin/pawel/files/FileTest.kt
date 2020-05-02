package pawel.files

import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

abstract class FileTest {
    val path: Path = Paths.get("")
    val dirName = System.currentTimeMillis().toString()
    val testDir = path.toAbsolutePath().toString() + "/" + dirName

    var files: List<String> = emptyList()

    @Before
    fun createDir() {
        mkDir(testDir)
    }

    @After
    open fun tearDown() {
        files.reversed().forEach(::delete)
    }

    internal fun mkDir(dir: String) {
        Assert.assertThat(
            File(dir).mkdir(),
            CoreMatchers.`is`(true)
        )
        files += dir
    }

    internal fun createMovieFile() = createMovieFile(System.currentTimeMillis().toString())

    internal fun createMovieFile(name: String) = createFile("$name.mkv")

    internal fun createFile(file: String = System.currentTimeMillis().toString()): String {
        val path = "$testDir/$file"
        Assert.assertThat(
            File(path).createNewFile(),
            CoreMatchers.`is`(true)
        )
        files += path
        return path
    }

    internal fun delete(path: String) {
        Assert.assertThat(
            File(path).delete(),
            CoreMatchers.`is`(true)
        )

        files -= path
    }
}