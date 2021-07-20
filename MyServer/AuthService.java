package Chat.MyServer;

public interface AuthService {
    void start();
    void stop();

    String getNickByLoginAndPass(String login, String password);
}
