

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

// House class to represent a rental property
class House {
    private String id;
    private String location;
    private String owner;
    private double price;
    private int bedrooms;
    private boolean isBooked;
    private String tenantId;

    public House(String id, String location, double price, int bedrooms, String owner) {
        this.id = id;
        this.location = location;
        this.price = price;
        this.bedrooms = bedrooms;
        this.owner = owner;
        this.isBooked = false;
        this.tenantId = "";
    }

    // Getters and setters
    public String getId() { return id; }
    public String getLocation() { return location; }
    public double getPrice() { return price; }
    public int getBedrooms() { return bedrooms; }
    public String getOwner() { return owner; }
    public boolean isBooked() { return isBooked; }
    public void setIsBooked(boolean isBooked) { this.isBooked = isBooked; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    @Override
    public String toString() {
        return "[" + id + ", " + location + ", " + price + ", " + bedrooms + ", " + owner + "]";
    }
}

// Tenant class to represent a tenant
class Tenant {
    private String id;
    private String name;
    private String contact;
    private String preferredLocation;

    public Tenant(String id, String name, String contact, String preferredLocation) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.preferredLocation = preferredLocation;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getPreferredLocation() { return preferredLocation; }
}

// RentalAgreement class to represent a lease agreement
class RentalAgreement {
    private String id;
    private House house;
    private Tenant tenant;
    private LocalDate startDate;
    private LocalDate endDate;
    private double deposit;
    private List<Payment> payments = new ArrayList<>();

    public RentalAgreement(String id, House house, Tenant tenant, LocalDate startDate, LocalDate endDate, double deposit) {
        this.id = id;
        this.house = house;
        this.tenant = tenant;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deposit = deposit;
    }

    // Getters
    public String getId() { return id; }
    public House getHouse() { return house; }
    public Tenant getTenant() { return tenant; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getDeposit() { return deposit; }
    public List<Payment> getPayments() { return payments; }
    public void addPayment(Payment payment) { payments.add(payment); }

    // Calculate the next due date
    public LocalDate getNextDueDate() {
        int monthsPaid = payments.size();
        return startDate.plusMonths(monthsPaid);
    }

    // Check if payment is overdue
    public boolean isPaymentOverdue(LocalDate currentDate) {
        LocalDate nextDueDate = getNextDueDate();
        return currentDate.isAfter(nextDueDate);
    }
}

// Payment class to represent a payment
class Payment {
    private LocalDate date;
    private double amount;

    public Payment(LocalDate date, double amount) {
        this.date = date;
        this.amount = amount;
    }

    public LocalDate getDate() { return date; }
    public double getAmount() { return amount; }
}

// Main class to manage the rental system
public class Main {
    private List<House> houses = new ArrayList<>();
    private List<Tenant> tenants = new ArrayList<>();
    private List<RentalAgreement> agreements = new ArrayList<>();
    private final String HOUSES_FILE = "houses.txt";
    private final String TENANTS_FILE = "tenants.txt";
    private final String AGREEMENTS_FILE = "agreements.txt";
    private int agreementCounter = 1;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Load houses from file
    public void loadHouses() throws IOException {
        houses.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(HOUSES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    House house = new House(parts[0], parts[1], Double.parseDouble(parts[2]), 
                                            Integer.parseInt(parts[3]), parts[4]);
                    house.setIsBooked(Boolean.parseBoolean(parts[5]));
                    house.setTenantId(parts[6]);
                    houses.add(house);
                }
            }
        } catch (FileNotFoundException e) {
            // Start with empty list if file not found
        }
    }

