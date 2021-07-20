package Chat.MyServer;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private Socket socket;
    private Server server;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String nick;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        server.unSubscribe(this);
        Message message = new Message();
        message.setMessage(nick + " вышел из чата");
        server.broadCastMessage(message);
        try {
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authentication() {
        while (true) {
            try {
                AuthMessage message = new Gson().fromJson(dataInputStream.readUTF(), AuthMessage.class);
                String nick = server.getAuthService().getNickByLoginAndPass(message.getLogin(), message.getPassword());
                if (nick != null && !server.isNickBusy(nick)) {
                    message.setAuthenticated(true);
                    dataOutputStream.writeUTF(new Gson().toJson(message));
                    Message broadCastMsg = new Message();
                    broadCastMsg.setMessage(nick + " вошел в чат");
                    server.broadCastMessage(broadCastMsg);
                    server.subscribe(this);
                    this.nick = nick;
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readMessages() throws IOException {
        while (true) {
            Message message = new Gson().fromJson(dataInputStream.readUTF(), Message.class);
            message.setNick(nick);
            System.out.println(message);

            if (!message.getMessage().startsWith("/")) {
                server.broadCastMessage(message);
                continue;
            }
//            /<command> <message>
            String[] tokens = message.getMessage().split("\\s");
            switch (tokens[0]) {
                case "/end":{
                    return;
                }
                case "/w": {
                    if (tokens.length < 3) {
                        Message msg = new Message();
                        msg.setMessage("Не хватает параметров, необходимо отпраить команду следующего вида: /w <ник> <сообщение>");
                        this.sendMessage(msg);
                    }
                    String nick = tokens[1];
                    String msg = tokens[2];
                    server.sendMsgToClient(this, nick, msg);
                    break;
                }
            }
            if ("/end".equals(message.getMessage())) {
                return;
            }
        }

    }



    public void sendMessage(Message message) {
        try {
            dataOutputStream.writeUTF(new Gson().toJson(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }
}
