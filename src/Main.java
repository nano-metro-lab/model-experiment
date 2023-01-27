import model.service.ModelService;
import model.service.ModelServiceImpl;

import java.util.List;

enum StationType implements model.core.StationType {
  CIRCLE, SQUARE, TRIANGLE
}

public class Main {
  public static void main(String[] args) {
    // can be used anywhere in the project
    var modelService = ModelServiceFactory.getInstance();

    GraphicalStation stationCircle = new GraphicalStation(StationType.CIRCLE);
    GraphicalStation stationSquare1 = new GraphicalStation(StationType.SQUARE);
    GraphicalStation stationSquare2 = new GraphicalStation(StationType.SQUARE);
    GraphicalStation stationTriangle1 = new GraphicalStation(StationType.TRIANGLE);
    GraphicalStation stationTriangle2 = new GraphicalStation(StationType.TRIANGLE);

    List<GraphicalStation> stations = List.of(stationCircle, stationSquare1, stationSquare2, stationTriangle1, stationTriangle2);
    for (GraphicalStation station : stations) {
      modelService.addStation(station, station.getType());
    }

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

    // when starts a new game
    modelService.reset();
  }
}

@SuppressWarnings("ALL")
class GraphicalStation {
  private final StationType type;
  private Object whateverAttribute;

  public GraphicalStation(StationType type) {
    this.type = type;
  }

  public StationType getType() {
    return type;
  }

  public void whateverMethod() {
  }
}

@SuppressWarnings("ALL")
class GraphicalLine {
  private Object whateverAttribute;

  public void whateverMethod() {
  }
}

class ModelServiceFactory {
  private static ModelService<GraphicalStation, GraphicalLine> instance;

  public static ModelService<GraphicalStation, GraphicalLine> getInstance() {
    if (instance == null) {
      instance = new ModelServiceImpl<>();
    }
    return instance;
  }
}
