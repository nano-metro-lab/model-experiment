package model.service;

import model.core.Line;
import model.core.Route;
import model.core.Station;
import model.core.StationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModelServiceProvider<StationId, LineId> implements ModelService<StationId, LineId> {
  private final Map<StationId, Station> stationMap = new HashMap<>();
  private final Map<LineId, Line> lineMap = new HashMap<>();

  @Override
  public Optional<StationId> findDestination(StationType destinationType, StationId stationId, StationId nextStationId) {
    Station station = getStation(stationId);
    Station nextStation = getStation(nextStationId);
    for (Route route : station.getRoutes(destinationType)) {
      if (route.start() == nextStation) {
        StationId endStationId = ModelServiceUtils.getKey(stationMap, route.end());
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
    stationMap.put(id, new Station(type));
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
    Line line = getLine(id);
    List<Station> stations = stationIds.stream().map(ModelServiceProvider.this::getStation).toList();
    line.update(stations);
    stationMap.values().forEach(Station::clearRoutesMap);
  }

  private Line getLine(LineId id) {
    return ModelServiceUtils.getValue(lineMap, id);
  }

  private Station getStation(StationId id) {
    return ModelServiceUtils.getValue(stationMap, id);
  }
}
