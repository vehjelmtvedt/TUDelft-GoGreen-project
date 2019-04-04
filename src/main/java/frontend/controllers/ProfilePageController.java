package frontend.controllers;

import com.jfoenix.controls.*;
import data.Achievement;
import data.User;
import data.UserAchievement;
import frontend.gui.Dialog;
import frontend.gui.ProfilePageLogic;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tools.Requests;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ProfilePageController implements Initializable {

    private static User thisUser;

    @FXML
    JFXHamburger menu;

    @FXML
    JFXDrawer drawer;

    @FXML
    AnchorPane mainPane;

    @FXML
    AnchorPane headerPane;

    @FXML
    private ImageView profilePicture;

    @FXML
    private Label userName;

    @FXML
    private JFXTextField firstName;

    @FXML
    private JFXTextField lastName;

    @FXML
    private JFXTextField age;

    @FXML
    private Label email;

    @FXML
    private Label lastseen;

    @FXML
    private Label score;

    @FXML
    private Label level;

    @FXML
    private JFXButton firstNameSave;

    @FXML
    private JFXButton lastNameSave;

    @FXML
    private JFXButton ageSave;

    @FXML
    private JFXProgressBar levelProgress;

    @FXML
    private JFXProgressBar carbonSavedProgress;

    @FXML
    private VBox com;

    @FXML
    private VBox incom;

    public static void setUser(User user) {
        thisUser = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        userName.setText(thisUser.getUsername());
        firstName.setText(thisUser.getFirstName());
        lastName.setText(thisUser.getLastName());
        email.setText(thisUser.getEmail());
        age.setText(thisUser.getAge() + "");
        lastseen.setText(thisUser.getLastLoginDate().toString());
        level.setText("Level: " + (thisUser.getProgress().getLevel()));
        score.setText("Total\nCarbon\nSaved: " + thisUser.getTotalCarbonSaved());
        profilePicture.setImage(new Image("frontend/Pics/user.png"));

        firstNameSave.setOnAction(e -> {
            if (!(firstName.getText().isEmpty())) {
                firstName.setUnFocusColor(Color.BLACK);
                System.out.println("test");
            } else {
                firstName.setUnFocusColor(Color.RED);
                try {
                    Dialog.show(mainPane, "First Name is Empty",
                            "Please Fill in a First Name", "DISMISS", "error");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        lastNameSave.setOnAction(e -> {
            if (!(lastName.getText().isEmpty())) {
                lastName.setUnFocusColor(Color.BLACK);
                System.out.println("test");
            } else {
                lastName.setUnFocusColor(Color.RED);
                try {
                    Dialog.show(mainPane, "Last Name is Empty",
                            "Please Fill in a Last Name", "DISMISS", "error");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        ageSave.setOnAction(e -> {
            if (age.getText().matches("^[0-9]{0,7}$")) {
                age.setUnFocusColor(Color.BLACK);
                System.out.println("test");
            } else {
                age.setUnFocusColor(Color.RED);
                try {
                    Dialog.show(mainPane, "Age is invalid",
                            "Please Fill in a appropriate age value", "DISMISS", "error");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

//        ObservableValue<Number> level = ObservableValue < Number > (5);
//        levelProgress.progressProperty().bind(thisUser.getProgress().getLevel());

        try {
            NotificationPanelController.addNotificationPanel(headerPane, mainPane);
            NavPanelController.setup(drawer, menu);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int count = 0;

        // for every completed achievement module  is created
        // and added to a VBox small pics might be added later
        for (int i = 0; i < thisUser.getProgress().getAchievements().size(); i++) {

            count++;

            HBox hbox = new HBox();

            hbox.setSpacing(10.0);
            ImageView achievementimage = new ImageView();
            Image path = new Image("achievementsimages/" + thisUser.getProgress()
                    .getAchievements().get(i).getId() + ".png");
            achievementimage.setFitHeight(32);
            achievementimage.setFitWidth(32);
            achievementimage.setImage(path);

            Text name = new Text(i + 1 + ") " + ProfilePageLogic.getNameString(
                    thisUser.getProgress().getAchievements().get(i)));
            name.setFill(Color.GREEN);
            Text bonus = new Text(",Got: " + ProfilePageLogic.getBonusString(
                    thisUser.getProgress().getAchievements().get(i)) + " Points");
            Text date = new Text(",Completed On: " + ProfilePageLogic.getDateString(
                    thisUser.getProgress().getAchievements().get(i)) + ".");

            hbox.getChildren().addAll(achievementimage, name, bonus, date);
            com.getChildren().add(hbox);
        }

        if (count == 0) {
            Label noachiements = new Label("No completed achievements yet");
            com.getChildren().add(noachiements);
        }

        for (Achievement a : ProfilePageLogic.getList()) {
            HBox hbox = new HBox();
            hbox.setSpacing(10.0);
            if (!isComplete(a)) {
                ImageView achievementimage1 = new ImageView();
                String image = "achievementsimages/" + a.getId() + ".png";
                Image path1 = new Image("achievementsimages/8.png");

                achievementimage1.setFitHeight(32);
                achievementimage1.setFitWidth(32);
                achievementimage1.setImage(path1);

                Text name = new Text(a.getName());
                Text points = new Text(",Complete to Get: " + a.getBonus() + " points.");

                hbox.getChildren().addAll(achievementimage1, name, points);
                incom.getChildren().add(hbox);
            }

        }

    }

    public static boolean isComplete(Achievement achievement) {
        for (UserAchievement userAchievement : thisUser.getProgress().getAchievements()) {
            if (achievement.getId() == userAchievement.getId()) {
                return true;
            }
        }
        return false;
    }

}




