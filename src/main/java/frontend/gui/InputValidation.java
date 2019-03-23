package frontend.gui;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import data.LoginDetails;
import data.User;
import frontend.controllers.ActivitiesController;
import frontend.controllers.FriendspageController;
import frontend.controllers.HomepageController;
import frontend.controllers.ProfilePageController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import tools.Requests;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidation {
    private static final String passPattern =
            "((?=.*[a-z]).{6,15})";
    private static final String emailPattern =
            "[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+([.][a-zA-Z]+)+";


    /**
     * .
     * Validation for signing in
     *
     * @param emailField email input field
     * @param passField  password input field
     * @param form       form containing input fields
     */
    public static void signInValidate(TextField emailField,
                                      PasswordField passField, AnchorPane form) throws IOException {

        LoginDetails loginDetails = new LoginDetails(emailField.getText(), passField.getText());

        User loggedUser = Requests.loginRequest(loginDetails);
        if (loggedUser != null) {
            Dialog.show(form, "Login successful", "Welcome to GoGreen, "
                    + loggedUser.getFirstName()
                    + " " + loggedUser.getLastName() + "!", "DISMISS", "sucess");
            HomepageController.setUser(loggedUser);
            ActivitiesController.setUser(loggedUser);
            FriendspageController.setUser(loggedUser);
            FriendspageController.setLoginDetails(loginDetails);
            ProfilePageController.setUser(loggedUser);

            //setup .fxml pages after successfully logging in
            try {
                FXMLLoader loader1 = new FXMLLoader(
                        Main.class.getResource("/frontend/fxmlPages/Homepage.fxml"));
                FXMLLoader loader2 = new FXMLLoader(
                        Main.class.getResource("/frontend/fxmlPages/Activities.fxml"));
                FXMLLoader loader3 = new FXMLLoader(
                        Main.class.getResource("/frontend/fxmlPages/FriendPage.fxml"));
                FXMLLoader loader4 = new FXMLLoader(
                        Main.class.getResource("/frontend/fxmlPages/ProfilePage.fxml"));
                Parent root1 = loader1.load();
                Parent root2 = loader2.load();
                Parent root3 = loader3.load();
                Parent root4 = loader4.load();
                Scene homepage = new Scene(root1, General.getBounds()[0], General.getBounds()[1]);
                Scene activities = new Scene(root2, General.getBounds()[0], General.getBounds()[1]);
                Scene friendPage = new Scene(root3, General.getBounds()[0], General.getBounds()[1]);
                Scene profilePage = new Scene(root4,
                        General.getBounds()[0], General.getBounds()[1]);

                //setup scenes in main class
                Main.setActivities(activities);
                Main.setHomepage(homepage);
                Main.setFriendPage(friendPage);
                Main.setProfilePage(profilePage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Go to homepage after logging in
            StageSwitcher.loginSwitch(Main.getPrimaryStage(), Main.getHomepage(), loggedUser);

        } else {

            Dialog.show(form, "Login failed",
                    "Incorrect credentials. Try again", "DISMISS", "error");
        }
    }

    /**
     * .
     * Validation for input in sign up form
     *
     * @param nameFields    array of fields containing the user's name
     * @param usernameField User's username name field
     * @param emailField    User's email field
     * @param passField     User's password field
     * @param passReField   User's re-password field
     * @param ageField      User's age field
     * @param form          Form containing input fields
     */
    public static void signUpValidate(JFXTextField[] nameFields,
                                      JFXTextField usernameField, JFXTextField emailField,
                                      JFXPasswordField passField, JFXPasswordField passReField,
                                      JFXTextField ageField, AnchorPane form) throws IOException {

        if (!signUpValidateFields(nameFields, usernameField, form)) {
            return;
        }
        if (!signUpValidatePass(emailField, passField, passReField, ageField, form)) {
            return;
        }

        //send requests to the server to see if username and password already exist
        //before proceeding to the questionnaire page
        String username = usernameField.getText();
        String email = emailField.getText();

        if (Requests.validateUserRequest(username)) {
            Dialog.show(form, "Username Error!",
                    "A user already exists with this username. Use another username",
                    "DISMISS", "error");
            return;
        }

        if (Requests.validateUserRequest(email)) {
            Dialog.show(form, "Email Error!", "A user already exists with this email."
                            + "Use another email",
                    "DISMISS", "error");
            return;
        }

        User user = new User(nameFields[0].getText(),
                nameFields[1].getText(),
                Integer.parseInt(ageField.getText()), emailField.getText(),
                usernameField.getText(), passField.getText());


        StageSwitcher.sceneSwitch(Main.getPrimaryStage(), Questionnaire.createScene(user, form));
    }

    private static boolean signUpValidateFields(JFXTextField[] nameFields,
                                                JFXTextField usernameField,
                                                AnchorPane form) throws IOException {
        if (nameFields[0].getText().isEmpty()) {
            Dialog.show(form, "Form Error!", "Please enter your First Name",
                    "DISMISS", "error");
            return false;
        }
        if (nameFields[1].getText().isEmpty()) {
            Dialog.show(form, "Form Error!", "Please enter your Last Name", "DISMISS", "error");
            return false;
        }
        if (usernameField.getText().isEmpty()) {
            Dialog.show(form, "Form Error!", "Please enter a username", "DISMISS", "error");
            return false;
        }
        return true;
    }

    private static boolean signUpValidatePass(JFXTextField emailField,
                                              JFXPasswordField passField,
                                              JFXPasswordField passReField,
                                              JFXTextField ageField,
                                              AnchorPane form) throws IOException {
        if (emailField.getText().isEmpty() || !validateEmail(emailField)) {
            Dialog.show(form, "Form Error!", "Please enter a valid email", "DISMISS", "error");
            return false;
        }

        if (passField.getText().isEmpty() || !validatePassword(passField)) {
            Dialog.show(form, "Form Error!", "Please enter a valid password", "DISMISS", "error");
            return false;
        }
        if (passReField.getText().isEmpty() || !passReField.getText().equals(passField.getText())) {
            Dialog.show(form, "Form Error!", "Passwords do not match", "DISMISS", "error");
            return false;
        }
        if (ageField.getText().isEmpty() || !validateAge(ageField)) {
            Dialog.show(form, "Form Error!", "Please enter a valid age", "DISMISS", "error");
            return false;
        }
        return true;
    }

    private static boolean validatePassword(JFXPasswordField input) {
        String pass = input.getText();
        Pattern pattern = Pattern.compile(passPattern);
        Matcher matcher = pattern.matcher(pass);

        return matcher.matches();
    }

    private static boolean validateEmail(JFXTextField input) {
        String email = input.getText();
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private static boolean validateAge(TextField input) {
        try {
            int age = Integer.parseInt(input.getText());
            return age >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}