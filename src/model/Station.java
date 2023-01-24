package model;

import java.util.ArrayList;
import java.util.List;

public class Station {
  private final StationType type;
  private final List<Passenger> passengers = new ArrayList<>();
  private final List<Line> lines = new ArrayList<>();
  private final RoutesMap routesMap = new RoutesMap(this);

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

  RoutesMap getRoutesMap() {
    return routesMap;
  }

  public void clearRoutesMap() {
    routesMap.clear();
  }
}
