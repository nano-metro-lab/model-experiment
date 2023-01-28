package model.core;

import java.util.*;
import java.util.stream.Stream;

public class Station {
  private final StationType type;
  private final List<Line> lines = new ArrayList<>();
  private final RoutesMap routesMap = new RoutesMap();

  public Station(StationType type) {
    this.type = type;
  }

  public StationType getType() {
    return type;
  }

  public void addLine(Line line) {
    lines.add(line);
  }

  public void removeLine(Line line) {
    lines.remove(line);
  }

  public List<Route> getRoutes(StationType destinationType) {
    return routesMap.get(destinationType);
  }

  public void clearRoutesMap() {
    routesMap.clear();
  }

  private class RoutesMap {
    private final Map<Line, LineRoutesMap> map = new HashMap<>();

    List<Route> get(StationType destinationType) {
      return Station.this.lines.stream()
        .flatMap(line -> {
          if (line.isFindingRoutes()) {
            return Stream.empty();
          }
          LineRoutesMap lineRoutesMap = getLineRoutesMap(line);
          return lineRoutesMap.get(destinationType).stream();
        }).toList();
    }

    void clear() {
      map.clear();
    }

    private LineRoutesMap getLineRoutesMap(Line line) {
      return Optional.ofNullable(map.get(line)).orElseGet(() -> {
        LineRoutesMap lineRoutesMap = new LineRoutesMap(line);
        map.put(line, lineRoutesMap);
        return lineRoutesMap;
      });
    }

    private class LineRoutesMap {
      private final Line line;
      private final Map<StationType, List<Route>> map = new HashMap<>();

      LineRoutesMap(Line line) {
        this.line = line;
      }

      List<Route> get(StationType destinationType) {
        return Optional.ofNullable(map.get(destinationType)).orElseGet(() -> {
          List<Route> routes = line.findRoutes(destinationType, Station.this);
          map.put(destinationType, routes);
          return routes;
        });
      }
    }
  }
}
