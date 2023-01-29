package model.service;

import model.core.Line;
import model.core.Station;
import model.core.StationType;

import java.util.List;

public class ModelServiceImpl<StationId, LineId> implements ModelService<StationId, LineId> {
  private final Dao<StationId, Station> stationDao = new DaoImpl<>();
  private final Dao<LineId, Line> lineDao = new DaoImpl<>();

  @Override
  public List<StationId> findDestinations(StationType destinationType, StationId stationId, StationId nextStationId) {
    Station station = stationDao.get(stationId);
    Station nextStation = stationDao.get(nextStationId);
    return station.getRoutes(destinationType)
      .filter(route -> route.next() == nextStation)
      .map(route -> stationDao.getId(route.last()))
      .toList();
  }

  @Override
  public void addStation(StationId id, StationType type) {
    stationDao.add(id, new Station(type));
  }

  @Override
  public void addLine(LineId id) {
    lineDao.add(id, new Line());
  }

  @Override
  public void updateLine(LineId id, List<StationId> stationIds) {
    Line line = lineDao.get(id);
    line.update(stationDao.getAll(stationIds));
    for (Station station : stationDao.getAll()) {
      station.clearRoutesMap();
    }
  }

  @Override
  public void reset() {
    stationDao.deleteAll();
    lineDao.deleteAll();
  }
}
