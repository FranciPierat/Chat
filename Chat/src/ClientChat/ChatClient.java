/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientChat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
/**
 *
 * @author Francesco
 */
public class ChatClient{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader inKeyBoard;
    private String nome;
    
    public ChatClient(String address, int port) {
        try {
            clientSocket = new Socket(address, port);
            setupStreams();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setupStreams() throws IOException {
        out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        inKeyBoard = new BufferedReader(new InputStreamReader(System.in));
    }

    public void sendMessage() throws IOException {
        while (!clientSocket.isClosed()) {
            String message = inKeyBoard.readLine();
            out.println(message);
        }
    }

    public void receiveMessage() throws IOException {
        while (!clientSocket.isClosed()) {
            String message = in.readLine();

            if (message == null) {
                System.exit(0);
            }

            System.out.println(message);

        }
    }
    
    public String getNome(){
        return this.nome;
    }

    public void start() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    receiveMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
