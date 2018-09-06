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
    private boolean running;


    public ClientRepresentation(Socket clientSocket, ClientHandler clientHandler, int clientNo){
        this.clientHandler = clientHandler;
        this.clientNo = clientNo;
        running = false;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        running = true;
        try{
            while(running){
                String message = bufferedReader.readLine();
                System.out.println("Message recieved: " + message);
                if(message.isEmpty()){
                    printWriter.println("Server: Empty message");
                }
                else if(message.charAt(0) == '/'){ //Command
                    switch(message.substring(1)){
                        case "quit": {
                            clientHandler.quit(this.clientNo);
                            running = false;
                        }
                    }
                }
                else{
                    clientHandler.broadcast(this.clientNo, message);
                }
            }
        }catch(IOException ioe){

        }
    }

    public int getClientNumber(){
        return this.clientNo;
    }

    public void sendTo(String message){
        printWriter.println(message);
    }

}
