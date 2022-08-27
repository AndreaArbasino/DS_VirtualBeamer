package view;

import elementsOfNetwork.User;

import javax.swing.*;
import java.util.ArrayList;

public interface SidePanelManager {
    public void createPanel(JPanel clientsPanel, ArrayList<User> userList, User localUser);
}
