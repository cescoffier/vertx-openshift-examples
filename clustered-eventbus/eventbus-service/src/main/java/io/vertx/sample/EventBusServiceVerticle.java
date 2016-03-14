package io.vertx.sample;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class EventBusServiceVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx.clusteredVertx(new VertxOptions(), result -> {
      System.out.println("Clustered vert.x " + result.result());
      Vertx vertx = result.result();
      vertx.deployVerticle(EventBusServiceVerticle.class.getName());
      System.out.println("Verticle deployed");
    });
  }


  @Override
  public void start() {
    String podName = System.getenv("HOSTNAME");

    vertx.createHttpServer().requestHandler(request -> request.response().end(podName + " should have registered the " +
        "eventbus service : " + dump())).listen(8080);
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
