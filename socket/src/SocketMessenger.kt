import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue


internal class MessageConsumer(private val queue: BlockingQueue<ByteArray>,
                               private val socket: Socket) : Runnable {
    override fun run() {
        try {
            System.out.println("ready to consume data...")
            while (true) {
                if (queue.isEmpty()) continue
                consume(queue.peek())
                queue.take()
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun consume(data: ByteArray) {
        System.out.println("consume data:${String(data)}")
        val outputStream = DataOutputStream(socket.getOutputStream())
        outputStream.write(data)
        outputStream.flush()
    }
}

class SocketMessenger(private val host: String, private val port: Int) {
    private val queue = ArrayBlockingQueue<ByteArray>(100)
    private var socket: Socket? = null
    private var consumerThread: Thread? = null

    fun open() {
        System.out.println("open")
        try {
            socket = Socket(host, port)
            socket?.let {
                val outputStream = DataOutputStream(it.getOutputStream())
                outputStream.flush()
                outputStream.write("open".toByteArray())
                outputStream.flush()
                consumerThread = Thread(MessageConsumer(queue, it))
                consumerThread?.start()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
    }

    fun send(data: ByteArray) {
        System.out.println("send, data:${String(data)}")
        queue.put(data)
    }

    fun close() {
        System.out.println("close start")
        while (!queue.isEmpty()) {
        }
        clear()
        System.out.println("close end")
    }

    private fun clear() {
        System.out.println("clear")
        queue.clear()
        socket?.getOutputStream()?.close()
        socket?.close()
        consumerThread?.interrupt()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val messenger = SocketMessenger("localhost", 1988)
            messenger.open()
            messenger.send("test ".toByteArray())
            messenger.send("yul2ya ".toByteArray())
            messenger.send("quit ".toByteArray())
            messenger.close()
        }
    }

}