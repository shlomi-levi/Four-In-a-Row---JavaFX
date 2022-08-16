import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

public class applicationLogic {

    private static int[][] board = new int[6][7]; // array to represent the board. empty cells in the board would have the value 0.
    private static Pane paneObject;
    private static double widthInterval, heightInterval;

    private static final double HEIGHT_SPACING = 50; // for 'clear' button.
    private static final int EMPTY_CELL = 0, PLAYER_BLACK = 1, PLAYER_RED = 2; // constants to represent the black and red players
    private static final int DISKS_TO_WIN = 4;
    private static final int ROW_NOT_FOUND = -1;
    private static int turn = PLAYER_BLACK; // the black player will start playing first
    private static boolean gameRunning = true;
    private static String winnerString = ""; // for when I resize the pane

    public static void setPaneObject(Pane paneObj) { // since all the logic will occur here I'm going to need the pane reference
        paneObject = paneObj;
    }

    // drawing the board. this will be done every time the window gets resized.
    static void drawBoard() {
        Button b = new Button(gameRunning ? "Clear" : winnerString);
        b.setLayoutX(paneObject.getWidth() / 2);
        b.setLayoutY(10);
        b.addEventHandler(MOUSE_CLICKED, e -> restartGame()); // the button 'b' will be used to restart the game.

        paneObject.getChildren().clear(); // clear the pane
        paneObject.getChildren().add(b); // add the button b

        /* Some calculations to draw the lines in proportion to the window size. HEIGHT_SPACING is used to space the button from the board */
        widthInterval = paneObject.getWidth() / board[0].length;
        heightInterval = (paneObject.getHeight() - HEIGHT_SPACING) / board.length;

        // Draw horizontal lines
        for (int i = 1; i <= board.length - 1; i++) {
            double y = i * heightInterval + HEIGHT_SPACING;
            Line l = new Line(0, y, paneObject.getWidth(), y);
            l.setStroke(Color.BLACK);
            paneObject.getChildren().add(l);
        }

        // Draw vertical lines
        for (int j = 1; j <= board[0].length; j++) {
            Line l = new Line(j * widthInterval, HEIGHT_SPACING, j * widthInterval, paneObject.getHeight());
            l.setStroke(Color.BLACK);
            paneObject.getChildren().add(l);
        }

        // If some disks were already placed, we need to place them again when the window gets resized.
        for(int i = 0 ; i < board.length ; i++)
            for(int j = 0 ; j < board[0].length ; j++)
                if(board[i][j] != 0)
                    placeDiskVisually(i, j, board[i][j]);
    }

    /* Function for restarting the game */
    static void restartGame() {
        gameRunning = true;
        turn = PLAYER_BLACK;
        board = new int[6][7];
        drawBoard();
    }

    /* This function places the disk both visually and in the array */
    static void placeDisk(double x, double y) {
        if(!gameRunning)
            return;

        if( x < 0 || x > paneObject.getWidth())
            return;

        if( y < HEIGHT_SPACING || y > paneObject.getHeight())
            return;

        int col = (int) (x / widthInterval), row = ROW_NOT_FOUND;

        for(int i = board.length - 1 ; i >= 0 ; i--) { // finding the correct row to place the disk on
            if(board[i][col] == EMPTY_CELL) {
                row = i;
                break;
            }
        }

        if(row == ROW_NOT_FOUND) // If all the rows in the selected column are taken
            return;

        board[row][col] = turn;

        placeDiskVisually(row, col, turn); // This is the actual function that places the disk visually.

        if(checkForWin(row, col)) // If there's a winner, return.
            return;

        /* Switch turns */
        if(turn == PLAYER_BLACK)
            turn = PLAYER_RED;

        else
            turn = PLAYER_BLACK;
    }

