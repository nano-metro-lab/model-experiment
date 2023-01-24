package model;

import java.util.Collection;
import java.util.function.Function;

public record Route(Station start, Station end, int transferTimes, int transferLength, int totalLength) implements Comparable<Route> {

  public static int average(Collection<Route> routes, Function<Route, Integer> mapper) {
    double average = (double) routes.stream().map(mapper).reduce(0, Integer::sum) / routes.size();
    return (int) Math.round(average);
  }

  @Override
  public int compareTo(Route that) {
    int transferTimesDifference = this.transferTimes - that.transferTimes;
    if (transferTimesDifference != 0) {
      return transferTimesDifference;
    }
    int totalLengthDifference = this.totalLength - that.totalLength;
    if (totalLengthDifference != 0) {
      return totalLengthDifference;
    }
    return this.transferLength - that.transferLength;
  }
}
