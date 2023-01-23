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

  public void addPassenger(Passenger passenger) {
    passengers.add(passenger);
  }

  public void removePassenger(Passenger passenger) {
    passengers.remove(passenger);
  }

  public Line[] getLines() {
    return lines.toArray(Line[]::new);
  }

  public void addLine(Line line) {
    lines.add(line);
  }

  public RoutesMap getRoutesMap() {
    return routesMap;
  }
}
