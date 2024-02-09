package io.github.raniagus.example.helpers;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum URLUtil {;
  public static String joinParams(String path, URLEncodedEntry... entries) {
    return path + Stream.of(entries)
        .map(URLEncodedEntry::toString)
        .collect(Collectors.joining("&", "?", ""));
  }

  public static <T extends CharSequence> URLEncodedEntry encode(String key, Iterable<T> values) {
    return new URLEncodedEntry(key, String.join(",", values));
  }

  public static URLEncodedEntry encode(String key, Object value) {
    return new URLEncodedEntry(key, value.toString());
  }
}
