package io.vertx.sample;

import io.vertx.core.AbstractVerticle;

public class SenderVerticle extends AbstractVerticle {

  @Override
  public void start() {
    vertx.setPeriodic(1000, l -> {
      vertx.eventBus().publish("data", "Hello " + System.currentTimeMillis());
    });
  }
}
