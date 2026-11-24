package bank.dao;

import bank.model.Bank;
import bank.model.Customer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Opens connections to the database (XAMPP MySQL, or H2 as a backup), creates the tables and loads the Bank from them. */
public class Database {
    private final String url;
    private final String user;
    private final String password;

    public Database(String url) { this(url, "sa", ""); }

    public Database(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /** MySQL/MariaDB from XAMPP. The database bac_banking_system is created automatically. XAMPP's default user is root with no password. */
    public static Database mysql() {
        return new Database("jdbc:mysql://localhost:3306/bac_banking_system?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true",
                "root", "");
    }

    /** Saved on disk in the ./data folder (used only if XAMPP/MySQL is not running). */
    public static Database file() { return new Database("jdbc:h2:file:./data/bank"); }

    /** Temporary database in memory (used by the tests). */
    public static Database inMemory(String name) { return new Database("jdbc:h2:mem:" + name + ";DB_CLOSE_DELAY=-1"); }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void createTables() throws SQLException {
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS customers ("
                    + "customer_id VARCHAR(10) PRIMARY KEY, customer_type VARCHAR(12) NOT NULL, "
                    + "address VARCHAR(200), phone VARCHAR(30), email VARCHAR(100), "
                    + "username VARCHAR(50) NOT NULL UNIQUE, password_hash VARCHAR(64) NOT NULL, pin_hash VARCHAR(64), "
                    + "registration_date DATE NOT NULL, failed_login_attempts INT NOT NULL, "
                    + "first_name VARCHAR(50), surname VARCHAR(50), date_of_birth DATE, id_number VARCHAR(30) UNIQUE, "
                    + "company_name VARCHAR(100), registration_number VARCHAR(30) UNIQUE, contact_person VARCHAR(100))");
            s.execute("CREATE TABLE IF NOT EXISTS accounts ("
                    + "account_number VARCHAR(20) PRIMARY KEY, customer_id VARCHAR(10) NOT NULL, "
                    + "account_type VARCHAR(12) NOT NULL, branch VARCHAR(20), date_opened DATE NOT NULL, "
                    + "status VARCHAR(10) NOT NULL, balance DOUBLE NOT NULL, last_interest_date DATE, "
                    + "employer_name VARCHAR(100), employer_address VARCHAR(200), employer_phone VARCHAR(30), salary_date DATE, "
                    + "FOREIGN KEY (customer_id) REFERENCES customers(customer_id))");
            s.execute("CREATE TABLE IF NOT EXISTS transactions ("
                    + "transaction_id INT AUTO_INCREMENT PRIMARY KEY, account_number VARCHAR(20) NOT NULL, "
                    + "tx_date DATETIME NOT NULL, tx_type VARCHAR(12) NOT NULL, amount DOUBLE NOT NULL, "
                    + "balance_after DOUBLE NOT NULL, description VARCHAR(200), "
                    + "FOREIGN KEY (account_number) REFERENCES accounts(account_number))");
        }
    }

    /** Builds the Bank (customers, their accounts and transaction history) from the database. */
    public Bank loadBank(String bankName, String branchCode) throws SQLException {
        CustomerDAO customerDAO = new CustomerDAO(this);
        AccountDAO accountDAO = new AccountDAO(this);
        Bank bank = new Bank(bankName, branchCode);
        for (Customer c : customerDAO.findAll()) {
            accountDAO.loadAccounts(c);
            bank.addExistingCustomer(c);
        }
        return bank;
    }
}
