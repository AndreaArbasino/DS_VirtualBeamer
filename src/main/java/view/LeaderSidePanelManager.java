package view;

import elementsOfNetwork.User;
import model.LocalController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LeaderSidePanelManager implements SidePanelManager{
    private LocalController controller;

    @Override
    public void createPanel(JPanel clientsPanel, LocalController controller) {
        this.controller = controller ;

        if(1 == controller.getLocalModel().getCurrentGroupUsers().size()){ //only the creator is in the group
            JLabel line1 = new JLabel("You are");
            JLabel line2 = new JLabel("the only");
            JLabel line3 = new JLabel("participant!");
            clientsPanel.add(line1);
            clientsPanel.add(line2);
            clientsPanel.add(line3);
        } else {
            JButton userButton;
            ClientButtonListener userButtonListener = new ClientButtonListener();
            ArrayList<User> userList = new ArrayList<>(controller.getLocalModel().getCurrentGroupUsers());
            userList.remove(controller.getLocalModel().getLocalUser());
            for (User user : userList){
                userButton = new UserButton(user);
                userButton.setLayout(new BorderLayout());
                JLabel label1 = new JLabel(user.getUsername());
                JLabel label2 = new JLabel("("+user.getIpAddress()+")", SwingConstants.RIGHT);
                userButton.add(BorderLayout.CENTER,label1);
                userButton.add(BorderLayout.SOUTH,label2);
                userButton.addActionListener(userButtonListener);
                clientsPanel.add(userButton);
            }
        }
    }

    private class ClientButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (controller.getLocalModel().isPresentationStarted()){
                //TODO:give to that selected user the leadership
                controller.passLeadershipTo(((UserButton) e.getSource()).getUser());
            } else {
                System.out.println("Error since not possible to pass control yet");
                ErrorMessageDisplay errorMessage= new ErrorMessageDisplay(new JFrame());
                errorMessage.displayErrorMessage("It is not possible to pass the control of the presentation before it is started!");
            }
        }
    }
}
