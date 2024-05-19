package com.yuriytkach.demo;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "data-generator")
public interface DataGeneratorConfig {
  int maxEntities();
}
