package io.vertx.sample;

import io.vertx.core.AbstractVerticle;

public class ReceiverVerticle extends AbstractVerticle {

  @Override
  public void start() {
    vertx.eventBus().consumer("data", message -> {
       System.out.println("Receiving " + message.body());
    });
  }
}
