package ch.mno.commitgame;

import ch.mno.commitgame.readers.LogsProvider;
import ch.mno.commitgame.readers.SVNLogsProvider;
import ch.mno.commitgame.transformers.Game1;
import ch.mno.commitgame.writers.VideoWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

public class CommitGameMain {

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length==0) {
            System.err.println("Syntaxe: [svn log filename] [movie filename]\n   e.g.: svnlog.txt out.mov\n   build svn log with 'svn log > svnlog.txt");
            System.exit(1);
        }

        LogsProvider logsProvider = new SVNLogsProvider(new FileInputStream(args[0]));
        VideoWriter writer = new VideoWriter(new File(args[1]));
        new Game1(logsProvider, writer).process();
        writer.close();
    }

}
