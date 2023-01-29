package model.core;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;

public record Route(
  Station next,
  Station last,
  int length,
  int transfer
) {
  static Stream<Route> getBest(Collection<Route> routes, Comparator<Route> comparator) {
    Route bestRoute = routes.stream().min(comparator).orElse(null);
    if (bestRoute == null) {
      return Stream.empty();
    }
    return routes.stream()
      .filter(route -> route == bestRoute || comparator.compare(route, bestRoute) == 0);
  }
}
