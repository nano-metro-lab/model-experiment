package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class Line {
  private final StationNode head;
  private final StationNode tail;

  public Line() {
    head = StationNode.newSentinel(null, null);
    tail = StationNode.newSentinel(head, null);
    head.right = tail;
  }

  public void addStation(Station station) {
    if (head.right != tail) {
      throw new RuntimeException("adjacent station is required");
    }
    addStation(station, new StationNode[]{head, tail});
  }

  public void addStation(Station station, Station endStation) {
    StationNode endNode = getNode(endStation);
    if (endNode.left == head) {
      addStation(station, new StationNode[]{head, endNode});
    } else if (endNode.right == tail) {
      addStation(station, new StationNode[]{endNode, tail});
    } else {
      throw new IllegalArgumentException("endStation is not at the end of this line");
    }
  }

  public void addStation(Station station, List<Station> adjacentStations) {
    if (adjacentStations.size() != 2) {
      throw new IllegalArgumentException("adjacentStations should contain 2 stations");
    }
    StationNode[] adjacentNodes = adjacentStations.stream().map(this::getNode).toArray(StationNode[]::new);
    addStation(station, adjacentNodes);
  }

  private void addStation(Station station, StationNode[] adjacentNodes) {
    StationNode leftNode;
    StationNode rightNode;
    if (adjacentNodes[0].right == adjacentNodes[1]) {
      leftNode = adjacentNodes[0];
      rightNode = adjacentNodes[1];
    } else if (adjacentNodes[1].right == adjacentNodes[0]) {
      leftNode = adjacentNodes[1];
      rightNode = adjacentNodes[0];
    } else {
      throw new IllegalArgumentException("adjacentStations are not connected on this line");
    }
    StationNode node = new StationNode(station, leftNode, rightNode);
    leftNode.right = node;
    rightNode.left = node;
    station.addLine(this);
  }

  Optional<Route> findRouteFromLeft(Station station, StationType destination) {
    return findRoute(station, destination, StationNode::getLeft);
  }

  Optional<Route> findRouteFromRight(Station station, StationType destination) {
    return findRoute(station, destination, StationNode::getRight);
  }

  private Optional<Route> findRoute(Station station, StationType destination, UnaryOperator<StationNode> getAdjacentNode) {
    checkValidation();
    final StationNode routeStartNode = getAdjacentNode.apply(getNode(station));
    if (routeStartNode.isSentinel()) {
      return Optional.empty();
    }
    int distance = 1;
    StationNode node = routeStartNode;
    while (!node.isSentinel()) {
      if (node.station.getType() == destination) {
        Route route = new Route(routeStartNode.station, node.station, 0, 0, distance);
        return Optional.of(route);
      }
      distance += 1;
      node = getAdjacentNode.apply(node);
    }
    List<Route> availableRoutes = new ArrayList<>();
    distance = 1;
    node = routeStartNode;
    while (!node.isSentinel()) {
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
      distance += 1;
      node = getAdjacentNode.apply(node);
    }
    return availableRoutes.stream().min(Route.comparator);
  }

  private StationNode getNode(Station station) {
    StationNode node = head.right;
    while (node != tail) {
      if (node.station == station) {
        return node;
      }
      node = node.right;
    }
    throw new IllegalArgumentException("station is not on this line");
  }

  private void checkValidation() {
    int stationCount = 0;
    StationNode node = head.right;
    while (node != tail) {
      if (++stationCount >= 2) {
        return;
      }
      node = node.right;
    }
    throw new RuntimeException("number of stations on this line should be greater than or equal to 2");
  }

  private static class StationNode {
    final Station station;
    StationNode left;
    StationNode right;

    StationNode(Station station, StationNode left, StationNode right) {
      this.station = station;
      this.left = left;
      this.right = right;
    }

    @SuppressWarnings("SameParameterValue")
    static StationNode newSentinel(StationNode left, StationNode right) {
      return new StationNode(null, left, right);
    }

    boolean isSentinel() {
      return station == null;
    }

    StationNode getLeft() {
      return left;
    }

    StationNode getRight() {
      return right;
    }
  }
}
