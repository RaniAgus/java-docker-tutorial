package io.github.raniagus.example.helpers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HtmlUtil {
  public static String encode(String... strings) {
    return URLEncoder.encode(String.join(" ", strings), StandardCharsets.UTF_8);
  }

  @SafeVarargs
  public static String encode(Map.Entry<String, Object>... entries) {
    return Stream.of(entries)
        .map(e -> encode(e.getKey()) + "=" + encode(e.getValue().toString()))
        .collect(Collectors.joining("&"));
  }
}
