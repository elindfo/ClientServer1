package lab1b;

import java.util.ArrayList;

public class ClientHandler {

    //TODO Fixa så att klienten tas bort från listan om anslutningen avbryts

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

    public synchronized void quit(int sendingClient){
        clients.get(getIndexOfClient(sendingClient)).sendTo("TERM");
        clients.remove(getIndexOfClient(sendingClient));
        broadcast(sendingClient, "Client " + sendingClient + " has disconnected.");
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
        clients.remove(getIndexOfClient(clientNo));
        broadcast(clientNo, "Client " + clientNo + " lost connection");
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
