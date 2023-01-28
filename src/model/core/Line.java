package model.core;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class Line {
  private static final List<Comparator<Route>> routeComparators;

  static {
    Comparator<Route> transferComparator = Comparator.comparingInt(Route::transfer);
    Comparator<Route> lengthComparator = Comparator.comparingInt(Route::length);
    routeComparators = List.of(transferComparator, lengthComparator);
  }

  private final Map<Station, StationNode> nodeMap = new HashMap<>();
  private boolean isFindingRoutes = false;

  private static Stream<Route> getBestRoutes(List<Route> routes) {
    if (routes.isEmpty()) {
      return Stream.empty();
    }
    return routeComparators.stream()
      .flatMap(routeComparator -> {
        Route bestRoute = routes.stream().min(routeComparator).orElseThrow();
        return routes.stream()
          .filter(route -> route == bestRoute || routeComparator.compare(route, bestRoute) == 0);
      }).distinct();
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

  List<Route> findRoutes(StationType destinationType, Station station) {
    isFindingRoutes = true;
    List<Route> routes = Stream.concat(
      findRoutes(destinationType, station, StationNode::getLeft),
      findRoutes(destinationType, station, StationNode::getRight)
    ).toList();
    isFindingRoutes = false;
    return getBestRoutes(routes).toList();
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
        for (Route transferRoute : transferRoutes) {
          int length = distance + transferRoute.length();
          int transfer = 1 + transferRoute.transfer();
          Route route = new Route(routeNextNode.station, node.station, length, transfer);
          routes.add(route);
        }
      }
    }
    return getBestRoutes(routes);
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
