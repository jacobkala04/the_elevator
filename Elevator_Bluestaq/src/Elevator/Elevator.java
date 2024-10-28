package Elevator;

import java.util.*;

/**
 * A class that simulates an elevator system with passenger pickup and dropoff.
 * Implements a basic elevator algorithm that handles multiple passenger requests
 * while maintaining direction and capacity constraints.
 */

public class Elevator {
    private static final int MAX_CAPACITY = 8; // Assume max capacity is 8 people
    private int currentFloor;
    private Direction currentDirection;
    private final List<Passenger> passengers; // List of all the passengers
    private final Set<Integer> stopFloors;    // Set of all floors where the elevator needs to stop
    private int nextPassengerId;              // Generates passanger ids
    private final Scanner scanner;            // Reads user input (used for destination)

    public Elevator() {
        this.currentFloor = 0; // Assume start at ground floor
        this.currentDirection = Direction.IDLE;
        this.passengers = new ArrayList<>();
        this.stopFloors = new TreeSet<>(); // TreeSet maintains sorted order of floors
        this.nextPassengerId = 1;
        this.scanner = new Scanner(System.in);
    }
    /**
     * Adds a new passenger request to the elevator system
     * @param sourceFloor Floor where passenger is waiting
     * @param direction Direction passenger wants to travel
     */
    public void addRequest(int sourceFloor, Direction direction) {
        Passenger passenger = new Passenger(nextPassengerId++, sourceFloor, direction);
        passengers.add(passenger);
        stopFloors.add(sourceFloor);
        
        System.out.println("New request added: Passenger #" + passenger.getId() + 
                          " waiting at floor " + sourceFloor + " going " + direction);

         // If elevator is idle, determine initial direction and handle immediate pickups
        if (currentDirection == Direction.IDLE) {
            currentDirection = determineInitialDirection();
            // Handle case where passenger is on same floor as idle elevator
            if (sourceFloor == currentFloor) {
                handlePassengersAtCurrentFloor(direction);
                updateStopFloors();
            }
        }
    }
    /**
     * Returns the current floor number
     */
    public int getCurrentFloor() {
        return currentFloor;
    }
    /**
     * Returns the current direction of the elevator
     */
    public Direction getCurrentDirection() {
        return currentDirection;
    }
    /**
     * Returns the current number of passengers inside the elevator
     */
    public int getCurrentPassengerCount() {
        return (int) passengers.stream().filter(Passenger::isInElevator).count();
    }
    /**
     * Main method for processing all pending elevator requests
     * Implements elevator movement logic and handles direction changes
     */
    public void processRequests() {
        if (stopFloors.isEmpty()) {
            System.out.println("No requests to process.");
            return;
        }
        // Continue processing until all requests are handled
        while (!stopFloors.isEmpty()) {
            //Handle special case when elevator was idle
            if (currentDirection == Direction.IDLE) {
                handlePassengersAtCurrentFloor(determineInitialDirection());
                updateStopFloors();
                if (stopFloors.isEmpty()) break;
                currentDirection = determineInitialDirection();
            }
            // Implement "sweep" algorithm: go all the way up, then all the way down
            if (currentDirection == Direction.UP) {
                processUpRequests();
                if (!stopFloors.isEmpty()) {
                    currentDirection = Direction.DOWN;
                    processDownRequests();
                }
            } else {
                processDownRequests();
                if (!stopFloors.isEmpty()) {
                    currentDirection = Direction.UP;
                    processUpRequests();
                }
            }
        }

        currentDirection = Direction.IDLE;
        System.out.println("All requests processed.");
    }
     /**
     * Processes all requests for upward movement
     * Continues moving up until no more upward requests exist
     */
    private void processUpRequests() {
        Integer nextFloor;
        while ((nextFloor = getNextUpFloor()) != null) {
            moveToFloor(nextFloor);
            handlePassengersAtCurrentFloor(Direction.UP);
            updateStopFloors();
        }
    }

    /**
     * Processes all requests for downward movement
     * Continues moving down until no more downward requests exist
     */
    private void processDownRequests() {
        Integer nextFloor;
        while ((nextFloor = getNextDownFloor()) != null) {
            moveToFloor(nextFloor);
            handlePassengersAtCurrentFloor(Direction.DOWN);
            updateStopFloors();
        }
    }
    
