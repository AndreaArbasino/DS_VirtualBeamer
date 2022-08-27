package view;

import elementsOfNetwork.User;
import model.LocalController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Presentation {

    private JFrame leaderFrame;
    private JFrame clientFrame;

    private JButton startButton;
    private JButton quitButton;

    private JButton nextButton;
    private JButton prevButton;

    private JPanel bottomPanel;
    private JPanel clientsPanel;

    private int currentPosition;
    private ImageIcon currentSlide;

    private JSplitPane splitPane;
    private JLabel slideLabel;

    private UserButton userButton;

    private LocalController controller;

    public Presentation(LocalController controller) {
        this.controller = controller;
        this.slideLabel = new JLabel();
        this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    }

    public void startLeaderFrame(){
        leaderFrame = new JFrame();
        leaderFrame.setTitle(controller.getLocalModel().getCurrentGroup().getName() + " (" + controller.getLocalModel().getCurrentGroup().getGroupAddress() + ")");
        leaderFrame.setLayout(new BorderLayout());
        leaderFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        slideLabel.setPreferredSize(new Dimension(500,500));
        //TODO: IMPOSTARE SLIDE INIZIALE

        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startButton = new JButton("START");
        StartButtonListener startButtonListener = new StartButtonListener();
        startButton.addActionListener(startButtonListener);
        bottomPanel.add(startButton);

        clientsPanel = new JPanel();
        clientsPanel.setLayout(new BoxLayout(clientsPanel, BoxLayout.Y_AXIS));

        displayParticipantsButtons();

        leaderFrame.add(clientsPanel, BorderLayout.WEST);
        splitPane.setTopComponent(slideLabel);
        splitPane.setBottomComponent(bottomPanel);

        leaderFrame.add(splitPane, BorderLayout.CENTER);
        leaderFrame.setVisible(true);
        leaderFrame.pack();
    }

    private class StartButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //controller.qualcosa
        }
    }

    private class ClientButtonListener implements  ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    public void refreshLeaderPresentation(){
        clientsPanel.removeAll();
        clientsPanel.revalidate();
        clientsPanel.repaint();
        displayParticipantsButtons();
        leaderFrame.pack();
    }

    private void displayParticipantsButtons() {
        ArrayList<User> userList = new ArrayList<>(controller.getLocalModel().getCurrentGroup().getUsers());

        if(1 == userList.size()){ //only the creator is in the group
            JLabel line1 = new JLabel("You are");
            JLabel line2 = new JLabel("the only");
            JLabel line3 = new JLabel("participant!");
            clientsPanel.add(line1);
            clientsPanel.add(line2);
            clientsPanel.add(line3);
        } else {
            ClientButtonListener userButtonListener = new ClientButtonListener();
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

}
