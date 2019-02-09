package guessmypic.gmp;

/**
 * Created by HONGWEI on 2018/9/20.
 */

public class Invite {
    String from,to,fromId,toId;
    public Invite(){}

    public Invite(String from, String fromId, String to,String toId) {
        this.from = from;
        this.to = to;
        this.fromId = fromId;
        this.toId = toId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }
}
