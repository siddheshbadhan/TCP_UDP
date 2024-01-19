/*
    Author: sbadhan Siddhesh Badhan
    Last Modified: 2/25/2023

    This class represents a server that can perform arithmetic operations on remote variables over UDP.
    The server listens on a designated port and receives requests from clients
    in the form of a string with comma-separated values.
 */

import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class RemoteVariableServerUDP{

    // Create a TreeMap to store the current values of the variables
    static Map<Integer, Integer> map = new TreeMap<>();

    /**
     The main method of the server.
     Creates a DatagramSocket object to listen to the client's request on the designated port.
     Whenever a request is received from the client, the server performs the corresponding arithmetic operation on the remote variable
     and returns the result to the client.
     The server prints out the ID of the visitor and the arithmetic operation type.
     @param args an array of command-line arguments for the server.
     */
    public static void main(String args[]){

        DatagramSocket socket = null;
        byte[] buffer = new byte[1000];
        System.out.println("Server started.");
        // Define the port number that the server will listen to
        int listenPort = 6789;

        try{

            // Create a DatagramSocket object to listen to the client's request on the designated port
            socket = new DatagramSocket(listenPort);
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);

            // Whenever the aSocket receive a request from the client side, it will add it to the sum variable
            // and return the sum value. It will then print out the sum
            while(true){

                // Receive a request from the client
                socket.receive(request);

                // Create a DatagramPacket object with the request
                String requestString = new String(request.getData()).substring(0,request.getLength());

                // Split the passed string into operator, id, and number
                String[] operation = requestString.split(",");
                int operator = Integer.parseInt(operation[0]);
                int id = Integer.parseInt(operation[1]);
                int value = Integer.parseInt(operation[2]);

                // Conduct corresponding arithmetic according to the three index
                int result = arithmetic(operator, id, value);

                // Return the outcome of calculation to the client
                byte [] response = String.valueOf(result).getBytes();
                DatagramPacket reply = new DatagramPacket(response,
                        response.length, request.getAddress(), request.getPort());

                // Send the reply back to the client
                System.out.println("Returning sum of " + result + " to client");
                socket.send(reply);
            }

        }catch (SocketException e){
            System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }finally {
            if(socket != null) socket.close();
        }
    }

    /**

     Performs arithmetic operations on a variable identified by a unique ID.
     @param operator An integer representing the type of arithmetic operation to be performed. 1 is for addition, 2 is for subtraction, and 3 is for checking the current value.
     @param id An integer representing the unique ID of the variable to be operated on.
     @param value An integer representing the value to be added or subtracted from the variable.
     @return An integer representing the current value of the variable after the arithmetic operation.
     */
    // Doing corresponding arithmetic according to the user's request
    public static int arithmetic(int operator, int id, int value){

        // Define an array to hold the operation type
        String[] operation = {"Addition", "Subtraction", "Check"};
        // Print out the visitor's ID and the operation type
        System.out.println("The visitor's ID is: " + id);
        System.out.println("Operand: " + operation[operator-1]);
        // Get the current value of the variable with the corresponding ID
        int currentValue = map.getOrDefault(id, 0);
        // Update the value of the variable based on the operator
        if(operator == 1){
            currentValue += value;
        }
        else if (operator == 2){
            currentValue -= value;
        }
        // Update the map with the new value of the variable
        map.put(id, currentValue);
        // Return the number after calculation
        return currentValue;
    }
}
