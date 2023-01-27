package model.service;

import model.core.Line;
import model.core.Route;
import model.core.Station;
import model.core.StationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModelServiceImpl<StationId, LineId> implements ModelService<StationId, LineId> {
  private final Map<Station, StationId> stationIdMap = new HashMap<>();
  private final Map<StationId, Station> stationMap = new HashMap<>();
  private final Map<LineId, Line> lineMap = new HashMap<>();

  private static <K, V> V getValue(Map<K, V> map, K key) {
    return Optional.ofNullable(map.get(key))
      .orElseThrow(() -> new RuntimeException(key + " does not exist"));
  }

  @Override
  public Optional<StationId> findDestination(StationType destinationType, StationId stationId, StationId nextStationId) {
    Station station = getValue(stationMap, stationId);
    Station nextStation = getValue(stationMap, nextStationId);
    for (Route route : station.getRoutes(destinationType)) {
      if (route.start() == nextStation) {
        StationId endStationId = getValue(stationIdMap, route.end());
        return Optional.of(endStationId);
      }
    }
    return Optional.empty();
  }

  @Override
  public void addStation(StationId id, StationType type) {
    if (stationMap.containsKey(id)) {
      throw new RuntimeException("station with id " + id + " already exists");
    }
    Station station = new Station(type);
    stationIdMap.put(station, id);
    stationMap.put(id, station);
  }

  @Override
  public void addLine(LineId id) {
    if (lineMap.containsKey(id)) {
      throw new RuntimeException("line with id " + id + " already exists");
    }
    lineMap.put(id, new Line());
  }

  @Override
  public void updateLine(LineId id, List<StationId> stationIds) {
    Line line = getValue(lineMap, id);
    List<Station> stations = stationIds.stream()
      .map(stationId -> getValue(stationMap, stationId))
      .toList();
    line.update(stations);
    stationMap.values().forEach(Station::clearRoutesMap);
  }

  @Override
  public void reset() {
    stationIdMap.clear();
    stationMap.clear();
    lineMap.clear();
  }
}
