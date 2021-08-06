package nio.channel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ChannelToChannelTransfersTest {
    @TempDir
    lateinit var temp: File

    private val fileIn by lazy { File("$temp/input.txt") }
    private val fileOut by lazy { File("$temp/output.txt") }

    @Test
    fun testForm() {
        fileIn.writeText("hello world")
        fileOut.delete()
        fileOut.createNewFile()
        val outputChannel = fileIn.inputStream().channel
        val inputChannel = fileIn.outputStream().channel
        inputChannel.transferFrom(outputChannel, 0, outputChannel.size())
        outputChannel.close()
        inputChannel.close()
        assertEquals(fileIn.readText(), fileOut.readText())
    }

    @Test
    fun testTo() {
        fileIn.writeText("hello world")
        fileOut.delete()
        fileOut.createNewFile()
        val outputChannel = fileIn.inputStream().channel
        val inputChannel = fileIn.outputStream().channel
        outputChannel.transferTo(0, outputChannel.size(), inputChannel)
        outputChannel.close()
        inputChannel.close()
        assertEquals(fileIn.readText(), fileOut.readText())
    }
}
