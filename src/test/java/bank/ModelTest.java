package bank;

import bank.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for the core classes. No database and no screens are involved. */
class ModelTest {
    private Customer person() {
        return new IndividualCustomer("CUS0001", "Thato", "Moyo", LocalDate.of(1995, 5, 21), "123456789",
                "Gaborone", "71234567", "t@mail.com", "thato", "pass1234");
    }

    @Test void depositAddsToBalanceAndRecordsTransaction() throws Exception {
        Account a = new SavingsAccount("A1", "1001", person());
        a.deposit(100.50);
        assertEquals(100.50, a.getBalance());
        assertEquals(1, a.getTransactionHistory().size());
    }

    @Test void invalidAmountsAreRejected() throws Exception {
        Account a = new ChequeAccount("A1", "1001", person(), "Acme", "Gaborone");
        assertThrows(BankException.class, () -> a.deposit(0));
        assertThrows(BankException.class, () -> a.deposit(-5));
        assertThrows(BankException.class, () -> a.deposit(10.123));      // 2 decimal places only
    }

    @Test void savingsNeverAllowsWithdrawals() throws Exception {
        Account a = new SavingsAccount("A1", "1001", person());
        a.deposit(100);
        assertThrows(BankException.class, () -> a.withdraw(10));
        assertEquals(100, a.getBalance());
    }

    @Test void investmentAndChequeRejectWithdrawalsAboveBalance() throws Exception {
        Customer p = person();
        Account inv = new InvestmentAccount("A1", "1001", p, 500);
        Account chq = new ChequeAccount("A2", "1001", p, "Acme", "Gaborone");
        chq.deposit(100);
        inv.withdraw(200);
        chq.withdraw(100);
        assertEquals(300, inv.getBalance());
        assertEquals(0, chq.getBalance());
        assertThrows(BankException.class, () -> inv.withdraw(300.01));
    }

    @Test void investmentNeedsAtLeast500() {
        assertThrows(BankException.class, () -> new InvestmentAccount("A1", "1001", person(), 499.99));
        assertDoesNotThrow(() -> new InvestmentAccount("A1", "1001", person(), 500));
    }

    @Test void chequeEmployerIsOptionalAndAccountNeedsCustomer() {
        assertDoesNotThrow(() -> new ChequeAccount("A1", "1001", person(), "", ""));
        assertThrows(IllegalArgumentException.class, () -> new SavingsAccount("A1", "1001", null));
    }

    @Test void interestRatesAndOncePerMonth() throws Exception {
        Customer p = person();
        SavingsAccount s = new SavingsAccount("A1", "1001", p);
        s.deposit(1000);
        InvestmentAccount i = new InvestmentAccount("A2", "1001", p, 1000);
        assertEquals(0.50, s.payInterest().getAmount());      // 0.05%
        assertEquals(50.00, i.payInterest().getAmount());     // 5%
        assertNull(s.payInterest());                          // already paid this month
    }

    @Test void loginLocksAfterThreeFailedAttempts() throws Exception {
        Customer c = person();
        for (int i = 0; i < 3; i++) assertThrows(BankException.class, () -> c.login("thato", "bad"));
        assertTrue(c.isLocked());
        assertThrows(BankException.class, () -> c.login("thato", "pass1234"));   // locked even with the right password
        c.unlock();
        c.login("thato", "pass1234");
        assertTrue(c.isLoggedIn());
    }

    @Test void pinHasFourDigitsAndPasswordIsHashed() throws Exception {
        Customer c = person();
        assertThrows(BankException.class, () -> c.setPin("123"));
        c.setPin("1234");
        assertTrue(c.verifyPin("1234"));
        assertFalse(c.verifyPin("0000"));
        assertNotEquals("pass1234", c.getPasswordHash());
    }
}
