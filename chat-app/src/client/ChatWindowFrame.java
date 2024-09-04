// The GUI class for the chat window.

package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// ChatWindowFrame är en klass som ärver från JFrame och representerar huvudfönstret i chattapplikationen
public class ChatWindowFrame extends JFrame {
    // Deklarera komponenter som används i GUI
    private JTextArea messageArea;  // Område där mottagna meddelanden visas
    private DefaultListModel<String> memberListModel;   // Modellen som håller data för listan över medlemmar
    private JList<String> memberList;   // Lista som visar medlemmar
    private JTextField messageField;    // Fält där användaren skriver meddelanden
    private JButton disconnectButton;   // Knapp för att koppla från chatten
    private SendMessageListener sendMessageListener;    // Lyssnare för att skicka meddelanden

    // Konstruktor för ChatWindowFrame
    public ChatWindowFrame() {
        setTitle("Chat Communication"); // Sätt titel för fönstret
        setSize(600, 400);  // Sätt storlek för fönstret
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Stäng applikationen när fönstret stängs
        setLocationRelativeTo(null);    // Centrera fönstret på skärmen

        // Skapa och konfigurera disconnect-knappen
        disconnectButton = new JButton("Disconnect");
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnect();   // Anropa disconnect-metoden när knappen klickas
            }
        });

        // Skapa ett panel för knappen och lägg till den i toppen av fönstret
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(disconnectButton);

        // Skapa och konfigurera området för mottagna meddelanden
        messageArea = new JTextArea();
        messageArea.setEditable(false); // Gör textområdet oändringsbart. Detta innebär att användaren kan se texten men inte ändra den.
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);    // Lägg till en vertikal scrollbar

        // Skapa modellen och listan för medlemmar
        memberListModel = new DefaultListModel<>();
        memberList = new JList<>(memberListModel);
        JScrollPane memberScrollPane = new JScrollPane(memberList);
        memberScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        memberScrollPane.setPreferredSize(new Dimension(150, 0));   // Sätt bredd för medlemslistan

        // Skapa fältet för att skriva meddelanden
        messageField = new JTextField();
        // FASE 5
        // Klienten skickar ett meddelande genom att skriva i sitt chattfönster och klicka på "Send"
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // När användaren trycker Enter och skickar ett meddelande anropas sendMessage() metoden som är här i ChatWindowFrame.java
                sendMessage();
            }
        });

        // Skapa och konfigurera knappen för att skicka meddelanden
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // // När användaren trycker på Enter och skickar ett meddelande anropas sendMessage() som är här i ChatWindowFrame.java
                sendMessage();
            }
        });

        // Lägg till fältet och knappen i en panel för inmatning
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Skapa en delad panel för meddelandeområdet och medlemslistan
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, messageScrollPane, memberScrollPane);
        splitPane.setDividerLocation(450);  // Sätt plats för delaren

        // Lägg till panelerna i fönstret
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // Gör fönstret synligt
        setVisible(true);
    }

    // Denna metod används för att sätta upp SendMessageListener-objektet, dvs. en lyssnare för att skicka meddelande.
    // Metoden anropas från Client-klassen för att länka ChatWindowFrame med klientens SendMessageListener-implementation.
    // Metoden tar new SendMessageListener() som parameter.
    public void setSendMessageListener(SendMessageListener listener) {
        this.sendMessageListener = listener;
    }

    // Denna metod används för att skicka meddelanden.
    // När användaren trycker på "Skicka"-knappen eller trycker på "Enter" i textfältet, anropas sendMessage()-metoden.
    private void sendMessage() {
        // Hämta texten från fältet och trimma eventuella mellanslag
        String message = messageField.getText().trim();
        // Kontrollera att meddelandet inte är tomt
        if (!message.isEmpty()) {
            // Om sendMessageListener inte är null dvs. att man kontrollera att lyssnaren är satt
            if (sendMessageListener != null) {
                // Anropa onSendMessage-metoden på sendMessageListener-objektet från client.java
                // Meddelandet skickas till servern
                sendMessageListener.onSendMessage(message);
            }
            // Rensa textfältet
            messageField.setText("");
        }
    }

    // Metod för att koppla bort en klient från servern och stänga chattfönstret
    private void disconnect() {
        // Kontrollera om sendMessageListener inte är null. Försäkra att det finns en instans av SendMessageListener innan man försöker använda den.
        if (sendMessageListener != null) {  // Om sendMessageListener är null, betyder det att ingen lyssnare har ställts in, och man kan inte hantera och skicka ett meddelande.
            // Anropar onSendMessage metoden och skickar strängen "/disconnect" som ett meddelande.
            sendMessageListener.onSendMessage("/disconnect");   // Meddelandet skickas till servern för att indikera att klienten vill koppla bort från servern.
        }
        // Stänger fönstret för chatten
        dispose(); // Metoden dispose är en del av JFrame-klassen (som ChatWindowFrame ärver från)
    }

    // Metod för att visa meddelanden som tas emot på chattfönstret.
    // sender parameter: En sträng som representerar avsändarens namn eller användarnamn.
    // message parameter: En sträng som innehåller själva meddelandet som skickats av avsändaren.
    public void receiveMessage(String sender, String message) { // Användaren "Alice" skickar ett meddelande "Hello" => receiveMessage("Alice", "Hello");
        // Lägger till Meddelandet i messageArea
        // messageArea är en JTextArea där alla meddelanden visas
        messageArea.append(sender + ": " + message + "\n"); // Metoden append lägger till text i slutet av det som redan finns i messageArea.
    }

    // Metod för att uppdatera listan över medlemmar som visas i chattfönstret.
    public void updateMemberList(String[] members) {    // members parameter: En array av strängar där varje sträng representerar ett medlemsnamn
        // Rensa den existerande listan över medlemmar
        memberListModel.clear();     // Det innebär att man börjar med en tom lista varje gång listan uppdateras.
        // Lägga till medlemmar i memberListModel på nytt => updateMemberList(new String[]{"Alice", "Bob", "Charlie"});
        for (String member : members) { // Man går igenom varje medlem i members-arrayen:
            // Kontrollerar om strängen member inte är tom
            if (!member.isEmpty()) {    // Säkerställa att man inte lägger till tomma namn i listan.
                // Om medlemmen inte är tom, lägg till member i memberListModel med metoden addElement
                memberListModel.addElement(member);
            }
        }
    }
}

