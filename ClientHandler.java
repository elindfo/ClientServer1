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

    public synchronized void quit(int clientNumber){
        clients.get(getIndexOfClient(clientNumber)).sendTo("TERM");
        clients.remove(getIndexOfClient(clientNumber));
        broadcast(clientNumber, "Client no: " + clientNumber + " has disconnected.");
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
