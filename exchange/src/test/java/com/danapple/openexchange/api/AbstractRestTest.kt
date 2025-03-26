package com.danapple.openexchange.api

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
open class AbstractRestTest {
    protected var customerKey = "noKey"

    @Autowired
    protected var template: TestRestTemplate? = null

    @BeforeEach
    @Throws(Exception::class)
    fun setUp() {

        val interceptor =
            ClientHttpRequestInterceptor { httpRequest: HttpRequest, bytes: ByteArray, clientHttpRequestExecution: ClientHttpRequestExecution ->
                httpRequest.headers.add("Cookie", "customerKey=$customerKey")
                print("Added headers ${httpRequest.headers}")
                clientHttpRequestExecution.execute(httpRequest, bytes)
            }

        template?.restTemplate?.interceptors = arrayListOf(interceptor)
    }
}
