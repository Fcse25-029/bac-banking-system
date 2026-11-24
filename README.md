# BAC BANKING SYSTEM (Part B), built from the submitted Part A

The code follows the Part A class diagram, requirements and use cases. Layers:

| Layer | Package | Classes |
|-------|---------|---------|
| Core model | `bank.model` | `Customer` (abstract), `IndividualCustomer`, `CompanyCustomer`, `Account` (abstract), `SavingsAccount`, `InvestmentAccount`, `ChequeAccount`, `InterestBearing`, `Transaction`, `TransactionType`, `Bank`, `BankException` |
| Database (JDBC, H2) | `bank.dao` | `Database`, `CustomerDAO`, `AccountDAO` |
| Controllers | `bank.controller` | `LoginController`, `AccountController`, `StaffController` |
| Screens (JavaFX) | `bank.view` | `LoginView`, `RegisterDialog`, `AccountView` (customer), `StaffView` |

`App` connects the layers and `Launcher` is the class to run.

## Run
```
mvn test            # unit tests + integration tests
mvn javafx:run      # start the program
```
Or run `Launcher.java` with the Run button. Data is saved in the `data` folder (delete it to reset).

## Logins
- Bank staff (demo): `staff` / `staff123`
- Customers: register on the login screen (individual or company). Bank staff then open their accounts.

## Part A to code
| Part A | Where in the code |
|--------|-------------------|
| FR-1, FR-2, FR-19 individual or company, unique ID | `IndividualCustomer`, `CompanyCustomer`, `Bank.registerCustomer`, `Bank.nextCustomerId` |
| FR-3, FR-18 only bank staff open accounts | `Bank.openAccount`, `StaffController.openAccount`, `StaffView` (the customer screen has no open-account option) |
| FR-4 account needs a customer | `Account` constructor rejects a null owner |
| FR-5 investment needs BWP500 | `InvestmentAccount` constructor |
| FR-6 cheque account | `ChequeAccount` (employer details are optional), `Bank.openAccount` |
| FR-7 to FR-10 deposits and withdrawals | `Account.deposit`, `withdraw()` in each account class |
| FR-11, FR-12 interest | `InterestBearing`, `SavingsAccount`, `InvestmentAccount`, `Bank.payMonthlyInterest` |
| FR-13 to FR-16 login, logout, history | `Customer.login`, `Customer.logout`, `Account.getTransactionHistory`, `AccountView` |
| FR-17 staff update records | `StaffController.updateCustomer`, `StaffView` |
| NFR-1 5 minute inactivity logout | `AccountView` (timer) |
| NFR-5 two decimal places | `Account.checkAmount` |
| NFR-6 BWP5000 per session | `AccountController.withdraw` |
| NFR-7 4 digit PIN | `Customer.setPin`, `Customer.verifyPin` |
| NFR-8 hashed passwords and PINs | `Customer.hash` |
| NFR-9 lock after 3 failed logins | `Customer.login` |
| NFR-10 date and time on transactions | `Transaction` |
| PayInterest state diagram and retries | `StaffController.payMonthlyInterest` |
| System Scheduler actor | `App.startScheduler` |

Differences from Part A (kept small): money is a `double`; the demo staff login is fixed in `LoginController`; extra
loading constructors and a `pinHash` field exist so data can be loaded from the database and PINs can be stored.

## GitHub Codespaces
Open the repository in a Codespace. It installs Java 17, Maven and a small desktop.
```
mvn test            # run the tests
mvn javafx:run      # start the program (open port 6080 in the Ports tab to see the window)
```
In Codespaces there is no XAMPP, so the program uses its local H2 file database in the `data` folder.
