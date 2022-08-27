package view;

import model.LocalController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Presentation {

    private JFrame mainFrame;

    private JButton startButton;
    private JButton terminateButton;
    private JButton nextButton;
    private JButton prevButton;

    private JPanel bottomPanel;
    private JPanel clientsPanel;
    private SidePanelManager sidePanelManager;

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
        sidePanelManager = new LeaderSidePanelManager();
        startMainFrame();
        //TODO: IMPOSTARE SLIDE INIZIALE

        addBottomButtons();
    }

    public void startClientFrame(){
        sidePanelManager = new ClientSidePanelManager();
        startMainFrame();
        //TODO: IMPOSTARE SLIDE INIZIALE
    }

    public void refresh(){
        clientsPanel.removeAll();
        clientsPanel.revalidate();
        clientsPanel.repaint();
        sidePanelManager.createPanel(clientsPanel,
                new ArrayList<>(controller.getLocalModel().getCurrentGroup().getUsers()),
                controller.getLocalModel().getLocalUser());
        mainFrame.pack();
    }

    public void changeFromClientToLeader(){
        sidePanelManager = new LeaderSidePanelManager();
        refresh();

        //add all buttons
        addBottomButtons();
    }

    public void changeFromLeaderToClient(){
        sidePanelManager = new ClientSidePanelManager();
        refresh();

        //remove all buttons
        bottomPanel.removeAll();
        bottomPanel.revalidate();
        bottomPanel.repaint();
        mainFrame.pack();
    }

    private void addBottomButtons(){
        startButton = new JButton("START");
        StartButtonListener startButtonListener = new StartButtonListener();
        startButton.addActionListener(startButtonListener);
        bottomPanel.add(startButton);

        terminateButton = new JButton("TERMINATE");
        TerminateButtonListener terminateButtonListener = new TerminateButtonListener();
        terminateButton.addActionListener(terminateButtonListener);
        bottomPanel.add(terminateButton);

        nextButton = new JButton("NEXT");
        NextButtonListener nextButtonListener = new NextButtonListener();
        nextButton.addActionListener(nextButtonListener);
        bottomPanel.add(nextButton);

        prevButton = new JButton("PREVIOUS");
        PreviousButtonListener previousButtonListener = new PreviousButtonListener();
        prevButton.addActionListener(previousButtonListener);
        bottomPanel.add(prevButton);
    }

    private void startMainFrame(){
        mainFrame = new JFrame();
        mainFrame.setTitle(controller.getLocalModel().getCurrentGroup().getName() +
                            " (" + controller.getLocalModel().getCurrentGroup().getGroupAddress() + ")");
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        slideLabel.setPreferredSize(new Dimension(500,500));
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        clientsPanel = new JPanel();
        clientsPanel.setLayout(new BoxLayout(clientsPanel, BoxLayout.Y_AXIS));
        sidePanelManager.createPanel(clientsPanel,
                                    new ArrayList<>(controller.getLocalModel().getCurrentGroup().getUsers()),
                                    controller.getLocalModel().getLocalUser());
        mainFrame.add(clientsPanel, BorderLayout.WEST);
        splitPane.setTopComponent(slideLabel);
        splitPane.setBottomComponent(bottomPanel);

        mainFrame.add(splitPane, BorderLayout.CENTER);

        mainFrame.add(
                new JLabel("Username: " + controller.getLocalModel().getUsername() +
                        " , ID: " + controller.getLocalModel().getLocalId() +
                        " , IP: " + controller.getLocalModel().getLocalUser().getIpAddress()),
                BorderLayout.SOUTH);

        mainFrame.setVisible(true);
        mainFrame.pack();
    }

    private class StartButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //controller.qualcosa
        }
    }

    private class TerminateButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //controller.qualcosa
        }
    }

    private class NextButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //controller.qualcosa
        }
    }

    private class PreviousButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //controller.qualcosa
        }
    }
}
