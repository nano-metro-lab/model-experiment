package model.core;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class Line {
  private static final Comparator<Route> routeComparator = Comparator
    .comparingInt(Route::transfer)
    .thenComparingInt(Route::length);

  private static final List<Comparator<Route>> routeComparators;

  static {
    Comparator<Route> transferComparator = Comparator.comparingInt(Route::transfer);
    Comparator<Route> lengthComparator = Comparator.comparingInt(Route::length);
    routeComparators = List.of(transferComparator, lengthComparator);
  }

  private final Map<Station, StationNode> nodeMap = new HashMap<>();
  private boolean isFindingRoutes = false;

  private static Stream<Route> getBestRoutes(List<Route> routes, Comparator<Route> comparator) {
    if (routes.isEmpty()) {
      return Stream.empty();
    }
    Route bestRoute = routes.stream().min(comparator).orElseThrow();
    return routes.stream().filter(route -> comparator.compare(route, bestRoute) == 0);
  }

  public void update(List<Station> stations) {
    if (stations.isEmpty()) {
      for (Station station : nodeMap.keySet()) {
        station.removeLine(Line.this);
      }
      nodeMap.clear();
      return;
    }
    if (!nodeMap.isEmpty()) {
      Set<Station> staleStations = new HashSet<>(nodeMap.keySet());
      for (Station station : stations) {
        staleStations.remove(station);
      }
      for (Station station : staleStations) {
        nodeMap.remove(station);
        station.removeLine(Line.this);
      }
    }
    StationNode sentinel = new StationNode(null);
    StationNode lastNode = sentinel;
    for (Station station : stations) {
      StationNode node = Optional.ofNullable(nodeMap.get(station)).orElseGet(() -> {
        StationNode newNode = new StationNode(station);
        nodeMap.put(station, newNode);
        station.addLine(Line.this);
        return newNode;
      });
      node.left = lastNode;
      node.right = null;
      lastNode.right = node;
      lastNode = node;
    }
    StationNode firstNode = sentinel.right;
    firstNode.left = null;
  }

  boolean isFindingRoutes() {
    return isFindingRoutes;
  }

  Stream<Route> findRoutes(StationType destinationType, Station station) {
    isFindingRoutes = true;
    List<Route> routes = Stream.concat(
      findRoutesFromLeft(destinationType, station),
      findRoutesFromRight(destinationType, station)
    ).toList();
    isFindingRoutes = false;
    return routeComparators.stream()
      .flatMap(routeComparator -> getBestRoutes(routes, routeComparator))
      .distinct();
  }

  private Stream<Route> findRoutesFromLeft(StationType destinationType, Station station) {
    return findRoutes(destinationType, station, StationNode::getLeft);
  }

  private Stream<Route> findRoutesFromRight(StationType destinationType, Station station) {
    return findRoutes(destinationType, station, StationNode::getRight);
  }

  private Stream<Route> findRoutes(StationType destinationType, Station station, UnaryOperator<StationNode> successor) {
    StationNode routeStartNode = Optional.ofNullable(nodeMap.get(station))
      .orElseThrow(() -> new RuntimeException("station " + station + " is not on this line"));
    StationNode routeNextNode = successor.apply(routeStartNode);
    if (routeNextNode == null) {
      return Stream.empty();
    }
    {
      StationNodeIterator nodeIterator = new StationNodeIterator(routeNextNode, successor);
      for (int distance = 1; nodeIterator.hasNext(); distance++) {
        StationNode node = nodeIterator.next();
        if (node.station.getType().equals(destinationType)) {
          Route route = new Route(routeNextNode.station, node.station, distance, 0);
          return Stream.of(route);
        }
      }
    }
    List<Route> routes = new ArrayList<>();
    {
      StationNodeIterator nodeIterator = new StationNodeIterator(routeNextNode, successor);
      for (int distance = 1; nodeIterator.hasNext(); distance++) {
        StationNode node = nodeIterator.next();
        List<Route> transferRoutes = node.station.getRoutes(destinationType);
        if (transferRoutes.isEmpty()) {
          continue;
        }
        int averageLength = Route.average(transferRoutes, Route::length);
        int averageTransfer = Route.average(transferRoutes, Route::transfer);
        Route route = new Route(routeNextNode.station, node.station, distance + averageLength, 1 + averageTransfer);
        routes.add(route);
      }
    }
    return getBestRoutes(routes, routeComparator);
  }

  private static class StationNode {
    final Station station;
    StationNode left;
    StationNode right;

    StationNode(Station station) {
      this.station = station;
    }

    StationNode getLeft() {
      return left;
    }

    StationNode getRight() {
      return right;
    }
  }

  private static class StationNodeIterator implements Iterator<StationNode> {
    private final UnaryOperator<StationNode> successor;
    private StationNode current;

    StationNodeIterator(StationNode current, UnaryOperator<StationNode> successor) {
      this.successor = successor;
      this.current = current;
    }

    @Override
    public boolean hasNext() {
      return current != null;
    }

    @Override
    public StationNode next() {
      StationNode node = current;
      current = successor.apply(current);
      return node;
    }
  }
}
