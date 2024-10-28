package Elevator;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Elevator elevator = new Elevator();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        System.out.println("Welcome to the Elevator System!");

        while (!exit) {
            try {
                System.out.println("\nChoose an option:");
                System.out.println("1. Request elevator");
                System.out.println("2. Process all requests");
                System.out.println("3. Exit");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.print("Enter your source floor: ");
                        int sourceFloor = scanner.nextInt();
                        System.out.print("Enter direction (1 for UP, 2 for DOWN): ");
                        int dirChoice = scanner.nextInt();
                        
                        Direction direction;
                        if (dirChoice == 1) {
                            direction = Direction.UP;
                        } else if (dirChoice == 2) {
                            direction = Direction.DOWN;
                        } else {
                            System.out.println("Invalid direction choice. Please try again.");
                            break;
                        }
                        
                        elevator.addRequest(sourceFloor, direction);
                        break;

                    case 2:
                        System.out.println("Processing all requests...");
                        elevator.processRequests();
                        break;

                    case 3:
                        System.out.println("Exiting the Elevator System.");
                        exit = true;
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: Invalid input. Please try again.");
                scanner.nextLine(); // Clear the scanner buffer
            }
        }

        scanner.close();
    }
}
