package view;

import elementsOfNetwork.Lobby;
import model.LocalController;
import model.LocalModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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


    public GUI() {
        frame = new JFrame();
    }

    public void start(){
        InsertString insertString = new InsertString(frame);
        userName = insertString.askInputString("INSERT YOUR USERNAME", "Username choice");

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



        //must be set to be performed after the decision of starting anew presentation is taken
        chooseImages();
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

              Image convertedImage = im.getScaledInstance(540, -1, BufferedImage.TYPE_INT_RGB);
              im = new BufferedImage(convertedImage.getWidth(null), convertedImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
              im.getGraphics().drawImage(convertedImage, 0, 0, null);

              localModel.addSlide(im);
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }

}
