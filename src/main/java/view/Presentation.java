package view;

import elementsOfNetwork.User;
import model.LocalController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        //TODO: GET THE LIST OF CLIENTS IN THE LOBBY MODIFY THIS
        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("pippo", "127.0.0.1"));
        userList.add(new User("pluto", "127.0.0.1"));
        userList.add(new User("nome_molto_molto_lungo", "127.0.0.1"));

        ClientButtonListener userButtonListener = new ClientButtonListener();
        for (User user : userList){
            userButton = new UserButton(user);
            userButton.setText(user.getUsername());
            userButton.addActionListener(userButtonListener);
            clientsPanel.add(userButton);
        }
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




}
