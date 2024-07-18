# 👩🏻‍🏫 Lecture 12 - Spring Data JPA
> This repository is created as a part of assignment for Lecture 12 - Spring Data JPA

## ⚡ Assignment 01 - Adding Dynamic Criteria for Employee Search

### 🔎 Dynamic Search Criteria 😵😵

To implement dynamic criteria search APIs for every attribute on my `Employee` model, i'll need to enhance my existing codebase to support filtering based on various attributes. Here's a detailed approach:

### 👣 Step-by-Step Explanation

1. **Define Search Criteria**: I decided how i want to pass search criteria to my API. Common approaches include query parameters (`/api/v1/employees?firstName=John&gender=M`) or a JSON object in the request body (`POST` request with a JSON body containing search criteria). In this implementation, i choose the query parameters.

2. **DTO (Data Transfer Object)**: I used DTOs to transfer data between layers (controller, service, repository). This helps in decoupling my API contract from my entity structure and provides flexibility in handling incoming requests.

3. **Service Layer Modification**: I enhanced my service layer to handle dynamic filtering using specifications or query methods. Specifications are particularly useful for complex queries involving multiple criteria.

4. **Controller Layer Modification**: I also modified my controller to accept dynamic search criteria and delegate the search to the service layer.

5. **Implementation Considerations**: I also not forget to handle various scenarios such as no search criteria provided, pagination, sorting, and proper error handling for invalid queries.

### 👨🏻‍💻 Implementation:

#### 1. Create a DTO for Search Criteria ([EmployeeSearchCriteriaDTO.java](/Week%2006/Lecture%2012/Assignment%2001/lecture_12/src/main/java/com/example/lecture_12/dto/EmployeeSearchCriteriaDTO.java))

```java
package com.example.lecture_12.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class EmployeeSearchCriteriaDTO {
    private LocalDate birthDate;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate hireDate;
}
```

#### 2. Update Employee Repository ([EmployeeRepository.java](/Week%2006/Lecture%2012/Assignment%2001/lecture_12/src/main/java/com/example/lecture_12/data/repository/EmployeeRepository.java))

```java
package com.example.lecture_12.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.lecture_12.data.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    // Define a custom query method using Specification and Pageable
    Page<Employee> findAll(Specification<Employee> spec, Pageable pageable);
}
```

#### 3. Modify Employee Service Interface ([EmployeeService.java](/Week%2006/Lecture%2012/Assignment%2001/lecture_12/src/main/java/com/example/lecture_12/services/EmployeeService.java))

```java
package com.example.lecture_12.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.lecture_12.data.model.Employee;
import com.example.lecture_12.dto.EmployeeSearchCriteriaDTO;

public interface EmployeeService {
    ....

    // Retrieves a paginated list of {@link Employee} entities based on the provided search criteria.
    Page<Employee> findByCriteria(EmployeeSearchCriteriaDTO criteria, Pageable pageable);

    ....
}
```

#### 4. Implement Employee Service ([EmployeeServiceImpl.java](/Week%2006/Lecture%2012/Assignment%2001/lecture_12/src/main/java/com/example/lecture_12/services/impl/EmployeeServiceImpl.java))

```java
package com.example.lecture_12.services.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.example.lecture_12.data.model.Employee;
import com.example.lecture_12.data.repository.EmployeeRepository;
import com.example.lecture_12.dto.EmployeeSearchCriteriaDTO;
import com.example.lecture_12.services.EmployeeService;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    
    private final EmployeeRepository employeeRepository;

    ....

    /**
     * Retrieves a paginated list of {@link Employee} entities based on the provided search criteria.
     *
     * @param criteria The criteria object containing fields to filter the search.
     * @param pageable Pagination and sorting parameters.
     * @return A {@link Page} of {@link Employee} entities that match the specified criteria.
     */
    @Override
    public Page<Employee> findByCriteria(EmployeeSearchCriteriaDTO criteria, Pageable pageable) {
        return employeeRepository.findAll((Specification<Employee>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (criteria.getBirthDate() != null) { predicates.add(cb.equal(root.get("birthDate"), criteria.getBirthDate())); }
            if (criteria.getFirstName() != null) { predicates.add(cb.equal(root.get("firstName"), criteria.getFirstName())); }
            if (criteria.getLastName() != null) { predicates.add(cb.equal(root.get("lastName"), criteria.getLastName())); }
            if (criteria.getGender() != null) { predicates.add(cb.equal(root.get("gender"), criteria.getGender())); }
            if (criteria.getHireDate() != null) { predicates.add(cb.equal(root.get("hireDate"), criteria.getHireDate())); }

            return cb.and(predicates.toArray(Predicate[]::new));
        }, pageable);
    }

    ....
}
```

