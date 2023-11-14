package io.github.raniagus.example.helpers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public enum HtmlUtil {;
  public static <T extends CharSequence> String encode(String key, Iterable<T> values) {
    return encode(key, String.join(",", values));
  }

  public static String encode(String key, Object value) {
    return encode(key) + "=" + encode(value.toString());
  }

  public static String encode(String... strings) {
    return URLEncoder.encode(String.join("", strings), StandardCharsets.UTF_8);
  }
}
