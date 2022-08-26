package view;

import model.LocalController;
import model.LocalModel;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.concurrent.TimeUnit;

public class GUI {

    private LocalController localController;
    private LocalModel localModel;
    private String userName;
    private String groupName;

    private JFrame frame;
    private JFrame existingGroupsFrame;
    private JPanel sessionPanel;
    private LobbySelection lobbySelection;
    private Presentation presentation;


    public GUI() {
        frame = new JFrame();
    }

    public void start(){
        InsertString insertString = new InsertString(frame);
        userName = insertString.askInputString("INSERT YOUR USERNAME", "Username choice");

        if (userName == null){
            System.out.println("no name inserted");
            System.exit(0);
        } else if (userName.length() == 0){
            System.out.println("empty name inserted");
            ErrorMessageDisplay errorMessageDisplay = new ErrorMessageDisplay(new JFrame());
            errorMessageDisplay.displayErrorMessage("The username must contain at least one character!");
            System.exit(0);
        }

        localController = new LocalController(userName, this);
        localModel = localController.getLocalModel();

        /* INITIAL DISCOVER */
        localController.sendDiscoverGroup();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        lobbySelection = new LobbySelection(localController);
        lobbySelection.start();
    }

    /**
     * Opens a system window that allows to select the slides (jpg images) and adds them to the model
     */
  public void chooseImages(){
      frame = new JFrame("Select slides");
      frame.setLocationRelativeTo(null);

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JFileChooser fc = new JFileChooser(new File(""));
      fc.setDialogTitle("Select slides");
      fc.setMultiSelectionEnabled(true);
      frame.setVisible(true);
      fc.showOpenDialog(frame);
      File[] selectedFile = fc.getSelectedFiles();
      frame.setVisible(false);

      for (File file : selectedFile) {
          try {
              BufferedImage im = ImageIO.read(file);

              try{
                  Image convertedImage = im.getScaledInstance(540, -1, BufferedImage.TYPE_INT_RGB);
                  im = new BufferedImage(convertedImage.getWidth(null), convertedImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
                  im.getGraphics().drawImage(convertedImage, 0, 0, null);
              } catch (NullPointerException e){
                  System.out.println("wrong type of file selected");
                  ErrorMessageDisplay errorMessageDisplay = new ErrorMessageDisplay(new JFrame());
                  errorMessageDisplay.displayErrorMessage("You have selected a file of the wrong type!");
                  System.exit(0);
              }

              localModel.addSlide(im);
          } catch (IOException e1) {
              e1.printStackTrace();
              ErrorMessageDisplay errorMessageDisplay = new ErrorMessageDisplay(new JFrame());
              errorMessageDisplay.displayErrorMessage("We are sorry but an error occurred!");
              System.exit(0);
          }

      }
      presentation = new Presentation(localController);
      presentation.startLeaderFrame();
  }

}