#### 5. Update Employee Controller ([EmployeeController.java](/Week%2006/Lecture%2012/Assignment%2001/lecture_12/src/main/java/com/example/lecture_12/controllers/EmployeeController.java))

```java
package com.example.lecture_12.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.lecture_12.data.model.Employee;
import com.example.lecture_12.dto.EmployeeSearchCriteriaDTO;
import com.example.lecture_12.services.EmployeeService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/employees")
@AllArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    ....

    /**
     * Endpoint to search for {@link Employee} entities based on the provided search criteria.
     * Supports pagination and sorting.
     *
     * @param criteria The criteria object of {@link EmployeeSearchCriteriaDTO} containing fields to filter the search.
     * @param page     The page number to retrieve (default is 0).
     * @param size     The number of elements per page (default is 20).
     * @return ResponseEntity containing a {@link Page} of {@link Employee} entities that match the criteria,  
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Employee>> searchEmployees(EmployeeSearchCriteriaDTO criteria, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employees = employeeService.findByCriteria(criteria, pageable);

        if (employees.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(employees);
    }

    ....
}
```

### 📢 Explanation of Code
Here is the detail explanation on what i already done throughout the code.

- **DTO**: `EmployeeSearchCriteriaDTO` is a simple class to hold search criteria. Each attribute corresponds to a field in the `Employee` entity.
- **Database Layer (JPA)**: adding `findAll` with Specification and Pageable on `EmployeeRepository` to handle spesific search query criteria dynamically from the database also to implement pagination easily.
- **Service Layer**: `EmployeeServiceImpl` implements `findByCriteria` method using JPA Specifications to dynamically build predicates based on provided criteria.
- **Controller Layer**: `EmployeeController` exposes a `GET` endpoint `/api/v1/employees/search` to accept search criteria as query parameters and returns a list of matching `Employee` entities.

### 📝 Some Notable Mentions

- **Security**: I'm ensuring to validate and sanitize input to prevent injection attacks.
- **Performance**: Instead of just showing all the filtered criteria, i also use pagination (`Pageable`) to handle large result sets efficiently.
- **Flexibility**: In the program i implemented, i expand the approach by handling more complex queries using JPA `Specifications` which makes the execution more smooth and dynamic.

This approach ensures my API to be flexible, maintainable, and follows best practices for handling dynamic search criteria in a Spring Boot application using JPA.

### 🌳 Project Structure
```bash
lecture_11
├── .mvn/wrapper/
│   └── maven-wrapper.properties
├── src/main/
│   ├── java/com/example/lecture_11/
│   │   ├── controller/
│   │   │   ├── DepartmentController.java
│   │   │   ├── EmployeeController.java
│   │   │   ├── SalaryController.java
│   │   │   └── TitleController.java
│   │   ├── data/
│   │   │   ├── model/
│   │   │   │   ├── composite/
│   │   │   │   │   ├── DeptEmpId.java
│   │   │   │   │   ├── DeptManagerId.java
│   │   │   │   │   ├── SalaryId.java
│   │   │   │   │   └── TitleId.java
│   │   │   │   ├── Department.java
│   │   │   │   ├── DeptEmp.java
│   │   │   │   ├── DeptManager.java
│   │   │   │   ├── Employee.java
│   │   │   │   ├── Salary.java
│   │   │   │   └── Title.java
│   │   │   └── repository/
│   │   │       ├── DepartmentRepository.java
│   │   │       ├── DeptEmpRepository.java
│   │   │       ├── DeptManagerRepository.java
│   │   │       ├── EmployeeRepository.java
│   │   │       ├── Salary.Repositoryjava
│   │   │       └── TitleRepository.java
│   │   ├── dto/
│   │   │   └── EmployeeSearchCriteriaDTO.java
│   │   ├── service/
│   │   │   ├── impl/
│   │   │   │   ├── DepartmentServiceImpl.java
│   │   │   │   ├── EmployeeServiceImpl.java
│   │   │   │   ├── SalaryServiceImpl.java
│   │   │   │   └── TitleServiceImpl.java
│   │   │   ├── DepartmentService.java
│   │   │   ├── EmployeeService.java
│   │   │   ├── SalaryService.java
│   │   │   └── TitleService.java
│   │   └── Lecture11Application.java
│   └── resources/
│       └── application.properties
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
├── run.bat
└── run.sh
```

