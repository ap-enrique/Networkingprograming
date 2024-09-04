// The main client class that handles connecting to the server and managing the chat window.
// Client.java connects to the server and sets up the GUI (ChatWindowFrame).
// Ansluter till servern, tar emot och skickar meddelanden via ett grafiskt användargränssnitt (ChatWindowFrame.java).

package client;

import javax.swing.*;   // Paketet som används för att skapa grafiska användargränssnitt (GUI).
import java.io.*;
import java.net.*;      // Importerar alla klasser i java.net paketet som används för nätverksoperationer.

public class Client {
    private Socket socket;  // Ett socket-objekt som används för att ansluta till servern.
    private PrintWriter out;    // Ett PrintWriter-objekt som används för att skicka data till servern.
    private BufferedReader in;  // Ett BufferReader-objekt som används för att ta emot data från servern.
    private ChatWindowFrame chatWindow; // Ett ChatWindowFrame-objekt som är GUI-fönstret för chatten.
    private String username;

    //FAS 2.2
    public Client(String serverAddress) throws IOException {
        // Detta nya klienten skapar en anslutning till servern. Objekten för anslutninggen heter socket.
        // Socket är en inbyggd klass från java.net bibliotek.
        socket = new Socket(serverAddress, 12345);  // socket_NOELIA = new Socket(localhost, 12345)

        // Ojbekt out innehåller en PrintWriter klass för att skicka data med metoden getOutputStream till servern.
        // "true" betyder att autoflush är aktiverat, vilket innebär att data skickas omedelbart.
        out = new PrintWriter(socket.getOutputStream(), true);
        // Objekt in innehåller en BufferedReader för att läsa data från servern.
        // BufferedReade är en kombination av klasser som omvandlar byte-strömmar från nätverkssocketen till teckenströmmar, vilket BufferedReader sedan kan läsa.
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String serverMessage = in.readLine();
        if (serverMessage != null && serverMessage.equals("Enter your username:")) {
            username = JOptionPane.showInputDialog("Enter your username:");
            out.println(username);
        }

        // Ett nytt ChatWindowFrame-fönster skapas och öppnas en chat window (ChatWindowFrame.java) för att visa chatten
        chatWindow = new ChatWindowFrame();
        chatWindow.setTitle("Chat Client - " + username);
        // Här sätts en lyssnare som skickar meddelanden när användaren skriver och skickar ett meddelande.
        // Här skapas en ny instans av SendMessageListener som parameter till metoden setSendMessageListener i ChatWindowFrame.
        chatWindow.setSendMessageListener(new SendMessageListener() {
            //  Override indikerar att vi implementerar en metod från interface SendMessageListener.
            @Override
            // Vi implementerar onSendMessage-metoden direkt i denna SendMessageListener instans.
            // När ett meddelande skickas i chatten, kallas denna metod och utför sin kod.
            public void onSendMessage(String message) {
                // Skickar meddelandet till servern
                // Exemeplvis när man trycker på "Disconnect"-knappen, anropas disconnect metoden, och "/disconnect" skickas till servern genom out.println(message);
                out.println(message);
            }
        });
        // Här startas en ny tråd som kör IncomingReader klass, en inre klass som hanterar inkommande meddelanden från servern.
        new Thread(new IncomingReader()).start();
    }

    // Metod som kör en separat tråd för att läsa inkommande meddelanden från servern kontinuerligt.
    // När ett meddelande tas emot från servern, visas det i chatfönstret eller uppdaterar medlemslistan beroende på meddelandets innehåll.
    private class IncomingReader implements Runnable {  // Runnable är en uppgift som kan köras i en separat tråd som ett litet program som körs samtidigt som andra trådar inom samma program.
        @Override
        // Metoden run körs när tråden startar.
        // run är en metod som du måste implementera när du använder Runnable
        public void run() {
            // En sträng för att hålla inkommande meddelanden
            String message;
            try {
                // Andra klienter tar emot meddelandet och uppdaterar sitt chattfönster
                while ((message = in.readLine()) != null) {
                    // Om meddelandet börjar med "MEMBERS:"
                    if (message.startsWith("MEMBERS:")) {
                        // Pppdaterar medlemslistan.
                        updateMemberList(message.substring(8));
                    // Annars visar den meddelandet i chatfönstret.
                    } else {
                        // Meddelande som kommer från serven upptäcks in.readline()
                        // klassen receiveMessage från chatWindowFrame anropas för att addera texten för varje klient chattfönster
                        chatWindow.receiveMessage("", message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Metod som uppdaterar medlemslistan i GUI
        private void updateMemberList(String members) {
            String[] memberArray = members.split(",");
            // Använder SwingUtilities.invokeLater för att säkerställa att GUI-uppdateringar görs på rätt tråd.
            SwingUtilities.invokeLater(() -> {
                chatWindow.updateMemberList(memberArray);
            });
        }
    }

    // Client programmets startpunkt.
    public static void main(String[] args) {

        // FAS 2.1
        try {
            // Visar en dialogruta där användaren kan skriva in serverns adress.
            String serverAddress = JOptionPane.showInputDialog("Enter server address:"); // serverAddress = localhost

            // FAS 2.2
            // En ny instans av klass Client skapas och ansluter till den angivna servern
            new Client(serverAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

