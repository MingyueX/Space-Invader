import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class InvaderBullet {
    ImageView bulletImg;
    static double INVADER_BULLET_HEIGHT = 20.0;

    public InvaderBullet(int type){
        String imageName = "images/invaderBullet" + type + ".png";
        Image image = new Image(imageName, 20, 20, true, true);
        bulletImg = new ImageView(image);
    }

    public ImageView getBulletImg() {
        return bulletImg;
    }
}
