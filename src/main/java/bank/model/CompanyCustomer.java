package bank.model;

/** A company. */
public class CompanyCustomer extends Customer {
    private String companyName;
    private final String registrationNumber;
    private String contactPerson;

    public CompanyCustomer(String customerID, String companyName, String registrationNumber, String contactPerson,
                           String address, String phone, String email, String username, String password) {
        super(customerID, address, phone, email, username, password);
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.contactPerson = contactPerson;
    }

    @Override public String getDisplayName() { return companyName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
}
