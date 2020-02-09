package ch.mno.commitgame.readers;

import ch.mno.commitgame.transformers.Sprite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Read sprites.png */
public class SpriteExplosionReader {

    public static Sprite read() throws IOException {
        BufferedImage bigImg = ImageIO.read(SpriteExplosionReader.class.getResourceAsStream("/explosion.png"));
        List<BufferedImage> images = new ArrayList<>();
        images.add(bigImg.getSubimage(0, 128, 64, 64));
        images.add(bigImg.getSubimage(0, 64, 64, 64));
        for (int y=0; y<256; y+=64) {
            for (int x=0; x<256; x+=64) {
                images.add(bigImg.getSubimage(x, y, 64, 64));
            }
        }
        return new Sprite(images);
    }

}
