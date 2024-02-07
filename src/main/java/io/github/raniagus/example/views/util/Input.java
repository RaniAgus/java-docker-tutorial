package io.github.raniagus.example.views.util;

public record Input(
    String name,
    String value,
    boolean invalid
) {
  public boolean touched() {
    return !value.isBlank() || invalid();
  }

  public boolean autofocus() {
    return value.isBlank() || invalid();
  }
}
