package view;

import elementsOfNetwork.Lobby;
import elementsOfNetwork.User;

import javax.swing.*;

public class LobbyButton extends JButton {

    private final Lobby lobby;

    public LobbyButton(Lobby lobby) {
        this.lobby = lobby;
    }

    public Lobby getLobby() {
        return lobby;
    }
}
