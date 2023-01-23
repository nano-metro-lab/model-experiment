package model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.Set;

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

  public void boardIfPossible(Train train) {
    Optional<Route> optionalRoute = findRoute(train);
    if (optionalRoute.isEmpty()) {
      return;
    }
    assert destinations.isEmpty();
    destinations.push(finalDestination);
    Station transferOrEndStation = optionalRoute.get().transferOrEnd();
    if (transferOrEndStation.getType() != finalDestination) {
      destinations.push(transferOrEndStation.getType());
    }
    currentStation.removePassenger(this);
    currentStation = null;
    currentTrain = train;
    currentTrain.addPassenger(this);
  }

  private Optional<Route> findRoute(Train train) {
    RoutesMap routesMap = currentStation.getRoutesMap();
    Set<Route> routes = routesMap.get(finalDestination);
    for (Route route : routes) {
      if (route.start() == train.getNextStation()) {
        return Optional.of(route);
      }
    }
    return Optional.empty();
  }

  public void arriveAt(Station station) {
    if (station.getType() != destinations.getLast()) {
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
