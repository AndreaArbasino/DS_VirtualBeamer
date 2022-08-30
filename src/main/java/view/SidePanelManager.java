package view;

import elementsOfNetwork.User;
import model.LocalController;

import javax.swing.*;
import java.util.ArrayList;

public interface SidePanelManager {
    public void createPanel(JPanel clientsPanel, LocalController controller);
}
