/*
    Author: sbadhan Siddhesh Badhan
    Last Modified: 2/25/2023

    The RemoteVariableClientUDP class is responsible for communicating with the RemoteVariableServerUDP
    over UDP protocol. The user is prompted for a port number for the server to connect to, and then
    presented with a menu of options to interact with the server.
 */

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class RemoteVariableClientUDP{
    static DatagramSocket socket = null; // declares a static datagram socket variable
    static int serverPort; // declares a static integer variable for server port
    static InetAddress host; // declares a static InetAddress variable for host

    /**
     * The main method is responsible for setting up the connection to the server, prompting the user for
     * input, and handling the interaction between the client and server. It runs until the user enters
     * option 4 to exit the client.
     * @param args command line arguments (not used)
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    public static void main(String aƒƒrgs[]){
        // displays that the client is running
        System.out.println("The client is running.");

        serverPort = getServerPort(); // gets the server port from the user

        try {
            host = InetAddress.getByName("localhost"); // gets the IP address of the localhost
            socket = new DatagramSocket(); // creates a new datagram socket
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage()); // handles socket exceptions
        } catch (IOException e){
            System.out.println("IO: " + e.getMessage()); // handles input/output exceptions
        }

        // loops until the user enters option 4
        while (true) {
            String option = getOption(); // gets user input for option
            if (option.equals("4")) { // if the user enters 4, break the loop
                break;
            }
            int result = pass(option); // passes the option to the server and receives the result
            System.out.println("The result is " + result + ".");
            System.out.println();
        }

        System.out.println("Client side quitting. The remote variable server is still running.");
        if(socket != null) socket.close(); // closes the socket if it exists
    }

    /**
     * The getOption method is responsible for presenting the user with a menu of options and
     * prompting them for input. It returns a string that represents the user's choice.
     * @return a string representing the user's choice
     */
    // function that returns a string concatenating type, id, number or 4 if the user enters 4
    public static String getOption(){
        Scanner scanner = new Scanner(System.in); // creates a new scanner object

        printOptions(); // prints the available options

        int choice = Integer.parseInt(scanner.nextLine()); // gets user input for choice
        String value = "0"; // initializes value to 0
        String id; // declares a string variable for id

        switch(choice) {
            case 1:
                System.out.println("Enter value to add:");
                value = scanner.nextLine(); // gets user input for value
                break;
            case 2:
                System.out.println("Enter value to subtract:");
                value = scanner.nextLine(); // gets user input for value
                break;
            case 4:
                return String.valueOf(choice); // returns 4 if the user enters 4
        }

        System.out.println("Enter your ID:");
        id = scanner.nextLine(); // gets user input for id
        return String.valueOf(choice) + "," + id + "," + value; // returns the concatenated string
    }

    /**
     * The printOptions method is responsible for printing the menu of available options to the user.
     */
    // prints the available options for the user
    public static void printOptions(){
        String[] options = new String[4];
        options[0] = "Add a value to your sum.";
        options[1] = "Subtract a value from your sum.";
        options[2] = "Get your sum.";
        options[3] = "Exit client.";
        for(int i = 0; i < options.length; i++){
            System.out.println((i+1) + ". " + options[i]);
        }
    }

    /**
     This method sends a request to a server with the given String parameter and returns an integer response.
     It creates a datagram packet with the String message and sends it to the server at the specified port.
     The method then waits for a reply from the server and parses the reply as an integer to return to the caller.
     @param s The message to send to the server
     @return The integer response received from the server
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    //This function takes the concatenated string from the client and pass it to the server
    public static int pass(String s){
        String replyString = null;
        try {
            byte [] m = String.valueOf(s).getBytes();
            DatagramPacket request = new DatagramPacket(m,  m.length, host, serverPort);
            // send the request
            socket.send(request);
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            // receive the reply from the server side and print it out.
            socket.receive(reply);
            replyString = new String(reply.getData()).substring(0, reply.getLength());
            // If replyString is "halt!" break the while loop
        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());
        }catch (IOException e){System.out.println("IO: " + e.getMessage());
        }
        return Integer.parseInt(replyString);
    }

    /**

     This function asks the user to enter the server's port number and returns it as an integer.
     @return The integer value of the server's port number entered by the user.
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