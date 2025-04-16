package tech.ecom.egts.configuration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import tech.ecom.egts.library.encoder.EgtsPacketEncoder

@SpringBootTest(
    classes = [EgtsAdapterConfiguration::class],
    properties = ["egts.initialize-encoders=true"],
)
class EgtsAdapterConfigurationTest {
    @Autowired
    lateinit var context: ApplicationContext

    @Test
    fun `should load application context`() {
        assertThat(context).isNotNull
    }

    @Test
    fun `should load EgtsPacketEncoder bean`() {
        assertThat(context.getBean(EgtsPacketEncoder::class.java)).isNotNull()
    }
}