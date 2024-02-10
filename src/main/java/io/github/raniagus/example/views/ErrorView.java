package io.github.raniagus.example.views;

public record ErrorView(
    String title,
    String message
) implements View {
  @Override
  public String filePath() {
    return "error.mustache";
  }
}
