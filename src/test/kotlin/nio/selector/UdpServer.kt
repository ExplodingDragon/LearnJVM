package nio.selector

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector

class UdpServer {

    private fun start() {
        print("bind port:")
        val allocate = ByteBuffer.allocate(1500)
        val channel = DatagramChannel.open()
        channel.bind(InetSocketAddress((readLine() ?: "8081").toInt()))
        val open = Selector.open()
        channel.configureBlocking(false)
        channel.register(open, SelectionKey.OP_ACCEPT, allocate)

    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            UdpServer().start()
        }
    }
}
