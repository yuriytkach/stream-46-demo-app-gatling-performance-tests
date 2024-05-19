package com.yuriytkach.demo;

import java.math.BigDecimal;
import java.util.Locale;

import com.github.javafaker.Faker;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DataGenerator {

  private final Faker faker = new Faker(Locale.US);

  @Inject
  DataGeneratorConfig config;

  @Transactional
  public void onStart(@Observes final StartupEvent ev) {
    for (int i = 0; i < config.maxEntities(); i++) {
      Product product = new Product();
      product.name = faker.commerce().productName();
      product.description = faker.lorem().sentence();
      product.price = new BigDecimal(faker.commerce().price().replace(",", ""));
      product.stock = faker.number().numberBetween(1, 100);
      product.createdDate = faker.date().past(365 * 2, java.util.concurrent.TimeUnit.DAYS).toInstant();
      product.persist();
    }
  }

}
