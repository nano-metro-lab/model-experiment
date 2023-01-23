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
    if (!map.containsKey(destination)) {
      // prevent infinite loop
      map.put(destination, List.of());
      List<Route> routes = getRoutes(destination);
      map.put(destination, routes);
      return routes;
    }
    return map.get(destination);
  }

  private List<Route> getRoutes(StationType destination) {
    List<Route> routes = new ArrayList<>();
    for (Line line : station.getLines()) {
      List<Route> routesFromLeft = line.findRoutesFromLeft(station, destination);
      routes.addAll(routesFromLeft);
      List<Route> routesFromRight = line.findRoutesFromRight(station, destination);
      routes.addAll(routesFromRight);
    }
    return List.copyOf(routes);
  }
}
