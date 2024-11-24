package org.carrental.main;

import org.carrental.dao.*;
import org.carrental.entity.*;

import java.util.Date;
import java.util.Scanner;

public class CarRentalSystem {

    public static void main(String[] args) {
        ICarLeaseRepositoryImpl repository = new ICarLeaseRepositoryImpl();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Car Rental System!");
        while (true) {
            System.out.println("\nCar Rental System Menu:");
            System.out.println("1. Add Car");
            System.out.println("2. Remove Car");
            System.out.println("3. List Available Cars");
            System.out.println("4. Add Customer");
            System.out.println("5. Remove Customer");
            System.out.println("6. List Customers");
            System.out.println("7. Create Lease");
            System.out.println("8. List Active Leases");
            System.out.println("9. List Lease History");
            System.out.println("10. Record Payment");
            System.out.println("11. Find Customer By Id");
            System.out.println("12. Exit");
            System.out.print("Choose an option: ");
            
            int option;
            try {
                option = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
                continue;
            }
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1: // Add Car
                    System.out.println("Enter Car Details (ID, Make, Model, Year, Rate, Status, Pass. Capacity, Engine Cap):");
                    int carId = scanner.nextInt();
                    String make = scanner.next();
                    String model = scanner.next();
                    int year = scanner.nextInt();
                    double rate = scanner.nextDouble();
                    String status = scanner.next();
                    int capacity = scanner.nextInt();
                    double engine = scanner.nextDouble();
                    Car car = new Car(carId, make, model, year, rate, status, capacity, engine);
                    try {
                        repository.addCar(car);
                        System.out.println("Car added successfully.");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 2: // Remove Car
                    System.out.println("Enter Car ID to remove:");
                    int removeCarId = scanner.nextInt();
                    try {
                        repository.removeCar(removeCarId);
                        System.out.println("Car removed successfully.");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 3: // List Available Cars
                    System.out.println("Available Cars:");
                    repository.listAvailableCars().forEach(c -> System.out.println(c.getMake() + " " + c.getModel()));
                    break;

                case 4: // Add Customer
                    System.out.println("Enter Customer Details (ID, First Name, Last Name, Email, Phone):");
                    int customerId = scanner.nextInt();
                    String firstName = scanner.next();
                    String lastName = scanner.next();
                    String email = scanner.next();
                    String phone = scanner.next();
                    Customer customer = new Customer(customerId, firstName, lastName, email, phone);
                    try {
                        repository.addCustomer(customer);
                        System.out.println("Customer added successfully.");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 5: // Remove Customer
                    System.out.println("Enter Customer ID to remove:");
                    int removeCustomerId = scanner.nextInt();
                    try {
                        repository.removeCustomer(removeCustomerId);
                        System.out.println("Customer removed successfully.");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 6: // List Customers
                    System.out.println("List of Customers:");
                    repository.listCustomers().forEach(c -> System.out.println(c.getFirstName() + " " + c.getLastName()));
                    break;

                case 7: // Create Lease
                    System.out.println("Enter Lease Details (Customer ID, Car ID, Start Date (yyyy-mm-dd), End Date (yyyy-mm-dd)):");
                    int leaseCustomerId = scanner.nextInt();
                    int leaseCarId = scanner.nextInt();
                    String startDateInput = scanner.next();
                    String endDateInput = scanner.next();
                    try {
                        Date startDate = java.sql.Date.valueOf(startDateInput);
                        Date endDate = java.sql.Date.valueOf(endDateInput);
                        Lease lease = repository.createLease(leaseCustomerId, leaseCarId, startDate, endDate);
                        System.out.println("Lease created successfully. Lease ID: " + lease.getLeaseID());
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 8: // List Active Leases
                    System.out.println("Active Leases:");
                    repository.listActiveLeases().forEach(l -> System.out.println("Lease ID: " + l.getLeaseID() + ", Car ID: " + l.getVehicleID()));
                    break;

                case 9: // List Lease History
                    System.out.println("Lease History:");
                    repository.listLeaseHistory().forEach(l -> System.out.println("Lease ID: " + l.getLeaseID() + ", Customer ID: " + l.getCustomerID()+", Start Date : " + l.getStartDate() +", End Date: " + l.getEndDate()));
                    break;

                case 10: // Record Payment
                    System.out.println("Enter Payment Details (Lease ID, Amount):");
                    int paymentLeaseId = scanner.nextInt();
                    double amount = scanner.nextDouble();
                    try {
                        repository.recordPayment(paymentLeaseId, amount);
                        System.out.println("Payment recorded successfully.");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 11: // Record Payment
                    System.out.println("Enter CustomerID to find :");
                    int customerId1 = scanner.nextInt();
                    try {
                        repository.findCustomerById(customerId1);
                        System.out.println("This is the Customer with ."+customerId1);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;


                case 12: // Exit
                    System.out.println("Thanks For Using Our Car Rental System!!!");
                    scanner.close();
                    repository.closeConnection();
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
