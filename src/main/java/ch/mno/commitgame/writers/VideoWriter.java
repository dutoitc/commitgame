package ch.mno.commitgame.writers;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.model.Picture;

import java.io.File;
import java.io.IOException;

public class VideoWriter {

    private final SequenceEncoder encoder;

    public VideoWriter(File fout) throws IOException {
        encoder = SequenceEncoder.create24Fps(fout);
    }

    public void write(Picture pic) throws IOException {
        encoder.encodeNativeFrame(pic);
    }

    public void close() throws IOException {
        encoder.finish();
    }

}
