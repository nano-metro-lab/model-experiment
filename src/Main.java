import model.*;

public class Main {
  public static void main(String[] args) {
    Station stationCircle = new Station(StationType.CIRCLE);
    Station stationSquare = new Station(StationType.SQUARE);
    Station stationTriangle1 = new Station(StationType.TRIANGLE);
    Station stationTriangle2 = new Station(StationType.TRIANGLE);

    new Passenger(stationCircle, StationType.TRIANGLE);

    Line lineCircleToSquare = new Line();
    lineCircleToSquare.addStartStation(stationCircle);
    lineCircleToSquare.addEndStation(stationSquare);

    Line lineSquareToTriangle1 = new Line();
    lineSquareToTriangle1.addStartStation(stationSquare);
    lineSquareToTriangle1.addEndStation(stationTriangle1);

    Line lineSquareToTriangle2 = new Line();
    lineSquareToTriangle2.addStartStation(stationSquare);
    lineSquareToTriangle2.addEndStation(stationTriangle2);

    Train trainCircleToSquare = new Train(lineCircleToSquare, stationCircle);
    Train trainSquareToTriangle1 = new Train(lineSquareToTriangle1, stationSquare);
    Train trainSquareToTriangle2 = new Train(lineSquareToTriangle2, stationSquare);

    trainCircleToSquare.start();
    assert stationCircle.getPassengers().length == 0;
    assert trainCircleToSquare.getPassengers().length > 0;

    trainCircleToSquare.stop();
    assert trainCircleToSquare.getPassengers().length == 0;
    assert stationSquare.getPassengers().length > 0;

    trainSquareToTriangle1.start();
    assert stationSquare.getPassengers().length == 0;
    assert trainSquareToTriangle1.getPassengers().length > 0;

    trainSquareToTriangle1.stop();
    assert trainSquareToTriangle1.getPassengers().length == 0;
    assert stationSquare.getPassengers().length == 0;

    trainCircleToSquare.start();
    trainCircleToSquare.stop();

    new Passenger(stationCircle, StationType.TRIANGLE);

    trainCircleToSquare.start();
    trainCircleToSquare.stop();

    trainSquareToTriangle2.start();
    assert trainSquareToTriangle2.getPassengers().length > 0;

    trainSquareToTriangle2.stop();
    assert trainSquareToTriangle2.getPassengers().length == 0;
  }
}
