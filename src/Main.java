import model.service.ModelService;
import model.service.ModelServiceImpl;

import java.util.List;

enum StationType implements model.shared.StationType {
  CIRCLE, TRIANGLE
}

public class Main {
  public static void main(String[] args) {
    // can be used anywhere in the project
    var modelService = ModelServiceFactory.getInstance();

    // ☻ ▬ ☻ ▬ ☻ ▬ ☻ ▬ ▲
    //     ║   ┼
    //     ☻   ▲
    //     ║
    //     ▲

    GraphicalStation circleA0 = new GraphicalStation(StationType.CIRCLE);
    GraphicalStation circleA1 = new GraphicalStation(StationType.CIRCLE);
    GraphicalStation circleA2 = new GraphicalStation(StationType.CIRCLE);
    GraphicalStation circleA3 = new GraphicalStation(StationType.CIRCLE);
    GraphicalStation triangleA = new GraphicalStation(StationType.TRIANGLE);

    GraphicalStation circleB = new GraphicalStation(StationType.CIRCLE);
    GraphicalStation triangleB = new GraphicalStation(StationType.TRIANGLE);

    GraphicalStation triangleC = new GraphicalStation(StationType.TRIANGLE);

    List<GraphicalStation> stations = List.of(
      circleA0,
      circleA1,
      circleA2,
      circleA3,
      triangleA,
      circleB,
      triangleB,
      triangleC
    );
    for (GraphicalStation station : stations) {
      modelService.addStation(station, station.getType());
    }

    GraphicalLine lineA = new GraphicalLine();
    modelService.addLine(lineA);
    modelService.updateLine(lineA, List.of(circleA0, circleA1, circleA2, circleA3, triangleA));

    GraphicalLine lineB = new GraphicalLine();
    modelService.addLine(lineB);
    modelService.updateLine(lineB, List.of(circleA1, circleB, triangleB));

    GraphicalLine lineC = new GraphicalLine();
    modelService.addLine(lineC);
    modelService.updateLine(lineC, List.of(circleA2, triangleC));

    List<GraphicalStation> destinations = modelService.findDestinations(StationType.TRIANGLE, circleA0, circleA1);
    assert destinations.size() == 3;
    assert destinations.containsAll(List.of(circleA1, circleA2, triangleA));

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
