# Java NIO 学习

> 采用 Junit 5 + Kotlin 1.5.21 

##  Channel

### 简单的 `Channel` 读取

```kotlin
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
```

### Channel#transferXXX

多 Channel 的克隆

```kotlin

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
```



## TCP Server



```kotlin

class SelectorTest {
    fun boot() {
        val selector = Selector.open()
        val channel = ServerSocketChannel.open()
        val buffer = ByteBuffer.allocate(1024)
        channel.configureBlocking(false)
        channel.bind(InetSocketAddress(8080))
        channel.register(selector, SelectionKey.OP_ACCEPT, buffer)
        while (true) {
            selector.select()
            val iterator = selector.selectedKeys().iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                iterator.remove()
                if (!next.isValid) {
                    continue
                }
                if (next.isAcceptable) {
                    val accept = (next.channel() as ServerSocketChannel).accept()
                    accept.configureBlocking(false)
                    accept.register(selector, SelectionKey.OP_READ, buffer)
                } else if (next.isReadable) {
                    val socketChannel = next.channel() as SocketChannel
                    val attachment = next.attachment() as ByteBuffer
                    attachment.clear()
                    val i = socketChannel.read(attachment)
                    attachment.flip()
                    println("${(socketChannel.remoteAddress as InetSocketAddress).address} message:" + String(attachment.array(), 0, i))
                    socketChannel.register(selector, SelectionKey.OP_WRITE, attachment)
                } else if (next.isWritable) {
                    val socketChannel = next.channel() as SocketChannel
                    val attachment = next.attachment() as ByteBuffer
                    val data = String(attachment.array(), 0, attachment.limit())
                    attachment.clear()
                    attachment.put("message:$data".toByteArray())
                    attachment.flip()
                    socketChannel.write(attachment)
                    socketChannel.register(selector, SelectionKey.OP_READ, attachment)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SelectorTest().boot()
        }
    }
}
```

