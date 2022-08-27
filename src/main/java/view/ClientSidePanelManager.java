package view;

import elementsOfNetwork.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ClientSidePanelManager implements SidePanelManager {

    @Override
    public void createPanel(JPanel clientsPanel, ArrayList<User> userList, User localUser) {
        UserButton userButton;
        ClientSidePanelManager.ClientButtonListener userButtonListener = new ClientSidePanelManager.ClientButtonListener();
        userList.remove(localUser);
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
