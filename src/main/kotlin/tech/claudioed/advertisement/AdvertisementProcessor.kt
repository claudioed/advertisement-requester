package tech.claudioed.advertisement

import io.cloudevents.kafka.CloudEventsKafkaProducer
import io.cloudevents.v1.AttributesImpl
import io.cloudevents.v1.CloudEventBuilder
import io.cloudevents.v1.kafka.Marshallers
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import tech.claudioed.advertisement.domain.AdvertisementRequest
import java.net.URI
import java.util.*


/**
 * @author claudioed on 07/03/20.
 * Project starter
 */
class AdvertisementProcessor: AbstractVerticle() {

  override fun start() {

    val props = Properties()
    props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = ByteArraySerializer::class.java

    val prod = CloudEventsKafkaProducer<String, AttributesImpl, String>(props,
      Marshallers.binary())

    this.vertx.eventBus().consumer<String>("request-adv") {

      val eventChecksum = it.headers()["event-checksum"]
      val source = it.headers()["source"]

      val data = Json.decodeValue(it.toString(), AdvertisementRequest::class.java)

      val ce = CloudEventBuilder.builder<String>()
        .withId(eventChecksum)
        .withSource(URI.create(source))
        .withDataContentType("application/json")
        .withType("adv.request")
        .withSubject("adv.request.${eventChecksum}")
        .withData(Json.encode(data)).build()

      // Sen
      prod.send(ProducerRecord("adv.request",data.id,ce))

    }

  }

}
