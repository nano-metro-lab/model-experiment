package model.core;

import java.util.*;
import java.util.function.UnaryOperator;

public class Line {
  private final Map<Station, StationNode> nodeMap = new HashMap<>();

  public void update(List<Station> stations) {
    if (stations.size() < 2) {
      throw new IllegalArgumentException("stations should be greater than or equal to 2");
    }
    if (!nodeMap.isEmpty()) {
      Set<Station> staleStations = new HashSet<>(nodeMap.keySet());
      stations.forEach(staleStations::remove);
      for (Station station : staleStations) {
        nodeMap.remove(station);
        station.removeLine(this);
      }
    }
    StationNode sentinel = new StationNode(null);
    StationNode lastNode = sentinel;
    for (Station station : stations) {
      StationNode node = getPossibleNode(station).orElseGet(() -> {
        StationNode newNode = new StationNode(station);
        nodeMap.put(station, newNode);
        station.addLine(this);
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

  public void reset() {
    for (Station station : nodeMap.keySet()) {
      station.removeLine(this);
    }
    nodeMap.clear();
  }

  Optional<Route> findRouteFromLeft(Station station, StationType destination) {
    return findRoute(station, destination, StationNode::getLeft);
  }

  Optional<Route> findRouteFromRight(Station station, StationType destination) {
    return findRoute(station, destination, StationNode::getRight);
  }

  private Optional<Route> findRoute(Station station, StationType destination, UnaryOperator<StationNode> successor) {
    StationNode routeStartNode = successor.apply(getNode(station));
    if (routeStartNode == null) {
      return Optional.empty();
    }
    StationNodeIterator nodeIterator = new StationNodeIterator(routeStartNode, successor);
    for (int distance = 1; nodeIterator.hasNext(); distance++) {
      StationNode node = nodeIterator.next();
      if (node.station.getType() == destination) {
        Route route = new Route(routeStartNode.station, node.station, 0, 0, distance);
        return Optional.of(route);
      }
    }
    List<Route> availableRoutes = new ArrayList<>();
    nodeIterator.reset(routeStartNode);
    for (int distance = 1; nodeIterator.hasNext(); distance++) {
      StationNode node = nodeIterator.next();
      for (Line line : node.station.getLines()) {
        if (line == this) {
          continue;
        }
        List<Route> transferRoutes = node.station.getRoutes(destination);
        if (transferRoutes.isEmpty()) {
          continue;
        }
        int transferTimes = 1 + Route.average(transferRoutes, Route::transferTimes);
        int transferLength = Route.average(transferRoutes, Route::totalLength);
        Route route = new Route(routeStartNode.station, node.station, transferTimes, transferLength, distance + transferLength);
        availableRoutes.add(route);
      }
    }
    return availableRoutes.stream().min(Route.comparator);
  }

  private StationNode getNode(Station station) {
    return getPossibleNode(station)
      .orElseThrow(() -> new IllegalArgumentException("station is not on this line"));
  }

  private Optional<StationNode> getPossibleNode(Station station) {
    return Optional.ofNullable(nodeMap.get(station));
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

    StationNodeIterator(StationNode node, UnaryOperator<StationNode> successor) {
      this.successor = successor;
      reset(node);
    }

    void reset(StationNode node) {
      current = node;
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
