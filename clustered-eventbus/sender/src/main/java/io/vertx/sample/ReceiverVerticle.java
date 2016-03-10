package io.vertx.sample;

import io.vertx.core.AbstractVerticle;

import java.util.concurrent.atomic.AtomicReference;

public class ReceiverVerticle extends AbstractVerticle {

  @Override
  public void start() {
    AtomicReference<String> last = new AtomicReference<>();
    vertx.eventBus().consumer("data", message -> {
      System.out.println("Receiving " + message.body());
      last.set((String) message.body());
    });

    vertx.createHttpServer().requestHandler(request -> request.response().end("receiving data from the event bus : "
        + last.get())).listen(8080);
  }
}
