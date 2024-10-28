package Elevator;

import org.junit.jupiter.api.*;
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;



/**
 * Test suite for the Elevator system
 * Uses JUnit 5 for testing core functionality and edge cases
 */
public class ElevatorTest {
    private Elevator elevator;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        // Simulate user input for destination floors
        String simulatedInput = "5\n3\n7\n";  // Multiple floors for different test cases
        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);
        
        elevator = new Elevator();
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
    }

    @Test
    @DisplayName("Test initial elevator state")
    void testInitialState() {
        assertEquals(0, elevator.getCurrentFloor());
        assertEquals(Direction.IDLE, elevator.getCurrentDirection());
        assertEquals(0, elevator.getCurrentPassengerCount());
    }

    @Test
    @DisplayName("Test single passenger pickup and dropoff")
    void testSinglePassengerJourney() {
        // Add request for floor 3 going up
        elevator.addRequest(3, Direction.UP);
        
        // Process the request
        elevator.processRequests();
        
        // Verify elevator moved to correct floor
        assertEquals(5, elevator.getCurrentFloor()); // 5 is from simulated input
        assertEquals(0, elevator.getCurrentPassengerCount());
    }

    @Test
    @DisplayName("Test invalid floor requests")
    void testInvalidFloorRequests() {
        // Simulate invalid destination input
        String invalidInput = "-1\n100\n5\n";  // Invalid floors followed by valid floor
        System.setIn(new ByteArrayInputStream(invalidInput.getBytes()));
        
        elevator.addRequest(2, Direction.UP);
        elevator.processRequests();
        
        // Verify elevator handled invalid input gracefully
        assertEquals(5, elevator.getCurrentFloor()); // Should end up at valid floor
    }

    @Test
    @DisplayName("Test empty request handling")
    void testEmptyRequests() {
        elevator.processRequests();
        assertEquals(Direction.IDLE, elevator.getCurrentDirection());
        assertEquals(0, elevator.getCurrentFloor());
    }

    @Test
    @DisplayName("Test multiple passenger pickup same floor")
    void testMultiplePassengersSameFloor() {
        // Add multiple requests from same floor
        elevator.addRequest(2, Direction.UP);
        elevator.addRequest(2, Direction.UP);
        
        elevator.processRequests();
        
        // Verify all passengers were handled
        assertEquals(0, elevator.getCurrentPassengerCount());
    }



    @Test
    @DisplayName("Test passenger boarding validation")
    void testPassengerBoardingValidation() {
        // Test passenger trying to go down when elevator is going up
        elevator.addRequest(2, Direction.DOWN);
        elevator.addRequest(5, Direction.UP);
        
        // The elevator should prioritize the up direction first
        assertEquals(Direction.UP, elevator.getCurrentDirection());
    }
}
