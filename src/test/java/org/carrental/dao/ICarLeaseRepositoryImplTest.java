package org.carrental.dao;

import org.carrental.entity.*;
import org.carrental.exception.CarNotFoundException;
import org.carrental.exception.CustomerNotFoundException;
import org.carrental.util.DBConnUtil;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) 
public class ICarLeaseRepositoryImplTest {

    private static Connection connection;
    private static ICarLeaseRepositoryImpl repository;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        connection = DBConnUtil.getConnection();
        repository = new ICarLeaseRepositoryImpl();
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Clear tables before each test
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM Payment");
            stmt.execute("DELETE FROM Lease");
            stmt.execute("DELETE FROM Vehicle");
            stmt.execute("DELETE FROM Customer");

      
            stmt.execute("INSERT INTO Customer (customerID, firstName, lastName, email, phoneNumber) VALUES (1, 'John', 'Doe', 'john.doe@example.com', '1234567890')");
            stmt.execute("INSERT INTO Vehicle (vehicleID, make, model, year, dailyRate, status, passengerCapacity, engineCapacity) VALUES (1, 'Toyota', 'Camry', 2022, 50.00, 'available', 5, 2.5)");
        }
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // ------------------ Test Cases ------------------

    @Test
    @Order(1)
    public void testAddCar() throws Exception {
        Car car = new Car(2, "Honda", "Civic", 2023, 60.00, "available", 5, 2.0);
        repository.addCar(car);

        // Verify the car is added
        Car retrievedCar = repository.findCarById(2);
        assertNotNull(retrievedCar);
        assertEquals("Honda", retrievedCar.getMake());
    }

    @Test
    @Order(2)
    public void testRemoveCustomer() throws Exception {
        repository.removeCustomer(1);

        // Verify the customer is removed
        assertThrows(CustomerNotFoundException.class, () -> repository.findCustomerById(1));
    }

    @Test
    @Order(3)
    public void testCreateLease() throws Exception {
        Lease lease = repository.createLease(1, 1, new java.sql.Date(System.currentTimeMillis()), new java.sql.Date(System.currentTimeMillis() + 86400000));

        // Verify the lease is created
        assertNotNull(lease);
        assertEquals(1, lease.getCustomerID());
        assertEquals(1, lease.getVehicleID());
    }

    @Test
    @Order(4)
    public void testListLeaseHistory() throws Exception {
        Lease lease = repository.createLease(1, 1, new java.sql.Date(System.currentTimeMillis()), new java.sql.Date(System.currentTimeMillis() + 86400000));
        repository.returnCar(lease.getLeaseID());

        List<Lease> leases = repository.listLeaseHistory();

        // Verify lease history
        assertNotNull(leases);
        assertEquals(1, leases.size());
    }
}
