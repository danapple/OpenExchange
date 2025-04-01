package com.danapple.openexchange.api

import com.danapple.openexchange.ExchangeTest
import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.dao.OrderQueryDao
import com.danapple.openexchange.orders.OrderFactory
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractRestTest(orderDao : OrderDao,
                                orderQueryDao: OrderQueryDao,
                                customerDao: CustomerDao,
                                instrumentDao: InstrumentDao,
                                orderFactory: OrderFactory) :
    ExchangeTest(orderDao, orderQueryDao, customerDao, instrumentDao, orderFactory)
{
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

        // Create user for test
        // customerKey = customerDao.createUser(this.javaClass.simpleName)

        // Create instrument for test
        // instrumentId = instrumentDao.createInstrument()
    }
}
