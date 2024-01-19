/*
    Author: sbadhan Siddhesh Badhan
    Last Modified: 2/25/2023

    A simple UDP client that sends user input to a server and prints the reply from the server.
 */

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class EchoClientUDP {
    /**
     * Main method that prompts the user for server information, reads input from standard input,
     * sends data to the server, and prints the reply from the server.
     * @param args arguments passed to the program, not used
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    public static void main(String args[]) {
        // args give message contents and server hostname
        DatagramSocket clientSocket = null;
        try {
            // Get the InetAddress for the server
            InetAddress serverAddress = InetAddress.getByName("localhost");
            System.out.println("The UDP client is running.");
            // Prompt the user for the server port number
            int serverPort = getServerPort();
            // Create a BufferedReader to read from standard input
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            clientSocket = new DatagramSocket();
            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                // Convert the input string to bytes
                byte[] m = nextLine.getBytes();
                // Create a datagram packet to send the data to the server
                DatagramPacket request = new DatagramPacket(m,  m.length, serverAddress, serverPort);
                clientSocket.send(request);
                // Create a datagram packet to receive the reply from the server
                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                clientSocket.receive(reply);
                // Convert the reply data to a string and print it out
                String replyString = new String(reply.getData()).substring(0, reply.getLength());
                System.out.println("Reply from server: " + replyString);
                // If the reply is "halt!", break the while loop
                if (replyString.equals("halt!")) {
                    System.out.println("Client side quitting");
                    break;
                }
            }

        } catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            if (clientSocket != null)
                clientSocket.close();
        }
    }

    /**
     * Method to prompt the user for the server port number to connect to.
     *
     * @return the port number entered by the user
     */
    // Method to prompt the user for the server port number to listen on
    public static int getServerPort() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the server-side port number.");
        String serverPortStr = scanner.nextLine();
        int serverPort = Integer.parseInt(serverPortStr);
        return serverPort;
    }
}
