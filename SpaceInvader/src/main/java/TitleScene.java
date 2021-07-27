import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class TitleScene {

    Scene titleScene;

    public TitleScene() {
        // logo
        Image image = new Image("images/si_logo.png", 400, 400, true, true);
        ImageView logo = new ImageView(image);

        // scores for different types of invader
        Invader invader1 = new Invader(1);
        ImageView enemy1 = invader1.getInvaderImg();
        Invader invader2 = new Invader(2);
        ImageView enemy2 = invader2.getInvaderImg();
        Invader invader3 = new Invader(3);
        ImageView enemy3 = invader3.getInvaderImg();
        Text score1 = new Text("- 10pts");
        Text score2 = new Text("- 20pts");
        Text score3 = new Text("- 30pts");
        setTextStyle(score1);
        setTextStyle(score2);
        setTextStyle(score3);
        HBox scoreBox = new HBox(enemy1,score1,enemy2,score2,enemy3,score3);
        scoreBox.setSpacing(6);
        scoreBox.setAlignment(Pos.CENTER);

        // Instructions
        Text instruction = new Text("Instruction");
        instruction.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
        instruction.setFill(Color.gray(0.2));
        instruction.setTextAlignment(TextAlignment.CENTER);

        Text details = new Text("ENTER - Start Game\n" +
                "1 or 2 or 3 - Start Game at a specific level\n" +
                "A or ◀ - Move ship left\n" +
                "D or ▶ - Move ship right\n" +
                "SPACE - Fire!\n" +
                "Q - Quit Game");
        setTextStyle(details);
        details.setLineSpacing(8);

        // student name and number
        Text studentInfo = new Text("Miranda Xie");
        setTextStyle(studentInfo);

        // display all components using Vbox
        VBox root = new VBox(logo,scoreBox,instruction,details,studentInfo);
        root.setSpacing(40);
        root.setAlignment(Pos.TOP_CENTER);

        titleScene = new Scene(root, 800, 600);
    }

    public void setTextStyle(Text text){
        text.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
        text.setTextAlignment(TextAlignment.CENTER);
    }

    public Scene getTitleScene() {
        return titleScene;
    }
}
