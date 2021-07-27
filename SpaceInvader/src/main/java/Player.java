import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player {
    Integer lives;
    ImageView tank;
    static double TANK_HEIGHT = 30;
    static double TANK_WIDTH = 45;

    public Player(){
        Image image = new Image("images/player.png",45,30,false,true);
        tank = new ImageView(image);
        lives = 3;
    }

    public void loseLive(){
        lives -= 1;
    }

    public Integer getLives(){
        return lives;
    }
}
