package nio.channel.file

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileChannelTest {
    companion object {
        @TempDir
        lateinit var temp: File

        private val file by lazy { File("$temp/input.txt") }

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            file.writeBytes("test all".toByteArray(Charsets.ISO_8859_1))
        }
    }

    @Test
    fun readFormFile() {
        RandomAccessFile(file, "r").use {
            val channel = it.channel
            val buffer = ByteBuffer.allocate(1024)
            if (channel.read(buffer) != -1) {
                buffer.flip()
                while (buffer.hasRemaining()) {
                    print(buffer.get().toInt().toChar())
                }
            }
            buffer.clear()
        }
    }
}
