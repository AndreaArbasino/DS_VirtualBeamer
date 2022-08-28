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

    private int currentPosition;
    private ImageIcon currentSlide;

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

        //add all buttons for managing the presentation
        addBottomButtons();
    }

    public void changeFromLeaderToClient(){
        sidePanelManager = new ClientSidePanelManager();
        refresh();

        //remove all buttons for managing the presentation
        bottomPanel.removeAll();
        bottomPanel.revalidate();
        bottomPanel.repaint();
        mainFrame.pack();
    }

    private void addBottomButtons(){
        JButton startButton = new JButton("START");
        StartButtonListener startButtonListener = new StartButtonListener();
        startButton.addActionListener(startButtonListener);
        bottomPanel.add(startButton);

        JButton terminateButton = new JButton("TERMINATE");
        TerminateButtonListener terminateButtonListener = new TerminateButtonListener();
        terminateButton.addActionListener(terminateButtonListener);
        bottomPanel.add(terminateButton);

        JButton nextButton = new JButton("NEXT");
        NextButtonListener nextButtonListener = new NextButtonListener();
        nextButton.addActionListener(nextButtonListener);
        bottomPanel.add(nextButton);

        JButton prevButton = new JButton("PREVIOUS");
        PreviousButtonListener previousButtonListener = new PreviousButtonListener();
        prevButton.addActionListener(previousButtonListener);
        bottomPanel.add(prevButton);
    }

    private void startMainFrame(){
        mainFrame = new JFrame();
        mainFrame.setTitle(controller.getLocalModel().getCurrentGroup().getGroupName() +
                            " (" + controller.getLocalModel().getCurrentGroup().getGroupAddress() + ")");
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //TODO: send a leaveNotificationMessage in multicast containing the local id
            }
        });

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

    //metodo per chiudere la finestra (nel caso di terminazione)
    public void close(){
        mainFrame.dispose();
    }

    private class StartButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO: attraverso il controller manda sia tutte le slide in multicast che un messaggio con la posizione corrente
        }
    }

    private class TerminateButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO: attraverso controller mandare un messaggio per terminare la presentazione
            //TODO: alla ricezione di tale messaggio, presentation si chiude e viene fatto display di  una finestra che informa l'utente
            mainFrame.dispose();
            JOptionPane.showMessageDialog(
                    new JFrame(),
                    "The presentation in the group " + controller.getLocalModel().getCurrentGroup().getGroupName() +
                            "(" + controller.getLocalModel().getCurrentGroup().getGroupAddress() + ")" + "was correctly terminated",
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
        }
    }

    private class PreviousButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO: se non poissibile, display messaggio di errore dicende che si è già all'ultima slide
            //TODO: muovere a slide precedente e mandare messaggio in multicast per far muovere a slide precedente
        }
    }
}
