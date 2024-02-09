package io.github.raniagus.example.helpers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class URLEncodedEntry {
  private final String key;
  private final String value;

  protected URLEncodedEntry(String key, String value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public String toString() {
    return "%s=%s".formatted(
        URLEncoder.encode(key, StandardCharsets.UTF_8),
        URLEncoder.encode(value, StandardCharsets.UTF_8));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    URLEncodedEntry that = (URLEncodedEntry) o;
    return Objects.equals(key, that.key) && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }
}
