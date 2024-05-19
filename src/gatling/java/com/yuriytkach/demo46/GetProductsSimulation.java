package com.yuriytkach.demo46;

// required for Gatling core structure DSL

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.CoreDsl.stressPeakUsers;
import static io.gatling.javaapi.core.OpenInjectionStep.nothingFor;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class GetProductsSimulation extends Simulation {

  final SecureRandom rnd = new SecureRandom();

  // Random product id feeder. Not used in the simulation
  Iterator<Map<String, Object>> feeder =
    Stream.generate((Supplier<Map<String, Object>>) () -> Map.of("productId", rnd.nextInt(10000))).iterator();

  ScenarioBuilder scn = scenario("Get Products")
    .exec(
      http("GET /products")
        .get("/products/price?minPrice=#{randomInt(5,50)}&size=#{randomInt(20,50)}")
        .check(
          status().is(200),
          jsonPath("$[*].id").findRandom().saveAs("productId")
        )
    )
    .exec(session -> {
      // simple logging of random product id that we've read on the previous step
      System.out.println(">>>>>>>>>>>>>> productId: " + session.get("productId"));
      return session;
    })
    .pause(Duration.ofMillis(300))

    // List feeder to get predefined product ids
//    .feed(listFeeder(List.of(
//      Map.of("productId", 1),
//      Map.of("productId", 10),
//      Map.of("productId", 100),
//      Map.of("productId", 1000),
//      Map.of("productId", 10000)
//    )).random())

    .exec(
      http("GET /products/{id}")
        .get("/products/#{productId}")
        .check(status().is(200))
        .check(jsonPath("$.id").isEL("#{productId}"))
        .check(jsonPath("$").ofMap().saveAs("product"))
    )
    .exec(session -> {
      // simple logging of product data that we've read on the previous step
      System.out.println(">>>>>>>>>>>>>> product: " + session.get("product"));

      // Modify the stock value of the product in the session
      final Map<String, Object> productData = session.getMap("product");
      final int updatedStock = (int) productData.get("stock") - 1;
      productData.put("stock", updatedStock);
      return session;
    })
    .exec(
      http("PUT /products/{id}")
        .put("/products/#{productId}")
        .body(StringBody(session -> {
          // Serialize updated product data to JSON
          Map<String, Object> updatedProductData = session.getMap("product");
          try {
            return new ObjectMapper().writeValueAsString(updatedProductData);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        }))
        .asJson()
        .check(status().within(200, 201, 204))
    );

  {
    setUp(
      scn.injectOpen(
        nothingFor(Duration.ofSeconds(2)), // 1
        atOnceUsers(100), // 2
        rampUsers(100).during(5), // 3
        constantUsersPerSec(300).during(80), // 4
        constantUsersPerSec(300).during(10).randomized(), // 5
        stressPeakUsers(1000).during(15) // 8
      ).protocols(httpProtocol())

// Uncomment the lines below to run the simulation with a single user
//      scn.injectOpen(
//        atOnceUsers(1)
//      ).protocols(httpProtocol())
    );
  }

  HttpProtocolBuilder httpProtocol() {
    return http
      .baseUrl("http://localhost:8080")
      .acceptHeader("application/json")
      .contentTypeHeader("application/json")
      .header("x-client-lib", "Gatling Tests");
  }
}
