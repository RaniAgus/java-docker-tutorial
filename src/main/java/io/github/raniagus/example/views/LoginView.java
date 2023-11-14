package io.github.raniagus.example.views;

import java.util.Set;

public record LoginView(
    String email,
    String origin,
    Set<?> errors
) { }
