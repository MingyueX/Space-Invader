import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;

public class Invader{
    static double INVADER_HEIGHT = 40.0;
    static double INVADER_WIDTH = 50.0;
    ImageView invaderImg;

    public Invader(int type){
        String imageName = "images/invader" + type + ".png";
        Image image = new Image(imageName, INVADER_WIDTH, INVADER_HEIGHT, false, true);
        invaderImg = new ImageView(image);
    }

    public ImageView getInvaderImg() {
        return invaderImg;
    }


}
