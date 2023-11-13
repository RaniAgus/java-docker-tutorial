package io.github.raniagus.example.views;

public record LoginView(
    String email,
    String origin,
    String error
) { }
