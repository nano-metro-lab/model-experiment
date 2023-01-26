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

  public Line[] getLines() {
    return lines.toArray(Line[]::new);
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
    private final Map<StationType, List<Route>> map = new HashMap<>();

    List<Route> get(StationType destinationType) {
      if (map.containsKey(destinationType)) {
        return map.get(destinationType);
      }
      // prevent infinite loop
      map.put(destinationType, List.of());
      List<Route> routes = find(destinationType);
      map.put(destinationType, routes);
      return routes;
    }

    private List<Route> find(StationType destinationType) {
      List<Route> routes = new ArrayList<>();
      for (Line line : Station.this.getLines()) {
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
