package model;

import java.util.*;

public class RoutesMap {
  private final Station station;
  private final Map<StationType, Set<Route>> map = new HashMap<>();

  public RoutesMap(Station station) {
    this.station = station;
  }

  public void clear() {
    map.clear();
  }

  public Set<Route> get(StationType destination) {
    if (!map.containsKey(destination)) {
      // prevent infinite loop
      map.put(destination, new HashSet<>());
      Set<Route> routes = getRoutes(destination);
      map.put(destination, routes);
      return Set.copyOf(routes);
    }
    return Set.copyOf(map.get(destination));
  }

  private Set<Route> getRoutes(StationType destination) {
    Set<Route> routes = new HashSet<>();
    for (Line line : station.getLines()) {
      List<Route> routesFromLeft = line.findRoutesFromLeft(station, destination);
      routes.addAll(routesFromLeft);
      List<Route> routesFromRight = line.findRoutesFromRight(station, destination);
      routes.addAll(routesFromRight);
    }
    return routes;
  }
}
