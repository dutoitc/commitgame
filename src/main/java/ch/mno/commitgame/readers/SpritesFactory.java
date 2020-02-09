package ch.mno.commitgame.readers;

import ch.mno.commitgame.transformers.Sprite;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Read sprites.png */
public class SpritesFactory {

    private Sprite spriteMonster;
    private Sprite manLeft;
    private Sprite manRight;

    public SpritesFactory() throws IOException {
        BufferedImage bigImg = ImageIO.read(SpritesFactory.class.getResourceAsStream("/sprites2.png"));
        List<BufferedImage> images = new ArrayList<>();
        for (int i=0; i<7; i++) {
            images.add(bigImg.getSubimage((int)(59.14*i),0, 59,39));
        }
        spriteMonster=new Sprite(images);


        List<BufferedImage> imagesLeft = new ArrayList<>();
        List<BufferedImage> imagesRight = new ArrayList<>();
        for (int i=0; i<6; i++) {
            imagesRight.add(bigImg.getSubimage((int)(59.14*i),40, 59,78));

            BufferedImage subImage = bigImg.getSubimage((int)(59.14*i),40, 59,78);
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-59, 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            subImage = op.filter(subImage, null);
            imagesLeft.add(subImage);
        }

        manLeft = new Sprite(imagesLeft);
        manRight = new Sprite(imagesRight);
    }

    public Sprite buildMonster() {
        return spriteMonster;
    }

    public Sprite buildMan(boolean toRight) {
        return toRight?manRight:manLeft;
    }

}
