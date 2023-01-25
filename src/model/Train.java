package model;

import java.util.ArrayList;
import java.util.List;

public class Train {
  private final List<Passenger> passengers = new ArrayList<>();
  private final Line line;
  private Station prevStation;
  private Station currStation;
  private Station nextStation;

  public Train(Line line, Station station) {
    this(line, station, null);
  }

  public Train(Line line, Station currStation, Station prevStation) {
    line.addTrain(this);
    this.line = line;
    this.prevStation = prevStation;
    this.currStation = currStation;
    nextStation = line.getNextStation(currStation, prevStation);
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

  Station getNextStation() {
    return nextStation;
  }

  public void start() {
    for (Passenger passenger : currStation.getPassengers()) {
      passenger.boardIfPossible(this);
    }
    prevStation = currStation;
    currStation = null;
  }

  public void stop() {
    currStation = nextStation;
    for (Passenger passenger : getPassengers()) {
      passenger.arriveAt(currStation);
    }
    nextStation = line.getNextStation(currStation, prevStation);
  }

  public void destroy() {
    throw new RuntimeException("not implemented");
  }
}
