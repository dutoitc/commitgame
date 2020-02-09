package ch.mno.commitgame.transformers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Sprite {

    private final List<BufferedImage> sprites;

    public Sprite(List<BufferedImage> sprites) {
        this.sprites = new ArrayList<>(sprites);
    }

    public int count() {
        return sprites.size();
    }

    public BufferedImage getSprite(int no) {
        return sprites.get(no);
    }

}
