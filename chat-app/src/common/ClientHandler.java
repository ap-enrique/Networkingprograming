// The class that handles individual client connections on the server side.
// ClientHandler.java manages individual client sessions on the server.
// Hanterar varje enskild klientanslutningar på serversidan.
// Läser meddelanden från klienten och vidarebefordrar dem till andra klienter via servern.
// Contains shared classes that are used by both the client and the server.

package common;

import server.Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {    // klassen implementerar Runnable-interface, vilket betyder att dess instanser kan köras i en separat tråd.
    private Socket socket;  // Håller referensen till klientens socket-anslutning
    private PrintWriter out;    // För att skicka meddelanden till klienten.
    private BufferedReader in;  // För att läsa meddelanden från klienten.
    private String username;    // Sparar klientens användarnamn.
    private Set<ClientHandler> clientHandlers;  // En referens till den gemensamma listan över alla anslutna klienter.

    // Konstruktorn initialiserar socket och clientHandlers med de värden som skickas som argument när en ny ClientHandler skapas.
    public ClientHandler(Socket socket, Set<ClientHandler> clientHandlers) {
        this.socket = socket;
        this.clientHandlers = clientHandlers;
    }

    @Override
    public void run() { // run-metoden körs när ClientHandler startas i en ny tråd.
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // FAS 4
            // ClientHandler uppmanar användaren att ange sitt användarnamn.
            out.println("Enter your username:");
            // Klienten uppmanas att ange sitt användarnamn, som sedan sparas i username.
            username = in.readLine();
            Server.broadcastMembers();
            System.out.println(username + " has joined the chat.");

            String message;
            // Medan det finns meddelanden att läsa från klienten, hanteras de.
            while ((message = in.readLine()) != null) {
                // Om en klient skickar "/disconnect", stänger ClientHandler anslutningen och tar bort klienten från serverns klientlista.
                if (message.equals("/disconnect")) {
                    break;
                }
                // Servern skriver till konsolen: "Received from Alice: Hello, Bob!".
                System.out.println("Received from " + username + ": " + message);
                // ClientHandler på serversidan tar emot meddelandet och sänder det till alla andra klienter
                // När en Klient skickar ett meddelande, tas det emot av ClientHandler.java
                // Server.broadcast används för att sända meddelandet till alla andra klienter
                // Servern anropar Server.broadcast("Hello, Bob!", "Alice", aliceHandler).
                Server.broadcast(message, username, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // I finally-blocket stängs socketen och klienten tas bort från serverns lista.
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.removeClient(this);
            System.out.println(username + " has left the chat.");
        }
    }

    // Denna metod skickar ett meddelande till klienten genom att skriva det till PrintWriter out.
    public void sendMessage(String message) {
        out.println(message);
    }

    // Denna metod returnerar användarnamnet för klienten.
    public String getUsername() {
        return username;
    }
}
