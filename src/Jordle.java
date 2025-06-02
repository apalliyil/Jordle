import java.util.Random;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.util.Arrays;

/**
 * Class Jordle.
 * @author apalliyil6
 * @version 1.0
 */
public class Jordle extends Application {

    private String guess = "";
    private static String correctWord = "";
    private boolean running = true;
    private int columnCount = 0;
    private int rowCount = 0;

    /**
     * Main method that runs application.
     * @param args passed in arguments
     */
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        //create window title
        primaryStage.setTitle("Jordle");

        //create Jordle header
        Text title = new Text("Jordle");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 50));
        generateWord();

        //create grid (GridPane) and center and set on BorderPane
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        GridPane grid = new GridPane();

        Label[][] wordGrid = new Label[5][6];
        for (int i = 0; i < wordGrid.length; i++) {
            for (int j = 0; j < wordGrid.length; j++) {
                wordGrid[i][j] = new Label();
                wordGrid[i][j].setStyle("-fx-background-color: transparent; -fx-border-color: black;");
                wordGrid[i][j].setPrefSize(60, 60);
                grid.add(wordGrid[i][j], i, j);
            }
        }
        //add grid spacing
        grid.setHgap(5);
        grid.setVgap(5);

        //***fix squishing of individual grids***
        //put grid (GridPane) onto root (BorderPane)
        grid.setAlignment(Pos.CENTER);
        root.setAlignment(grid, Pos.CENTER);
        root.setCenter(grid);


        //add title to BorderPane
        root.setAlignment(title, Pos.CENTER);
        root.setTop(title);

        //create Instructions button, implement event handling with anonymous class and add to BorderPane
        Button instructions = new Button("Instructions");
        instructions.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage instructionWindow = new Stage();
                instructionWindow.setTitle("Instructions");
                Text howToPlay = new Text("You have to guess the five-letter word in six goes or less.\n"
                    + "Hit the enter button to submit a word.\n\n"
                    + "After each guess, the color of the tiles will change.\nThis depends on how close your guess "
                    + "was to the word.\n"
                    + "A gray tile means the letter is not in any spot.\nThe yellow tile means the letter is"
                    + " in the word but is not in the right spot.\n"
                    + "The green tile means the letter is in the word and in the right spot.\n");

                VBox layout = new VBox(30);
                layout.setPadding(new Insets(10));
                layout.getChildren().add(howToPlay);
                Scene scene1 = new Scene(layout, 450, 300);
                instructionWindow.setScene(scene1);
                instructionWindow.show();
            }
        });

        //create text label with message and add to BorderPane
        Label message = new Label("Try guessing a word!");

        //create restart button and add to BorderPane
        Button restart = new Button("Restart");
        //BorderPane.setAlignment(restart, Pos.CENTER_RIGHT);
        //root.setBottom(restart);
        restart.setOnMouseClicked(event -> {
            for (int i = 0; i < wordGrid.length; i++) {
                for (int j = 0; j < wordGrid.length; j++) {
                    wordGrid[i][j].setStyle("-fx-background-color: transparent; -fx-border-color: black;");
                    wordGrid[i][j].setText("");
                }
            }
            rowCount = 0;
            columnCount = 0;
            wordGrid[0][0].requestFocus();
            message.setText("Try guessing a word!");
            correctWord = "";
            generateWord();
            guess = "";
            running = true;
        });
        //take in text input into grid
        root.setOnKeyPressed(e -> {
            if (running) {
                if (e.getCode().equals(KeyCode.BACK_SPACE)) {
                    if (columnCount > 0) {
                        columnCount--;
                        wordGrid[columnCount][rowCount].setText("");
                        String temp = guess;
                        guess = temp.substring(0, temp.length() - 1);
                        //System.out.println("Backspace Guess: " + guess);
                    }
                } else if (columnCount < 5 && e.getCode().isLetterKey()) {
                    wordGrid[columnCount][rowCount].setText(e.getCode().toString());
                    wordGrid[columnCount][rowCount].setAlignment(Pos.CENTER);
                    guess += e.getCode().toString().toLowerCase();
                    //System.out.println(guess);
                    columnCount++;
                } else if (columnCount == 5 && e.getCode().equals(KeyCode.ENTER)) {
                    //System.out.println("Word guessed");
                    //method call here
                    //System.out.println(guess);
                    checkGuess(guess, wordGrid, rowCount, message);
                    rowCount++;
                    columnCount = 0;
                    if (rowCount == 5) {
                        message.setText("Game over. The correct word was " + correctWord);
                        //stop taking text
                        running = false;
                        wordGrid[0][0].requestFocus();
                    }
                } else if (columnCount < 5 && e.getCode().equals(KeyCode.ENTER)) {
                    Alert lengthError = new Alert(Alert.AlertType.ERROR);
                    lengthError.setContentText("Invalid guess. Guesses must be 5 letters in length.");
                    lengthError.showAndWait();
                }
                wordGrid[0][0].requestFocus();
            }
        });

        //create HBox to add text and buttons to BorderPane
        HBox bottomButtons = new HBox(8);
        bottomButtons.getChildren().add(message);
        bottomButtons.getChildren().add(instructions);
        bottomButtons.getChildren().add(restart);
        bottomButtons.setAlignment(Pos.CENTER);
        root.setBottom(bottomButtons);

        //creates primary stage and displays it
        Scene mainScene = new Scene(root, 500, 500);
        //mainScene.setFill(Color.BLACK);
        primaryStage.setScene(mainScene);

        primaryStage.show();
    }

    /**
     * Helper method that checks if a guess has the correct letters and if they are in the correct place.
     * @param s String guessed word
     * @param grid 2D Label Array that represents the board where the guesses go
     * @param rowNumber int that represents the row of the grid that the game is on
     * @param bottomMessage Label for the message below the grid
     */
    public void checkGuess(String s, Label[][] grid, int rowNumber, Label bottomMessage) {
        //iterate through each position in word by splitting guess string into an array,
        // if letter in correct place, turn green, else if letter in word, turn yellow, else turn grey
        //if all greens, return new congratulatory message and terminate text input
        //System.out.println("Guess:");
        //System.out.println(s);
        //System.out.println("Correct Word:");
        //System.out.println(correctWord);

        String[] guessSplit = new String[5];
        String[] correctWordSplit = new String[5];

        for (int i = 0; i < guessSplit.length; i++) {
            guessSplit[i] = guess.substring(i, i + 1);
        }

        //System.out.println(Arrays.toString(guessSplit));

        for (int j = 0; j < correctWordSplit.length; j++) {
            correctWordSplit[j] = correctWord.substring(j, j + 1);
        }
        //System.out.println(Arrays.toString(correctWordSplit));

        for (int i = 0; i < guessSplit.length; i++) {
            //System.out.println("guess char: " + guessSplit[i]);
            //System.out.println("correct char: " + correctWordSplit[i]);
            //System.out.println("Green? " + guessSplit[i].equals(correctWordSplit[i]));
            //System.out.println("Yellow? " + Arrays.asList(guessSplit).contains(correctWordSplit[i]));
            //System.out.println("Row number: " + rowNumber + " " + "Index position: " + i);
            if (guessSplit[i].equals(correctWordSplit[i])) {
                //make tile green
                grid[i][rowNumber].setStyle("-fx-background-color: green");
            } else if (Arrays.asList(correctWordSplit).contains(guessSplit[i])) {
                //make tile yellow
                //System.out.println("This is yellow" + Arrays.asList(guessSplit));
                //System.out.println("This is yellow" + correctWordSplit[i]);
                grid[i][rowNumber].setStyle("-fx-background-color: yellow");
            } else {
                //make tile grey
                grid[i][rowNumber].setStyle("-fx-background-color: gray");
            }



        }
        if (s.equals(correctWord)) {
            bottomMessage.setText("Congratulations! You've guessed the word!");
            //stop taking text
            running = false;
        }
        guess = "";
    }

    /**
     * Helper method that generates a new word.
     */
    public static void generateWord() {
        Random rand = new Random();
        int index = rand.nextInt(Words.list.size());
        String cWord = Words.list.get(index);
        //System.out.println("Correct word: " + cWord);
        correctWord += cWord;


    }



}
