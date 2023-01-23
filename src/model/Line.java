package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class Line {
  private static final class StationNode {
    public final Station station;
    public StationNode left;
    public StationNode right;

    private StationNode(Station station, StationNode left, StationNode right) {
      this.station = station;
      this.left = left;
      this.right = right;
    }

    public StationNode getNext(StationNode prev) {
      if (prev == left) {
        return right;
      } else if (prev == right) {
        return left;
      }
      throw new RuntimeException("stationNode is not connected to this node");
    }
  }

  private StationNode startNode;
  private StationNode endNode;

  public Line(Station startStation, Station endStation) {
    startNode = new StationNode(startStation, null, null);
    endNode = new StationNode(endStation, null, null);
    startNode.right = endNode;
    endNode.left = startNode;
  }

  public Station getNextStation(Station prevStation, Station currStation) {
    StationNode prevNode = getNode(prevStation);
    StationNode currNode = getNode(currStation);
    StationNode nextNode = currNode.getNext(prevNode);
    return nextNode.station;
  }

  public void addStation(Station station, Station adjacentStation) {
    StationNode node;
    StationNode adjacentNode = getNode(adjacentStation);
    if (adjacentNode == startNode) {
      node = new StationNode(station, null, startNode);
      startNode.left = node;
      startNode = node;
    } else if (adjacentNode == endNode) {
      node = new StationNode(station, endNode, null);
      endNode.right = node;
      endNode = node;
    } else {
      throw new RuntimeException("adjacentStation is not at the start or end of this line");
    }
    station.addLine(this);
  }

  public Optional<Route> findRouteFromLeft(Station station, StationType destination) {
    return findRoute(station, destination, node -> node.left);
  }

  public Optional<Route> findRouteFromRight(Station station, StationType destination) {
    return findRoute(station, destination, node -> node.right);
  }

  private Optional<Route> findRoute(Station station, StationType destination, UnaryOperator<StationNode> getAdjacentNode) {
    final StationNode adjacentNode = getAdjacentNode.apply(getNode(station));
    if (adjacentNode == null) {
      return Optional.empty();
    }
    int distance = 1;
    StationNode node = adjacentNode;
    while (node != null) {
      if (node.station.getType() == destination) {
        Route route = new Route(station, node.station, 0, distance);
        return Optional.of(route);
      }
      distance += 1;
      node = getAdjacentNode.apply(node);
    }
    List<Route> routes = new ArrayList<>();
    distance = 1;
    node = adjacentNode;
    while (node != null) {
      for (Line line : node.station.getLines()) {
        if (line == this) {
          continue;
        }
        RoutesMap transferRoutesMap = node.station.getRoutesMap();
        Route[] transferRoutes = transferRoutesMap.get(destination);
        if (transferRoutes.length == 0) {
          continue;
        }
        int averageTransferTime = Route.getAverageTransferTime(transferRoutes);
        int averageLength = Route.getAverageLength(transferRoutes);
        Route route = new Route(station, node.station, 1 + averageTransferTime, distance + averageLength);
        routes.add(route);
      }
      distance += 1;
      node = getAdjacentNode.apply(node);
    }
    return routes.stream().sorted().findFirst();
  }

  private StationNode getNode(Station station) {
    StationNode node = startNode;
    while (node != null) {
      if (node.station == station) {
        return node;
      }
      node = node.right;
    }
    throw new RuntimeException("station is not on this line");
  }
}
