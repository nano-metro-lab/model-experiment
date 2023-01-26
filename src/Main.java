import model.Line;
import model.Passenger;
import model.Station;

import java.util.List;

public class Main {
  public static void main(String[] args) {
    Station stationCircle = new Station(StationType.CIRCLE);
    Station stationSquare = new Station(StationType.SQUARE);
    Station stationTriangle1 = new Station(StationType.TRIANGLE);
    Station stationTriangle2 = new Station(StationType.TRIANGLE);

    Passenger passengerTriangle = new Passenger(StationType.TRIANGLE);

    Line lineCircleToSquare = new Line();
    lineCircleToSquare.update(List.of(stationCircle, stationSquare));

    Line lineSquareToTriangle1 = new Line();
    lineSquareToTriangle1.update(List.of(stationSquare, stationTriangle1));

    Line lineSquareToTriangle2 = new Line();
    lineSquareToTriangle2.update(List.of(stationSquare, stationTriangle2));
  }

  private enum StationType implements model.StationType {
    CIRCLE, SQUARE, TRIANGLE
  }
}