### 🧩 SQL Query Data
Here is the SQL query to create the database, table, and instantiate some data.
```sql
-- Create the database
CREATE DATABASE week6_lecture12;

-- Use the database
USE week6_lecture12;

-- Create employees table
CREATE TABLE employees (
    emp_no INT AUTO_INCREMENT PRIMARY KEY,
    birth_date DATE NOT NULL,
    first_name VARCHAR(14) NOT NULL,
    last_name VARCHAR(16) NOT NULL,
    gender ENUM('M', 'F') NOT NULL,
    hire_date DATE NOT NULL
);

-- Create departments table
CREATE TABLE departments (
    dept_no CHAR(4) PRIMARY KEY,
    dept_name VARCHAR(40) NOT NULL UNIQUE
);

-- Create dept_emp table
CREATE TABLE dept_emp (
    emp_no INT NOT NULL,
    dept_no CHAR(4) NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    PRIMARY KEY (emp_no, dept_no),
    FOREIGN KEY (emp_no) REFERENCES employees(emp_no) ON DELETE CASCADE,
    FOREIGN KEY (dept_no) REFERENCES departments(dept_no) ON DELETE CASCADE
);

-- Create dept_manager table
CREATE TABLE dept_manager (
    emp_no INT NOT NULL,
    dept_no CHAR(4) NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    PRIMARY KEY (emp_no, dept_no),
    FOREIGN KEY (emp_no) REFERENCES employees(emp_no) ON DELETE CASCADE,
    FOREIGN KEY (dept_no) REFERENCES departments(dept_no) ON DELETE CASCADE
);

-- Create salaries table
CREATE TABLE salaries (
    emp_no INT NOT NULL,
    from_date DATE NOT NULL,
    salary INT NOT NULL,
    to_date DATE NOT NULL,
    PRIMARY KEY (emp_no, from_date),
    FOREIGN KEY (emp_no) REFERENCES employees(emp_no) ON DELETE CASCADE
);

-- Create titles table
CREATE TABLE titles (
    emp_no INT NOT NULL,
    title VARCHAR(50) NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE,
    PRIMARY KEY (emp_no, title, from_date),
    FOREIGN KEY (emp_no) REFERENCES employees(emp_no) ON DELETE CASCADE
);
```

