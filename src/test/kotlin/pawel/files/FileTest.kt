package pawel.files

import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

abstract class FileTest {
    companion object {
        val path: Path = Paths.get("")
        val dirName = System.currentTimeMillis().toString()
        val testDir = path.toAbsolutePath().toString() + "/" + dirName

        var files: List<String> = emptyList()
        
        internal fun mkDir(dir: String) {
            val file = File(dir)
            val existsAlready = file.exists()

            file.mkdir()
            
            if (!existsAlready) {
                files += dir
            }
        }
        
        @BeforeClass
        @JvmStatic
        fun createDirStatic() {
            mkDir(testDir)
        }
    }

    @Before
    fun createDir() {
        mkDir(testDir)
    }

    @After
    open fun tearDown() {
        files.reversed().forEach(::delete)
        files = emptyList()
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