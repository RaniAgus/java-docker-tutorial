package io.github.raniagus.example.view;

import java.util.Set;

public record LoginView(
    String emailValue,
    String originValue,
    Set<String> errors
) implements View {
  public Input email() {
    return new Input("email", emailValue, errors.contains("email"));
  }

  public Input password() {
    return new Input("password", "", errors.contains("password"));
  }

  public Input origin() {
    return new Input("origin", originValue, false);
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  @Override
  public String filePath() {
    return "login.mustache";
  }
}
