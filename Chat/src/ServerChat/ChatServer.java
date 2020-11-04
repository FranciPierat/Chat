/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerChat;

import ClientChat.ChatClient;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
/**
 *
 * @author Francesco
 */

public class ChatServer{
    private Scanner input = new Scanner(System.in);
    public List<GestioneClient> clients;
    private ServerSocket serverSocket;
    private String nomeClient;
    public PrintStream output = null;
    
    public void start(){

        try {

            // Crea server socket
            int portNumber = 1939;
            serverSocket = new ServerSocket(portNumber);
            ExecutorService threadPool = Executors.newCachedThreadPool();
            clients = new LinkedList<>();
            InetAddress ipAddress = InetAddress.getLocalHost();
            System.out.println("Il server della chat è in funzione, i client si possono connettere alla porta " + portNumber + " (IP: " + ipAddress.toString() + ").\n");

            // Aspetta le connessioni
            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    output = new PrintStream(clientSocket.getOutputStream());
                    output.println(String.format("Inserisci il tuo nome:")); //avviene il salvataggio del nome
                    statusServer("Aspettando che il client dia il suo nome...");
                    nomeClient = input.readLine().trim();
                    for (int i = 0; i < clients.size(); i++) {                                      //controllo sul nome del client
                        GestioneClient client = clients.get(i);
                        if (client.getName().equals(nomeClient)) {
                            statusServer("L'utente ha inserito un nome già esistente...");
                            output.println(String.format("Il nome inserito esiste già." + '\n' + "Inserire un altro nome:"));
                            statusServer("Aspettando che il client dia il suo nome...");
                            nomeClient = input.readLine().trim();
                            i = -1;
                        }
                    }                                               
                    for (GestioneClient client : clients) {                                     //per dire ai client chi si è connesso
                        client.sendMessage("L'utente " + nomeClient + " è entrato nella chat");
                    }
                    System.out.println(nomeClient + " si è connesso alla chat.");
                    output.println(String.format("Benvenuto nella chat" + '\n'));
                    elencoComandi();
                    GestioneClient client = new GestioneClient(nomeClient, clientSocket, this);
                    threadPool.submit(client);
                    clients.add(client);
                }catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void broadcast(String message) {
        synchronized (clients) {
            statusServer("Ricevuto un messaggio da " + nomeClient + "...");
            for (GestioneClient client : clients) {
                if(clients.size() == 1){
                    output.println(String.format("C'è un solo client"));
                    break;
                }
                client.sendMessage(message);
            }
            statusServer("Inoltrando il messaggio a tutti gli utenti...");
        }
    }

    public boolean isActive() {
        return !serverSocket.isClosed();
    }

    public void removeClient(GestioneClient client) {
        synchronized (clients) {
            String nome = client.getName();
            statusServer("Rimuovendo " + client.getName() + " dalla chat...");
            statusServer("Rimosso " + client.getName() + " dalla chat...");
            clients.remove(client);
            for (GestioneClient c : clients) {                                     //per dire ai client chi si è connesso
                c.sendMessage("L'utente " + nome + " è uscito dalla chat");
            }
        }
    }

    public StringBuilder activeClients() {
        statusServer("Mostrando all'utente " + nomeClient + " gli utenti connessi...");
        StringBuilder list = new StringBuilder("Utenti loggati: \n");
        for (GestioneClient client : clients) {
            list.append(client.getName()).append("\n");
        }
        return list;
    }

    public List<GestioneClient> getClientList() {
        return clients;
    }
    
    public void elencoComandi(){
        statusServer("Mostrando a " + nomeClient + " i comandi della chat...");
        output.println(String.format("I comandi da inserire nella chat sono: " + '\n' + "/t <NomeUtente> <messaggio> serve per mandare ad un singolo utente un messaggio" + '\n' + "/logout per uscire dalla chat " + '\n' +
        "/list per vedere gli utenti connessi alla chat " + '\n' + "/comandi per vedere i comandi " + '\n' + "Per mandare un messaggio a tutti gli utenti connessi basta solo scrivere il messaggio ed inviarlo" + '\n'));
    }
    
    public void statusServer(String m){
        System.out.println(m);
    }
}
