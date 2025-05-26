package io.github.raniagus.example.view;

public record ErrorView(
    String title,
    String message
) implements View {
  @Override
  public String filePath() {
    return "error.mustache";
  }
}
