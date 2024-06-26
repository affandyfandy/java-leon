package app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import model.Employee;
import utils.DateUtils;
import utils.FileUtils;

public class AppManager {
    private static AppManager instance;
    private List<Employee> employees;
    private final Scanner scanner;
    private boolean useOpenCSV;

    // ANSI color codes
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BOLD = "\u001B[1m";

    private AppManager() {
        employees = new ArrayList<>();
        scanner = new Scanner(System.in);
        useOpenCSV = false; // Default to manual reading
    }

    /**
     * Returns the singleton instance of the AppManager class.
     * If the instance is null, a new instance is created and returned.
     *
     * @return the singleton instance of the AppManager class
     */
    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * Starts the main application loop. This method will keep running until the user chooses to exit.
     */
    public void start() {
        printHeader();
        while (true) {
            showMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                System.out.println("--------------------------------------------------------------------");
                switch (choice) {
                    case 0 -> {
                        System.out.println(ANSI_RED + "Exiting..." + ANSI_RESET);
                        System.exit(0);
                    }
                    case 1 -> importData();
                    case 2 -> addEmployee();
                    case 3 -> printFilteredEmployees();
                    case 4 -> exportFilteredEmployees();
                    default -> System.out.println(ANSI_RED + "Invalid choice. Please try again." + ANSI_RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "Invalid input. Please enter a number." + ANSI_RESET);
            }
        }
    }

    /**
     * Prints the header of the application.
     *
     * This method prints the welcome message and the logo of the application.
     */
    private void printHeader() {
        System.out.println(ANSI_CYAN + "       _____                 _                               ");
        System.out.println("      | ____|_ __ ___  _ __ | |_ ___  ___  ___ _ __ ___  ___ ");
        System.out.println("      |  _| | '_ ` _ \\| '_ \\| __/ _ \\/ __|/ _ \\ '__/ __|/ _ \\");
        System.out.println("      | |___| | | | | | |_) | ||  __/\\__ \\  __/ |  \\__ \\  __/");
        System.out.println("      |_____|_| |_| |_| .__/ \\__\\___||___/\\___|_|  |___/\\___|");
        System.out.println("                      |_|                                     " + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_PURPLE + "             Welcome to the Employee Management System\n" + ANSI_RESET);
    }

    /**
     * Displays the main menu of the application.
     *
     * This method prints the welcome message and the logo of the application,
     * followed by the main menu options.
     */
    private void showMenu() {
        System.out.println("====================================================================");
        System.out.println(ANSI_GREEN + "Menu:");
        System.out.println("0 - Exit");
        System.out.println("1 - Select File, Import data");
        System.out.println("2 - Add new Employee");
        System.out.println("3 - Filter Employees");
        System.out.println("4 - Export filtered Employees");
        System.out.print("Choose an option: " + ANSI_RESET);
    }

    /**
     * Imports data from a CSV file.
     *
     * @param useOpenCSV  A boolean value indicating whether to use the OpenCSV library for reading the CSV file.
     * @param filePath  The path to the CSV file containing the employee data.
     *
     * @throws FileNotFoundException  If the specified file does not exist.
     * @throws IOException  If an error occurs while reading the file.
     */
    private void importData() {
        System.out.println(ANSI_BLUE + "Select CSV reading method:");
        System.out.println("1 - Manual");
        System.out.println("2 - OpenCSV");
        System.out.print("Choose an option: " + ANSI_RESET);
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1 -> useOpenCSV = false;
                case 2 -> useOpenCSV = true;
                default -> {
                    System.out.println(ANSI_RED + "Invalid choice. Defaulting to manual reading." + ANSI_RESET);
                    useOpenCSV = false;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "Invalid input. Defaulting to manual reading." + ANSI_RESET);
            useOpenCSV = false;
        }

