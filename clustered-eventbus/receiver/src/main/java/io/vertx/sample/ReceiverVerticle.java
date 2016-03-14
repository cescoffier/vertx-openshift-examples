package io.vertx.sample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.concurrent.atomic.AtomicReference;

public class ReceiverVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx.clusteredVertx(new VertxOptions(), result -> {
      System.out.println("Clustered vert.x " + result.result());
      Vertx vertx = result.result();
      vertx.deployVerticle(ReceiverVerticle.class.getName());
      System.out.println("Verticle deployed");
    });
  }

  @Override
  public void start() {
    String podName = System.getenv("HOSTNAME");

    AtomicReference<String> last = new AtomicReference<>();
    vertx.eventBus().consumer("data", message -> {
      System.out.println("Receiving " + message.body());
      last.set((String) message.body());
    });

    vertx.createHttpServer().requestHandler(request -> request.response().end(podName + " is receiving data from the " +
        "event bus : "
        + last.get())).listen(8080);
  }
}