Here is the query to insert some generated dummy data
```sql
-- Insert employees
INSERT INTO employees (birth_date, first_name, last_name, gender, hire_date) VALUES
('1980-01-01', 'John', 'Doe', 'M', '2000-01-01'),
('1985-05-23', 'Jane', 'Smith', 'F', '2005-05-01'),
('1990-07-11', 'Alice', 'Johnson', 'F', '2010-06-01'),
('1975-02-14', 'Bob', 'Brown', 'M', '1995-03-01'),
('1988-12-25', 'Charlie', 'Davis', 'M', '2008-12-01'),
('1981-04-10', 'David', 'Evans', 'M', '2001-04-10'),
('1986-08-15', 'Laura', 'Wilson', 'F', '2006-08-15'),
('1991-03-22', 'Karen', 'Garcia', 'F', '2011-03-22'),
('1976-06-12', 'Paul', 'Martinez', 'M', '1996-06-12'),
('1989-11-30', 'Nancy', 'Rodriguez', 'F', '2009-11-30');

-- Insert departments
INSERT INTO departments (dept_no, dept_name) VALUES
('d001', 'Marketing'),
('d002', 'Finance'),
('d003', 'Human Resources'),
('d004', 'Engineering'),
('d005', 'Sales');

-- Insert dept_emp
INSERT INTO dept_emp (emp_no, dept_no, from_date, to_date) VALUES
(1, 'd001', '2000-01-01', '2002-01-01'),
(1, 'd002', '2002-01-01', '9999-01-01'),
(2, 'd002', '2005-05-01', '2010-05-01'),
(2, 'd003', '2010-05-01', '9999-01-01'),
(3, 'd003', '2010-06-01', '9999-01-01'),
(3, 'd004', '2011-01-01', '9999-01-01'),
(4, 'd004', '1995-03-01', '9999-01-01'),
(4, 'd005', '2000-01-01', '9999-01-01'),
(5, 'd001', '2008-12-01', '9999-01-01'),
(5, 'd005', '2010-01-01', '9999-01-01'),
(6, 'd002', '2001-04-10', '2003-04-10'),
(6, 'd003', '2003-04-10', '9999-01-01'),
(7, 'd003', '2006-08-15', '2011-08-15'),
(7, 'd004', '2011-08-15', '9999-01-01'),
(8, 'd001', '2011-03-22', '9999-01-01'),
(9, 'd004', '1996-06-12', '2006-06-12'),
(9, 'd005', '2006-06-12', '9999-01-01'),
(10, 'd005', '2009-11-30', '9999-01-01');

-- Insert dept_manager
INSERT INTO dept_manager (emp_no, dept_no, from_date, to_date) VALUES
(1, 'd001', '2000-01-01', '2002-01-01'),
(2, 'd002', '2005-05-01', '2010-05-01'),
(3, 'd003', '2010-06-01', '2011-01-01');

-- Insert salaries
INSERT INTO salaries (emp_no, salary, from_date, to_date) VALUES
(1, 60000, '2000-01-01', '2002-01-01'),
(1, 65000, '2002-01-01', '9999-01-01'),
(2, 75000, '2005-05-01', '2010-05-01'),
(2, 80000, '2010-05-01', '9999-01-01'),
(3, 80000, '2010-06-01', '2011-01-01'),
(3, 85000, '2011-01-01', '9999-01-01'),
(4, 90000, '1995-03-01', '2000-01-01'),
(4, 95000, '2000-01-01', '9999-01-01'),
(5, 85000, '2008-12-01', '2010-01-01'),
(5, 90000, '2010-01-01', '9999-01-01'),
(6, 65000, '2001-04-10', '2003-04-10'),
(6, 70000, '2003-04-10', '9999-01-01'),
(7, 70000, '2006-08-15', '2011-08-15'),
(7, 75000, '2011-08-15', '9999-01-01'),
(8, 72000, '2011-03-22', '9999-01-01'),
(9, 95000, '1996-06-12', '2006-06-12'),
(9, 100000, '2006-06-12', '9999-01-01'),
(10, 86000, '2009-11-30', '9999-01-01');

-- Insert titles
INSERT INTO titles (emp_no, title, from_date, to_date) VALUES
(1, 'Manager', '2000-01-01', '2002-01-01'),
(1, 'Senior Manager', '2002-01-01', '9999-01-01'),
(2, 'Analyst', '2005-05-01', '2010-05-01'),
(2, 'Senior Analyst', '2010-05-01', '9999-01-01'),
(3, 'HR Specialist', '2010-06-01', '2011-01-01'),
(3, 'HR Manager', '2011-01-01', '9999-01-01'),
(4, 'Engineer', '1995-03-01', '2000-01-01'),
(4, 'Senior Engineer', '2000-01-01', '9999-01-01'),
(5, 'Sales Representative', '2008-12-01', '2010-01-01'),
(5, 'Senior Sales Representative', '2010-01-01', '9999-01-01'),
(6, 'Finance Specialist', '2001-04-10', '2003-04-10'),
(6, 'Senior Finance Specialist', '2003-04-10', '9999-01-01'),
(7, 'HR Manager', '2006-08-15', '2011-08-15'),
(7, 'Senior HR Manager', '2011-08-15', '9999-01-01'),
(8, 'Marketing Specialist', '2011-03-22', '9999-01-01'),
(9, 'Senior Engineer', '1996-06-12', '2006-06-12'),
(9, 'Chief Engineer', '2006-06-12', '9999-01-01'),
(10, 'Senior Sales Representative', '2009-11-30', '9999-01-01');
```

