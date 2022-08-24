package view;

import elementsOfNetwork.User;

import javax.swing.*;

public class UserButton extends JButton {

    private User user;

    public UserButton(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
