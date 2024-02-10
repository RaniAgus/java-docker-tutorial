package io.github.raniagus.example.views;

import java.util.Map;

public interface View {
  String filePath();

  default Map<String, Object> toMap() {
    return Map.of("view", this);
  }
}
