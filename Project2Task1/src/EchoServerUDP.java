/*
    Author: sbadhan Siddhesh Badhan
    Last Modified: 2/25/2023

    A simple UDP Echo Server implementation.
    Listens for incoming datagrams, echoes the received data back to the client.
    The server can be terminated by sending the message "halt!" from the client.
 */

import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class EchoServerUDP {
    /**
     * Entry point for the EchoServerUDP program.
     * @param args Command line arguments.
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    public static void main(String args[]) {
        // Create a DatagramSocket object to listen for incoming datagrams
        DatagramSocket serverSocket = null;
        // Create a byte array to store the data received in each datagram
        byte[] buffer = new byte[1000];
        try {
            // Print a message indicating that the server is running
            System.out.println("The UDP server is running.");
            // Prompt the user to enter the port number that the server should listen on
            serverSocket = new DatagramSocket(getServerPort());
            // Create a DatagramPacket object to store the incoming datagram
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            // Loop indefinitely to receive and process incoming datagrams
            while (true) {
                // Wait for an incoming datagram
                serverSocket.receive(request);
                // Create a DatagramPacket object to send a reply
                DatagramPacket reply = new DatagramPacket(request.getData(),
                        request.getLength(), request.getAddress(), request.getPort());
                // Convert the received data to a String
                String requestString = new String(request.getData()).substring(0, request.getLength());
                // Send the reply datagram back to the client
                serverSocket.send(reply);
                // Print a message indicating that the server echoed the client's message
                System.out.println("Echoing: " + requestString);
                // Check if the client's message was "halt!" to determine if the server should quit
                if (requestString.equals("halt!")) {
                    System.out.println("Server side quitting");
                    break;
                }
            }
        } catch (SocketException e) {
            // Print an error message if there was a problem with the socket
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            // Print an error message if there was a problem with I/O
            System.out.println("IO: " + e.getMessage());
        } finally {
            // Close the socket when the server is done running
            if (serverSocket != null) serverSocket.close();
        }
    }

    /**
     * Prompts the user for the server port number to listen on.
     * @return The port number to listen on.
     */
    // Method to prompt the user for the server port number to listen on
    public static int getServerPort() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the server port number to listen.");
        String serverPortStr = scanner.nextLine();
        int serverPort = Integer.parseInt(serverPortStr);
        // Print a message indicating the server is listening on the specified port number
        System.out.println("Listening on port " + serverPort);
        return serverPort;
    }
}
