/*
    Author: sbadhan Siddhesh Badhan
    Last Modified: 2/25/2023

    RemoteVariableServerTCP is a server that performs arithmetic operations on variables and returns the results to the client.
    It listens for incoming client connections on a specified port and accepts input strings from the client.
    The server runs indefinitely and handles situations when the client shuts down by renewing the client socket and in/out.
 */

// import necessary packages
import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

// define the main class
public class RemoteVariableServerTCP {
    // initialize a map to store the variables and their corresponding values
    static Map<Integer, Integer> map = new TreeMap<>();

    /**
     * The main function of the server that listens for incoming client connections and accepts input strings from the client.
     * The input strings are split into operator, ID, and value, and the corresponding arithmetic operation is performed on the variables.
     * The result of the operation is returned to the client.
     * The server runs indefinitely and handles situations when the client shuts down by renewing the client socket and in/out.
     *
     * @param args command line arguments passed to the program
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    public static void main(String args[]) {
        Socket clientSocket = null;
        System.out.println("Server started.");
        try {
            int serverPort = 7777; // the server port we are using
            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);
            // wait for a client connection
            clientSocket = listenSocket.accept();
            // If we get here, then we are now connected to a client.
            // Set up "in" to read from the client socket
            Scanner scanner = new Scanner(clientSocket.getInputStream());
            // Set up "out" to write to the client socket
            PrintWriter writer;
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            // In order for the server to run forever, we need to handle the situation when client shut down
            while (true) {
                String data;
                // When the client is connected, there will be string pass to in.
                // Under the situation, we can safely conduct the calculation and return the value.
                if(scanner.hasNextLine()){
                    // read the input string from the client
                    data = scanner.nextLine();
                    // split the input string by comma
                    String[] operation = data.split(",");
                    // parse the operator, ID, and value from the input string
                    int operator = Integer.parseInt(operation[0]);
                    int id = Integer.parseInt(operation[1]);
                    int value = Integer.parseInt(operation[2]);
                    // conduct corresponding arithmetic according to the three indices
                    int outcome = arithmetic(operator, id, value);
                    // return the outcome of calculation to the client
                    System.out.println("Returning sum of " + outcome + " to client");
                    writer.println(outcome);
                    writer.flush();
                }
                // However, when the client is shut down, there will not be next line pass to in.
                // In such cases, we will need to have clientSocket to accept another socket and renew the in/out
                else {
                    clientSocket = listenSocket.accept();
                    scanner = new Scanner(clientSocket.getInputStream());
                    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                }
            }
            // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());

            // If quitting (typically by you sending quit signal) clean up sockets
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }
    /**

     Performs arithmetic operations on a variable identified by a unique ID.
     @param operator An integer representing the type of arithmetic operation to be performed. 1 is for addition, 2 is for subtraction, and 3 is for checking the current value.
     @param id An integer representing the unique ID of the variable to be operated on.
     @param value An integer representing the value to be added or subtracted from the variable.
     @return An integer representing the current value of the variable after the arithmetic operation.
     */

    // function to perform arithmetic operation on the variables
    public static int arithmetic(int operator, int id, int value){
        // initialize an array to store the operator names
        String[] operation = {"Addition", "Subtraction", "Check"};
        // print the user's ID and the operator
        System.out.println("The visitor's ID is: " + id);
        System.out.println("Operator: " + operation[operator-1]);
        if(operator == 1){
            // perform addition on the specified variable
            map.put(id, map.getOrDefault(id,0) + value);
        }
        else if (operator == 2){
            // perform subtraction on the specified variable
            map.put(id, map.getOrDefault(id,0) - value);
        }
        // return the number after calculation
        return map.get(id);
    }
}