package bank.dao;

import bank.model.CompanyCustomer;
import bank.model.Customer;
import bank.model.IndividualCustomer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Create, read, update and delete customers (individuals and companies share one table). */
public class CustomerDAO {
    private final Database db;

    public CustomerDAO(Database db) { this.db = db; }

    // ---- Create ----
    public void create(Customer c) throws SQLException {
        String sql = "INSERT INTO customers (customer_id, customer_type, address, phone, email, username, password_hash, "
                + "pin_hash, registration_date, failed_login_attempts, first_name, surname, date_of_birth, id_number, "
                + "company_name, registration_number, contact_person) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = db.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerID());
            ps.setString(2, c instanceof IndividualCustomer ? "INDIVIDUAL" : "COMPANY");
            ps.setString(3, c.getAddress());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getUsername());
            ps.setString(7, c.getPasswordHash());
            ps.setString(8, c.getPinHash());
            ps.setDate(9, Date.valueOf(c.getRegistrationDate()));
            ps.setInt(10, c.getFailedLoginAttempts());
            if (c instanceof IndividualCustomer) {
                IndividualCustomer i = (IndividualCustomer) c;
                ps.setString(11, i.getFirstName());
                ps.setString(12, i.getSurname());
                ps.setDate(13, Date.valueOf(i.getDateOfBirth()));
                ps.setString(14, i.getIdNumber());
                ps.setNull(15, Types.VARCHAR);
                ps.setNull(16, Types.VARCHAR);
                ps.setNull(17, Types.VARCHAR);
            } else {
                CompanyCustomer co = (CompanyCustomer) c;
                ps.setNull(11, Types.VARCHAR);
                ps.setNull(12, Types.VARCHAR);
                ps.setNull(13, Types.DATE);
                ps.setNull(14, Types.VARCHAR);
                ps.setString(15, co.getCompanyName());
                ps.setString(16, co.getRegistrationNumber());
                ps.setString(17, co.getContactPerson());
            }
            ps.executeUpdate();
        }
    }

    // ---- Read ----
    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        try (Connection con = db.connect(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM customers ORDER BY customer_id")) {
            while (rs.next()) list.add(toCustomer(rs));
        }
        return list;
    }

    // ---- Update ----
    public void update(Customer c) throws SQLException {
        String sql = "UPDATE customers SET address=?, phone=?, email=?, failed_login_attempts=?, "
                + "first_name=?, surname=?, company_name=?, contact_person=? WHERE customer_id=?";
        try (Connection con = db.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getAddress());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setInt(4, c.getFailedLoginAttempts());
            if (c instanceof IndividualCustomer) {
                ps.setString(5, ((IndividualCustomer) c).getFirstName());
                ps.setString(6, ((IndividualCustomer) c).getSurname());
                ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.VARCHAR);
            } else {
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.VARCHAR);
                ps.setString(7, ((CompanyCustomer) c).getCompanyName());
                ps.setString(8, ((CompanyCustomer) c).getContactPerson());
            }
            ps.setString(9, c.getCustomerID());
            ps.executeUpdate();
        }
    }

    // ---- Delete ----
    public void delete(String customerId) throws SQLException {
        try (Connection con = db.connect();
             PreparedStatement ps = con.prepareStatement("DELETE FROM customers WHERE customer_id = ?")) {
            ps.setString(1, customerId);
            ps.executeUpdate();
        }
    }

    private Customer toCustomer(ResultSet rs) throws SQLException {
        Customer c;
        if ("INDIVIDUAL".equals(rs.getString("customer_type"))) {
            c = new IndividualCustomer(rs.getString("customer_id"), rs.getString("first_name"), rs.getString("surname"),
                    rs.getDate("date_of_birth").toLocalDate(), rs.getString("id_number"), rs.getString("address"),
                    rs.getString("phone"), rs.getString("email"), rs.getString("username"), "unused");
        } else {
            c = new CompanyCustomer(rs.getString("customer_id"), rs.getString("company_name"),
                    rs.getString("registration_number"), rs.getString("contact_person"), rs.getString("address"),
                    rs.getString("phone"), rs.getString("email"), rs.getString("username"), "unused");
        }
        c.restore(rs.getString("password_hash"), rs.getString("pin_hash"),
                rs.getDate("registration_date").toLocalDate(), rs.getInt("failed_login_attempts"));
        return c;
    }
}
