import java.util.Date;

public class Request implements java.io.Serializable {
    private final String head;
    private final String source;
    private final String body;
    private final Date date;

    private static final long serialVersionUID = 2L;

    public Request(String head, String body,String source) {
        this.head = head;
        this.body = body;
        this.source = source;
        date = new Date();
    }

    public String getHead() {
        return head;
    }

    public String getBody() {
        return body;
    }

    public String getSource() {
        return source;
    }

    public Date getDate() {
        return date;
    }
}
