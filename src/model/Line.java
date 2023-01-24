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

  public Station getNextStation(Station currStation, Station prevStation) {
    StationNode currNode = getNode(currStation);
    StationNode prevNode = Optional.ofNullable(prevStation).map(this::getNode).orElseGet(() -> {
      if (currNode.left == head) {
        return head;
      } else if (currNode.right == tail) {
        return tail;
      } else {
        throw new IllegalArgumentException("prevStation is null and currStation is not at the start or end of this line");
      }
    });
    StationNode nextNode = getNextNode(currNode, prevNode);
    return nextNode.station;
  }

  private StationNode getNextNode(StationNode currNode, StationNode prevNode) {
    if (prevNode == currNode.left) {
      return currNode.right == tail ? prevNode : currNode.right;
    } else if (prevNode == currNode.right) {
      return currNode.left == head ? prevNode : currNode.left;
    } else {
      throw new IllegalArgumentException("prevNode is not connected to currNode");
    }
  }

  public void addStartStation(Station station) {
    addStation(station, new StationNode[]{head, head.right});
  }

  public void addEndStation(Station station) {
    addStation(station, new StationNode[]{tail.left, tail});
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

  public Optional<Route> findRouteFromLeft(Station station, StationType destination) {
    return findRoute(station, destination, node -> node.left);
  }

  public Optional<Route> findRouteFromRight(Station station, StationType destination) {
    return findRoute(station, destination, node -> node.right);
  }

  private Optional<Route> findRoute(Station station, StationType destination, UnaryOperator<StationNode> getAdjacentNode) {
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
        RoutesMap transferRoutesMap = node.station.getRoutesMap();
        List<Route> transferRoutes = transferRoutesMap.get(destination);
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
    return availableRoutes.stream().sorted().findFirst();
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

  private static class StationNode {
    public final Station station;
    public StationNode left;
    public StationNode right;

    public StationNode(Station station, StationNode left, StationNode right) {
      this.station = station;
      this.left = left;
      this.right = right;
    }

    public static StationNode newSentinel(StationNode left, StationNode right) {
      return new StationNode(null, left, right);
    }

    public boolean isSentinel() {
      return station == null;
    }
  }
}
