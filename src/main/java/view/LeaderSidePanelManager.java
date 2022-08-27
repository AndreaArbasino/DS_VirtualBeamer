package view;

import elementsOfNetwork.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LeaderSidePanelManager implements SidePanelManager{

    @Override
    public void createPanel(JPanel clientsPanel, ArrayList<User> userList, User localUser) {
        //ArrayList<User> userList = ;

        if(1 == userList.size()){ //only the creator is in the group
            JLabel line1 = new JLabel("You are");
            JLabel line2 = new JLabel("the only");
            JLabel line3 = new JLabel("participant!");
            clientsPanel.add(line1);
            clientsPanel.add(line2);
            clientsPanel.add(line3);
        } else {
            JButton userButton;
            LeaderSidePanelManager.ClientButtonListener userButtonListener = new LeaderSidePanelManager.ClientButtonListener();
            userList.remove(localUser);
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
            //give to that selected user the leadership
        }
    }
}
