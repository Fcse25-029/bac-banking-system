package bank.controller;

import bank.dao.CustomerDAO;
import bank.model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Registration and login for customers and bank staff. */
public class LoginController {
    private final Bank bank;
    private final CustomerDAO customerDAO;

    public LoginController(Bank bank, CustomerDAO customerDAO) {
        this.bank = bank;
        this.customerDAO = customerDAO;
    }

    public Customer loginCustomer(String username, String password) throws BankException {
        if (isEmpty(username) || isEmpty(password)) throw new BankException("Enter your username and password.");
        Customer customer = bank.findCustomer(username.trim());
        if (customer == null) throw new BankException("Wrong username or password.");
        try {
            customer.login(username.trim(), password);
        } catch (BankException e) {
            save(customer);          // remember the failed attempt (it can lock the login)
            throw e;
        }
        save(customer);
        return customer;
    }

    public void logout(Customer customer) { customer.logout(); }

    /** Bank staff log in with one fixed demo account to keep things simple. */
    public void loginStaff(String username, String password) throws BankException {
        if (!"staff".equals(username) || !"staff123".equals(password)) {
            throw new BankException("Wrong staff username or password.");
        }
    }

    public Customer registerIndividual(String firstName, String surname, String dateOfBirth, String idNumber,
                                       String address, String phone, String email,
                                       String username, String password, String pin) throws BankException {
        require(firstName, "First name");
        require(surname, "Surname");
        require(idNumber, "ID number");
        LocalDate dob = parseDate(dateOfBirth);
        checkCommon(address, phone, email, username, password);
        Customer c = new IndividualCustomer(bank.nextCustomerId(), firstName.trim(), surname.trim(), dob,
                idNumber.trim(), address.trim(), phone.trim(), email.trim(), username.trim(), password);
        return finish(c, pin);
    }

    public Customer registerCompany(String companyName, String registrationNumber, String contactPerson,
                                    String address, String phone, String email,
                                    String username, String password, String pin) throws BankException {
        require(companyName, "Company name");
        require(registrationNumber, "Registration number");
        require(contactPerson, "Contact person");
        checkCommon(address, phone, email, username, password);
        Customer c = new CompanyCustomer(bank.nextCustomerId(), companyName.trim(), registrationNumber.trim(),
                contactPerson.trim(), address.trim(), phone.trim(), email.trim(), username.trim(), password);
        return finish(c, pin);
    }

    private Customer finish(Customer c, String pin) throws BankException {
        c.setPin(pin);                 // must be 4 digits
        bank.registerCustomer(c);      // username, ID number and registration number must be unique
        try {
            customerDAO.create(c);
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
        return c;
    }

    private void checkCommon(String address, String phone, String email, String username, String password) throws BankException {
        require(address, "Address");
        require(phone, "Phone");
        require(email, "Email");
        require(username, "Username");
        require(password, "Password");
        if (password.length() < 4) throw new BankException("Password must be at least 4 characters.");
    }

    private void save(Customer c) throws BankException {
        try {
            customerDAO.update(c);
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    private static void require(String value, String field) throws BankException {
        if (isEmpty(value)) throw new BankException(field + " is required.");
    }

    private static LocalDate parseDate(String text) throws BankException {
        require(text, "Date of birth");
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new BankException("Date of birth must look like 1990-05-21.");
        }
    }

    private static boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }
}
