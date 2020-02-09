package ch.mno.commitgame.transformers;

import ch.mno.commitgame.common.DateIterator;
import ch.mno.commitgame.readers.Commit;
import ch.mno.commitgame.readers.LogsProvider;
import ch.mno.commitgame.readers.SpriteExplosionReader;
import ch.mno.commitgame.readers.SpritesFactory;
import ch.mno.commitgame.writers.VideoWriter;
import org.jcodec.scale.AWTUtil;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Game1 {

    public static final SimpleDateFormat SDF_YMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final LogsProvider logsProvider;
    private final VideoWriter writer;
    private final SpriteInstance spriteMonster;
    private final SpriteInstance spriteExplosion;
    private Map<String, Integer> commiterAngle = new HashMap<>();
    int width = 1280;
    int height=960;
    int circleDist = (int)(height*0.45);
    int cx = width/2;
    int cy = height/2;
    private List<Shot> shots = new ArrayList<>();
    private Map<String, SpriteInstance> spritesInstancesCommiters = new HashMap<>();
    private BufferedImage imgBack;

    public Game1(LogsProvider logsProvider, VideoWriter writer) throws IOException {
        this.logsProvider = logsProvider;
        this.writer = writer;

        SpritesFactory spritesFactory = new SpritesFactory();
        spriteMonster = new SpriteInstance(spritesFactory.buildMonster(), width/2, height/2);
        spriteExplosion = new SpriteInstance(SpriteExplosionReader.read(), width/2, height/2);
        imgBack = ImageIO.read(getClass().getResourceAsStream("/back1.png"));

        // Find commiters and compute angles
        List<String> commiters = logsProvider.getCommiters();
        logsProvider.stats();
        for (int i=0; i<commiters.size(); i++) {
            String commiter = commiters.get(i);
            int angle = i * 360 / commiters.size();
            commiterAngle.put(commiter, angle);
            int x = cx +(int)(Math.cos(angle*Math.PI/180.0)*circleDist);
            int y = height-(cy +(int)(Math.sin(angle*Math.PI/180.0)*circleDist));
            spritesInstancesCommiters.put(commiter, new SpriteInstance(spritesFactory.buildMan(angle>90&&angle<270), x, y));
        }
    }

    // Count number of commit per days and user, big circle for many commits
    public void process() {
        List<Commit> commits = new ArrayList<>(logsProvider.getCommits());

        // Iterate until now + 20 days with a step of 4 hours
        DateIterator it = new DateIterator(commits.get(0).getDate(), new Date(new Date().getTime()+1000*3600*24*20), 3600*4);
        Date currDate = null;
        while (it.hasNext()) {
            currDate = it.next();
            System.out.println("Building day " +  SDF_YMDHMS.format(currDate));

            // Read step commits, count by user
            Map<String, Integer> nbCommitsByUser = new HashMap<>();
            while (!commits.isEmpty() && commits.get(0).getDate().before(currDate)) {
                Commit commit = commits.remove(0);
                Integer nb = nbCommitsByUser.get(commit.getUser());
                nbCommitsByUser.put(commit.getUser(), nb==null?1:nb+1);
            }

            // Move shots and remove finished one
            shots = shots.stream()
                    .peek(shot->shot.distance-=2)
                    .filter(shot->shot.distance>5)
                    .collect(Collectors.toList());

            // Add new shots for commiters
            for (Map.Entry<String, Integer> el: nbCommitsByUser.entrySet()) {
                int angle = commiterAngle.get(el.getKey());
                shots.add(new Shot(angle, circleDist, el.getValue()));
            }

            // Write image
            try {
                BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = bi.createGraphics();

                buildImage(bi, g2d, true, true);
                write(g2d, SDF_YMDHMS.format(currDate), 20, 20); // Date
                writer.write(AWTUtil.fromBufferedImageRGB(bi)); // Buffer
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // Explode
        try {
            for (int i=0; i<spriteExplosion.size(); i++) {
                BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = bi.createGraphics();
                buildImage(bi, g2d, false, false);
                write(g2d, SDF_YMDHMS.format(currDate), 20, 20); // Date
                spriteExplosion.write(bi);
                writer.write(AWTUtil.fromBufferedImageRGB(bi)); // Buffer
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Victory 3''
        try {
            for (int i=0; i<24*3; i++) {
                BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = bi.createGraphics();
                buildImage(bi, g2d, false, false);
                write(g2d, SDF_YMDHMS.format(currDate), 20, 20); // Date
                writer.write(AWTUtil.fromBufferedImageRGB(bi)); // Buffer
                g2d.dispose();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void buildImage(BufferedImage bi,  Graphics2D g2d, boolean writeShots, boolean writeMonster) {
//        g2d.drawImage(imgBack,0,0,null);
        g2d.setColor(new Color(101,150,72));
        g2d.fillRect(0,0,width,height);

        // Write commiters
        for (Map.Entry<String, Integer> commiter: commiterAngle.entrySet()) {
            //spritesInstancesCommiters
            int angle = commiter.getValue();
            int x = cx + 35+ (int)(Math.cos(angle*Math.PI/180.0)*circleDist);
            int y = height-(cy +(int)(Math.sin(angle*Math.PI/180.0)*circleDist));
            write(g2d, commiter.getKey(), x, y);
        }
        for (SpriteInstance s: spritesInstancesCommiters.values()) {
            s.write(bi);
        }

        // Write shots
        if (writeShots) {
            for (Shot shot : shots) {
                int x = cx + (int) (Math.cos(shot.angle * Math.PI / 180.0) * shot.distance);
                int y = height - (cy + (int) (Math.sin(shot.angle * Math.PI / 180.0) * shot.distance));
                bi.setRGB(x, y, Color.RED.getRGB());
                int s = 1 + (int) Math.sqrt(shot.size);
                g2d.drawOval(x - s / 2, y - s / 2, s, s);
            }
        }

        // Write monster
        if (writeMonster) {
            spriteMonster.write(bi);
        }
    }

    private void write(Graphics2D g2d, String text, int x, int y) {
        Font font = new Font("TimesRoman", Font.PLAIN, circleDist / 30);
        g2d.setColor(Color.BLUE);
        g2d.setFont(font);
        g2d.drawString(text, x, y);
    }



    private class Shot {
        int angle;
        int distance;
        int size;

        public Shot(int angle, int distance, int size) {
            this.angle = angle;
            this.distance = distance;
            this.size = size;
        }
    }

}
