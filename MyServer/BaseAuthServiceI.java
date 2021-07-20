package Chat.MyServer;

import java.util.ArrayList;
import java.util.List;

public class BaseAuthServiceI implements AuthService {

    private List<Entry> entries;

    public BaseAuthServiceI() {
        entries = new ArrayList<>();
        entries.add(new Entry("ivan", "password", "Neivanov"));
        entries.add(new Entry("sharik", "gav", "Auf"));
        entries.add(new Entry("otvertka", "shurup", "Kruchu-verchu"));
    }

    private class Entry {
        private String login;
        private String password;
        private String nick;

        public Entry(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }
    }

    @Override
    public void start() {
        System.out.println("Сервис авторизации запущен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис авторизации остановлен");
    }


    @Override
    public String getNickByLoginAndPass(String login, String password) {
        for (Entry entry : entries) {
            if (login.equals(entry.login) && password.equals(entry.password)) {
                return entry.nick;
            }
        }
        return null;
    }
}
