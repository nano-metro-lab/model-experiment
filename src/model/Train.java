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
    this.line = line;
    currStation = station;
    nextStation = line.getNextStation(null, currStation);
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
    Passenger[] passengersAtStation = currStation.getPassengers();
    for (Passenger passenger : passengersAtStation) {
      passenger.boardIfPossible(this);
    }
    prevStation = currStation;
    currStation = null;
  }

  public void stop() {
    currStation = nextStation;
    for (Passenger passenger : passengers) {
      passenger.arriveAt(currStation);
    }
    nextStation = line.getNextStation(prevStation, currStation);
  }
}
