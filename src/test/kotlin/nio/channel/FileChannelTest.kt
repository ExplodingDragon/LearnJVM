package nio.channel

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer

/**
 * Java Nio File Channel 测试
 */
class FileChannelTest {
    @TempDir
    lateinit var temp: File

    private val file by lazy { File("$temp/input.txt") }

    @Test
    fun readFormFile() {
        file.writeBytes("test all".toByteArray(Charsets.ISO_8859_1))
        RandomAccessFile(file, "r").use {
            val channel = it.channel
            val buffer = ByteBuffer.allocate(2)
            while (channel.read(buffer) != -1) {
                buffer.flip()
                while (buffer.hasRemaining()) {
                    print(buffer.get().toInt().toChar())
                }
                buffer.compact()
//                buffer.clear()
            }
        }
        println()
    }
}
