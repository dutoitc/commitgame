package ch.mno.commitgame.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SVNLogsProvider implements LogsProvider {

    private List<Commit> commits;

    public SVNLogsProvider(InputStream is) throws IOException, ParseException {
        commits = new SVNLogsReader(is).commits;
        commits.sort(Comparator.comparing(Commit::getDate));
    }

    @Override
    public List<String> getCommiters() {
        return commits.stream()
                .map(c->c.getUser())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public void stats() {
        Map<String, Integer> nbLinesPerUser = new HashMap<>();
        Map<String, Integer> nbCommitsPerUser = new HashMap<>();

        for (Commit commit: commits) {
            if (nbLinesPerUser.containsKey(commit.getUser())) {
                nbLinesPerUser.put(commit.getUser(), nbLinesPerUser.get(commit.getUser())+commit.getNbLines());
                nbCommitsPerUser.put(commit.getUser(), nbCommitsPerUser.get(commit.getUser())+1);
            } else {
                nbLinesPerUser.put(commit.getUser(), commit.getNbLines());
                nbCommitsPerUser.put(commit.getUser(), 1);
            }
        }

        nbLinesPerUser.forEach((key, value)->System.out.println(key+"  " + value + " lines of commit, " + nbCommitsPerUser.get(key) + " commits"));
    }

    @Override
    public List<Commit> getCommits() {
        return commits;
    }


    /** Read SVN logs */
    private class SVNLogsReader {
        private List<Commit> commits = new ArrayList<>();
        private Commit commit = new Commit();
        private final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        private SVNLogsReader(InputStream is) throws IOException, ParseException {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line = reader.readLine();

                // Read all lines
                while (line!=null) {
                    readLine(line);
                    line = reader.readLine();
                }

                // Last one
                if (!commit.isEmpty()) {
                    commits.add(commit);
                }
            }
        }

        private void readLine(String line) throws ParseException {
            if (line.startsWith("---------------")) {
                if (!commit.isEmpty()) {
                    commits.add(commit);
                }
                commit = new Commit();
                return;
            }

            if (line.startsWith("r") && line.endsWith(" lignes")) {
                //     r123154 | sfsdfss | 2020-01-07 12:56:29 +0100 (ven. 07 févr. 2020) | 42 lignes
                String[] spl = line.split("\\|");
                if (spl.length!=4) {
                    throw new RuntimeException("Format not supported: " + line);
                }
                commit.setRevision(Integer.parseInt(spl[0].replaceAll("r", "").trim()));
                commit.setUser(spl[1].trim());
                commit.setNbLines(Integer.parseInt(spl[3].replaceAll(" lignes", "").trim()));
                String sdate = spl[2].substring(1,20);
                commit.setDate(SDF.parse(sdate));
            } else {
                commit.addCommentLine(line);
            }

        }

    }


}
