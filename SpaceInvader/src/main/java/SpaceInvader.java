import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SpaceInvader extends Application {
    Scene title,game;
    static Integer level;
    static Integer score;
    GameScene gameScene;
    static Integer invaderRemain;
    public static boolean GAME_OVER;
    public static float SCREEN_WIDTH = 800;
    public static float SCREEN_HEIGHT = 600;

    enum SCENE {TITLE, GAME};

    @Override
    public void start(Stage stage) {
        // play main bgm
        String sound = getClass().getClassLoader().getResource("sounds/spaceInvaders.mpeg").toString();
        AudioClip clip = new AudioClip(sound);
        clip.play();

        // initialize
        level = 1;
        score = 0;
        GAME_OVER = false;
        invaderRemain = 50;
        stage.setTitle("Space Invaders");
        TitleScene titleScene = new TitleScene();
        title = titleScene.getTitleScene();

        setInstructionKeyEffect(stage,title);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // if no invader remaining, player get access to the next level
                if (invaderRemain == 0){
                    level++;
                    // if player passes level3, player wins the game.
                    if (level > 3){
                        GameScene.playerWin = true;
                    } else {
                        startGame(stage);
                    }
                }

                if (GAME_OVER){
                    setInstructionKeyEffect(stage,game);
                    GAME_OVER = false;
                }
            }
        };
        timer.start();

        setScene(stage, SCENE.TITLE);
        stage.show();
    }

    void setScene(Stage stage, SCENE scene) {
        switch(scene) {
            case TITLE:
                stage.setScene(title);
                break;
            case GAME:
                stage.setScene(game);
                break;
        }
    }

    void setGameKeyEffect(Scene scene){
        scene.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case LEFT, A:
                    GameScene.tankDirection = GameScene.DIRECTION.LEFT;
                    break;
                case RIGHT, D:
                    GameScene.tankDirection = GameScene.DIRECTION.RIGHT;
                    break;
                case SPACE:
                    gameScene.fire();
                    break;
                case Q:
                    System.exit(0);
            }
        });
        scene.setOnKeyReleased(event -> {
            GameScene.tankDirection = GameScene.DIRECTION.NONE;
        });
    }

    void startGame(Stage stage){
        gameScene = new GameScene();
        game = gameScene.getGameScene();
        setGameKeyEffect(game);
        setScene(stage, SCENE.GAME);
        gameScene.animateInvader();
    }

    void setInstructionKeyEffect(Stage stage, Scene scene){
        scene.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case ENTER,DIGIT1:
                    level = 1;
                    score = 0;
                    startGame(stage);
                    break;
                case DIGIT2:
                    level = 2;
                    score = 0;
                    startGame(stage);
                    break;
                case DIGIT3:
                    level = 3;
                    score = 0;
                    startGame(stage);
                    break;
                case I:
                    setScene(stage, SCENE.TITLE);
                    break;
                case Q:
                    System.exit(0);
            }
        });
    }
}
