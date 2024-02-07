package io.github.raniagus.example.views;

public record HomeView(
    String nombre,
    String apellido
) implements View {
  @Override
  public String filePath() {
    return "home";
  }
}
