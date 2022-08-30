package view;

import elementsOfNetwork.User;
import model.LocalController;

import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ClientSidePanelManager implements SidePanelManager {

    @Override
    public void createPanel(JPanel clientsPanel, LocalController controller) {
        UserButton userButton;
        ClientButtonListener userButtonListener = new ClientButtonListener();
        ArrayList<User> userList = new ArrayList<>(controller.getLocalModel().getCurrentGroupUsers());
        userList.remove(controller.getLocalModel().getLocalUser());
        for (User user : userList){
            userButton = new UserButton(user);
            userButton.setLayout(new BorderLayout());
            JLabel label = new JLabel(user.getUsername());
            userButton.add(BorderLayout.CENTER,label);
            userButton.addActionListener(userButtonListener);
            clientsPanel.add(userButton);
        }
    }


    private class ClientButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            UserButton button = (UserButton) e.getSource();
            JOptionPane.showMessageDialog(
                    new JFrame(),
                    "username :" + button.getUser().getUsername() + "\nIP :" + button.getUser().getIpAddress(),
                    "info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
