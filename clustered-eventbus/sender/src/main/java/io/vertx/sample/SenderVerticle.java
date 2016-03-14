package io.vertx.sample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class SenderVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx.clusteredVertx(new VertxOptions(), result -> {
      System.out.println("Clustered vert.x " + result.result());
      Vertx vertx = result.result();
      vertx.deployVerticle(SenderVerticle.class.getName());
      System.out.println("Verticle deployed");
    });
  }


  @Override
  public void start() {
    String podName = System.getenv("HOSTNAME");

    vertx.setPeriodic(5000, l -> {
      String message = "Hello " + System.currentTimeMillis();
      System.out.println("Sending " + message);
      vertx.eventBus().publish("data", message);
    });

    vertx.createHttpServer().requestHandler(request -> request.response().end(podName + " is sending data on the " +
        "event bus")).listen(8080);
  }
}