    // Save houses to file
    public void saveHouses() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HOUSES_FILE))) {
            for (House house : houses) {
                writer.write(String.format("%s,%s,%.0f,%d,%s,%b,%s", 
                    house.getId(), house.getLocation(), house.getPrice(), house.getBedrooms(), 
                    house.getOwner(), house.isBooked(), house.getTenantId()));
                writer.newLine();
            }
        }
    }

    // Load tenants from file
    public void loadTenants() throws IOException {
        tenants.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(TENANTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    tenants.add(new Tenant(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (FileNotFoundException e) {
            // Start with empty list if file not found
        }
    }

    // Save tenants to file
    public void saveTenants() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TENANTS_FILE))) {
            for (Tenant tenant : tenants) {
                writer.write(tenant.getId() + "," + tenant.getName() + "," + 
                             tenant.getContact() + "," + tenant.getPreferredLocation());
                writer.newLine();
            }
        }
    }

    // Load agreements from file
    public void loadAgreements() throws IOException {
        agreements.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(AGREEMENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0];
                String houseId = parts[1];
                String tenantId = parts[2];
                LocalDate startDate = LocalDate.parse(parts[3], dateFormatter);
                LocalDate endDate = LocalDate.parse(parts[4], dateFormatter);
                double deposit = Double.parseDouble(parts[5]);
                House house = findHouseById(houseId);
                Tenant tenant = findTenantById(tenantId);
                RentalAgreement agreement = new RentalAgreement(id, house, tenant, startDate, endDate, deposit);
                if (parts.length > 6) {
                    String paymentsStr = parts[6].replace("[", "").replace("]", "");
                    if (!paymentsStr.isEmpty()) {
                        String[] paymentParts = paymentsStr.split(";");
                        for (String payment : paymentParts) {
                            String[] paymentDetails = payment.split(":");
                            LocalDate paymentDate = LocalDate.parse(paymentDetails[0], dateFormatter);
                            double amount = Double.parseDouble(paymentDetails[1]);
                            agreement.addPayment(new Payment(paymentDate, amount));
                        }
                    }
                }
                agreements.add(agreement);
                int counter = Integer.parseInt(id.substring(2)) + 1;
                if (counter > agreementCounter) agreementCounter = counter;
            }
        } catch (FileNotFoundException e) {
            // Start with empty list if file not found
        }
    }

    // Save agreements to file
    public void saveAgreements() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AGREEMENTS_FILE))) {
            for (RentalAgreement agreement : agreements) {
                StringBuilder sb = new StringBuilder();
                sb.append(agreement.getId()).append(",")
                  .append(agreement.getHouse().getId()).append(",")
                  .append(agreement.getTenant().getId()).append(",")
                  .append(agreement.getStartDate().format(dateFormatter)).append(",")
                  .append(agreement.getEndDate().format(dateFormatter)).append(",")
                  .append(agreement.getDeposit());
                List<Payment> payments = agreement.getPayments();
                sb.append(",[");
                if (!payments.isEmpty()) {
                    for (int i = 0; i < payments.size(); i++) {
                        Payment p = payments.get(i);
                        sb.append(p.getDate().format(dateFormatter)).append(":").append(p.getAmount());
                        if (i < payments.size() - 1) sb.append(";");
                    }
                }
                sb.append("]");
                writer.write(sb.toString());
                writer.newLine();
            }
        }
    }

    // Find house by ID
    private House findHouseById(String id) {
        return houses.stream().filter(h -> h.getId().equals(id)).findFirst().orElse(null);
    }

    // Find tenant by ID
    private Tenant findTenantById(String id) {
        return tenants.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
    }

    // Add house
    public void addHouse(String id, String location, double price, int bedrooms, String owner) throws Exception {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
        if (houses.stream().anyMatch(h -> h.getId().equals(id))) throw new Exception("House ID already exists.");
        houses.add(new House(id, location, price, bedrooms, owner));
        saveHouses();
    }

    // Remove house
    public void removeHouse(String id) throws Exception {
        House house = houses.stream().filter(h -> h.getId().equals(id)).findFirst()
                           .orElseThrow(() -> new Exception("House not found."));
        if (house.isBooked()) throw new Exception("Cannot remove booked house.");
        houses.remove(house);
        saveHouses();
    }

    // Search houses
    public List<House> searchHouses(String location, double maxPrice) {
        return houses.stream()
                     .filter(h -> !h.isBooked())
                     .filter(h -> h.getLocation().equalsIgnoreCase(location) && h.getPrice() <= maxPrice)
                     .collect(Collectors.toList());
    }

    // Register tenant
    public void registerTenant(String id, String name, String contact, String preferredLocation) throws Exception {
        if (tenants.stream().anyMatch(t -> t.getId().equals(id))) throw new Exception("Tenant ID already exists.");
        tenants.add(new Tenant(id, name, contact, preferredLocation));
        saveTenants();
    }

    // Match tenant with houses
    public List<House> matchTenantWithHouses(String tenantId) throws Exception {
        Tenant tenant = tenants.stream().filter(t -> t.getId().equals(tenantId)).findFirst()
                              .orElseThrow(() -> new Exception("Tenant not found."));
        return houses.stream()
                     .filter(h -> !h.isBooked())
                     .filter(h -> h.getLocation().equalsIgnoreCase(tenant.getPreferredLocation()))
                     .collect(Collectors.toList());
    }

    // Book house
    public void bookHouse(String houseId, String tenantId, LocalDate startDate, LocalDate endDate, double deposit) throws Exception {
        House house = houses.stream().filter(h -> h.getId().equals(houseId)).findFirst()
                           .orElseThrow(() -> new Exception("House not found."));
        if (house.isBooked()) throw new Exception("House already booked.");
        Tenant tenant = tenants.stream().filter(t -> t.getId().equals(tenantId)).findFirst()
                              .orElseThrow(() -> new Exception("Tenant not found."));
        if (deposit < 0) throw new IllegalArgumentException("Deposit cannot be negative.");
        String agreementId = "RA" + agreementCounter++;
        RentalAgreement agreement = new RentalAgreement(agreementId, house, tenant, startDate, endDate, deposit);
        agreements.add(agreement);
        house.setIsBooked(true);
        house.setTenantId(tenantId);
        saveHouses();
        saveAgreements();
    }

    // Record payment
    public void recordPayment(String agreementId, LocalDate date, double amount) throws Exception {
        RentalAgreement agreement = agreements.stream().filter(a -> a.getId().equals(agreementId)).findFirst()
                                             .orElseThrow(() -> new Exception("Agreement not found."));
        agreement.addPayment(new Payment(date, amount));
        saveAgreements();
    }

    // Check due date for an agreement
    public void checkDueDate(String agreementId) throws Exception {
        RentalAgreement agreement = agreements.stream().filter(a -> a.getId().equals(agreementId)).findFirst()
                                             .orElseThrow(() -> new Exception("Agreement not found."));
        LocalDate currentDate = LocalDate.now();
        LocalDate nextDueDate = agreement.getNextDueDate();
        boolean isOverdue = agreement.isPaymentOverdue(currentDate);
        System.out.println("Next due date for agreement " + agreementId + ": " + nextDueDate);
        if (isOverdue) {
            System.out.println("Payment is overdue!");
        } else {
            System.out.println("Payment is not yet due.");
        }
    }

    // Main method with menu
    public static void main(String[] args) {
        Main rms = new Main();
        Scanner scanner = new Scanner(System.in);

        try {
            rms.loadHouses();
            rms.loadTenants();
            rms.loadAgreements();
        } catch (IOException e) {
            System.out.println("Error loading files: " + e.getMessage());
        }

        while (true) {
            System.out.println("\n=== House Rental Management System ===");
            System.out.println("1. Add House");
            System.out.println("2. Remove House");
            System.out.println("3. Search Houses");
            System.out.println("4. Register Tenant");
            System.out.println("5. Match Tenant with Houses");
            System.out.println("6. Book House");
            System.out.println("7. Record Payment");
            System.out.println("8. Check Due Dates");
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1: // Add House
                        System.out.print("ID: "); String id = scanner.nextLine();
                        System.out.print("Location: "); String loc = scanner.nextLine();
                        System.out.print("Price: "); double price = Double.parseDouble(scanner.nextLine());
                        System.out.print("Bedrooms: "); int beds = Integer.parseInt(scanner.nextLine());
                        System.out.print("Owner: "); String owner = scanner.nextLine();
                        rms.addHouse(id, loc, price, beds, owner);
                        System.out.println("House added.");
                        break;

                    case 2: // Remove House
                        System.out.print("House ID: "); String removeId = scanner.nextLine();
                        rms.removeHouse(removeId);
                        System.out.println("House removed.");
                        break;

                    case 3: // Search Houses
                        System.out.print("Location: "); String searchLoc = scanner.nextLine();
                        System.out.print("Max Price: "); double maxPrice = Double.parseDouble(scanner.nextLine());
                        List<House> results = rms.searchHouses(searchLoc, maxPrice);
                        System.out.println("Found " + results.size() + " house(s): " + results);
                        break;

                    case 4: // Register Tenant
                        System.out.print("ID: "); String tId = scanner.nextLine();
                        System.out.print("Name: "); String name = scanner.nextLine();
                        System.out.print("Contact: "); String contact = scanner.nextLine();
                        System.out.print("Preferred Location: "); String prefLoc = scanner.nextLine();
                        rms.registerTenant(tId, name, contact, prefLoc);
                        System.out.println("Tenant registered.");
                        break;

                    case 5: // Match Tenant with Houses
                        System.out.print("Tenant ID: "); String matchId = scanner.nextLine();
                        List<House> matches = rms.matchTenantWithHouses(matchId);
                        System.out.println("Matches: " + matches);
                        break;

                    case 6: // Book House
                        System.out.print("House ID: "); String hId = scanner.nextLine();
                        System.out.print("Tenant ID: "); String tId2 = scanner.nextLine();
                        System.out.print("Start Date (yyyy-MM-dd): "); 
                        LocalDate start = LocalDate.parse(scanner.nextLine(), rms.dateFormatter);
                        System.out.print("End Date (yyyy-MM-dd): "); 
                        LocalDate end = LocalDate.parse(scanner.nextLine(), rms.dateFormatter);
                        System.out.print("Deposit: "); double deposit = Double.parseDouble(scanner.nextLine());
                        rms.bookHouse(hId, tId2, start, end, deposit);
                        System.out.println("House booked.");
                        break;

                    case 7: // Record Payment
                        System.out.print("Agreement ID: "); String aId = scanner.nextLine();
                        System.out.print("Date (yyyy-MM-dd): "); 
                        LocalDate date = LocalDate.parse(scanner.nextLine(), rms.dateFormatter);
                        System.out.print("Amount: "); double amount = Double.parseDouble(scanner.nextLine());
                        rms.recordPayment(aId, date, amount);
                        System.out.println("Payment recorded.");
                        break;

                    case 8: // Check Due Dates
                        System.out.print("Agreement ID: "); String checkId = scanner.nextLine();
                        rms.checkDueDate(checkId);
                        break;

                    case 9: // Exit
                        System.out.println("Goodbye!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}