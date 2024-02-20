import java.sql.*;
import java.util.Scanner;

class User {
    private Connection connection;
    private Scanner scanner;

    User(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void register() {
        scanner.nextLine();
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        if (userExists(email)) {
            System.out.println("User Already Exists for this Email Address!!");
            return;
        }
        String registerQuery = "INSERT INTO User(full_name, email, password) VALUES(?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(registerQuery);
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Registration Successful!");
            } else {
                System.out.println("Registration Failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String login() {
        scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        String loginQuery = "SELECT * FROM User WHERE email = ? AND password = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(loginQuery);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return email;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean userExists(String email) {
        String query = "SELECT * FROM User WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void changeSecurityPin(String email) {
        scanner.nextLine();
        System.out.print("Enter New Security Pin: ");
        String newPin = scanner.nextLine();
        String updateQuery = "UPDATE User SET security_pin = ? WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, newPin);
            preparedStatement.setString(2, email);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Security Pin Updated Successfully!");
            } else {
                System.out.println("Failed to Update Security Pin!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUserInfo(String email) {
        scanner.nextLine();
        System.out.print("Enter New Full Name: ");
        String newFullName = scanner.nextLine();
        System.out.print("Enter New Email: ");
        String newEmail = scanner.nextLine();
        String updateQuery = "UPDATE User SET full_name = ?, email = ? WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, newFullName);
            preparedStatement.setString(2, newEmail);
            preparedStatement.setString(3, email);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User Information Updated Successfully!");
            } else {
                System.out.println("Failed to Update User Information!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class Accounts {
    private Connection connection;
    private Scanner scanner;

    Accounts(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public long openAccount(String email) {
        if (!accountExists(email)) {
            String openAccountQuery = "INSERT INTO bank_account(account_number, full_name, email, balance, security_pin) VALUES(?, ?, ?, ?, ?)";
            scanner.nextLine();
            System.out.print("Enter Full Name: ");
            String fullName = scanner.nextLine();
            System.out.print("Enter Initial Amount: ");
            double balance = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Enter Security Pin: ");
            String securityPin = scanner.nextLine();
            try {
                long accountNumber = generateAccountNumber();
                PreparedStatement preparedStatement = connection.prepareStatement(openAccountQuery);
                preparedStatement.setLong(1, accountNumber);
                preparedStatement.setString(2, fullName);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setString(5, securityPin);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return accountNumber;
                } else {
                    throw new RuntimeException("Account Creation failed!!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Account Already Exists!");
    }

    public long getAccountNumber(String email) {
        String query = "SELECT account_number from bank_account WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("account_number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Account Number Doesn't Exist!");
    }

    private long generateAccountNumber() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT account_number from bank_account ORDER BY account_number DESC LIMIT 1");
            if (resultSet.next()) {
                long lastAccountNumber = resultSet.getLong("account_number");
                return lastAccountNumber + 1;
            } else {
                return 10000100;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 10000100;
    }

    public boolean accountExists(String email) {
        String query = "SELECT account_number from bank_account WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void closeAccount(long accountNumber) {
        String deleteQuery = "DELETE FROM bank_account WHERE account_number = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setLong(1, accountNumber);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account Closed Successfully!");
            } else {
                System.out.println("Failed to Close Account!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class AccountManager {
    private Connection connection;
    private Scanner scanner;

    AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void creditMoney(long accountNumber) {
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String securityPin = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bank_account WHERE account_number = ? AND security_pin = ?");
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, securityPin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String creditQuery = "UPDATE bank_account SET balance = balance + ? WHERE account_number = ?";
                PreparedStatement updateStatement = connection.prepareStatement(creditQuery);
                updateStatement.setDouble(1, amount);
                updateStatement.setLong(2, accountNumber);
                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println(amount + " credited Successfully");
                    connection.commit();
                    connection.setAutoCommit(true);
                    return;
                } else {
                    System.out.println("Transaction Failed!");
                    connection.rollback();
                    connection.setAutoCommit(true);
                }
            } else {
                System.out.println("Invalid Security Pin!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void debitMoney(long accountNumber) {
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String securityPin = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bank_account WHERE account_number = ? AND security_pin = ?");
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, securityPin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double currentBalance = resultSet.getDouble("balance");
                if (amount <= currentBalance) {
                    String debitQuery = "UPDATE bank_account SET balance = balance - ? WHERE account_number = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(debitQuery);
                    updateStatement.setDouble(1, amount);
                    updateStatement.setLong(2, accountNumber);
                    int rowsAffected = updateStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println(amount + " debited Successfully");
                        connection.commit();
                        connection.setAutoCommit(true);
                        return;
                    } else {
                        System.out.println("Transaction Failed!");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                } else {
                    System.out.println("Insufficient Balance!");
                }
            } else {
                System.out.println("Invalid Security Pin!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transferMoney(long senderAccountNumber) {
        scanner.nextLine();
        System.out.print("Enter Receiver Account Number: ");
        long receiverAccountNumber = scanner.nextLong();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String securityPin = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bank_account WHERE account_number = ? AND security_pin = ?");
            preparedStatement.setLong(1, senderAccountNumber);
            preparedStatement.setString(2, securityPin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double currentBalance = resultSet.getDouble("balance");
                if (amount <= currentBalance) {
                    String debitQuery = "UPDATE bank_account SET balance = balance - ? WHERE account_number = ?";
                    String creditQuery = "UPDATE bank_account SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement debitStatement = connection.prepareStatement(debitQuery);
                    PreparedStatement creditStatement = connection.prepareStatement(creditQuery);
                    debitStatement.setDouble(1, amount);
                    debitStatement.setLong(2, senderAccountNumber);
                    creditStatement.setDouble(1, amount);
                    creditStatement.setLong(2, receiverAccountNumber);
                    int debitRowsAffected = debitStatement.executeUpdate();
                    int creditRowsAffected = creditStatement.executeUpdate();
                    if (debitRowsAffected > 0 && creditRowsAffected > 0) {
                        System.out.println("Transaction Successful!");
                        System.out.println(amount + " Transferred Successfully");
                        connection.commit();
                        connection.setAutoCommit(true);
                        return;
                    } else {
                        System.out.println("Transaction Failed");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                } else {
                    System.out.println("Insufficient Balance!");
                }
            } else {
                System.out.println("Invalid Security Pin!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getBalance(long accountNumber) {
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String securityPin = scanner.nextLine();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT balance FROM bank_account WHERE account_number = ? AND security_pin = ?");
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, securityPin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                System.out.println("Balance: " + balance);
            } else {
                System.out.println("Invalid Pin!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public class BankingApp {
    private static final String URL = "jdbc:mysql://localhost:3305/banking_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "W7301@jqir#";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            User user = new User(connection, scanner);
            Accounts accounts = new Accounts(connection, scanner);
            AccountManager accountManager = new AccountManager(connection, scanner);

            String email;
            long accountNumber;

            while (true) {
                System.out.println("*** WELCOME TO BANKING SYSTEM ***");
                System.out.println();
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int choice1 = scanner.nextInt();
                switch (choice1) {
                    case 1:
                        user.register();
                        break;
                    case 2:
                        email = user.login();
                        if (email != null) {
                            System.out.println("\nUser Logged In!");
                            if (!accounts.accountExists(email)) {
                                System.out.println("\n1. Open a new Bank Account");
                                System.out.println("2. Exit");
                                System.out.print("Enter your choice: ");
                                if (scanner.nextInt() == 1) {
                                    accountNumber = accounts.openAccount(email);
                                    System.out.println("Account Created Successfully");
                                    System.out.println("Your Account Number is: " + accountNumber);
                                } else {
                                    break;
                                }
                            }
                            accountNumber = accounts.getAccountNumber(email);
                            int choice2 = 0;
                            while (choice2 != 5) {
                                System.out.println("\n1. Debit Money");
                                System.out.println("2. Credit Money");
                                System.out.println("3. Transfer Money");
                                System.out.println("4. Check Balance");
                                System.out.println("5. Log Out");
                                System.out.print("Enter your choice: ");
                                choice2 = scanner.nextInt();
                                switch (choice2) {
                                    case 1:
                                        accountManager.debitMoney(accountNumber);
                                        break;
                                    case 2:
                                        accountManager.creditMoney(accountNumber);
                                        break;
                                    case 3:
                                        accountManager.transferMoney(accountNumber);
                                        break;
                                    case 4:
                                        accountManager.getBalance(accountNumber);
                                        break;
                                    case 5:
                                        break;
                                    default:
                                        System.out.println("Enter Valid Choice!");
                                        break;
                                }
                            }
                        } else {
                            System.out.println("Incorrect Email or Password!");
                        }
                        break;
                    case 3:
                        System.out.println("THANK YOU FOR USING BANKING SYSTEM!!!");
                        System.out.println("Exiting System!");
                        return;
                    default:
                        System.out.println("Enter Valid Choice");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
