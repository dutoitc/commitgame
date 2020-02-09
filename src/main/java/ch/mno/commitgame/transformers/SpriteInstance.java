package ch.mno.commitgame.transformers;

import java.awt.image.BufferedImage;

public class SpriteInstance {

    private final Sprite sprite;
    int step;
    int cx;
    int cy;

    public SpriteInstance(Sprite sprite, int cx, int cy) {
        this.sprite = sprite;
        this.cx = cx;
        this.cy = cy;
        this.step = (int)(Math.random()*sprite.count());
    }

    public BufferedImage getNext() {
        BufferedImage image = sprite.getSprite(step);
        step = (step+1) % sprite.count();
        return image;
    }

    public int size() {
        return sprite.count();
    }

    public void write(BufferedImage bi) {
        BufferedImage next = getNext();
        bi.createGraphics().drawImage(next, cx-next.getWidth()/2, cy-next.getHeight()/2, null);
    }
}
