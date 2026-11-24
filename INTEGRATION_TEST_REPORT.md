# Integration Test Report: BAC BANKING SYSTEM

**Student:** ______________  **Student number:** ______________  **Date:** ______________

## 1. Purpose
Check that the screens, controllers, core model and database work together as one program.

## 2. How the tests work
- Tools: GitHub Codespaces, Maven, JUnit 5 and an H2 in-memory database (a fresh one for each test).
- The screens only call the controllers, so each test calls the controllers the same way the screens do.
- After each flow the `Bank` is rebuilt from the database, to prove the data was really saved.
- Chain tested: controller -> core model -> DAO -> database. Run with `mvn test`.

## 3. Test cases
Fill in **Actual result** and **Status** from your `mvn test` output.

| ID | Test | Expected result | Part A | Actual result | Status |
|----|------|-----------------|--------|---------------|--------|
| IT1 | Register an individual and a company, then log in | IDs CUS0001 and CUS0002; right customer types after reload; password stored as a hash; duplicate username rejected | FR-1, FR-2, FR-13, NFR-8 | | |
| IT2 | Three wrong passwords | Login locked (still locked after reload); works again after staff unlock | NFR-9 | | |
| IT3 | Staff open accounts | Savings, investment (500), cheque (with employer) opened; company cheque uses its own name; under 500 and unknown customer rejected and not saved | FR-3, FR-5, FR-6, FR-18 | | |
| IT4 | Full flow: deposit 250.50, withdraw 50.25 | Balance 200.25 after reload; history has a deposit then a withdrawal with date and balance | FR-7, FR-9, FR-15, NFR-10 | | |
| IT5 | Withdrawal rules | Savings, wrong PIN and bad amount rejected; BWP5000 session limit; new login gives a new allowance | FR-8, NFR-5, NFR-6, NFR-7 | | |
| IT6 | Overdraw a cheque account | Over the balance rejected; exact balance allowed | FR-9, FR-10 | | |
| IT7 | Monthly interest | Savings 1000.50, investment 1050.00, cheque 1000.00; a second run in the same month pays nothing | FR-11, FR-12 | | |
| IT8 | Staff update a customer record | New address saved and found after reload | FR-17 | | |

`ModelTest` also tests the account rules, the PIN and the login lock without a database.

## 4. Results
- Tests run: ____  Passed: ____  Failed: ____
- Paste a screenshot of the `mvn test` output here.

## 5. Problems found and fixed
(none, or list them)

## 6. Conclusion
State whether all layers work together. Limitations: money is stored as `double` to keep the code simple, and a deposit or withdrawal saves the balance and the transaction in two separate database calls.
