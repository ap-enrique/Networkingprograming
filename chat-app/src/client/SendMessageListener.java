// The interface used for sending messages from the chat window.
// SendMessageListener.java facilitates message sending from the GUI.

package client;

// SendMessageListener är ett interface som används för att hantera meddelanden från GUI till servern.
// interface är ett nyckelord i Java som används för att deklarera ett interface.
// Ett interface är en typ som endast innehåller abstrakta metoder (metoder utan kropp) som måste implementeras av klasser som implementerar interface.
public interface SendMessageListener {
    // När en klass implementerar SendMessageListener-interface, måste den tillhandahålla en implementation för onSendMessage-metoden.
    // Det här metoden kommer att anropas varje gång ett meddelande ska skickas från klienten till servern.
    void onSendMessage(String message); // Hittas i Client.java
}