package org.carrental.dao;

import org.carrental.entity.*;
import org.carrental.exception.*;
import org.carrental.util.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ICarLeaseRepositoryImpl implements ICarLeaseRepository {

    private final Connection connection;

    public ICarLeaseRepositoryImpl() {
        this.connection = DBConnUtil.getConnection();
    }

    // ----------------- Car Management -----------------

    @Override
    public void addCar(Car car) {
        String query = "INSERT INTO Vehicle (vehicleID, make, model, year, dailyRate, status, passengerCapacity, engineCapacity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, car.getVehicleID());
            stmt.setString(2, car.getMake());
            stmt.setString(3, car.getModel());
            stmt.setInt(4, car.getYear());
            stmt.setDouble(5, car.getDailyRate());
            stmt.setString(6, car.getStatus());
            stmt.setInt(7, car.getPassengerCapacity());
            stmt.setDouble(8, car.getEngineCapacity());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding car: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeCar(int carID) {
        String query = "DELETE FROM Vehicle WHERE vehicleID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, carID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new CarNotFoundException("Car with ID " + carID + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error removing car: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Car> listAvailableCars() {
        String query = "SELECT * FROM Vehicle WHERE status = 'available'";
        List<Car> cars = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                cars.add(extractCarFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching available cars: " + e.getMessage(), e);
        }
        return cars;
    }

    @Override
    public List<Car> listRentedCars() {
        String query = "SELECT * FROM Vehicle WHERE status = 'notAvailable'";
        List<Car> cars = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                cars.add(extractCarFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching rented cars: " + e.getMessage(), e);
        }
        return cars;
    }

    @Override
    public Car findCarById(int carID) {
        String query = "SELECT * FROM Vehicle WHERE vehicleID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, carID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCarFromResultSet(rs);
                } else {
                    throw new CarNotFoundException("Car with ID " + carID + " not found.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding car by ID: " + e.getMessage(), e);
        }
    }

    // ----------------- Customer Management -----------------

    @Override
    public void addCustomer(Customer customer) {
        String query = "INSERT INTO Customer (customerID, firstName, lastName, email, phoneNumber) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customer.getCustomerID());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getPhoneNumber());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding customer: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeCustomer(int customerID) {
        String query = "DELETE FROM Customer WHERE customerID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new CustomerNotFoundException("Customer with ID " + customerID + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error removing customer: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Customer> listCustomers() {
        String query = "SELECT * FROM Customer";
        List<Customer> customers = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing customers: " + e.getMessage(), e);
        }
        return customers;
    }

    @Override
    public Customer findCustomerById(int customerID) {
        String query = "SELECT * FROM Customer WHERE customerID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCustomerFromResultSet(rs);
                } else {
                    throw new CustomerNotFoundException("Customer with ID " + customerID + " not found.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding customer by ID: " + e.getMessage(), e);
        }
    }

    // ----------------- Lease Management -----------------

    @Override
    public Lease createLease(int customerID, int carID, Date startDate, Date endDate) {
        String query = "INSERT INTO Lease (customerID, vehicleID, startDate, endDate, type) VALUES (?, ?, ?, ?, 'daily')";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, customerID);
            stmt.setInt(2, carID);
            stmt.setDate(3, new java.sql.Date(startDate.getTime()));
            stmt.setDate(4, new java.sql.Date(endDate.getTime()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int leaseID = rs.getInt(1);
                    return new Lease(leaseID, carID, customerID, startDate, endDate, "daily");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating lease: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void returnCar(int leaseID) {
        // Fetch the lease details using the leaseID
        String queryLease = "SELECT vehicleID FROM Lease WHERE leaseID = ?";
        try (PreparedStatement stmtLease = connection.prepareStatement(queryLease)) {
            stmtLease.setInt(1, leaseID);
            ResultSet rsLease = stmtLease.executeQuery();
            if (rsLease.next()) {
                int vehicleID = rsLease.getInt("vehicleID");

                // Mark the car as available
                String queryUpdateVehicle = "UPDATE Vehicle SET status = 'available' WHERE vehicleID = ?";
                try (PreparedStatement stmtVehicle = connection.prepareStatement(queryUpdateVehicle)) {
                    stmtVehicle.setInt(1, vehicleID);
                    stmtVehicle.executeUpdate();
                }

                // Optionally, update lease status or remove lease record (if business logic dictates)
                String queryUpdateLease = "UPDATE Lease SET status = 'completed' WHERE leaseID = ?";
                try (PreparedStatement stmtLeaseUpdate = connection.prepareStatement(queryUpdateLease)) {
                    stmtLeaseUpdate.setInt(1, leaseID);
                    stmtLeaseUpdate.executeUpdate();
                }

                System.out.println("Car returned successfully.");
            } else {
                System.out.println("Lease ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error returning car: " + e.getMessage());
        }
    }

    @Override
    public List<Lease> listActiveLeases() {
        List<Lease> activeLeases = new ArrayList<>();
        String query = "SELECT * FROM Lease ";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Lease lease = new Lease(
                    rs.getInt("leaseID"),
                    rs.getInt("vehicleID"),
                    rs.getInt("customerID"),
                    rs.getDate("startDate"),
                    rs.getDate("endDate"),
                    rs.getString("type")
                );
                activeLeases.add(lease);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching active leases: " + e.getMessage());
        }
        return activeLeases;
    }

    @Override
    public List<Lease> listLeaseHistory() {
        List<Lease> leaseHistory = new ArrayList<>();
        String query = "SELECT * FROM Lease ";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Lease lease = new Lease(
                    rs.getInt("leaseID"),
                    rs.getInt("vehicleID"),
                    rs.getInt("customerID"),
                    rs.getDate("startDate"),
                    rs.getDate("endDate"),
                    rs.getString("type")
                );
                leaseHistory.add(lease);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching lease history: " + e.getMessage());
        }
        return leaseHistory;
    }


    @Override
    public void recordPayment(int leaseID, double amount) {
        String query = "INSERT INTO Payment (leaseID, paymentDate, amount) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, leaseID);
            stmt.setDate(2, new java.sql.Date(System.currentTimeMillis())); 
            stmt.setDouble(3, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error recording payment: " + e.getMessage(), e);
        }
    }

    private Car extractCarFromResultSet(ResultSet rs) throws SQLException {
        return new Car(
                rs.getInt("vehicleID"),
                rs.getString("make"),
                rs.getString("model"),
                rs.getInt("year"),
                rs.getDouble("dailyRate"),
                rs.getString("status"),
                rs.getInt("passengerCapacity"),
                rs.getDouble("engineCapacity")
        );
    }

    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("customerID"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("email"),
                rs.getString("phoneNumber")
        );
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(">> Database connection closed Successfully <<.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection: " + e.getMessage());
        }
    }
}
