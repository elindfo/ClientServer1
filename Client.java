package lab1b;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String serverIp;
    private int serverPort;

    public static void main(String[] args) {
        Client client = new Client(args[0], Integer.parseInt(args[1]));
        client.connect();
    }

    public Client(String serverIp, int serverPort){
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void connect(){
        Socket socket;
        BufferedReader in;
        PrintWriter out;
        try{
            socket = new Socket(serverIp, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            ClientSend clientSend = new ClientSend(out);
            ClientReceive clientReceive = new ClientReceive(in);
            Thread send = new Thread(clientSend);
            Thread receive = new Thread(clientReceive);
            send.start();
            receive.start();

            try {
                send.join();
                receive.join();
                System.out.println("Exited");
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }

    private class ClientSend implements Runnable{

        private PrintWriter out;

        public ClientSend(PrintWriter out){
            this.out = out;
        }

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            String message;
            do{
                System.out.print("> ");
                message = scanner.nextLine();
                out.println(message);
            }while(!message.equals("/quit"));
        }
    }

    private class ClientReceive implements Runnable{

        private BufferedReader in;
        private boolean running;

        public ClientReceive(BufferedReader in){
            this.in = in;
            running = true;
        }

        @Override
        public void run() {
            while(running){
                try {
                    String message = in.readLine();
                    System.out.println(message);
                    switch(message){
                        case "TERM": {
                            running = false;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


