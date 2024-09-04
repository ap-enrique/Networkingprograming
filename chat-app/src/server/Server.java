// The main server class that listens for client connections and manages them.
// Server.java handles client connections and broadcasting messages.
// Hanterar klientanslutningar och meddelandeöverföringar.

package server;

import common.ClientHandler;    // Importerar ClientHandler klassen som hanterar individuella klientanslutningar.

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    // En Set är en samling som inte tillåter dubbletter, vilket innebär att varje element är unikt
    // HashSet används för att lagra ClientHandler-objekt, som håller referenser till alla anslutna klienter.
    // HashSet är en av de mest använda implementationerna av Set-gränssnittet
    // Den använder en hash-tabell för att lagra elementen och erbjuder snabb åtkomsttid för operationer som att lägga till, ta bort och söka efter element.
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    // Server startas och väntar på att klienter ska ansluta
    public static void main(String[] args) throws IOException { // IOException om något går fel med nätverksanslutningarna.
        // FAS 1
        // Instansen serverSocket skapas av en ServerSocket klass är TCP-komponenter i Java som används för att skapa och hantera anslutningar.
        // Servern lyssnar på port 12345 för inkommande anslutningar.
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Server started. Waiting for clients...");

        while (true) {  // En oändlig loop som gör att servern alltid är redo att acceptera nya klienter.
            // FAS 3
            // En klientanslutning accepteras när en klient ansluter. Detta skapar en Socket-anslutning till klienten.
            // variabeln socket används för att referera anslutningen.
            Socket clientSocket = serverSocket.accept();  //Socket NOELIA_SOCKET
            System.out.println("New client connected.");
            // En ny clientHandler instans skapas för att hantera den nya anslutna klient med Clienthandler klassen från ClientHandler.java
            // Den anslutna klienten skapas och passerar den anslutna sockeln och listan över klienter.
            ClientHandler clientHandler = new ClientHandler(clientSocket, clientHandlers);    //ClientHandler handler_NOELIA = new ClientHandler(NOELIA_SOCKET, clientHandlers)
            // I listan över klienter "clientHandlers" läggs till den nya "clientHandler" klient instansen.
            clientHandlers.add(clientHandler);  // clientHandlers.add(handler_NOELIA)
            // Här startas en ny tråd för att hantera klientens kommunikation
            new Thread(clientHandler).start();  // new Thread(handler_NOELIA).start()
            // Nu innehåller clientHandlers en ClientHandler objekt för Noelia som heter handler_NOELIA.
            // Vid utökning koommer clientHandlers innehålla en ti objekt för ELian, Beto och kike. (handler_ELIAN, handler_BETO och handler_KIKE)
        }
    }

    // Metod för att skicka meddelanden till alla klienter utom avsändaren
    // När en klient skickar ett meddelande, tas det emot av ClientHandler.java först
    // Server.broadcast används för att sända meddelandet till alla andra klienter
    public static void broadcast(String message, String sender, ClientHandler senderHandler) {
        // Itererar över alla anslutna klienter med indixering "clienthandler" i listan clientHandlers
        for (ClientHandler clientHandler : clientHandlers) {
            // Här säkerställs att meddelandet inte skickas tillbaka till avsändaren
            if (clientHandler == senderHandler) {
                clientHandler.sendMessage("You: " + message);
            } else {
                // Här skickas meddelandet till alla andra klienter.
                // sendMessage klass från Clienthandler.java
                clientHandler.sendMessage(sender + ": " + message);
            }
        }
    }

    // Metod för att tar bort en klient från listan över anslutna klienter.
    public static void removeClient(ClientHandler clientHandler) {
        // tar bort den specificerade klienten från listan
        clientHandlers.remove(clientHandler);
        // Anropar metoden broadcastMembers för att uppdatera och sänder medlemslistan till alla återstående klienter
        broadcastMembers();
    }

    // Metod för att sända en lista över alla anslutna medlemmar till klienterna.
    public static void broadcastMembers() {
        // Skapar instansen members med en StringBuilder klass för att bygga medlemslistan.
        StringBuilder members = new StringBuilder("MEMBERS:");
        // Itererar över alla anslutna klienter med indixering "clienthandler" i listan clientHandlers.
        for (ClientHandler clientHandler : clientHandlers) {
            //  Lägger till varje klients användarnamn till medlemslistan.
            members.append(clientHandler.getUsername()).append(",");
        }
        // Konverterar StringBuilder till en sträng
        String membersList = members.toString();
        // itererar igen över alla klienter
        for (ClientHandler clientHandler : clientHandlers) {
            // skickar medlemslistan till varje klient
            clientHandler.sendMessage(membersList);
        }
    }
}

