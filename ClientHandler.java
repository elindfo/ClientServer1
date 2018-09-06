package lab1b;

import java.util.ArrayList;

public class ClientHandler {

    private ArrayList<ClientRepresentation> clients;

    public ClientHandler(ArrayList<ClientRepresentation> clients){
        this.clients = clients;
    }

    public synchronized void broadcast(int sendingClient, String message){
        clients.forEach(client -> {
            if(client.getClientNumber() != sendingClient){
                client.sendTo(sendingClient + ": " + message);
            }
        });
    }

    public synchronized void quit(int sendingClient){
        clients.get(getIndexOfClient(sendingClient)).sendTo("TERM");
        clients.remove(getIndexOfClient(sendingClient));
        broadcast(sendingClient, "Client no: " + sendingClient + " has disconnected.");
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
