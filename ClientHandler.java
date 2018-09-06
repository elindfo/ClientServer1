package lab1b;

import java.util.ArrayList;

public class ClientHandler {

    private ArrayList<ClientRepresentation> clients;

    public ClientHandler(ArrayList<ClientRepresentation> clients){
        this.clients = clients;
    }


    public synchronized void broadcast(String message){
        clients.forEach(client -> client.getPrintWriter().println(message));
    }


}
