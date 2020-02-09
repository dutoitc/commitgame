package ch.mno.commitgame.readers;

import java.util.Date;

public class Commit {

    private int revision;
    private String user;
    private Date date;
    private int nbLines;
    private String comment;

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNbLines() {
        return nbLines;
    }

    public void setNbLines(int nbLines) {
        this.nbLines = nbLines;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isEmpty() {
        return revision ==0;
    }

    public void addCommentLine(String line) {
        if (comment==null) {
            comment = line;
        } else {
            comment +="\n"+line;
        }
    }
}
