package bank.controller;

import bank.dao.AccountDAO;
import bank.dao.CustomerDAO;
import bank.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** What bank staff can do: open accounts, update customer records and pay monthly interest. */
public class StaffController {
    private final Bank bank;
    private final CustomerDAO customerDAO;
    private final AccountDAO accountDAO;

    public StaffController(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        this.bank = bank;
        this.customerDAO = customerDAO;
        this.accountDAO = accountDAO;
    }

    public List<Customer> getCustomers() { return new ArrayList<>(bank.getCustomers()); }

    /** Opens an account on behalf of a customer (found by customer ID or username). */
    public Account openAccount(String customerIdOrUsername, String type, String depositText,
                               String employerName, String employerAddress) throws BankException {
        Customer customer = bank.findCustomerById(String.valueOf(customerIdOrUsername).trim());
        if (customer == null) customer = bank.findCustomer(String.valueOf(customerIdOrUsername).trim());
        double deposit = 0;
        if (depositText != null && !depositText.trim().isEmpty()) {
            try {
                deposit = Double.parseDouble(depositText.trim());
            } catch (NumberFormatException e) {
                throw new BankException("Amount must be a number, for example 500.00");
            }
        }
        Account account = bank.openAccount(customer, type, deposit, employerName, employerAddress);
        try {
            accountDAO.create(account);
            if (account.getLastTransaction() != null) accountDAO.saveTransaction(account.getLastTransaction());
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
        return account;
    }

    public void updateCustomer(Customer customer, String address, String phone, String email) throws BankException {
        if (isEmpty(address) || isEmpty(phone) || isEmpty(email)) throw new BankException("All fields are required.");
        customer.setAddress(address.trim());
        customer.setPhone(phone.trim());
        customer.setEmail(email.trim());
        save(customer);
    }

    public void unlockCustomer(Customer customer) throws BankException {
        customer.unlock();
        save(customer);
    }

    /**
     * Pays this month's interest. Saving each payment is tried up to 3 times; if all fail the
     * process stops and staff are told (see the PayInterest state diagram). Returns accounts paid.
     */
    public int payMonthlyInterest() throws BankException {
        List<Transaction> paid = bank.payMonthlyInterest();
        for (Transaction t : paid) {
            Account account = bank.findAccount(t.getAccountNumber());
            boolean saved = false;
            for (int attempt = 1; attempt <= 3 && !saved; attempt++) {
                try {
                    accountDAO.update(account);
                    accountDAO.saveTransaction(t);
                    saved = true;
                } catch (SQLException e) {
                    saved = false;
                }
            }
            if (!saved) {
                throw new BankException("Interest payment aborted after 3 attempts on account "
                        + t.getAccountNumber() + ". Bank staff must check it.");
            }
        }
        return paid.size();
    }

    private void save(Customer c) throws BankException {
        try {
            customerDAO.update(c);
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    private static boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }
}
