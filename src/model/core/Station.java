package model.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  Line[] getLines() {
    return lines.toArray(Line[]::new);
  }

  public void addLine(Line line) {
    lines.add(line);
  }

  public void removeLine(Line line) {
    lines.remove(line);
  }

  List<Route> getRoutes(StationType destination) {
    return routesMap.get(destination);
  }

  public void clearRoutesMap() {
    routesMap.clear();
  }

  private class RoutesMap {
    private final Map<StationType, List<Route>> map = new HashMap<>();

    List<Route> get(StationType destination) {
      if (map.containsKey(destination)) {
        return map.get(destination);
      }
      // prevent infinite loop
      map.put(destination, List.of());
      List<Route> routes = find(destination);
      map.put(destination, routes);
      return routes;
    }

    private List<Route> find(StationType destination) {
      List<Route> routes = new ArrayList<>();
      for (Line line : Station.this.getLines()) {
        line.findRouteFromLeft(destination, Station.this).ifPresent(routes::add);
        line.findRouteFromRight(destination, Station.this).ifPresent(routes::add);
      }
      return List.copyOf(routes);
    }

    void clear() {
      map.clear();
    }
  }
}
