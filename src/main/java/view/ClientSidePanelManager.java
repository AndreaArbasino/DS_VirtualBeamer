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

        System.out.println("Currently there are " + controller.getLocalModel().getCurrentGroupUsers().size() + " participants (Array)");

        if(1 == controller.getLocalModel().getCurrentGroupUsers().size()){ //only the current client is in the group
            JLabel line1 = new JLabel("You are");
            JLabel line2 = new JLabel("the only");
            JLabel line3 = new JLabel("participant!");
            clientsPanel.add(line1);
            clientsPanel.add(line2);
            clientsPanel.add(line3);
        } else {
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
