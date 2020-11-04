/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerChat;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
/**
 *
 * @author Francesco
 */
public class GestioneClient implements Runnable {

    private Socket client;
    private ChatServer chatServer;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String name;

    public GestioneClient(String name, Socket client, ChatServer chatServer) {
        this.client = client;
        this.chatServer = chatServer;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            initialiseStreams();
            listenForMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initialiseStreams() throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        printWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
    }

    private void listenForMessages() throws IOException {
        chatServer.statusServer("Aspettando di ricevere un messaggio da qualche client...");
        while (chatServer.isActive()) {
            String message = bufferedReader.readLine();

            if (message == null) {
                closeAll();
                return;
            }

            switch (message.split(" ")[0]) {
                case "/t":
                    whisperMessage(message);
                    break;
                case "/logout":
                    closeAll();
                    return;
                case "/list":
                    sendMessage(chatServer.activeClients(name).toString());
                    break;
                case "/comandi":
                    chatServer.elencoComandi(name);
                    break;
                default:
                    chatServer.broadcast(name + ">> " + message, name);
                    break;
            }
        }
    }

    public void sendMessage(String message) {
        printWriter.println(message);
    }

    private void closeAll() throws IOException {
        try {
            chatServer.removeClient(this);
            client.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void whisperMessage(String message) {
        String recipient = message.split(" ")[1];
        chatServer.statusServer("Cercando l'utente " + recipient + " per l'utente " + getName() + "...");
        for (int i = 0; i < chatServer.getClientList().size(); i++) {
            GestioneClient client = chatServer.getClientList().get(i);
            if (client.getName().equals(recipient)) {
                chatServer.statusServer("Utente trovato..." + '\n' + "Invio del messaggio all'utente " + client.getName() + "...");
                String[] messageArray = message.split(" ");
                String whisperedMessage = "";
                int messageIndex = 2;
                for (int x = messageIndex; x < messageArray.length; x++) {
                    whisperedMessage += messageArray[x] + " ";
                }
                client.sendMessage(name + " ti ha scritto: " + whisperedMessage);
                sendMessage("Hai scritto a " + client.getName() + ": " + (message = whisperedMessage));
                break;
            }
            if(i == (chatServer.getClientList().size() - 1)){
                chatServer.statusServer("Utente non trovato...");
                sendMessage("L'utente da lei cercato non esiste");
            }
        }
    }

    public String getName() {
        return name;
    }

}
