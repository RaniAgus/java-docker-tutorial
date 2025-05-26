package io.github.raniagus.example.view;

import java.util.Map;

public interface View {
  String filePath();

  default Map<String, Object> model() {
    return Map.of("view", this);
  }
}
