package tech.claudioed.advertisement

import io.cloudevents.http.vertx.VertxCloudEvents
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.eventbus.DeliveryOptions
import tech.claudioed.advertisement.infra.CheckSum

class AdvertisementRequesterApp : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    vertx
      .createHttpServer()
      .requestHandler { req ->
        VertxCloudEvents.create().readFromRequest(req) {
          val cloudEvent = it.result()
          if (!cloudEvent.data.isEmpty) {
            val eventChecksum = CheckSum(cloudEvent.data.get()).checkSum()

            // Source should be the URI customer identifier
            val source = cloudEvent.attributes.source
            this.vertx.eventBus().publish("request-adv", cloudEvent.data.get(),
              DeliveryOptions().addHeader("event-checksum", eventChecksum)
                .addHeader("source",source.toString()))
            req.response().setStatusCode(201).end()
          } else {
            req.response().setStatusCode(400).end()
          }
        }
      }
      .listen(8888) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          vertx.deployVerticle(AdvertisementProcessor())
          println("HTTP server started on port 8888")
        } else {
          startPromise.fail(http.cause());
        }
      }
  }

}
