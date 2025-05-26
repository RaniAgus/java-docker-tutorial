package io.github.raniagus.example.constants;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public enum Routes {
  HOME("/"),
  LOGIN("/login"),
  LOGOUT("/logout"),
  PUBLIC("/public"),
  ;

  private final String route;

  Routes(String route) {
    this.route = route;
  }

  public String getRoute() {
    return route;
  }

  public String getRoute(List<String> pathParams) {
    if (pathParams == null || pathParams.isEmpty()) {
      return getRoute();
    }
    return pathParams.stream().reduce(route, (acc, param) -> acc.replaceFirst("\\{[^/]+}", param));
  }

  public String getRoute(Map<String, String> queryParams) {
    return getRoute(null, queryParams);
  }

  public String getRoute(List<String> pathParams, Map<String, String> queryParams) {
    String pathWithParams = getRoute(pathParams);
    if (queryParams == null || queryParams.isEmpty()) {
      return pathWithParams;
    }
    return pathWithParams + queryParams.entrySet().stream()
            .map(e -> URLEncoder.encode(e.getKey(), UTF_8) + "=" + URLEncoder.encode(e.getValue(), UTF_8))
            .collect(Collectors.joining("&", "?", ""));
  }

  @Override
  public String toString() {
    return route;
  }
}
