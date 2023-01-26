package model.service;

import model.core.StationType;

import java.util.List;
import java.util.Optional;

public interface ModelService<StationId, LineId> {
  void addStation(StationId id, StationType type);

  void addLine(LineId id);

  void updateLine(LineId id, List<StationId> stationIds);

  Optional<StationId> findDestination(StationType destinationType, StationId stationId, StationId nextStationId);
}
