package model;

import java.util.*;

public class RoutesMap {
  private final Station station;
  private final Map<StationType, List<Route>> map = new HashMap<>();

  public RoutesMap(Station station) {
    this.station = station;
  }

  public void clear() {
    map.clear();
  }

  public Route[] get(StationType destination) {
    if (!map.containsKey(destination)) {
      // prevent infinite loop
      map.put(destination, new ArrayList<>());
      List<Route> routes = getRoutes(destination);
      map.put(destination, routes);
      return routes.toArray(Route[]::new);
    }
    return map.get(destination).toArray(Route[]::new);
  }

  private List<Route> getRoutes(StationType destination) {
    List<Route> routes = new ArrayList<>();
    for (Line line : station.getLines()) {
      List<Route> routesFromLeft = line.findRoutesFromLeft(station, destination);
      routes.addAll(routesFromLeft);
      List<Route> routesFromRight = line.findRoutesFromRight(station, destination);
      routes.addAll(routesFromRight);
    }
    return routes;
  }
}
