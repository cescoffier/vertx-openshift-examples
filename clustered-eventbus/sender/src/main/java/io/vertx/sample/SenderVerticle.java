package io.vertx.sample;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
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
        "event bus " + dump())).listen(8080);
  }

  private String dump() {
    String dump = "\n";
    OpenShiftClient client = new DefaultOpenShiftClient();
    ServiceList list = client.services().inNamespace("vertx-demo-cluster").list();
    for (Service svc : list.getItems()) {
      dump += svc.getMetadata().getNamespace() + " / " + svc.getMetadata().getName() + " (" + svc.getMetadata()
          .getGenerateName() + ") \n";
    }
    return dump;

  }
}
