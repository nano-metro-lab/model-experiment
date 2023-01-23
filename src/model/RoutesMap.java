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
      Optional<Route> routeFromLeft = line.findRouteFromLeft(station, destination);
      routeFromLeft.ifPresent(routes::add);
      Optional<Route> routeFromRight = line.findRouteFromRight(station, destination);
      routeFromRight.ifPresent(routes::add);
    }
    return routes;
  }
}
