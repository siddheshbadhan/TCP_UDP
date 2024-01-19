/*
    Author: sbadhan Siddhesh Badhan
    Last Modified: 2/25/2023

    This class represents a server that listens for UDP requests containing integers and
    returns their sum to the client.

 */

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class AddingServerUDP{
    static int sum;
    /**
     * The main method initializes the sum to zero, listens for incoming UDP requests on a specified port,
     * and sends the sum back to the client as a response.
     * @param args an array of command-line arguments for the program
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    public static void main(String args[]){
        // Initialize the sum to zero
        sum = 0;
        DatagramSocket socket = null;
        byte[] buffer = new byte[1000];
        System.out.println("Server started.");
        // Set the port number that the server will listen on
        int listenPort = 6789;
        try{
            // Create a new DatagramSocket object to listen on the given port
            socket = new DatagramSocket(listenPort);
            // Create a new DatagramPacket object to store incoming requests
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            // Continuously listen for requests
            while(true){
                // Wait for a request to come in
                socket.receive(request);
                // Extract the string representation of the request from the byte array
                String requestStr = new String(request.getData()).substring(0,request.getLength());
                // Convert the request string to an integer and add it to the sum variable
                add(Integer.parseInt(requestStr));
                // Convert the updated sum value to a byte array to send back to the client
                byte [] m = String.valueOf(sum).getBytes();
                // Create a new DatagramPacket object to send the response back to the client
                DatagramPacket reply = new DatagramPacket(m,
                        m.length, request.getAddress(), request.getPort());
                // Send the response back to the client
                System.out.println("Returning sum of " + sum + " to client");
                System.out.println();
                socket.send(reply);
            }
        }catch (SocketException e){
            // Handle exceptions related to socket creation
            System.out.println("Socket: " + e.getMessage());
        }catch (IOException e){
            // Handle exceptions related to I/O operations
            System.out.println("IO: " + e.getMessage());
        }finally{
            // Close the socket when the server is shutting down
            if(socket != null) socket.close();
        }
    }

    /**
     * This method adds the given value to the sum variable and prints a message to the console.
     * @param value the integer value to add to the sum
     */
    // This method adds the given value to the sum variable
    public static void add(int value){
        System.out.println("Adding: " + value + " to " + sum);
        sum += value;
    }
}