    /**
     * Handles passenger boarding and exiting at the current floor
     * Key considerations:
     * 1. Exits happen before boarding to free up capacity
     * 2. Boarding only happens if passenger is going in current direction
     * 3. Respects maximum capacity constraints
     * 4. Validates that destination matches current direction
     */
    private void handlePassengersAtCurrentFloor(Direction direction) {
        boolean doorOpened = false;

        // Handle exits first to make room
        List<Passenger> passengersToRemove = new ArrayList<>();
        for (Passenger passenger : passengers) {
            if (passenger.isInElevator() && passenger.getDestinationFloor() == currentFloor) {
                if (!doorOpened) {
                    System.out.println("  🚪   Doors opening at floor " + currentFloor);
                    doorOpened = true;
                }
                System.out.println("  👤   Passenger #" + passenger.getId() + " exiting at floor " + currentFloor);
                passenger.exit();
                passengersToRemove.add(passenger);
            }
        }
        passengers.removeAll(passengersToRemove);
    
        // Handle boarding if there's capacity
        for (Passenger passenger : passengers) {
            if (!passenger.isInElevator() && 
                passenger.getSourceFloor() == currentFloor && 
                passenger.getDirection() == direction) {
    
                if (getCurrentPassengerCount() >= MAX_CAPACITY) {
                    System.out.println("  ⚠️   Elevator at maximum capacity. Cannot board Passenger #" + 
                                     passenger.getId() + " at this time.");
                    continue;
                }
    
                if (!doorOpened) {
                    System.out.println("  🚪   Doors opening at floor " + currentFloor);
                    doorOpened = true;
                }
    
                System.out.println("  👤   Passenger #" + passenger.getId() + " boarding at floor " + currentFloor);
                System.out.print("Enter destination floor for Passenger #" + passenger.getId() + ": ");
                int destinationFloor = scanner.nextInt();
                
                // Validate destination matches direction
                if ((direction == Direction.UP && destinationFloor <= currentFloor) ||
                    (direction == Direction.DOWN && destinationFloor >= currentFloor)) {
                    System.out.println("  ⚠️   Invalid destination floor for current direction. Passenger must wait.");
                    continue;
                }
    
                // Update destination and board passenger
                passenger.setDestinationFloor(destinationFloor);
                passenger.board();
                stopFloors.add(destinationFloor);
                
                System.out.println("      Passenger #" + passenger.getId() + " going to floor " + destinationFloor);
                System.out.println("      Current passengers in elevator: " + getCurrentPassengerCount() + "/" + MAX_CAPACITY);
            }
        }
    
        if (doorOpened) {
            System.out.println("  🚪   Doors closing");
        }
    }

    private void updateStopFloors() {
        stopFloors.remove(currentFloor);
        
        Set<Integer> newStops = new TreeSet<>();
        for (Passenger passenger : passengers) {
            if (!passenger.isInElevator()) {
                newStops.add(passenger.getSourceFloor());
            }
            if (passenger.isInElevator() && passenger.getDestinationFloor() != null) {
                newStops.add(passenger.getDestinationFloor());
            }
        }
        stopFloors.clear();
        stopFloors.addAll(newStops);
    }

    /**
     * Finds the next floor to stop at when moving up
     * @return The next highest floor with a stop, or null if none exists
     */
    private Integer getNextUpFloor() {
        return stopFloors.stream()
            .filter(floor -> floor > currentFloor)
            .min(Integer::compareTo)
            .orElse(null);
    }

    /**
     * Finds the next floor to stop at when moving down
     * @return The next lowest floor with a stop, or null if none exists
     */
    private Integer getNextDownFloor() {
        return stopFloors.stream()
            .filter(floor -> floor < currentFloor)
            .max(Integer::compareTo)
            .orElse(null);
    }

    /**
     * Moves the elevator to the target floor, printing movement indicators
     * @param targetFloor The floor to move to
     */
    private void moveToFloor(int targetFloor) {
        while (currentFloor != targetFloor) {
            if (currentFloor < targetFloor) {
                currentFloor++;
                System.out.println("   ↑   Now at floor " + currentFloor);
            } else {
                currentFloor--;
                System.out.println("   ↓   Now at floor " + currentFloor);
            }
        }
    }

    /**
     * Determines initial direction when elevator is idle
     * Key logic points:
     * 1. If there are requests above and below, prioritize going up
     * 2. If only requests in one direction, go that direction
     * 3. If on a floor with requests, look at other requests to determine direction
     */
    private Direction determineInitialDirection() {
        if (stopFloors.isEmpty()) {
            return Direction.IDLE;
        }
    
        // Check for requests above and below current floor
        boolean hasFloorsAbove = false;
        boolean hasFloorsBelow = false;
        
        for (Integer floor : stopFloors) {
            if (floor > currentFloor) hasFloorsAbove = true;
            if (floor < currentFloor) hasFloorsBelow = true;
        }
        
        // Special case: if we're on a floor with a request
        if (stopFloors.contains(currentFloor)) {
            if (hasFloorsAbove) return Direction.UP;
            if (hasFloorsBelow) return Direction.DOWN;
            return Direction.UP;  // Default direction if only current floor has requests
        }
        
        // Standard direction determination
        if (hasFloorsAbove) return Direction.UP;
        return Direction.DOWN;
    }
}
