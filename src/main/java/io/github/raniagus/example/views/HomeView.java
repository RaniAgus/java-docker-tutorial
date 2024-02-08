package io.github.raniagus.example.views;

public record HomeView() implements View {
  @Override
  public String filePath() {
    return "home";
  }
}
