import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class applicationController {

    @FXML
    private Pane paneObj;

    @FXML
    public void initialize() {
        applicationLogic.setPaneObject(paneObj);

        /* Add listener for resize in order to draw the board and disks again */
        paneObj.widthProperty().addListener( e -> applicationLogic.drawBoard());
        paneObj.heightProperty().addListener( e -> applicationLogic.drawBoard());
    }

    @FXML
    void onClick(MouseEvent event) {
        applicationLogic.placeDisk(event.getSceneX(), event.getSceneY());
    }
}
