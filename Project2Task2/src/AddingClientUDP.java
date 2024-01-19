/*
    Author: sbadhan Siddhesh Badhan
    Last Modified: 2/25/2023

    This class represents a UDP client that sends integer values to a server and receives a response from it.
    The client asks the user for the server's port number and sends the input to the server.
    If the user enters "halt!", the client quits.
 */

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class AddingClientUDP{
    // Declare a DatagramSocket object and two static variables
    static DatagramSocket socket = null;
    static int serverPort;
    static InetAddress host;

    /**
     * The main method of the AddingClientUDP class.
     * It shows that the client is running and asks the user for the server's port number.
     * It creates a new socket and receives input from the user until the user enters "halt!".
     * Then, it prints the response from the server and quits.
     * @param args the command line arguments
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    public static void main(String args[]){
        // Show that the client is running
        Scanner scanner = new Scanner(System.in);
        System.out.println("The client is running.");
        // Get the server port number from the user
        serverPort = getServerPort();
        try {
            // Get the IP address of the server and create a new socket
            host = InetAddress.getByName("localhost");
            socket = new DatagramSocket();
        }catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        }catch (IOException e){
            System.out.println("IO: " + e.getMessage());
        }

        System.out.println();
        while(true) {
            // Get input from the user
            String s = scanner.nextLine();
            if(s.equals("halt!")) {
                // If the user enters "halt!", break the loop
                break;
            }
            // Send the input to the server and print the response
            int total = add(Integer.parseInt(s));
            System.out.println("The server returned " + total + ".");
        }

        // Show that the client is quitting
        System.out.println("Client side quitting.");
        // Close the socket if it is not null
        if(socket != null) socket.close();
    }

    /**
     * This function sends a request to the server to add the given value to the sum and returns the server's response.
     * It converts the integer to a byte array and creates a new packet with the data, server's IP address, and port number.
     * It sends the packet to the server, receives the server's response, and converts it to a string.
     * If the server sends "halt!", it breaks the while loop.
     * @param value the integer value to be added to the sum
     * @return the server's response as an integer
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    // This function sends a request to the server to add the given value to the sum and returns the server's response
    public static int add(int value){
        String replyString = null;
        try {
            // Convert the integer to a byte array and create a new packet with the data, server's IP address, and port number
            byte [] m = String.valueOf(value).getBytes();
            DatagramPacket request = new DatagramPacket(m,  m.length, host, serverPort);
            // Send the packet to the server
            socket.send(request);
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            // Receive the server's response and convert it to a string
            socket.receive(reply);
            replyString = new String(reply.getData()).substring(0, reply.getLength());
            // If replyString is "halt!", break the while loop
        }catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        }catch (IOException e){
            System.out.println("IO: " + e.getMessage());
        }
        return Integer.parseInt(replyString);
    }

    /**

     This function prompts the user to input the server's port number and returns it as an integer.
     @return the server's port number as an integer
     */
    // This function asks the user for the server's port number and returns it as an integer
    public static int getServerPort() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the server-side port number.");
        String serverPortStr = scanner.nextLine();
        int serverPort = Integer.parseInt(serverPortStr);
        return serverPort;
    }
}
