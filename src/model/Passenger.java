package model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class Passenger {
  private final StationType finalDestination;
  private final Deque<StationType> destinations = new ArrayDeque<>();
  private Station currStation;
  private Train currTrain;

  public Passenger(Station currStation, StationType finalDestination) {
    currStation.addPassenger(this);
    this.currStation = currStation;
    this.finalDestination = finalDestination;
  }

  public StationType getFinalDestination() {
    return finalDestination;
  }

  void boardIfPossible(Train train) {
    Route route = findRoute(train).orElse(null);
    if (route == null) {
      return;
    }
    destinations.push(finalDestination);
    StationType routeDestination = route.end().getType();
    if (routeDestination != finalDestination) {
      destinations.push(routeDestination);
    }
    currStation.removePassenger(this);
    currStation = null;
    currTrain = train;
    currTrain.addPassenger(this);
  }

  private Optional<Route> findRoute(Train train) {
    List<Route> routes = currStation.getRoutes(finalDestination);
    for (Route route : routes) {
      if (route.start() == train.getNextStation()) {
        return Optional.of(route);
      }
    }
    return Optional.empty();
  }

  void arriveAt(Station station) {
    if (station.getType() != destinations.peek()) {
      return;
    }
    currTrain.removePassenger(this);
    currTrain = null;
    currStation = station;
    destinations.pop();
    if (!destinations.isEmpty()) {
      currStation.addPassenger(this);
      destinations.clear();
    }
  }
}
