package view;

import elementsOfNetwork.User;
import model.LocalController;
import model.LocalModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class DownloadSelection {

    private JFrame frame;
    private JPanel panel;
    private UserButton userButton;
    private LocalController localController;
    private LocalModel localModel;

    public DownloadSelection(LocalController localController) {
        this.localController = localController;
        this.localModel = this.localController.getLocalModel();
        frame = new JFrame();
        panel = new JPanel();
    }

    public void start() {
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.setPreferredSize(new Dimension(500, 500));

        ArrayList<User> users = new ArrayList<>(localModel.getCurrentGroupUsers());
        users.remove(localModel.getLocalUser());
        ClientButtonDownloadListener clientButtonDownloadListener = new ClientButtonDownloadListener();


        for (User user : users) {
            userButton = new UserButton(user);
            userButton.setText(user.getUsername());
            userButton.addActionListener(clientButtonDownloadListener);
            panel.add(userButton);
        }

        frame.add(panel);

        frame.setVisible(true);
        frame.pack();
    }

    private class ClientButtonDownloadListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            UserButton button = (UserButton) e.getSource();

            localController.sendDownloadRequestMessage(button.getUser());

            frame.dispose();
        }
    }

}