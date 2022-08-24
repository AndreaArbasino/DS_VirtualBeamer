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
    private JFrame frame;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JSplitPane splitPane;
    private JButton refreshButton;
    private JButton createButton;
    private LobbyButton lobbyButton;
    private LocalController localController;
    private LocalModel localModel;

    public LobbySelection(LocalController localController) {
        this.localController = localController;
        this.localModel = this.localController.getLocalModel();
        frame = new JFrame();
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    }

    public void start(){
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topPanel.setPreferredSize(new Dimension(500,500));

        displayLobbyButtons();

        frame.add(topPanel);
        refreshButton = new JButton("refresh");
        refreshButton.addActionListener(new RefreshListener());

        createButton = new JButton("Create presentation");
        createButton.addActionListener(new CreateListener());

        bottomPanel.add(refreshButton);
        bottomPanel.add(createButton);

        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(bottomPanel);
        frame.add(splitPane);
        frame.setVisible(true);
        frame.pack();
    }

    private class LobbyButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //localController.enter that lobby
        }
    }

    private class RefreshListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            localController.sendDiscoverGroup();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException err) {
                throw new RuntimeException(err);
            }
            displayLobbyButtons();
            //TODO: controllare che effettivamente si aggiorni senza fare nulla
        }
    }

    private class CreateListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //in this case a new presentation, and so a lobby... is created
        }
    }

    private void displayLobbyButtons(){
        ArrayList<Lobby> lobbies = new ArrayList<>(localModel.getLobbies());
        LobbyButtonListener lobbyButtonListener = new LobbyButtonListener();


        lobbies.add(new Lobby("adad","adada","Nome_lobby1"));
        lobbies.add(new Lobby("adad","adada","Nome_lobby_molto_Lungo"));
        lobbies.add(new Lobby("adad","adada","vaffanculoJPK"));


        for (Lobby lobby : lobbies){
            lobbyButton = new LobbyButton(lobby);
            lobbyButton.setText(lobby.getNameOfLobby());
            lobbyButton.addActionListener(lobbyButtonListener);
            topPanel.add(lobbyButton);
        }
    }
}
