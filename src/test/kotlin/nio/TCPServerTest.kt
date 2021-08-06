package nio

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class TCPServerTest {
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
                    println("${((next.channel() as SocketChannel).remoteAddress as InetSocketAddress).address}}连接断开")
                    continue
                } else if (next.isAcceptable) {
                    val accept = (next.channel() as ServerSocketChannel).accept()
                    accept.configureBlocking(false)
                    accept.register(selector, SelectionKey.OP_READ, buffer)
                } else if (next.isReadable) {
                    val socketChannel = next.channel() as SocketChannel
                    val attachment = next.attachment() as ByteBuffer
                    attachment.clear()
                    val i = socketChannel.read(attachment)
                    attachment.flip()
                    println(
                        "${(socketChannel.remoteAddress as InetSocketAddress).address} message:" + String(
                            attachment.array(),
                            0,
                            i
                        )
                    )
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
            TCPServerTest().boot()
        }
    }
}
