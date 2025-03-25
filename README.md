# House Rental Management System

## Overview

The **House Rental Management System** is a console-based Java application designed to simplify the management of house rentals. It enables users to manage properties, tenants, bookings, and payments, with data persistence through file handling. This project serves as both a practical tool for rental management and an educational resource for learning core Java concepts such as object-oriented programming (OOP), file I/O, and exception handling.

## Features

- **Property Management**:
  - Add new houses with details like ID, location, price, bedrooms, and owner
  - Remove houses that are not currently booked
  - Search for available houses by location and price range

- **Tenant Management**:
  - Register tenants with name, contact information, and preferred location
  - Match tenants with available houses based on their preferred location

- **Booking & Payments**:
  - Book a house for a tenant and generate a lease agreement
  - Record payments for lease agreements and track payment history

- **Data Persistence**:
  - Save house and tenant data to `houses.txt` and `tenants.txt` files
  - (Optional) Save rental agreements to `agreements.txt` for persistent storage

## Technologies Used

- **Java**: Core Java with OOP principles
- **File I/O**: `BufferedReader` and `BufferedWriter` for reading and writing data to files
- **Java Streams**: For efficient filtering and processing of house and tenant data
- **Exception Handling**: Custom exceptions and input validation for robustness
- **Thread Safety**: Synchronized methods for booking operations to ensure thread safety

## Prerequisites

- **Java Development Kit (JDK)**: Version 8 or higher
- A terminal or command prompt to compile and run the application

## Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/house-rental-management-system.git
   cd house-rental-management-system
   ```

2. **Compile the Java Files**:
   ```bash
   javac *.java
   ```

3. **Run the Application**:
   ```bash
   java RentalManagementSystem
   ```

## Usage Guide

Upon running the application, you will be greeted with a menu-driven interface:

```
=== House Rental Management System ===
1. Add House
2. Remove House
3. Search Houses
4. Register Tenant
5. Match Tenant with Houses
6. Book House
7. Record Payment
8. Exit
Choose an option:
```

### Menu Options

- **1. Add House**: Add a new house to the system.
  - Example:
    ```
    Enter ID: H1
    Enter Location: Bangalore
    Enter Price: 50000
    Enter Bedrooms: 3
    Enter Owner: John Doe
    ```

- **2. Remove House**: Remove an unbooked house by its ID.
  - Example:
    ```
    Enter House ID: H1
    ```

- **3. Search Houses**: Search for available houses by location and maximum price.
  - Example:
    ```
    Enter Location: Bangalore
    Enter Max Price: 60000
    ```

- **4. Register Tenant**: Register a new tenant with their details.
  - Example:
    ```
    Enter ID: T1
    Enter Name: Jane Doe
    Enter Contact: 9876543210
    Enter Preferred Location: Bangalore
    ```

- **5. Match Tenant with Houses**: Find available houses in the tenant’s preferred location.
  - Example:
    ```
    Enter Tenant ID: T1
    ```

- **6. Book House**: Book a house for a tenant and create a lease agreement.
  - Example:
    ```
    Enter House ID: H1
    Enter Tenant ID: T1
    Enter Start Date (yyyy-MM-dd): 2023-12-01
    Enter End Date (yyyy-MM-dd): 2024-11-30
    Enter Deposit: 10000
    ```

- **7. Record Payment**: Record a payment for a lease agreement.
  - Example:
    ```
    Enter Agreement ID: RA1
    Enter Date (yyyy-MM-dd): 2023-12-01
    Enter Amount: 50000
    ```

- **8. Exit**: Close the application.

### Data Storage

- **Houses**: Stored in `houses.txt` with the format:
  ```
  H1,Bangalore,50000,3,John Doe,false,
  ```
- **Tenants**: Stored in `tenants.txt` with the format:
  ```
  T1,Jane Doe,9876543210,Bangalore
  ```
- **Rental Agreements** (Optional): Stored in `agreements.txt` with the format:
  ```
  RA1,H1,T1,2023-12-01,2024-11-30,10000,[2023-12-01:50000]
  ```

**Note**: By default, rental agreements are stored in memory and lost on restart. To persist agreements, enable the optional feature by implementing the `saveAgreements` and `loadAgreements` methods.

## Project Structure

- **`House.java`**: Represents a rental property with attributes like ID, location, price, and booking status
- **`Tenant.java`**: Represents a tenant with attributes like ID, name, contact, and preferred location
- **`RentalAgreement.java`**: Represents a lease agreement with details like house, tenant, dates, deposit, and payments
- **`Payment.java`**: Represents a payment made by a tenant
- **`RentalManagementSystem.java`**: The main class that manages the application logic, user interface, and file operations


