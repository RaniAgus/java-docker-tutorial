package io.github.raniagus.example.views;

import java.util.Set;

public record LoginView(
    String email,
    String origin,
    Set<String> errors
) implements View {
  @Override
  public String filePath() {
    return "login";
  }
}
