package lab1b;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ClientRepresentation extends Thread{

    //TODO Fix "option not found" bug when entering certain commands (probably substring-related)

    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String nickname;
    private ClientHandler clientHandler;
    private int clientNo;
    private boolean running;

    private static final String AVAILABLE_COMMANDS =
            "Available commands:\n" +
                    "\t/who - display other channel users\n" +
                    "\t/nick <nickname> - set nickname\n" +
                    "\t/quit - exit chat\n" +
                    "\t/help - show this message";


    public ClientRepresentation(Socket clientSocket, ClientHandler clientHandler, int clientNo){
        this.clientSocket = clientSocket;
        bufferedReader = null;
        printWriter = null;
        this.clientHandler = clientHandler;
        this.clientNo = clientNo;
        running = false;
        nickname = "";
    }

    @Override
    public void run() {
        running = true;
        try{
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);

            printWriter.println("Server: Welcome To The Chat! You are client number " + (this.clientNo));

            while(running){
                String message = bufferedReader.readLine();
                System.out.println("Message recieved: " + message);
                if(message == null){
                    throw new IOException("Connection lost");
                }
                else if(message.isEmpty()){
                    printWriter.println("Empty message");
                }
                else if(message.charAt(0) == '/'){ //Command
                    String[] splitMessage = message.substring(1).split(" ");
                    if(message.length() < 2 || splitMessage.length < 1){
                        printWriter.println("Option not found");
                    }
                    else{
                        Arrays.stream(splitMessage).forEach(keyword -> System.out.println("[" + keyword + "]"));
                        switch(splitMessage[0]){
                            case "quit": {
                                clientHandler.quit(this.clientNo);
                                running = false;
                                break;
                            }
                            case "who":{
                                clientHandler.who(this.clientNo);
                                break;
                            }
                            case "nick": {
                                if(splitMessage.length == 2) {
                                    clientHandler.nickname(this.clientNo, splitMessage[1]);
                                }
                                else{
                                    printWriter.println("Invalid username");
                                }
                                break;
                            }
                            case "help": {
                                printWriter.println(AVAILABLE_COMMANDS);
                                break;
                            }
                            default: {
                                printWriter.println("Option not found");
                                printWriter.flush();
                            }
                        }
                    }
                }
                else{
                    clientHandler.broadcast(this.clientNo, message);
                }
            }
        }catch(IOException ioe){
            clientHandler.disconnect(this.clientNo);
        }finally{
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }

            if(printWriter != null){
                printWriter.close();
            }

            if(clientSocket != null){
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
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
