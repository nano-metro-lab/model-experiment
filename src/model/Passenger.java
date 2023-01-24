package model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class Passenger {
  private final StationType finalDestination;
  private final Deque<StationType> destinations = new ArrayDeque<>();
  private Station currentStation;
  private Train currentTrain;

  public Passenger(Station station, StationType destination) {
    currentStation = station;
    currentStation.addPassenger(this);
    finalDestination = destination;
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
    currentStation.removePassenger(this);
    currentStation = null;
    currentTrain = train;
    currentTrain.addPassenger(this);
  }

  private Optional<Route> findRoute(Train train) {
    RoutesMap routesMap = currentStation.getRoutesMap();
    List<Route> routes = routesMap.get(finalDestination);
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
    currentTrain.removePassenger(this);
    currentTrain = null;
    currentStation = station;
    destinations.pop();
    if (!destinations.isEmpty()) {
      currentStation.addPassenger(this);
      destinations.clear();
    }
  }
}
