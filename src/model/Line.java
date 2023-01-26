package model;

import java.util.*;
import java.util.function.UnaryOperator;

public class Line {
  private final StationNode head;
  private final StationNode tail;
  private final Map<Station, StationNode> nodeMap = new HashMap<>();

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
    nodeMap.put(station, node);
    station.addLine(this);
  }

  Optional<Route> findRouteFromLeft(Station station, StationType destination) {
    return findRoute(station, destination, StationNode::getLeft);
  }

  Optional<Route> findRouteFromRight(Station station, StationType destination) {
    return findRoute(station, destination, StationNode::getRight);
  }

  private Optional<Route> findRoute(Station station, StationType destination, UnaryOperator<StationNode> successor) {
    checkValidation();
    final StationNode routeStartNode = successor.apply(getNode(station));
    if (routeStartNode.isSentinel()) {
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
    return Optional.ofNullable(nodeMap.get(station))
      .orElseThrow(() -> new IllegalArgumentException("station is not on this line"));
  }

  private void checkValidation() {
    if (nodeMap.size() < 2) {
      throw new RuntimeException("number of stations on this line should be greater than or equal to 2");
    }
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
      return !current.isSentinel();
    }

    @Override
    public StationNode next() {
      StationNode node = current;
      current = successor.apply(current);
      return node;
    }
  }
}
