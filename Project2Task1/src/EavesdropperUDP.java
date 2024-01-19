/*
    Author: sbadhan Siddhesh Badhan
    Last Modified: 2/25/2023

    The EavesdropperUDP class is a program that listens for UDP packets on a specified port, eavesdrops on
    the conversation between a client and a server by modifying the messages in transit, and masquerades as
    the client by sending modified messages to the server and returning the modified replies to the client.
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class EavesdropperUDP {

    // Define a constant for the size of the buffer
    private static final int BUFFER_SIZE = 1000;
    /**
     The main method is the entry point of the program. It initializes the sockets, receives and processes packets
     from the client and server, modifies the messages and forwards them to the respective party.
     @param args an array of command-line arguments
     */
    //Referred Github Project 2 Repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    //Referred some basics of socket programming from google
    //https://www.geeksforgeeks.org/socket-programming-in-java/
    public static void main(String[] args) {
        try {
            // Print a message to indicate that the program has started
            System.out.println("EavesdropperUDP is running");
            // Get the port number to listen on from the user
            int listenPort = getInputFromUser("Please insert the port to listen on");
            // Get the port number to masquerade as from the user
            int masqueradePort = getInputFromUser("Please insert the port number to masquerade");
            // Create a datagram socket for the client
            DatagramSocket clientSocket = new DatagramSocket();
            // Create a datagram socket for the server and bind it to the specified port
            DatagramSocket serverSocket = new DatagramSocket(listenPort);
            // Get the IP address of the local host
            InetAddress serverAddress = InetAddress.getLocalHost();
            // Create a buffer for receiving and sending packets
            byte[] buffer = new byte[BUFFER_SIZE];

            // Start an infinite loop to receive and process packets
            while (true) {
                // Create a datagram packet for receiving a packet from the client
                DatagramPacket clientPacket = new DatagramPacket(buffer, buffer.length);
                // Receive a packet from the client
                serverSocket.receive(clientPacket);
                // Extract the message from the packet
                String request = new String(clientPacket.getData(), 0, clientPacket.getLength());
                // Modify the message
                String modifiedRequest = modifyMessage(request);
                // Print the original and modified messages
                System.out.println("Request from client before eavesdropping: " + request);
                System.out.println("Request from client after eavesdropping: " + modifiedRequest);
                // Create a datagram packet for sending the modified message to the server
                DatagramPacket serverPacket = new DatagramPacket(modifiedRequest.getBytes(), modifiedRequest.length(),
                        serverAddress, masqueradePort);

                // Send the modified message to the server
                clientSocket.send(serverPacket);
                // Create a datagram packet for receiving a packet from the server
                DatagramPacket replyPacket = new DatagramPacket(buffer, buffer.length);
                // Receive a packet from the server
                clientSocket.receive(replyPacket);

                // Extract the message from the packet
                String reply = new String(replyPacket.getData(), 0, replyPacket.getLength());
                // Modify the reply
                String modifiedReply = modifyReply(reply);
                // Print the original and modified replies
                System.out.println("Reply from server before eavesdropping: " + reply);
                System.out.println("Reply from server after eavesdropping: " + modifiedReply);

                // Create a datagram packet for sending the modified reply to the client
                DatagramPacket modifiedReplyPacket = new DatagramPacket(modifiedReply.getBytes(), modifiedReply.length(),
                        clientPacket.getAddress(), clientPacket.getPort());

                // Send the modified reply to the client
                serverSocket.send(modifiedReplyPacket);
            }
        } catch (SocketException e) {
            // Handle exceptions related to sockets
            System.err.println("Socket exception: " + e.getMessage());
        } catch (UnknownHostException e) {
            // Handle exceptions related to unknown hosts
            System.err.println("Unknown host: " + e.getMessage());
        } catch (IOException e) {
            // Handle general I/O exceptions
            System.err.println("IO exception: " + e.getMessage());
        }
    }

    /**

     This method modifies a given message by adding an exclamation mark at the end if it is not "halt!".
     @param message the message to be modified
     @return the modified message with an exclamation mark at the end, or "halt!" if the original message is "halt!"
     */
    public static String modifyMessage(String message) {
        // Check if message is "halt!"
        if (message.equals("halt!")) {
            return message; // Return "halt!" as is
        } else {
            return message + "!"; // Otherwise, add "!" to the end of the message and return it
        }
    }

    /**

     This method modifies a given message by removing the last exclamation mark and adding two exclamation marks at the end
     if the message still ends with an exclamation mark. If the original message is "halt!", it returns it as is.
     @param message the message to be modified
     @return the modified message with at most one exclamation mark at the end, or "halt!" if the original message is "halt!"
     */
    public static String modifyReply(String message) {
        String modifiedReply = message; // Create a copy of the message to modify
        if (message.equals("halt!")) {
            modifiedReply = "halt!"; // If the message is "halt!", return it as is
        }
        // Otherwise, if the message ends with "!", remove it from the modified message
        else if (message.endsWith("!")) {
            modifiedReply = message.substring(0, message.length() - 1);
            // If the modified message now ends with "!", replace it with "!!"
            if (modifiedReply.endsWith("!")) {
                modifiedReply = modifiedReply.substring(0, modifiedReply.length() - 1) + "!";
            }
        }
        return modifiedReply; // Return the modified message
    }

    /**

     This method prompts the user for input by printing a message to the console and reading an integer from the console input.
     @param message the message to be printed to prompt the user for input
     @return the integer input by the user
     */
    // Method to prompt the user for the input
    public static int getInputFromUser(String message) {
        Scanner scanner = new Scanner(System.in); // Create a Scanner object to read user input from the console
        System.out.println(message); // Print the message to prompt the user for input
        return scanner.nextInt(); // Read an integer input from the user and return it
    }
}
