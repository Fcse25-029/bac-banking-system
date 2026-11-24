package bank.model;

import java.time.LocalDate;

/** A single person. */
public class IndividualCustomer extends Customer {
    private String firstName;
    private String surname;
    private final LocalDate dateOfBirth;
    private final String idNumber;

    public IndividualCustomer(String customerID, String firstName, String surname, LocalDate dateOfBirth,
                              String idNumber, String address, String phone, String email,
                              String username, String password) {
        super(customerID, address, phone, email, username, password);
        this.firstName = firstName;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.idNumber = idNumber;
    }

    @Override public String getDisplayName() { return firstName + " " + surname; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getIdNumber() { return idNumber; }
}
