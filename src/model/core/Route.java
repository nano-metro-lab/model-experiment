package model.core;

import java.util.Collection;
import java.util.function.ToIntFunction;

public record Route(
  Station next,
  Station last,
  int length,
  int transfer
) {
  static int average(Collection<Route> routes, ToIntFunction<Route> mapper) {
    double average = routes.stream().mapToInt(mapper).average().orElse(0.0);
    return (int) Math.round(average);
  }
}
