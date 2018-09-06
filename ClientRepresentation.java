package lab1b;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientRepresentation extends Thread{

    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String nickname;
    private ClientHandler clientHandler;
    private int clientNo;


    public ClientRepresentation(Socket clientSocket, ClientHandler clientHandler, int clientNo){
        this.clientHandler = clientHandler;
        this.clientNo = clientNo;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            while(true){
                String message = bufferedReader.readLine();
                System.out.println("Message recieved: " + message);
                clientHandler.broadcast("message: " + message + ", client: " + clientNo);
            }
        }catch(IOException ioe){

        }
    }

    public PrintWriter getPrintWriter(){
        return this.printWriter;
    }

}
