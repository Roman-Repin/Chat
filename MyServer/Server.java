package Chat.MyServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static final int PORT = 8081;
    private List<ClientHandler> clients;
    private AuthService authService;

    public Server() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            authService = new BaseAuthServiceI();
            authService.start();

            clients = new ArrayList<>();
            while (true) {
                System.out.println("Ожидаем подключение клиентов");
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }

    }

    public synchronized void broadcastClientList() {
        StringBuilder sb = new StringBuilder("/clients ");
        for (ClientHandler client : clients) {
            sb.append(client.getNick()).append(" ");
        }
        Message message = new Message();
        message.setMessage(sb.toString());
        broadCastMessage(message);
    }


    public synchronized void sendMsgToClient(ClientHandler from, String nickTo, String msg) {
        for (ClientHandler client : clients) {
            if (client.getNick().equals(nickTo)) {
                System.out.printf("Отправляем личное сообщение от %s, кому %s", from.getNick(), nickTo);
                Message message = new Message();
                message.setNick(from.getNick());
                message.setMessage(msg);
                client.sendMessage(message);
                return;
            }
        }
        System.out.printf("Клиент с ником %s не подключен к чату", nickTo);
        Message message= new Message();
        message.setMessage("Клиент с этим ником не подлючен к чату");
        from.sendMessage(message);
    }


    public synchronized void broadCastMessage(Message message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler client : clients) {
            if (nick.equals(client.getNick())) {
                return true;
            }
        }
        return false;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientList();

    }

    public synchronized void unSubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }
}
