package io.github.raniagus.example.view;

public record HomeView(
    String nombre,
    String apellido
) implements View {
  @Override
  public String filePath() {
    return "home.mustache";
  }
}
