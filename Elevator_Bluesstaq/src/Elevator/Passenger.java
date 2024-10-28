package Elevator;

public class Passenger {
    private final int id;
    private final int sourceFloor;
    private Integer destinationFloor;
    private final Direction direction;
    private boolean inElevator;

    public Passenger(int id, int sourceFloor, Direction direction) {
        this.id = id;
        this.sourceFloor = sourceFloor;
        this.direction = direction;
        this.inElevator = false;
    }

    public int getId() {
        return id;
    }

    public int getSourceFloor() {
        return sourceFloor;
    }

    public Integer getDestinationFloor() {
        return destinationFloor;
    }

    public void setDestinationFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isInElevator() {
        return inElevator;
    }

    public void board() {
        this.inElevator = true;
    }

    public void exit() {
        this.inElevator = false;
    }
}
