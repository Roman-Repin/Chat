package Chat.MyServer;

public class Message {

    private String nick;
    private String message;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "nick='" + nick + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

