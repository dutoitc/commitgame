package ch.mno.commitgame.readers;

import java.util.List;

public interface LogsProvider {
    List<String> getCommiters();

    void stats();

    List<Commit> getCommits();
}
