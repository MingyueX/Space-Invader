import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Random;

public class GameScene {
    Scene gameScene;
    Group game;
    Player player;
    ImageView playerTank;
    AnimationTimer timer;
    AnimationTimer enemyBulletTimer;
    AnimationTimer playerBulletTimer;
    Text scoreNum;
    Text liveNum;
    Text levelNum;
    long enemyLastFire;
    long playerLastFire;
    long soundLastPlay;
    double enemySpeed;
    int invaderSoundIndex;
    double soundInterval;

    public static boolean outOfBound = false;
    public static boolean playerWin = false;

    enum DIRECTION {LEFT, RIGHT, NONE}
    static DIRECTION tankDirection = DIRECTION.NONE;
    public static final double TANK_SPEED = 3.0;
    public static final double PLAYER_BULLET_SPEED = 6.0;
    public static final double ENEMY1_BULLET_SPEED = 4.0;
    public static final double ENEMY2_BULLET_SPEED = 5.0;
    public static final double ENEMY3_BULLET_SPEED = 6.0;
    public static final double ENEMY_SPEED = 0.5;
    public static final double ENEMY_SPEEDUP_V = 20;
    public static final double ENEMY_SPEEDUP_H = 0.05;
    public static final double PLAYER_FIRE_INTERVAL_MIN = 400;
    public static final double ENEMY_FIRE_INTERVAL_MIN = 1200;

