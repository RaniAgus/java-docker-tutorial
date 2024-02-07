package io.github.raniagus.example.views;

import io.github.raniagus.example.constants.Params;
import java.util.Set;

public record LoginView(
    String email,
    String origin,
    Set<String> errors
) implements View {
  public boolean hasEmail() {
    return !email.isBlank() || hasEmailError();
  }

  public boolean hasEmailError() {
    return errors.contains(Params.EMAIL);
  }

  public boolean shouldFocusEmail() {
    return hasEmailError() || email.isBlank();
  }

  public boolean hasPasswordError() {
    return errors.contains(Params.PASSWORD);
  }

  public boolean shouldFocusPassword() {
    return !shouldFocusEmail() && hasPasswordError();
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  @Override
  public String filePath() {
    return "login";
  }
}
