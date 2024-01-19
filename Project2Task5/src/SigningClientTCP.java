/*
    Author: sbadhan Siddhesh Badhan
    Last Modified: 2/25/2023

    This class implements a client that connects to a server to perform arithmetic operations.
    The client generates a public and private key using RSA algorithm to sign messages. It also generates a unique ID
    using the last 20 bytes of the public key. The client can add, subtract and retrieve the sum of the
    values it has added before. The client sends a message to the server containing the option chosen by
    the user and the signed message. The signed message is created by concatenating the ID, public key,
    option and value, and then signing it with the private key.
 */

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;
import java.security.MessageDigest;

public class SigningClientTCP {
    static Socket clientSocket = null;
    static int serverPort;
    static BufferedReader reader;
    static PrintWriter writer;
    static BigInteger n, e, d, id;
    static String public_key, private_key;

    /**
     * The main method of the class connects to the server, generates a public and private key, and
     * generates a unique ID. Then, it enters a loop where it gets an option from the user, creates
     * a signed message, sends it to the server, and receives the result. If the option is 4, it
     * exits the loop and closes the connection to the server.
     *
     * @param args hostname of the server
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    public static void main(String args[]) {
        // arguments supply hostname
        System.out.println("The client is running.");
        generateKey();
        generateID();
        // get the port number from the client
        serverPort = getServerPort();
        try {
            clientSocket = new Socket("localhost", serverPort);
            String message;
            String encryptedMessage;
            while (true) {
                String option = getOption();
                if (option.equals("4")) {
                    break;
                }
                encryptedMessage = sign(option);
                String pack = option + " " + encryptedMessage;
                int result = pass(pack);
                System.out.println("The result is " + result + ".");
                //System.out.println();
            }
            System.out.println("Client side quitting. The remote variable server is still running.");
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
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
     * This method gets an option from the user (1, 2, 3 or 4), asks the user for a value if the option
     * is 1 or 2, and creates a message to be signed using the ID, public key, option and value. It then
     * signs the message using the private key and returns the signed message.
     *
     * @return the signed message
     */
    // This function returns a string concatenating type, id, number
    // And returns 4 if the user insert 4.
    public static String getOption(){
        Scanner scanner = new Scanner(System.in);
        printOptions();
        int choice = Integer.parseInt(scanner.nextLine());
        String value = "0";
        //String id;
        // return string based on user's insertion.
        switch(choice) {
            case 1:
                System.out.println("Enter value to add:");
                value = scanner.nextLine();
                break;
            case 2:
                System.out.println("Enter value to subtract:");
                value = scanner.nextLine();
                break;
            // if the user insert 4, then return 4 to exit the client program
            case 4:
                return String.valueOf(choice);
        }
        //System.out.println("Enter your ID:");
        //id = scanner.nextLine();
        return id + "," + e + "," + n + "," + String.valueOf(choice) + "," + value;
    }

    /**
     * This method prints the options available to the user.
     */
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
            String signed_s = sign(s);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            writer.println(s);
            writer.flush();
            data = reader.readLine(); // read a line of data from the stream
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        }
        return  Integer.parseInt(data);
    }

    /**
     Generates RSA public and private keys using the RSA algorithm.
     Uses two large random primes p and q, and computes n = p * q and phi(n) = (p-1) * (q-1).
     Selects a small odd integer e that is relatively prime to phi(n), and computes d as the multiplicative inverse of e modulo phi(n).
     The resulting public key is (e,n), and the private key is (d,n).
     Stores the public key and private key as strings.
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    // Code Exploit from RSAExample,
    // it will generate the public key and private key everytime the client starts the program
    public static void generateKey(){
        System.out.println("Generating keys");
        Random rnd = new Random();
        // Step 1: Generate two large random primes.
        // We use 400 bits here, but best practice for security is 2048 bits.
        // Change 400 to 2048, recompile, and run the program again, and you will
        // notice it takes much longer to do the math with that many bits.
        BigInteger p = new BigInteger(400, 100, rnd);
        BigInteger q = new BigInteger(400, 100, rnd);

        // Step 2: Compute n by the equation n = p * q.
        n = p.multiply(q);

        // Step 3: Compute phi(n) = (p-1) * (q-1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Step 4: Select a small odd integer e that is relatively prime to phi(n).
        // By convention the prime 65537 is used as the public exponent.
        e = new BigInteger("65537");

        // Step 5: Compute d as the multiplicative inverse of e modulo phi(n).
        d = e.modInverse(phi);
        public_key = String.valueOf(e) + String.valueOf(n);
        private_key = String.valueOf(d) + String.valueOf(n);
        System.out.println("your public key = (" + public_key + ")");  // Step 6: (e,n) is the RSA public key
        System.out.println("your private key = (" + private_key + ")");  // Step 7: (d,n) is the RSA private key
    }
    /**

     Generates a unique ID using the last 20 bytes of the RSA public key.
     Computes the SHA-256 hash value of the public key and copies the last 20 bytes to an ID byte array.
     Converts the ID byte array to a BigInteger and stores it as a string.
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    // This method generate a unique id with the last 20  byte of the public key
    public static void generateID(){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(public_key.getBytes());
            byte[] hash_value = md.digest();
            byte[] id_byte = new byte[20];
            int len_of_hash_value = hash_value.length;
            // copy the last 20 bytes to id_byte
            for(int i = 0; i < 20; i++){
                id_byte[20-i-1] = hash_value[len_of_hash_value - i - 1];
            }
            id = new BigInteger(id_byte);
            System.out.println("Your id is: " + id);
        }
        catch(NoSuchAlgorithmException e) {
            System.out.println("No Hash available" + e);
        }
    }
    /**
     Computes the signature of a message using the RSA algorithm.
     Computes the SHA-256 hash value of the message and adds a 0 byte as the most significant byte to keep the value to be signed non-negative.
     Encrypts the resulting digest with the private key using modular exponentiation, and returns the resulting signature as a string.
     @param message the message to be signed
     @return the signature of the message as a string
     */
    //Code from Github Project 2 repository
    //https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    // compute the signature (the hash_value of the whole message)
    static public String sign(String message) {
        // compute the digest with SHA-256
        BigInteger c = null;
        try{
            byte[] bytesOfMessage = message.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bigDigest = md.digest(bytesOfMessage);
            // we add a 0 byte as the most significant byte to keep
            // the value to be signed non-negative.
            // Copy every byte of bigDigest
            byte[] messageDigest = new byte[bigDigest.length + 1];
            messageDigest[0] = 0;   // most significant set to 0
            for(int i = 0; i < bigDigest.length; i++){
                messageDigest[i+1] = bigDigest[i];
            }
            // From the digest, create a BigInteger
            BigInteger m = new BigInteger(messageDigest);
            // encrypt the digest with the private key
            c = m.modPow(d, n);
            // return this as a big integer string
        }catch (Exception e){
            e.printStackTrace();
        }
        return c.toString();
    }

    /**
     This function asks the user to enter the server's port number and returns it as an integer.
     @return The integer value of the server's port number entered by the user.
     */
    public static int getServerPort() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the server-side port number.");
        String serverPortStr = scanner.nextLine();
        int serverPort = Integer.parseInt(serverPortStr);
        return serverPort;
    }
}