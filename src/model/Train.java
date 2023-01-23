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

  public Train(Line line, Station station, Station prevStation) {
    this.line = line;
    this.prevStation = prevStation;
    currStation = station;
    nextStation = line.getNextStation(currStation, prevStation);
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

  public Station getNextStation() {
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
}
