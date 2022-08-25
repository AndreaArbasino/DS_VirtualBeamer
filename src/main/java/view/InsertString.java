package view;

import javax.swing.*;

public class InsertString {

    private JFrame frame;

    public InsertString(JFrame frame) {
        this.frame = frame;
    }

    public String askInputString(String message, String title){
        return (String) JOptionPane.showInputDialog(
                                frame,
                                message,
                                title,
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                "");
    }
}
