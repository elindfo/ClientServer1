package lab1b;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private ArrayList<ClientRepresentation> clients;
    private ClientHandler clientHandler;
    private int serverPort;
    private boolean running;

    private static int clientNo = 0;

    public Server(int serverPort){
        this.serverPort = serverPort;
        clients = new ArrayList<>();
        running = false;
        clientHandler = new ClientHandler(clients);
    }

    public void start(){

        running = true;

        ServerSocket serverSocket = null;
        PrintWriter out = null;


        try{
            serverSocket = new ServerSocket(serverPort);

            while(running){
                System.out.println("Waiting for connection...");
                Socket clientSocket = serverSocket.accept();
                ClientRepresentation client = new ClientRepresentation(clientSocket, clientHandler, clientNo++);
                clients.add(client);
                client.start();
                System.out.println("New Client connected");
            }
        }catch(IOException ioe){
            System.err.println("Socket Error: " + ioe.getMessage());
        }finally{
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Unable to close ServerSocket: serverSocket");
                }
            }
            if(out != null){
                out.close();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(Integer.parseInt(args[0]));
        server.start();
    }
}
