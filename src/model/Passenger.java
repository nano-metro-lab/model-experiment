package model;

import java.util.List;
import java.util.Optional;

public class Passenger {
  private final StationType finalDestination;
  private StationType transfer;

  public Passenger(StationType destination) {
    this.finalDestination = destination;
  }

  public StationType getFinalDestination() {
    return finalDestination;
  }

  public StationType getCurrDestination() {
    return Optional.ofNullable(transfer).orElse(finalDestination);
  }

  public void setTransfer(StationType transfer) {
    this.transfer = transfer;
  }

  public Optional<Route> findRoute(Station station, Station nextStation) {
    List<Route> routes = station.getRoutes(finalDestination);
    for (Route route : routes) {
      if (route.start() == nextStation) {
        return Optional.of(route);
      }
    }
    return Optional.empty();
  }
}
