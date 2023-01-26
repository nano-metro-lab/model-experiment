package model.service;

import model.core.Line;
import model.core.Station;
import model.core.StationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModelServiceProvider<StationId, LineId> implements ModelService<StationId, LineId> {
  private final Map<StationId, Station> stationMap = new HashMap<>();
  private final Map<LineId, Line> lineMap = new HashMap<>();

  private static <K, V> V get(Map<K, V> map, K key) {
    return Optional.ofNullable(map.get(key))
      .orElseThrow(() -> new IllegalArgumentException("key " + key + " does not exist"));
  }

  private static <K, V> K getKey(Map<K, V> map, V value) {
    for (Map.Entry<K, V> entry : map.entrySet()) {
      if (entry.getValue().equals(value)) {
        return entry.getKey();
      }
    }
    throw new IllegalArgumentException("value " + value + " does not exist");
  }

  @Override
  public void addStation(StationId id, StationType type) {
    if (stationMap.containsKey(id)) {
      throw new IllegalArgumentException("station with id " + id + " already exists");
    }
    stationMap.put(id, new Station(type));
  }

  @Override
  public void addLine(LineId id) {
    if (lineMap.containsKey(id)) {
      throw new IllegalArgumentException("line with id " + id + " already exists");
    }
    lineMap.put(id, new Line());
  }

  @Override
  public void updateLine(LineId id, List<StationId> stationIds) {
    Line line = getLine(id);
    List<Station> stations = stationIds.stream().map(ModelServiceProvider.this::getStation).toList();
    line.update(stations);
    stationMap.values().forEach(Station::clearRoutesMap);
  }

  @Override
  public Optional<ModelService.Route<StationId>> findRoute(StationType destination, StationId stationId, StationId nextStationId) {
    Station station = getStation(stationId);
    Station nextStation = getStation(nextStationId);
    for (var route : station.getRoutes(destination)) {
      if (route.start() == nextStation) {
        StationId endStationId = getKey(stationMap, route.end());
        return Optional.of(new Route<>(nextStationId, endStationId));
      }
    }
    return Optional.empty();
  }

  private Line getLine(LineId id) {
    return get(lineMap, id);
  }

  private Station getStation(StationId id) {
    return get(stationMap, id);
  }

  private record Route<TStationId>(
    TStationId start,
    TStationId end
  ) implements ModelService.Route<TStationId> {
  }
}