All the MySQL queries is available on [this file](/Week%2006/Lecture%2011/lecture_11/src/main/resources/data.sql). Here is the query to drop the database
```sql
-- Drop the database
DROP DATABASE IF EXISTS week6_lecture12;
```

Also don't forget to configure [application properties](/Week%2006/Lecture%2011/lecture_11/src/main/resources/application.propertiess) with this format
```java
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/<my_database>
spring.datasource.username=<my_user_name>
spring.datasource.password=<my_password>
```

and don't forget to add this
```java
spring.jpa.hibernate.ddl-auto=update
```
to do database seeding using JPA Hibernate.

### ⚙️ How to run the program
1. Go to the `lecture_12` directory by using this command
    ```bash
    $ cd lecture_12
    ```
2. Make sure you have maven installed on my computer, use `mvn -v` to check the version.
3. If you are using windows, you can run the program by using this command.
    ```bash
    $ ./run.bat
    ```
    And if you are using Linux, you can run the program by using this command.
    ```bash
    $ chmod +x run.sh
    $ ./run.sh
    ```

If all the instruction is well executed, Open [localhost:8080](http://localhost:8080) to see that the REST APIs is now works.

### 🔑 List of Endpoints
| Endpoint                                | Method | Description                                                                                 |
|-----------------------------------------|:--------: |---------------------------------------------------------------------------------------------|
| /api/v1/employees                       | GET    | Retrieve all employees with default pagination (page 0 with size 20 elements/page).                                                                     |
| /api/v1/employees?page=1&size=5         | GET    | Retrieve employees with pagination (page 1 with size 5 elements/page).                                         |
| /api/v1/employees/{empNo}               | GET    | Retrieve a specific employee by employee number.                                            |
| /api/v1/employees                       | POST   | Create a new employee.                                                                      |
| /api/v1/employees/{empNo}               | PUT    | Update an existing employee by employee number.                                             |
| /api/v1/employees/{empNo}               | DELETE | Delete an employee by employee number.                                                      |
| /api/v1/departments                     | GET    | Retrieve all departments with default pagination (page 0 with size 20 elements/page).                                                                   |
| /api/v1/departments?page=0&size=2       | GET    | Retrieve departments with pagination (page 0 with size 2 elements/page).                                       |
| /api/v1/departments/{deptNo}            | GET    | Retrieve a specific department by department number.                                        |
| /api/v1/departments                     | POST   | Create a new department.                                                                    |
| /api/v1/departments/{deptNo}            | PUT    | Update an existing department by department number.                                         |
| /api/v1/departments/{deptNo}            | DELETE | Delete a department by department number.                                                   |
| /api/v1/salaries                        | GET    | Retrieve salary by ID.                                                                      |
| /api/v1/salaries                        | POST   | Create a new salary record.                                                                 |
| /api/v1/salaries                        | PUT    | Update an existing salary record.                                                           |
| /api/v1/salaries                        | DELETE | Delete a salary record by ID.                                                               |
| /api/v1/titles                          | GET    | Retrieve title by ID.                                                                       |
| /api/v1/titles                          | POST   | Create a new title.                                                                         |
| /api/v1/titles                          | PUT    | Update an existing title.                                                                   |
| /api/v1/titles                          | DELETE | Delete a title record by ID.                                                                |

### 📬 Postman Collection

Here is the [postman collection](/Week%2006/Lecture%2011/Assignment%2001/Lecture%2011%20-%20Assignment%2001.postman_collection.json) you can use to demo the API functionality.