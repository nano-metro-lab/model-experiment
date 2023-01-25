package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Station {
  private final StationType type;
  private final List<Passenger> passengers = new ArrayList<>();
  private final List<Line> lines = new ArrayList<>();
  private final RoutesMap routesMap = new RoutesMap();

  public Station(StationType type) {
    this.type = type;
  }

  public StationType getType() {
    return type;
  }

  public Passenger[] getPassengers() {
    return passengers.toArray(Passenger[]::new);
  }

  void addPassenger(Passenger passenger) {
    passengers.add(passenger);
  }

  void removePassenger(Passenger passenger) {
    passengers.remove(passenger);
  }

  Line[] getLines() {
    return lines.toArray(Line[]::new);
  }

  void addLine(Line line) {
    lines.add(line);
  }

  void removeLine(Line line) {
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
      for (Line line : Station.this.lines) {
        line.findRouteFromLeft(Station.this, destination).ifPresent(routes::add);
        line.findRouteFromRight(Station.this, destination).ifPresent(routes::add);
      }
      return List.copyOf(routes);
    }

    void clear() {
      map.clear();
    }
  }
}
