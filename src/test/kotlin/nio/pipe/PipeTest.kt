package nio.pipe

import org.junit.jupiter.api.Test
import java.nio.channels.Pipe

class PipeTest {
    @Test
    fun test() {
        val open = Pipe.open()
    }
}
