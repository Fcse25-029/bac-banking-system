package bank.view;

import bank.controller.LoginController;
import bank.model.Customer;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/** Login screen for customers and bank staff. It only shows things and collects input. */
public class LoginView {
    private final VBox root = new VBox(15);

    public LoginView(LoginController controller, Consumer<Customer> onCustomerLogin, Runnable onStaffLogin) {
        Label title = new Label("BAC BANKING SYSTEM");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        ComboBox<String> role = new ComboBox<>(FXCollections.observableArrayList("Customer", "Bank staff"));
        role.getSelectionModel().selectFirst();
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        Label message = new Label();
        message.setWrapText(true);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);
        form.add(new Label("Login as"), 0, 0);
        form.add(role, 1, 0);
        form.add(new Label("Username"), 0, 1);
        form.add(username, 1, 1);
        form.add(new Label("Password"), 0, 2);
        form.add(password, 1, 2);

        Button login = new Button("Login");
        login.setDefaultButton(true);

        login.setOnAction(e -> {
            try {
                if ("Customer".equals(role.getValue())) {
                    onCustomerLogin.accept(controller.loginCustomer(username.getText(), password.getText()));
                } else {
                    controller.loginStaff(username.getText(), password.getText());
                    onStaffLogin.run();
                }
            } catch (Exception ex) {
                message.setStyle("-fx-text-fill: #c0392b;");
                message.setText(ex.getMessage());
            }
        });

        HBox buttons = new HBox(10, login);
        buttons.setAlignment(Pos.CENTER);
        root.getChildren().addAll(title, form, buttons, message);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
    }

    public Parent getRoot() { return root; }
}
