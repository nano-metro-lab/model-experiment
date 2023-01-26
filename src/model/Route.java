package model;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;

record Route(
  Station start,
  Station end,
  int transferTimes,
  int transferLength,
  int totalLength
) {
  static final Comparator<Route> comparator = Comparator.comparingInt(Route::transferTimes)
    .thenComparingInt(Route::totalLength)
    .thenComparingInt(Route::transferLength);

  static int average(Collection<Route> routes, Function<Route, Integer> mapper) {
    double average = (double) routes.stream().map(mapper).reduce(0, Integer::sum) / routes.size();
    return (int) Math.round(average);
  }
}
