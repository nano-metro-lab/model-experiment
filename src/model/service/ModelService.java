package model.service;

import model.shared.StationType;

import java.util.List;

public interface ModelService<StationId, LineId> {
  List<StationId> findDestinations(StationType destinationType, StationId stationId, StationId nextStationId);

  void addStation(StationId id, StationType type);

  void addLine(LineId id);

  void updateLine(LineId id, List<StationId> stationIds);

  void reset();
}
