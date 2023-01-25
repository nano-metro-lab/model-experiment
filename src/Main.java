import model.Line;
import model.Passenger;
import model.Station;
import model.StationType;

public class Main {
  public static void main(String[] args) {
    Station stationCircle = new Station(StationType.CIRCLE);
    Station stationSquare = new Station(StationType.SQUARE);
    Station stationTriangle1 = new Station(StationType.TRIANGLE);
    Station stationTriangle2 = new Station(StationType.TRIANGLE);

    Passenger passengerTriangle = new Passenger(StationType.TRIANGLE);

    Line lineCircleToSquare = new Line();
    lineCircleToSquare.addStation(stationCircle);
    lineCircleToSquare.addStation(stationSquare, stationCircle);

    Line lineSquareToTriangle1 = new Line();
    lineSquareToTriangle1.addStation(stationSquare);
    lineSquareToTriangle1.addStation(stationTriangle1, stationSquare);

    Line lineSquareToTriangle2 = new Line();
    lineSquareToTriangle2.addStation(stationSquare);
    lineSquareToTriangle2.addStation(stationTriangle2, stationSquare);
  }
}
