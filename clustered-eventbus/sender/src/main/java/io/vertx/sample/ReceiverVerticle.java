package io.vertx.sample;

import io.vertx.core.AbstractVerticle;

public class ReceiverVerticle extends AbstractVerticle {

  @Override
  public void start() {
    vertx.setPeriodic(1000, l -> {
      vertx.eventBus().publish("data", "Hello " + System.currentTimeMillis());
    });

    vertx.createHttpServer().requestHandler(request -> request.response().end("sending data on the event bus"))
        .listen(8080);
  }
}
