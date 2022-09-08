package view;

import model.LocalController;
import timeElements.LeaderCrashTimer;
import timeElements.SlidesReadyTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Presentation {

    private JFrame mainFrame;
    private JPanel bottomPanel;
    private JPanel clientsPanel;
    private SidePanelManager sidePanelManager;
    private final JSplitPane splitPane;
    private final JLabel slideLabel;
    private ImageIcon currentSlide;
    private final LocalController controller;
    private SlidesReadyTimer timerForSlides;

    public Presentation(LocalController controller) {
        this.controller = controller;
        this.slideLabel = new JLabel("", SwingConstants.CENTER);
        this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    }

    public void startLeaderFrame(){
        sidePanelManager = new LeaderSidePanelManager();
        startMainFrame();

        //in the beginning only the start button is present
        JButton startButton = new JButton("START");
        StartButtonListener startButtonListener = new StartButtonListener();
        startButton.addActionListener(startButtonListener);
        bottomPanel.add(startButton);
        mainFrame.pack();
    }

    public void startClientFrame(){
        sidePanelManager = new ClientSidePanelManager();
        startMainFrame();
        mainFrame.pack();
    }

    public void createHidden(){
        sidePanelManager = new ClientSidePanelManager();
        startMainFrame();
    }

    public void refresh(){
        clientsPanel.removeAll();
        clientsPanel.revalidate();
        clientsPanel.repaint();
        sidePanelManager.createPanel(clientsPanel, controller);
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
        sidePanelManager.createPanel(clientsPanel, controller);
        mainFrame.add(clientsPanel, BorderLayout.WEST);
        splitPane.setTopComponent(slideLabel);
        splitPane.setBottomComponent(bottomPanel);

        mainFrame.add(splitPane, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }

    public void showPresentation(){
        mainFrame.pack();
    }

    //metodo per chiudere la finestra (nel caso di terminazione)
    public void close(){
        mainFrame.dispose();
    }

    public void changeSlide(){
        if (controller.slidesReady()){
            if (timerForSlides != null){
                timerForSlides.close();
            }
            currentSlide = new ImageIcon(controller.getCurrentSlide());
            slideLabel.setIcon(currentSlide);
            slideLabel.setText("");
            slideLabel.repaint();
            mainFrame.pack();
        } else {
            if (timerForSlides != null){
                timerForSlides.resetTimer();
            } else {
                timerForSlides = new SlidesReadyTimer(this);
                timerForSlides.start();
            }
        }
    }

    private class StartButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            controller.startPresentation();
            slideLabel.removeAll();
            slideLabel.setText("");
            currentSlide = new ImageIcon(controller.getCurrentSlide());
            slideLabel.setIcon(currentSlide);
            slideLabel.repaint();
            bottomPanel.removeAll();
            addBottomButtons();
            bottomPanel.repaint();

            controller.sendTotalNumberOfSlidesToGroup();
            controller.sendPresentationImages();
            controller.sendCurrentSlideMessage();

        }

    }

    private class TerminateButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            controller.sendTerminationMessage();
            mainFrame.dispose();
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
            try{
                currentSlide = new ImageIcon(controller.getNextSlide());
                controller.sendCurrentSlideMessage();
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
            try{
                currentSlide = new ImageIcon(controller.getPreviousSlide());
                controller.sendCurrentSlideMessage();
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
