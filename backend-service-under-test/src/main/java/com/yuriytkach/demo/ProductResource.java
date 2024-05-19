package com.yuriytkach.demo;

import java.util.List;
import java.util.Map;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;
import io.quarkus.panache.common.Sort;
import io.quarkus.rest.data.panache.ResourceProperties;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@ResourceProperties(path = "products")
public interface ProductResource extends PanacheEntityResource<Product, Long> {













  @GET
  @Path("/price")
  @Produces("application/json")
  default List<Product> findByPrice(
    @QueryParam("minPrice") double price,
    @QueryParam("sort") List<String> sortQuery,
    @QueryParam("page") @DefaultValue("0") int pageIndex,
    @QueryParam("size") @DefaultValue("20") int pageSize
  ) {
    final Sort sort = Sort.ascending(sortQuery.toArray(new String[0]));
    return Product.find("price > :price", sort, Map.of("price", price))
      .range(pageIndex, pageSize)
      .list();
  }

}
