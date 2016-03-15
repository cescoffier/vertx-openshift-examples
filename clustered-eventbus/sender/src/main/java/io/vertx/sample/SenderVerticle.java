package io.vertx.sample;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.dns.DnsClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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

    System.out.println(dump());
  }

  private String dump() {
    String dump = "\n";
    String accountToken = getAccountToken();
    System.out.println("Kubernetes Discovery: Bearer Token { " + accountToken + " }");
    Config config = new ConfigBuilder().withOauthToken(accountToken).build();
    KubernetesClient client = new DefaultKubernetesClient(config);
    ServiceList list = client.services().inNamespace("vertx-demo-cluster").list();
    for (Service svc : list.getItems()) {
      dump += svc.getMetadata().getNamespace() + " / " + svc.getMetadata().getName() + " (" + svc.getMetadata()
          .getLabels() + ") \n";
      dump += "\t" + svc.getAdditionalProperties() + "\n";
      dump += "\t" + svc.getStatus() + "\n";
      dump += "\t" + svc.getSpec() + "\n";
    }
    dump += "------ \n";
    for (Map.Entry<String, String> env : System.getenv().entrySet()) {
      dump += "\t" + env.getKey() + " = " + env.getValue() + "\n";
    }

    dump += "------ \n";

    return dump;

  }

  private String getAccountToken() {
    try {
      String tokenFile = "/var/run/secrets/kubernetes.io/serviceaccount/token";
      File file = new File(tokenFile);
      byte[] data = new byte[(int) file.length()];
      InputStream is = new FileInputStream(file);
      is.read(data);
      return new String(data);

    } catch (IOException e) {
      throw new RuntimeException("Could not get token file", e);
    }
  }
}
