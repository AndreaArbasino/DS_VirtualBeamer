package view;

import elementsOfNetwork.Lobby;
import model.LocalController;
import model.LocalModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LobbySelection {
    private final JFrame frame;
    private final JPanel topPanel;
    private final JPanel bottomPanel;
    private final JSplitPane splitPane;
    private final LocalController localController;
    private final LocalModel localModel;

    public LobbySelection(LocalController localController) {
        this.localController = localController;
        this.localModel = this.localController.getLocalModel();
        frame = new JFrame();
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        addElementsForGraphTest(false);
    }

    public void start(){
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topPanel.setPreferredSize(new Dimension(300,300));

        displayLobbyButtons();

        frame.add(topPanel);
        JButton refreshButton = new JButton("refresh");
        refreshButton.addActionListener(new RefreshButtonListener());

        JButton createButton = new JButton("Create presentation");
        createButton.addActionListener(new CreateButtonListener());

        bottomPanel.add(refreshButton);
        bottomPanel.add(createButton);

        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(bottomPanel);
        frame.add(splitPane);
        frame.setVisible(true);
        frame.pack();
    }

    public void restart(){
        addElementsForGraphTest(true);
        topPanel.removeAll();
        topPanel.revalidate();
        topPanel.repaint();
        displayLobbyButtons();
        frame.pack();
    }

    private class LobbyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LobbyButton button = (LobbyButton) e.getSource();
            localController.sendJoinMessage(button.getLobby());
            frame.dispose();
        }
    }

    private class RefreshButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            localController.sendDiscoverGroup();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException err) {
                throw new RuntimeException(err);
            }
            restart();
        }
    }

    private class CreateButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
            InsertString insertString = new InsertString(new JFrame());
            String beamGroupName = insertString.askInputString("INSERT GROUP NAME", "Group name choice");

            if (beamGroupName == null){
                System.out.println("no name inserted");
                System.exit(0);
            } else if (beamGroupName.length() == 0){
                System.out.println("empty name inserted");
                ErrorMessageDisplay errorMessageDisplay = new ErrorMessageDisplay(new JFrame());
                errorMessageDisplay.displayErrorMessage("The name of the group must contain at least one character!");
                System.exit(0);
            }
            localController.createBeamGroup(beamGroupName);
        }
    }

    private void displayLobbyButtons(){
        ArrayList<Lobby> lobbies = new ArrayList<>(localModel.getLobbies());
        LobbyButtonListener lobbyButtonListener = new LobbyButtonListener();

        for (Lobby lobby : lobbies){
            LobbyButton lobbyButton = new LobbyButton(lobby);
            lobbyButton.setText(lobby.getNameOfLobby());
            lobbyButton.addActionListener(lobbyButtonListener);
            topPanel.add(lobbyButton);
        }
    }

    private void addElementsForGraphTest(boolean isForRefresh){
        if (isForRefresh){
            localModel.addLobby("adad","adada","Refresh_lobby1");
            localModel.addLobby("adad","adada","Refresh_Nome_lobby_molto_Lungo");
        } else {
            localModel.addLobby("adad","adada","Nome_lobby1");
            localModel.addLobby("adad","adada","Nome_lobby_molto_Lungo");
            localModel.addLobby("adad","adada","NomeLobbyCaso");
        }
    }
}
