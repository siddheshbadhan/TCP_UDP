/*
    Author: sbadhan Siddhesh Badhan
    Last Modified: 2/25/2023

    The RemoteVariableClientTCP class is used to create a TCP client that communicates with a remote variable server.
    It allows the user to add or subtract values from their sum, get their sum, or exit the client.
 */

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class RemoteVariableClientTCP {
    static Socket clientSocket = null; // declare a static variable of type Socket named clientSocket and initialize it to null
    static int serverPort; // declare a static variable of type int named serverPort
    static BufferedReader reader; // declare a static variable of type BufferedReader named reader
    static PrintWriter writer; // declare a static variable of type PrintWriter named writer

    /**
     * The main method starts the client and connects to the server.
     * It prompts the user for input and sends it to the server, and prints the resulting sum.
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    public static void main(String args[]) {
        System.out.println("The client is running.");
        serverPort = getServerPort(); // call the getServerPort method and store the result in the serverPort variable
        try {
            clientSocket = new Socket("localhost", serverPort); // create a new Socket object and assign it to the clientSocket variable, passing "localhost" and the serverPort as arguments to the constructor
            while (true) { // start an infinite loop
                String option = getOption(); // call the getOption method and store the result in the option variable
                if (option.equals("4")) { // if the option is "4", break out of the loop
                    break;
                }
                int result = pass(option); // call the pass method with the option and store the result in the result variable
                System.out.println("The result is " + result + ".");
                System.out.println();
            }
            System.out.println("Client side quitting. The remote variable server is still running.");
        } catch (IOException e) { // catch any IOExceptions that occur
            System.out.println("IO Exception:" + e.getMessage());
        } finally { // run this block of code regardless of whether an exception is caught or not
            try {
                if (clientSocket != null) { // if the clientSocket variable is not null
                    clientSocket.close(); // close the socket
                }
            } catch (IOException e) { // catch any IOExceptions that occur when closing the socket
                // ignore exception on close
            }
        }
    }

    /**
     * The getOption method prompts the user for input and returns a string concatenating type, id, number.
     * If the user enters 4, it returns "4".
     * @return A string concatenating type, id, number.
     */
    // This function returns a string concatenating type, id, number
    // And returns 4 if the user insert 4.
    public static String getOption(){
        Scanner scanner = new Scanner(System.in); // create a new Scanner object with System.in as the argument
        printOptions(); // call the printOptions method
        int choice = Integer.parseInt(scanner.nextLine()); // read the user's input and parse it as an integer
        String value = "0"; // initialize the value variable to "0"
        String id; // declare a variable of type String named id
        switch(choice) { // evaluate the value of choice
            case 1:
                System.out.println("Enter value to add:");
                value = scanner.nextLine(); // read the user's input and store it in the value variable
                break;
            case 2:
                System.out.println("Enter value to subtract:");
                value = scanner.nextLine(); // read the user's input and store it in the value variable
                break;
            case 4:
                return String.valueOf(choice); // return the String value of choice
        }
        System.out.println("Enter your ID:");
        id = scanner.nextLine(); // read the user's input and store it in the id variable
        return String.valueOf(choice) + "," + id + "," + value; // concatenate the choice, id, and value variables and return the resulting string
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
        String data = null;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            writer.println(s);
            writer.flush();
            data = reader.readLine(); // read a line of data from the stream
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        }
        return Integer.parseInt(data);
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

