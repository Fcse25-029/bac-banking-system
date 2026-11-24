package bank.model;

import java.time.LocalDate;

/** Cheque account. No interest (FR-10, FR-12). The employer details are optional. */
public class ChequeAccount extends Account {
    private String employerName;
    private String employerAddress;
    private String employerPhone = "";
    private LocalDate salaryDate;

    public ChequeAccount(String accountNumber, String branch, Customer owner,
                         String employerName, String employerAddress) throws BankException {
        super(accountNumber, branch, owner);
        this.employerName = isBlank(employerName) ? "" : employerName.trim();
        this.employerAddress = isBlank(employerAddress) ? "" : employerAddress.trim();
    }

    @Override public String getType() { return "Cheque"; }

    @Override
    public Transaction withdraw(double amount) throws BankException {
        return takeOut(amount);
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    public String getEmployerName() { return employerName; }
    public void setEmployerName(String employerName) { this.employerName = employerName; }
    public String getEmployerAddress() { return employerAddress; }
    public void setEmployerAddress(String employerAddress) { this.employerAddress = employerAddress; }
    public String getEmployerPhone() { return employerPhone; }
    public void setEmployerPhone(String employerPhone) { this.employerPhone = employerPhone == null ? "" : employerPhone; }
    public LocalDate getSalaryDate() { return salaryDate; }
    public void setSalaryDate(LocalDate salaryDate) { this.salaryDate = salaryDate; }
}
