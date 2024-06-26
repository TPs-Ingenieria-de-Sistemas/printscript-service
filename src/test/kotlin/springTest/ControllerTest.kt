package springTest

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureWebTestClient
class ControllerTest {

    private val BASE = "/command"


    /*fun `should return linted file`() {
        val uri = mintUri("commands", "basic-command.ps")
        createCommand(uri, BASIC_COMMAND)
        val response = client.get().uri(uri).exchange()
        response.expectStatus().isOk
        assert(response.hasBody(BASIC_COMMAND))
    }*/
}
