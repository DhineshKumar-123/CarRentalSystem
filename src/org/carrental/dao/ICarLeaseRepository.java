package org.carrental.dao;

import org.carrental.entity.*;

import java.util.List;
import java.util.Date;

public interface ICarLeaseRepository {


    void addCar(Car car);
    void removeCar(int carID);
    List<Car> listAvailableCars();
    List<Car> listRentedCars();
    Car findCarById(int carID);

    void addCustomer(Customer customer);
    void removeCustomer(int customerID);
    List<Customer> listCustomers();
    Customer findCustomerById(int customerID);


    Lease createLease(int customerID, int carID, Date startDate, Date endDate);
    void returnCar(int leaseID);
    List<Lease> listActiveLeases();
    List<Lease> listLeaseHistory();

    void recordPayment(int leaseID, double amount);
}
