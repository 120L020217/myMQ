package P2P;

public class Request implements java.io.Serializable {
    private final String head;
    private final String body;

    private static final long serialVersionUID = 1L;

    public Request(String head, String body) {
        this.head = head;
        this.body = body;
    }

    public String getHead() {
        return head;
    }

    public String getBody() {
        return body;
    }
}

