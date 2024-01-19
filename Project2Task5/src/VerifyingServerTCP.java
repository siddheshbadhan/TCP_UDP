/*
    Author: sbadhan Siddhesh Badhan
    Last Modified: 2/25/2023

    VerifyingServerTCP is a class that receives requests from a client and verifies the hash of the message.
    If the hash is verified, it conducts the corresponding arithmetic according to the request.
 */
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class VerifyingServerTCP {
    static BigInteger n, e, id;
    static int operator;
    static Map<BigInteger, Integer> map = new TreeMap<>();
    /**
     * This method is the main entry point for the server program. It listens for requests from clients and
     * processes them accordingly. It also verifies the hash of the message before processing the request.
     *
     * @param args the command line arguments
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
            clientSocket = listenSocket.accept();
            // If we get here, then we are now connected to a client.
            // Set up scanner to read from the client socket
            Scanner scanner = new Scanner(clientSocket.getInputStream());
            // Set up writer to write to the client socket
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            // In order for the server to run forever, we need to handle the situation when client shut down
            while (true) {
                String data;
                // When the client is connected, there will be string pass to in.
                // Under the situation, we can safely conduct the calculation and return the value.
                if(scanner.hasNextLine()){
                    data = scanner.nextLine();
                    // The string passed by the client is id, e, n, operator, number seperated by ","
                    // and the hash message seperated by " "
                    String[] message = data.split(" ");
                    String[] operation = message[0].split(",");
                    id = new BigInteger(operation[0]);
                    e = new BigInteger(operation[1]);
                    n = new BigInteger(operation[2]);
                    operator = Integer.parseInt(operation[3]);
                    int value = Integer.parseInt(operation[4]);
                    // conduct corresponding arithmetic according to the three index
                    // verify the hash message
                    if(idMatch(message[0], message[1])){
                        int outcome = arithmetic(operator, id, value);
                        // return the outcome of calculation to the client
                        System.out.println("Returning sum of " + outcome + " to client");
                        writer.println(outcome);
                    }
                    else{
                        writer.println("The request is wrongly encrypted");
                    }
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
    // Doing corresponding arithmetic according to the user's request
    public static int arithmetic(int operator, BigInteger id, int value){
        String[] operation = {"Addition", "Subtraction", "Check"};
        System.out.println("The visitor's ID is: " + id);
        System.out.println("Operator: " + operation[operator-1]);
        if(operator == 1){
            map.put(id, map.getOrDefault(id,0) + value);
        }
        else if (operator == 2){
            map.put(id, map.getOrDefault(id,0) - value);
        }
        // return the number after calculation
        return map.getOrDefault(id,0);
    }

    /**

     This method checks whether the provided message hash matches the encrypted hash string.
     @param messageToCheck the message to check.
     @param encryptedHashStr the encrypted hash string to check.
     @return true if the provided message hash matches the encrypted hash string, false otherwise.
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    // Check if the hash of messageToCheck is same as the decryption if encryptedHashStr
    // Exploit from ShortMessageVerify.java provided by the handout
    public static boolean idMatch(String messageToCheck, String encryptedHashStr) {
        BigInteger decryptedHash = null;
        BigInteger bigIntegerToCheck = null;
        try{
            // Decrypt it
            decryptedHash = new BigInteger(encryptedHashStr).modPow(e, n);
            // Get the bytes from messageToCheck
            byte[] bytesOfMessageToCheck = messageToCheck.getBytes("UTF-8");
            // compute the digest of the message with SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] bigDigest = md.digest(bytesOfMessageToCheck);

            // messageToCheckDigest is a full SHA-256 digest
            // add a zero byte in front of bigDigest
            byte[] messageToCheckDigest  = new byte[bigDigest.length + 1];
            messageToCheckDigest [0] = 0;   // most significant set to 0
            for(int i = 0; i < bigDigest.length; i++){
                messageToCheckDigest [i+1] = bigDigest[i];
            }
            bigIntegerToCheck = new BigInteger(messageToCheckDigest);
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("The hash message provided by the client: " + decryptedHash);
        System.out.println("The hash value of the message:" + bigIntegerToCheck);
        System.out.println("Verify result: " + bigIntegerToCheck.equals(decryptedHash));
        if(bigIntegerToCheck.equals(decryptedHash)){
            return true;
        } else {
            return false;
        }
    }
}