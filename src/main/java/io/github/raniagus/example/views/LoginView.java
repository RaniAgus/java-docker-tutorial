package io.github.raniagus.example.views;

import java.util.Set;

public record LoginView(
    String email,
    String origin,
    Set<String> errors
) implements View {
  public boolean hasEmailError() {
    return errors.contains("email");
  }

  public boolean shouldFocusEmail() {
    return hasEmailError() || email.isBlank();
  }

  public boolean hasPasswordError() {
    return errors.contains("password");
  }

  public boolean shouldFocusPassword() {
    return hasPasswordError();
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  @Override
  public String filePath() {
    return "login";
  }
}
