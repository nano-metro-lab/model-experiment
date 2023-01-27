package model.core;

import java.util.*;

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
    private static final List<Route> emptyRoutes = Collections.emptyList();
    private final Map<StationType, List<Route>> map = new HashMap<>();

    List<Route> get(StationType destinationType) {
      return Optional.ofNullable(map.get(destinationType)).orElseGet(() -> {
        // prevent infinite loop
        map.put(destinationType, emptyRoutes);
        List<Route> routes = find(destinationType);
        map.put(destinationType, routes);
        return routes;
      });
    }

    private List<Route> find(StationType destinationType) {
      List<Route> routes = new ArrayList<>();
      for (Line line : Station.this.lines) {
        line.findRouteFromLeft(destinationType, Station.this).ifPresent(routes::add);
        line.findRouteFromRight(destinationType, Station.this).ifPresent(routes::add);
      }
      return List.copyOf(routes);
    }

    void clear() {
      map.clear();
    }
  }
}
