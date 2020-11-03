/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientChat;

import java.io.*;

/**
 *
 * @author Francesco
 */
public class ProgChatClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        ChatClient client = new ChatClient("localhost", 1939);
        client.start();
    }
    
}
