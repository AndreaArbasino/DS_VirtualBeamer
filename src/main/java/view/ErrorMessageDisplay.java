package view;

import javax.swing.*;

public class ErrorMessageDisplay {

    private JFrame frame;

    public ErrorMessageDisplay(JFrame frame) {
        this.frame = frame;
    }

    public void displayErrorMessage(String message){
        JOptionPane.showMessageDialog(
                frame,
                message,
                "Error!",
                JOptionPane.ERROR_MESSAGE);
    }
}
