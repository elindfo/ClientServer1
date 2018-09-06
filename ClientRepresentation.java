package lab1b;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientRepresentation extends Thread{

    //TODO Fix "option not found" bug when entering certain commands (probably substring-related)

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
        nickname = "";
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
                    switch(message.substring(1).split(" ")[0]){
                        case "quit": {
                            clientHandler.quit(this.clientNo);
                            running = false;
                        }
                        case "who": {
                            clientHandler.who(this.clientNo);
                        }
                        case "nick": {
                            clientHandler.nickname(this.clientNo, message.substring(1).split(" ")[1]);
                        }
                        default: {
                            printWriter.println("Server: Option not found");
                            printWriter.flush();
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

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public String getNickname(){
        return this.nickname;
    }

    public void sendTo(String message){
        printWriter.println(message);
        printWriter.flush();
    }

}