    /* Place the disk visually on the pane */
    static void placeDiskVisually(int row, int col, int playerID) {
        double centerX = col * widthInterval + (widthInterval / 2);
        double centerY = row * heightInterval + HEIGHT_SPACING + (heightInterval / 2);

        // The disk will catch around 75% of the cell
        final double diskSizeRelativeToCell = 0.75;

        Circle c = new Circle(centerX, centerY, Math.min(diskSizeRelativeToCell * widthInterval / 2, diskSizeRelativeToCell * heightInterval / 2));
        c.setFill(playerID == PLAYER_BLACK ? Color.BLACK : Color.RED);
        paneObject.getChildren().add(c);
    }

    /* End game when there's a winner */
    static void endGame(int turn) {
        gameRunning = false;
        winnerString = "Player ";
        winnerString += (turn == PLAYER_BLACK) ? "Black" : "Red";
        winnerString += " has won the game. Press here to restart";

        Button b = (Button) paneObject.getChildren().get(0);
        b.setText(winnerString);
        b.setLayoutX(paneObject.getWidth() / 2);
        b.setLayoutY(10);
    }

    /* Check for win when a disk was placed. this function checks all the combinations in which a player can win */
    static boolean checkForWin(int row, int col) {
        int rowSequence = 0, colSequence = 0, diagonalSequenceRight = 0, diagonalSequenceLeft = 0;

        /* Row stuff */
        for(int j = col ; j < board[row].length ; j++) { // going right
            if(board[row][j] != turn)
                break;

            rowSequence++;
        }

        if(rowSequence >= DISKS_TO_WIN) {
            endGame(turn);
            return true;
        }

        rowSequence--; // since we are going to count that cell again when going left.

        for(int j = col ; j >=0 ; j--) { // going left
            if(board[row][j] != turn)
                break;

            rowSequence++;
        }

        if(rowSequence >= DISKS_TO_WIN) {
            endGame(turn);
            return true;
        }
        /* End of row stuff */

        /* Column stuff */
        for(int i = row ; i >=0 ; i--) { // going upwards
            if(board[i][col] != turn)
                break;

            colSequence++;
        }

        if(colSequence >= DISKS_TO_WIN) {
            endGame(turn);
            return true;
        }

        colSequence--; // since we are going to count that cell again when going downwards

        for(int i = row ; i < board.length ; i++) { // going downwards
            if(board[i][col] != turn)
                break;

            colSequence++;
        }

        if(colSequence >= DISKS_TO_WIN) {
            endGame(turn);
            return true;
        }
        /* End of column stuff */

        /* Diagonal stuff */

        /* First diagonal - the one that looks like this: /  */

        for(int i = row, j = col ; i >= 0 && j < board[0].length ; i--, j++) {
            if(board[i][j] != turn)
                break;

            diagonalSequenceRight++;
        }

        if(diagonalSequenceRight >= DISKS_TO_WIN) {
            endGame(turn);
            return true;
        }

        diagonalSequenceRight--; // since we are going to count that cell again

        for(int i = row, j = col ; i < board.length && j >= 0 ; i++, j--) {
            if(board[i][j] != turn)
                break;

            diagonalSequenceRight++;
        }

        if(diagonalSequenceRight >= DISKS_TO_WIN) {
            endGame(turn);
            return true;
        }

        /* Second diagonal - the one that looks like this: \  */

        for(int i = row, j = col ; i >= 0 && j >= 0 ; i--, j--) {
            if(board[i][j] != turn)
                break;

            diagonalSequenceLeft++;
        }

        if(diagonalSequenceLeft >= DISKS_TO_WIN) {
            endGame(turn);
            return true;
        }

        diagonalSequenceLeft--; // since we are going to count that cell again.

        for(int i = row, j = col ; i < board.length && j < board[0].length ; i++, j++) {
            if(board[i][j] != turn)
                break;

            diagonalSequenceLeft++;
        }

        if(diagonalSequenceLeft >= DISKS_TO_WIN) {
            endGame(turn);
            return true;
        }
        return false;
    }
}
