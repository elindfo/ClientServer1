package lab1b;

import java.util.ArrayList;

public class ClientHandler {

    private ArrayList<ClientRepresentation> clients;

    public ClientHandler(ArrayList<ClientRepresentation> clients){
        this.clients = clients;
    }

    public synchronized void add(ClientRepresentation client){
        clients.add(client);
        client.start();
    }

    public synchronized void broadcast(int sendingClient, String message){
        clients.forEach(client -> {
            if(client.getClientNumber() != sendingClient){
                client.sendTo("Client " + sendingClient + ": " + message);
            }
        });
    }

    public synchronized void broadcast(String clientName, String message){
        clients.forEach(client -> {
            if(!client.getNickname().equals(clientName)){
                client.sendTo("Client " + clientName + ": " + message);
            }
        });
    }

    public synchronized void quit(int sendingClient){
        int i = getIndexOfClient(sendingClient);
        String nick = clients.get(i).getNickname();
        clients.get(i).sendTo("TERM");
        clients.remove(getIndexOfClient(sendingClient));
        if(nick.equals("")){
            broadcast(sendingClient, "disconnected");
        }
        else{
            broadcast(nick,  "disconnected");
        }
    }

    public synchronized void who(int sendingClient){
        StringBuffer clientNames = new StringBuffer("Others in chat");
        clientNames.append("\n");
        clients.forEach(client -> {
            if(client.getClientNumber() != sendingClient){
                clientNames.append("Client No: ");
                clientNames.append(client.getClientNumber());
                clientNames.append(", Nickname: ");
                clientNames.append(client.getNickname());
                clientNames.append("\n");
            }
        });
        clients.get(getIndexOfClient(sendingClient)).sendTo(clientNames.toString());
    }

    public synchronized void nickname(int clientNo, String nickname){
        boolean duplicateFound = false;
        for(ClientRepresentation c : clients){
            if(c.getNickname().toUpperCase().equals(nickname.toUpperCase())){
                duplicateFound = true;
                break;
            }
        }
        if(!duplicateFound){
            clients.get(getIndexOfClient(clientNo)).setNickname(nickname);
            clients.get(getIndexOfClient(clientNo)).sendTo("Nickname set: " + nickname);
        }
        else{
            clients.get(getIndexOfClient(clientNo)).sendTo("Nickname unavailable");
        }
    }

    public synchronized void disconnect(int clientNo) {
        int i = getIndexOfClient(clientNo);
        String nick = clients.get(i).getNickname();
        clients.remove(getIndexOfClient(clientNo));
        if(nick.equals("")){
            broadcast(clientNo, "lost connection");
        }
        else{
            broadcast(nick,  "lostConnection");
        }
    }

    private int getIndexOfClient(int clientNumber){
        int index = -1;
        for(int i = 0; i < clients.size(); i++){
            if(clients.get(i).getClientNumber() == clientNumber){
                index = i;
                break;
            }
        }
        return index;
    }
}
