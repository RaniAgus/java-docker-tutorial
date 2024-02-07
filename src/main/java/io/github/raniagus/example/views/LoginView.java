package io.github.raniagus.example.views;

import io.github.raniagus.example.constants.Params;
import io.github.raniagus.example.views.util.Input;
import java.util.Set;

public record LoginView(
    String emailValue,
    String origin,
    Set<String> errors
) implements View {
  public Input email() {
    return new Input(Params.EMAIL, emailValue, errors.contains(Params.EMAIL));
  }

  public Input password() {
    return new Input(Params.PASSWORD, "", errors.contains(Params.PASSWORD));
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  @Override
  public String filePath() {
    return "login";
  }
}
