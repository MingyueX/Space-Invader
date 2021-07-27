import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlayerBullet {
    static double PLAYER_BULLET_HEIGHT = 20;
    ImageView bullet;

    public PlayerBullet(){
        Image image = new Image("images/playerBullet.png",20,20,true,true);
        bullet = new ImageView(image);
    }

    public ImageView getBullet(){
        return bullet;
    }
}
