package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutesMap {
  private final Station station;
  private final Map<StationType, List<Route>> map = new HashMap<>();

  public RoutesMap(Station station) {
    this.station = station;
  }

  public void clear() {
    map.clear();
  }

  public List<Route> get(StationType destination) {
    if (map.containsKey(destination)) {
      return map.get(destination);
    }
    // prevent infinite loop
    map.put(destination, List.of());
    List<Route> routes = getRoutes(destination);
    map.put(destination, routes);
    return routes;
  }

  private List<Route> getRoutes(StationType destination) {
    List<Route> routes = new ArrayList<>();
    for (Line line : station.getLines()) {
      line.findRouteFromLeft(station, destination).ifPresent(routes::add);
      line.findRouteFromRight(station, destination).ifPresent(routes::add);
    }
    return List.copyOf(routes);
  }
}
