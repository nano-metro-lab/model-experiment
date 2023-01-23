package model;

import java.util.Arrays;
import java.util.function.Function;

public record Route(Station start, Station transferOrEnd, int transferTime, int length) implements Comparable<Route> {
  public static int getAverageTransferTime(Route[] routes) {
    return getAverage(routes, Route::transferTime);
  }

  public static int getAverageLength(Route[] routes) {
    return getAverage(routes, Route::length);
  }

  private static int getAverage(Route[] routes, Function<Route, Integer> mapper) {
    double average = (double) Arrays.stream(routes).map(mapper).reduce(0, Integer::sum) / routes.length;
    return (int) Math.round(average);
  }

  @Override
  public int compareTo(Route that) {
    int transferTimeDifference = this.transferTime - that.transferTime;
    if (transferTimeDifference != 0) {
      return transferTimeDifference;
    }
    return this.length - that.length;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (Route) obj;
    return this.transferTime == that.transferTime && this.length == that.length;
  }
}
