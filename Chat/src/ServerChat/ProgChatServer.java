/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerChat;

/**
 *
 * @author Francesco
 */
public class ProgChatServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }
    
}
