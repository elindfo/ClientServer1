package lab1b;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String serverIp;
    private int serverPort;

    public static void main(String[] args) {
        if(args.length != 2){
            System.err.println("Invalid program arguments");
            System.exit(1);
        }
        Client client = new Client(args[0], Integer.parseInt(args[1]));
        client.connect();
    }

    public Client(String serverIp, int serverPort){
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void connect(){
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try{
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverIp, serverPort), 2000);
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
            System.err.println("No connection to server...");
        }finally{
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }

            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }

            if(out != null){
                out.close();
            }
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


