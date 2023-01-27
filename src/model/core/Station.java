package model.core;

import java.util.*;
import java.util.stream.Stream;

public class Station {
  private final StationType type;
  private final List<Line> lines = new ArrayList<>();
  private final RoutesMap routesMap = new RoutesMap();

  public Station(StationType type) {
    this.type = type;
  }

  public StationType getType() {
    return type;
  }

  public void addLine(Line line) {
    lines.add(line);
  }

  public void removeLine(Line line) {
    lines.remove(line);
  }

  public List<Route> getRoutes(StationType destinationType) {
    return routesMap.get(destinationType);
  }

  public void clearRoutesMap() {
    routesMap.clear();
  }

  private class RoutesMap {
    private static final List<Comparator<Route>> routeComparators;

    static {
      Comparator<Route> transferComparator = Comparator.comparingInt(Route::transfer);
      Comparator<Route> lengthComparator = Comparator.comparingInt(Route::length);
      routeComparators = List.of(transferComparator, lengthComparator);
    }

    private final Map<StationType, List<Route>> map = new HashMap<>();

    List<Route> get(StationType destinationType) {
      return Optional.ofNullable(map.get(destinationType)).orElseGet(() -> {
        // prevent infinite loop
        map.put(destinationType, List.of());
        List<Route> routes = find(destinationType);
        map.put(destinationType, routes);
        return routes;
      });
    }

    private List<Route> find(StationType destinationType) {
      return Station.this.lines.stream()
        .flatMap(line -> {
          List<Route> routes = Stream.concat(
            line.findRoutesFromLeft(destinationType, Station.this),
            line.findRoutesFromRight(destinationType, Station.this)
          ).toList();
          if (routes.isEmpty()) {
            return Stream.empty();
          }
          return routeComparators.stream()
            .flatMap(routeComparator -> {
              Route bestRoute = routes.stream().min(routeComparator).orElseThrow();
              return routes.stream()
                .filter(route -> route == bestRoute || routeComparator.compare(route, bestRoute) == 0);
            }).distinct();
        }).toList();
    }

    void clear() {
      map.clear();
    }
  }
}
