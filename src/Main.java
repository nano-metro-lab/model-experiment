import model.service.ModelService;
import model.service.ModelServiceProvider;

import java.util.ArrayList;
import java.util.List;

public class Main {
  private static final ModelService<GraphicalStation, GraphicalLine> modelService = new ModelServiceProvider<>();

  public static void main(String[] args) {
    GraphicalStation stationCircle = new GraphicalStation(StationType.CIRCLE);
    GraphicalStation stationSquare1 = new GraphicalStation(StationType.SQUARE);
    GraphicalStation stationSquare2 = new GraphicalStation(StationType.SQUARE);
    GraphicalStation stationTriangle1 = new GraphicalStation(StationType.TRIANGLE);
    GraphicalStation stationTriangle2 = new GraphicalStation(StationType.TRIANGLE);

    List<GraphicalStation> stations = new ArrayList<>(List.of(stationCircle, stationSquare1, stationSquare2, stationTriangle1, stationTriangle2));
    stations.forEach(station -> modelService.addStation(station, station.getType()));

    GraphicalLine lineCircleToSquare = new GraphicalLine();
    modelService.addLine(lineCircleToSquare);
    modelService.updateLine(lineCircleToSquare, List.of(stationCircle, stationSquare1, stationSquare2));

    GraphicalLine lineSquare2ToTriangle1 = new GraphicalLine();
    modelService.addLine(lineSquare2ToTriangle1);
    modelService.updateLine(lineSquare2ToTriangle1, List.of(stationSquare2, stationTriangle1));

    GraphicalLine lineSquare2ToTriangle2 = new GraphicalLine();
    modelService.addLine(lineSquare2ToTriangle2);
    modelService.updateLine(lineSquare2ToTriangle2, List.of(stationSquare2, stationTriangle2));

    GraphicalStation destination = modelService.findDestination(StationType.TRIANGLE, stationCircle, stationSquare1).orElseThrow();
    assert destination == stationSquare2;
  }

  private enum StationType implements model.core.StationType {
    CIRCLE, SQUARE, TRIANGLE
  }

  @SuppressWarnings("ClassCanBeRecord")
  private static class GraphicalStation {
    private final StationType type;

    GraphicalStation(StationType type) {
      this.type = type;
    }

    StationType getType() {
      return type;
    }

    @SuppressWarnings("unused")
    void render() {
    }
  }

  private static class GraphicalLine {
    @SuppressWarnings("unused")
    void render() {
    }
  }
}
