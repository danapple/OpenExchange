package com.danapple.openexchange.api

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URL

/* Use DEFINED_PORT instead of RANDOM_PORT in order to run the server on 8080 so that the
  visualizer.py can connect during text execution. */
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
open class AbstractRestTest {
    @LocalServerPort
    protected var port: Int = 0

    protected var base: URL? = null

    @Autowired
    protected var template: TestRestTemplate? = null

    @BeforeEach
    @Throws(Exception::class)
    fun setUp() {
        this.base = URL("http://localhost:$port")
//        var deleted = false
//        do {
//            deleted = false
//            val response: ResponseEntity<APIResult> =
//                template.getForEntity()<APIResult>(base.toString() + "/widgets?limit=450", APIResult::class.java)
//            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode())
//            Assertions.assertNotNull(response.getBody())
//            for (returnedWidget in response.getBody().getWidgets()) {
//                val deleteResponse: ResponseEntity<Void> = delete(returnedWidget)
//                Assertions.assertEquals(HttpStatus.OK, deleteResponse.getStatusCode())
//                deleted = true
//            }
//        } while (deleted)
    }
//
//    protected fun createWidget(x: Int, y: Int, z: Int, width: Int, height: Int): APIWidget {
//        val inWidget: APIWidget = APIWidget(x, y, z, width, height)
//
//        val response: ResponseEntity<APIResult> =
//            template.postForEntity<APIResult>(base.toString() + "/widgets", inWidget, APIResult::class.java)
//        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode())
//        Assertions.assertNotNull(response.getBody())
//
//        val widgets: List<APIWidget> = response.getBody().getWidgets()
//        ExtraAssertions.assertSize(1, widgets)
//        val outWidget: APIWidget = response.getBody().getWidgets().get(0)
//        return outWidget
//    }
//
//    protected fun delete(@Nonnull apiWidget: APIWidget): ResponseEntity<Void> {
//        val link: APIWidget.Link = apiWidget.get_links().get(APIWidget.Action.delete)
//        Assertions.assertNotNull(link)
//
//        val headers = HttpHeaders()
//        headers.contentType = MediaType.APPLICATION_JSON
//        val entity: HttpEntity<APIWidget> = HttpEntity<APIWidget>(apiWidget, headers)
//
//        val response: ResponseEntity<Void> = template.exchange(
//            base.toString() + link.getPath(), link.getMethod(), entity,
//            Void::class.java
//        )
//        return response
//    }
}