    public GameScene() {
        SpaceInvader.invaderRemain = 50;
        enemySpeed = ENEMY_SPEED;

        enemyLastFire = System.currentTimeMillis();
        playerLastFire = System.currentTimeMillis();
        soundLastPlay = System.currentTimeMillis();

        player = new Player();
        playerTank = player.tank;
        playerWin = false;

        invaderSoundIndex = 1;
        soundInterval = 600;

        Text scoreText = new Text("Score: ");
        customizeText(scoreText);
        scoreNum = new Text(SpaceInvader.score.toString());
        customizeText(scoreNum);
        Text liveText = new Text("Lives: ");
        customizeText(liveText);
        liveNum = new Text(player.getLives().toString());
        customizeText(liveNum);
        Text levelText = new Text("Level: ");
        customizeText(levelText);
        levelNum = new Text(SpaceInvader.level.toString());
        customizeText(levelNum);
        HBox scores = new HBox(scoreText,scoreNum);
        HBox lives = new HBox(liveText,liveNum);
        HBox levels = new HBox(levelText,levelNum);

        // initialize invaders
        //   each row of invaders are in a group
        game = new Group();
        double marginTop = Invader.INVADER_HEIGHT;
        for (int row = 0; row < 5; row++) {
            Group invaderInRow = new Group();
            int marginLeft = (int) (SpaceInvader.SCREEN_WIDTH - 10 * Invader.INVADER_WIDTH)/2;
            for (int column = 0; column < 10; column++){
                ImageView invader;
                // using different types of invaders
                if (row == 0) {
                    Invader newInvader = new Invader(3);
                    invader = newInvader.getInvaderImg();
                }
                else if ((row == 1) || (row == 2)) {
                    Invader newInvader = new Invader(2);
                    invader = newInvader.getInvaderImg();
                }
                else {
                    Invader newInvader = new Invader(1);
                    invader = newInvader.getInvaderImg();
                }
                invaderInRow.getChildren().add(invader);
                invader.setX(marginLeft);
                marginLeft += Invader.INVADER_WIDTH;
                invader.setY(marginTop);
            }
            marginTop += Invader.INVADER_HEIGHT;
            game.getChildren().add(invaderInRow);
        }

        game.getChildren().addAll(scores,lives,levels,playerTank);

        // initialize positions in screen
        playerTank.setX((SpaceInvader.SCREEN_WIDTH - Player.TANK_WIDTH)/2);
        playerTank.setY(SpaceInvader.SCREEN_HEIGHT - Player.TANK_HEIGHT - 10);
        scores.setLayoutX(50);
        lives.setLayoutX(500);
        levels.setLayoutX(650);

        gameScene = new Scene(game, SpaceInvader.SCREEN_WIDTH, SpaceInvader.SCREEN_HEIGHT);
        gameScene.setFill(Color.BLACK);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // move the tank
                if (tankDirection == DIRECTION.LEFT && playerTank.getX() > 0) playerTank.setX(playerTank.getX() - TANK_SPEED);
                if (tankDirection == DIRECTION.RIGHT &&
                        playerTank.getX() + Player.TANK_WIDTH < SpaceInvader.SCREEN_WIDTH) playerTank.setX(playerTank.getX() + TANK_SPEED);

                // play invader sounds
                if (System.currentTimeMillis() - 600 > soundLastPlay) {
                    if (invaderSoundIndex == 3){
                        invaderSoundIndex = 1;
                    }
                    soundLastPlay = System.currentTimeMillis();
                    invaderSound(invaderSoundIndex);
                    invaderSoundIndex++;
                }

                for (int row = 0; row < 5; row++) {
                    Group group = (Group) game.getChildren().get(row);
                    for (int i = 0; i < group.getChildren().size(); i++) {
                        ImageView node = (ImageView) group.getChildren().get(i);

                        // if invaders get to the bottom of screen, game over.
                        if (node.getY() + Invader.INVADER_HEIGHT >= SpaceInvader.SCREEN_HEIGHT) {
                            stop();
                            gameOver();
                        }

                        // if player's tank collides an invader, lose 1 life.
                        if (node.getBoundsInParent().intersects(playerTank.getBoundsInParent())) {
                            player.loseLive();
                            liveNum.setText(player.getLives().toString());
                            Random rand = new Random();
                            int randX = rand.nextInt((int)SpaceInvader.SCREEN_WIDTH);
                            playerTank.setX(randX);
                            loseLivesSound();
                        }

                        // check if invaders reach the bound of screen
                        if ((node.getX() <= 0) || (node.getX() + Invader.INVADER_WIDTH >= SpaceInvader.SCREEN_WIDTH)) {
                            outOfBound = true;
                            break;
                        }
                    }
                    if (outOfBound) break;
                }

                // when invaders reach the bound of screen
                if (outOfBound) {
                    Random rand = new Random();
                    int randX = rand.nextInt(4);
                    int randY = rand.nextInt(9);
                    for (int row = 0; row < 5; row++) {
                        Group group = (Group) game.getChildren().get(row);
                        for (int i = 0; i < group.getChildren().size(); i++) {
                            ImageView node = (ImageView) group.getChildren().get(i);
                            node.setY(node.getY() + ENEMY_SPEEDUP_V);

                            // perform a random shoot
                            if (row == randX && i == randY) {
                                double x = node.getX();
                                double y = node.getY();

                                if (row == 0) {
                                    enemyShoot(3,x,y);
                                } else if (row == 1 || row == 2){
                                    enemyShoot(2,x,y);
                                } else {
                                    enemyShoot(1,x,y);
                                }
                            }
                        }
                    }
                    // change direction of invaders
                    enemySpeed *= -1;
                    outOfBound = false;
                }

                // perform random shoots from invaders throughout the game
                Random rand = new Random();
                int randX = rand.nextInt(4);
                int randY = rand.nextInt(9);
                for (int row = 0; row < 5; row++) {
                    Group group = (Group) game.getChildren().get(row);
                    for (int i = 0; i < group.getChildren().size(); i++) {
                        ImageView node = (ImageView) group.getChildren().get(i);
                        node.setX(node.getX() + enemySpeed);

                        if (row == randX && i == randY) {
                            // control the shooting rate of invaders
                            if (System.currentTimeMillis() - ENEMY_FIRE_INTERVAL_MIN > enemyLastFire) {
                                enemyLastFire = System.currentTimeMillis();

                                double x = node.getX();
                                double y = node.getY();

                                // different types of invaders shoot different bullets
                                if (row == 0) {
                                    enemyShoot(3,x,y);
                                } else if (row == 1 || row == 2){
                                    enemyShoot(2,x,y);
                                } else {
                                    enemyShoot(1,x,y);
                                }
                            }
                        }
                    }
                }

                // when player loses all lives or passed level 3, game over.
                if(player.getLives() == 0 || playerWin){
                    stop();
                    gameOver();
                }
            }
        };
    }

    public void animateInvader(){
        timer.start();
    }

    public Scene getGameScene() {
        return gameScene;
    }

    public void customizeText(Text text) {
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        text.setFill(Color.WHITE);
    }

    public void enemyShoot(Integer enemyType, double posX, double posY){
        InvaderBullet invaderBullet = new InvaderBullet(enemyType);
        ImageView bullet = invaderBullet.getBulletImg();
        game.getChildren().add(bullet);
        bullet.setX(posX + Invader.INVADER_WIDTH/2);
        bullet.setY(posY + Invader.INVADER_HEIGHT);

        enemyBulletTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // different bullet speeds
                if(enemyType == 1) {
                    bullet.setY(bullet.getY() + ENEMY1_BULLET_SPEED);
                } else if (enemyType == 2){
                    bullet.setY(bullet.getY() + ENEMY2_BULLET_SPEED);
                } else if (enemyType == 3){
                    bullet.setY(bullet.getY() + ENEMY3_BULLET_SPEED);
                }

                // delete when bullet reach the bottom
                if(bullet.getY() > SpaceInvader.SCREEN_HEIGHT) {
                    game.getChildren().remove(bullet);
                }

                // if enemy bullet collides with player's tank, player lose 1 life.
                if(playerTank.contains(bullet.getX(),bullet.getY())){
                    loseLivesSound();
                    player.loseLive();
                    liveNum.setText(player.getLives().toString());
                    Random rand = new Random();
                    int randX = rand.nextInt((int)SpaceInvader.SCREEN_WIDTH);
                    playerTank.setX(randX);
                }
            }
        };
        enemyBulletTimer.start();
    }

    public void fire(){
        // control shooting rate of player's tank
        if (System.currentTimeMillis() - PLAYER_FIRE_INTERVAL_MIN > playerLastFire) {
            playerLastFire = System.currentTimeMillis();

            PlayerBullet playerBullet = new PlayerBullet();
            ImageView bullet = playerBullet.getBullet();
            game.getChildren().add(bullet);
            bullet.setX(playerTank.getX() + Player.TANK_WIDTH/3 + 4);
            bullet.setY(SpaceInvader.SCREEN_HEIGHT - Player.TANK_HEIGHT - PlayerBullet.PLAYER_BULLET_HEIGHT- 10);
            shootSound();

            playerBulletTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    // bullet movement
                    bullet.setY(bullet.getY() - PLAYER_BULLET_SPEED);

                    // delete if bullet reach top of screen
                    if (bullet.getY() < 0) {
                        stop();
                        game.getChildren().remove(bullet);
                    }

                    for (int row = 0; row < 5; row++) {
                        Group group = (Group) game.getChildren().get(row);
                        for (int i = 0; i < group.getChildren().size(); i++) {
                            Node node = group.getChildren().get(i);

                            // if player's bullet collides an invader, delete the invader,
                            //   player gets points and invaders speed up.
                            if (node.contains(bullet.getX(), bullet.getY())) {
                                stop();
                                killInvaderSound();
                                game.getChildren().remove(bullet);
                                group.getChildren().remove(node);
                                soundInterval -= 10;
                                if (enemySpeed < 0) {
                                    enemySpeed -= ENEMY_SPEEDUP_H;
                                } else {
                                    enemySpeed += ENEMY_SPEEDUP_H;
                                }
                                SpaceInvader.invaderRemain -= 1;
                                if (row == 0) {
                                    SpaceInvader.score += 30;
                                } else if (row == 1 || row == 2) {
                                    SpaceInvader.score += 20;
                                } else {
                                    SpaceInvader.score += 10;
                                }
                                // update score
                                scoreNum.setText(SpaceInvader.score.toString());
                                break;
                            }
                        }
                    }
                }
            };
            playerBulletTimer.start();
        }
    }

    public void gameOver(){
        if (enemyBulletTimer != null) {
            enemyBulletTimer.stop();
        }
        if (playerBulletTimer != null) {
            playerBulletTimer.stop();
        }

        Rectangle msgBox = new Rectangle(SpaceInvader.SCREEN_WIDTH/2,SpaceInvader.SCREEN_HEIGHT/2);
        msgBox.setFill(Color.WHITE);
        msgBox.setArcHeight(50);
        msgBox.setArcWidth(50);
        msgBox.setX((SpaceInvader.SCREEN_WIDTH - msgBox.getWidth())/2);
        msgBox.setY((SpaceInvader.SCREEN_HEIGHT - msgBox.getHeight())/2);

        Text msg;
        if (playerWin) {
            msg = new Text("\nYou Win!");
        } else {
            msg = new Text("\nGameOver");
        }
        msg.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
        msg.setFill(Color.gray(0.2));
        msg.setTextAlignment(TextAlignment.CENTER);

        Text finalScore = new Text("Final score:" + SpaceInvader.score);
        finalScore.setFill(Color.gray(0.2));
        finalScore.setFont(Font.font("Verdana", FontWeight.NORMAL, 20));
        finalScore.setTextAlignment(TextAlignment.CENTER);

        Text instructions = new Text("ENTER - Start New Game\n" +
                "I - Back to Title\n" +
                "Q - Quit Game\n" +
                "1 or 2 or 3 - Start New Game at a specific level");
        instructions.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
        instructions.setFill(Color.gray(0.2));
        instructions.setLineSpacing(8);
        instructions.setTextAlignment(TextAlignment.CENTER);

        VBox box = new VBox(msg,finalScore,instructions);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(20);
        box.setLayoutX((SpaceInvader.SCREEN_WIDTH - msgBox.getWidth())/2 + 20);
        box.setLayoutY((SpaceInvader.SCREEN_HEIGHT - msgBox.getHeight())/2);

        game.getChildren().add(msgBox);
        game.getChildren().add(box);

        SpaceInvader.GAME_OVER = true;
    }

    // sounds
    public void killInvaderSound(){
        String sound = getClass().getClassLoader().getResource("sounds/invaderKilled.wav").toString();
        AudioClip clip = new AudioClip(sound);
        clip.play();
    }

    public void loseLivesSound(){
        String sound = getClass().getClassLoader().getResource("sounds/explosion.wav").toString();
        AudioClip clip = new AudioClip(sound);
        clip.play();
    }

    public void invaderSound(int i){
        String sound = getClass().getClassLoader().getResource("sounds/fastinvader" + i +".wav").toString();
        AudioClip clip = new AudioClip(sound);
        clip.play();
    }

    public void shootSound(){
        String sound = getClass().getClassLoader().getResource("sounds/shoot.wav").toString();
        AudioClip clip = new AudioClip(sound);
        clip.play();
    }
}
