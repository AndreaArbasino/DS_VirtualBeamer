package view;

import model.LocalController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Presentation {

    private JFrame mainFrame;
    private JPanel bottomPanel;
    private JPanel clientsPanel;
    private SidePanelManager sidePanelManager;
    private final JSplitPane splitPane;
    private final JLabel slideLabel;
    private ImageIcon currentSlide;
    private final LocalController controller;

    public Presentation(LocalController controller) {
        this.controller = controller;
        this.slideLabel = new JLabel("", SwingConstants.CENTER);
        this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    }

    public void startLeaderFrame(){
        sidePanelManager = new LeaderSidePanelManager();
        startMainFrame();
        //TODO: IMPOSTARE SLIDE INIZIALE

        //in the beginning only the start button is present
        JButton startButton = new JButton("START");
        StartButtonListener startButtonListener = new StartButtonListener();
        startButton.addActionListener(startButtonListener);
        bottomPanel.add(startButton);
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
                new ArrayList<>(controller.getLocalModel().getCurrentGroupUsers()),
                controller.getLocalModel().getLocalUser());
        mainFrame.pack();
    }

    public void switchToOtherView(){
        if (sidePanelManager instanceof LeaderSidePanelManager){
            sidePanelManager = new ClientSidePanelManager();
            refresh();

            //remove all buttons for managing the presentation
            bottomPanel.removeAll();
        } else {
            sidePanelManager = new LeaderSidePanelManager();
            refresh();

            //add all buttons for managing the presentation
            addBottomButtons();
        }
        //TODO: check if the following instructions are working ine for both switches or must be put only in the positive evaluation of if clause
        bottomPanel.revalidate();
        bottomPanel.repaint();
        mainFrame.pack();
    }

    private void addBottomButtons(){
        JButton prevButton = new JButton("PREV");
        PreviousButtonListener previousButtonListener = new PreviousButtonListener();
        prevButton.addActionListener(previousButtonListener);
        bottomPanel.add(prevButton);

        JButton terminateButton = new JButton("TERMINATE");
        TerminateButtonListener terminateButtonListener = new TerminateButtonListener();
        terminateButton.addActionListener(terminateButtonListener);
        bottomPanel.add(terminateButton);

        JButton nextButton = new JButton("NEXT");
        NextButtonListener nextButtonListener = new NextButtonListener();
        nextButton.addActionListener(nextButtonListener);
        bottomPanel.add(nextButton);
    }

    private void startMainFrame(){
        currentSlide = null;
        mainFrame = new JFrame();
        mainFrame.setTitle(controller.getLocalModel().getCurrentGroupName() +
                            " (" + controller.getLocalModel().getCurrentGroupAddress() + ")");
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                controller.sendLeaveNotificationMessage();
                System.out.println("Event of closing correctly captured");
            }
        });

        mainFrame.add(
                new JLabel("Username: " + controller.getLocalModel().getUsername() +
                        " , ID: " + controller.getLocalModel().getLocalId() +
                        " , IP: " + controller.getLocalModel().getLocalUser().getIpAddress()),
                BorderLayout.SOUTH);


        slideLabel.setPreferredSize(new Dimension(500,500));
        slideLabel.setText("The presentation is not started yet!");
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        clientsPanel = new JPanel();
        clientsPanel.setLayout(new BoxLayout(clientsPanel, BoxLayout.Y_AXIS));
        sidePanelManager.createPanel(clientsPanel,
                                    new ArrayList<>(controller.getLocalModel().getCurrentGroupUsers()),
                                    controller.getLocalModel().getLocalUser());
        mainFrame.add(clientsPanel, BorderLayout.WEST);
        splitPane.setTopComponent(slideLabel);
        splitPane.setBottomComponent(bottomPanel);

        mainFrame.add(splitPane, BorderLayout.CENTER);

        mainFrame.setVisible(true);
        mainFrame.pack();
    }

    //metodo per chiudere la finestra (nel caso di terminazione)
    public void close(){
        mainFrame.dispose();
    }

    private class StartButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

            controller.startPresentation();
            slideLabel.removeAll();
            currentSlide = new ImageIcon(controller.getCurrentSlide());
            slideLabel.setIcon(currentSlide);
            slideLabel.repaint();
            bottomPanel.removeAll();
            addBottomButtons();
            bottomPanel.repaint();
            //TODO: attraverso il controller manda sia tutte le slide in multicast che un messaggio con la posizione corrente
        }
    }

    private class TerminateButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            controller.sendTerminationMessage();
            mainFrame.dispose();
            //TODO: uccide thread che ascolta messaggi per entrare
            JOptionPane.showMessageDialog(
                    new JFrame(),
                    "The presentation in the group " + controller.getLocalModel().getCurrentGroupName() +
                            " (" + controller.getLocalModel().getCurrentGroupAddress() + ")" + " was correctly terminated",
                    "presentation correctly terminated",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    private class NextButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO: se non poissibile, display messaggio di errore dicende che si è già alla prima slide
            //TODO: muovere a slide successiva e mandare messaggio in multicast per far muovere a slide successiva
            try{
                currentSlide = new ImageIcon(controller.getNextSlide());
                slideLabel.setIcon(currentSlide);
                slideLabel.repaint();
            } catch (IndexOutOfBoundsException err){
                JOptionPane.showMessageDialog(
                        new JFrame(),
                        "There aren't other slides after the one displayed!",
                        "ATTENTION!",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class PreviousButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO: se non poissibile, display messaggio di errore dicende che si è già all'ultima slide
            //TODO: muovere a slide precedente e mandare messaggio in multicast per far muovere a slide precedente
            try{
                currentSlide = new ImageIcon(controller.getPreviousSlide());
                slideLabel.setIcon(currentSlide);
                slideLabel.repaint();
            } catch (IndexOutOfBoundsException err){
                JOptionPane.showMessageDialog(
                        new JFrame(),
                        "There aren't other slides before the one displayed!",
                        "ATTENTION!",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