        try {
            System.out.println(ANSI_BLUE + "Enter the file path: " + ANSI_RESET);
            String filePath = scanner.nextLine();
            if (useOpenCSV) {
                employees = FileUtils.readEmployeesFromCSVOpenCSV(filePath);
            } else {
                employees = FileUtils.readEmployeesFromCSVManual(filePath);
            }
            System.out.println(ANSI_GREEN + "Data imported successfully, here is all the imported data from CSV file." + ANSI_RESET);
            printEmployees(employees);
        } catch (FileNotFoundException e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
        } catch (IOException e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
        }
    }

    /**
     * Adds a new employee to the list of employees.
     *
     * @throws DateTimeParseException if the date of birth is not in the format dd/MM/yyyy.
     */
    private void addEmployee() {
        try {
            System.out.print(ANSI_BLUE + "Enter ID: " + ANSI_RESET);
            String id = scanner.nextLine().trim();
            
            // Check if the employee ID already exists
            Employee existingEmployee = employees.stream()
                    .filter(e -> e.getId().equalsIgnoreCase(id))
                    .findFirst()
                    .orElse(null);
            
            if (existingEmployee != null) {
                System.out.println(ANSI_YELLOW + "Employee with ID " + id + " already exists." + ANSI_RESET);
                System.out.print(ANSI_BLUE + "Do you want to overwrite? (yes/no): " + ANSI_RESET);
                String response = scanner.nextLine().trim().toLowerCase();
                if (!response.equals("yes")) {
                    System.out.println(ANSI_RED + "Addition cancelled." + ANSI_RESET);
                    return;
                }
                // Remove existing employee if overwrite is chosen
                employees.remove(existingEmployee);
            }
    
            System.out.print(ANSI_BLUE + "Enter Name: " + ANSI_RESET);
            String name = scanner.nextLine().trim();
            System.out.print(ANSI_BLUE + "Enter Date of Birth (d/M/yyyy): " + ANSI_RESET);
            String dobString = scanner.nextLine().trim();
            System.out.print(ANSI_BLUE + "Enter Address: " + ANSI_RESET);
            String address = scanner.nextLine().trim();
            System.out.print(ANSI_BLUE + "Enter Department: " + ANSI_RESET);
            String department = scanner.nextLine().trim();
    
            Employee employee = new Employee(id, name, DateUtils.parseDate(dobString), address, department);
            employees.add(employee);
            System.out.println(ANSI_GREEN + "Employee added successfully." + ANSI_RESET);
        } catch (DateTimeParseException e) {
            System.out.println(ANSI_RED + "Invalid date format. Please use the format d/M/yyyy." + ANSI_RESET);
        } catch (Exception e) {
            System.out.println(ANSI_RED + "An error occurred while adding the employee. Please try again." + ANSI_RESET);
        }
    }    

    /**
     * Filter employees based on name, ID, DoB, department, stc
     *
     * @return A list of {@link Employee} objects as result of filter.
     */
    private List<Employee> filterEmployees() {
        System.out.println(ANSI_BLUE + "Filter by:");
        System.out.println("1 - Name");
        System.out.println("2 - ID");
        System.out.println("3 - Date of Birth (year)");
        System.out.println("4 - Department");
        System.out.println("5 - All employees");
        System.out.print("Choose an option: " + ANSI_RESET);
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            List<Employee> filtered = new ArrayList<>();
            switch (choice) {
                case 1 -> {
                    System.out.print(ANSI_BLUE + "Enter name pattern: " + ANSI_RESET);
                    String namePattern = scanner.nextLine();
                    filtered = employees.stream()
                            .filter(e -> e.getName().contains(namePattern))
                            .collect(Collectors.toList());
                }
                case 2 -> {
                    System.out.print(ANSI_BLUE + "Enter ID pattern: " + ANSI_RESET);
                    String idPattern = scanner.nextLine();
                    filtered = employees.stream()
                            .filter(e -> e.getId().contains(idPattern))
                            .collect(Collectors.toList());
                }
                case 3 -> {
                    System.out.print(ANSI_BLUE + "Enter year of birth: " + ANSI_RESET);
                    int year = Integer.parseInt(scanner.nextLine().trim());
                    filtered = employees.stream()
                            .filter(e -> e.getDateOfBirth().getYear() == year)
                            .collect(Collectors.toList());
                }
                case 4 -> {
                    System.out.print(ANSI_BLUE + "Enter Department: " + ANSI_RESET);
                    String department = scanner.nextLine();
                    filtered = employees.stream()
                            .filter(e -> e.getDepartment().equalsIgnoreCase(department))
                            .collect(Collectors.toList());
                }
                case 5 -> filtered = new ArrayList<>(employees);
                default -> System.out.println(ANSI_RED + "Invalid choice. Returning to menu." + ANSI_RESET);
            }

            return filtered;
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "Invalid input. Please enter a number." + ANSI_RESET);
            return new ArrayList<>();
        }
    }

    /**
     * Prints the list of employees in a formatted table.
     *
     * @param listOfEmployee The list of employees to be printed.
     */
    private void printEmployees(List<Employee> listOfEmployee) {
        // Determine the maximum length of each column
        int maxIdLength = "ID".length();
        int maxNameLength = "Name".length();
        int maxDobLength = "Date of Birth".length();
        int maxAddressLength = "Address".length();
        int maxDepartmentLength = "Department".length();

        for (Employee e : listOfEmployee) {
            maxIdLength = Math.max(maxIdLength, e.getId().length());
            maxNameLength = Math.max(maxNameLength, e.getName().length());
            maxDobLength = Math.max(maxDobLength, DateUtils.formatDate(e.getDateOfBirth()).length());
            maxAddressLength = Math.max(maxAddressLength, e.getAddress().length());
            maxDepartmentLength = Math.max(maxDepartmentLength, e.getDepartment().length());
        }

        // Format strings for table rows
        String format = String.format("%%-%ds | %%-%ds | %%-%ds | %%-%ds | %%-%ds",
                maxIdLength, maxNameLength, maxDobLength, maxAddressLength, maxDepartmentLength);

        // Print table header
        String header = String.format(format, "ID", "Name", "Date of Birth", "Address", "Department");
        String separator = new String(new char[header.length()]).replace("\0", "-");
        System.out.println(ANSI_BOLD + "\nShowing " + listOfEmployee.size() + " employee(s) data..." + ANSI_RESET);
        System.out.println(ANSI_CYAN + separator);
        System.out.println(header);
        System.out.println(separator + ANSI_RESET);

        // Print table rows
        listOfEmployee.stream()
                .sorted(Comparator.comparing(Employee::getDateOfBirth)) // Sort by Date of Birth
                .forEach(e -> {
                    String row = String.format(format,
                            e.getId(),
                            e.getName(),
                            DateUtils.formatDate(e.getDateOfBirth()),
                            e.getAddress(),
                            e.getDepartment());
                    System.out.println(ANSI_YELLOW + row + ANSI_RESET);
                });

        System.out.println(ANSI_CYAN + separator + ANSI_RESET);
    }

    /**
     * Print the filtered employees
     */
    private void printFilteredEmployees() {
        List<Employee> listOfEmployee = filterEmployees();

        if (listOfEmployee.isEmpty()) {
            System.out.println(ANSI_CYAN + "\nNo employees found." + ANSI_RESET);
            return;
        }

        printEmployees(listOfEmployee);
    }

    /**
     * Export the filtered list of employees and then export to CSV.
     */
    private void exportFilteredEmployees() {
        List<Employee> listOfEmployee = filterEmployees().stream()
                .sorted((e1, e2) -> e1.getDateOfBirth().compareTo(e2.getDateOfBirth()))
                .collect(Collectors.toList());

        try {
            System.out.print(ANSI_BLUE + "Enter the file path to save: " + ANSI_RESET);
            String filePath = scanner.nextLine();
            FileUtils.writeEmployeesToCSV(listOfEmployee, filePath);
            System.out.println(ANSI_GREEN + "Filtered data exported successfully." + ANSI_RESET);
        } catch (IOException e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
        }
    }
}
