

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Scanner;


public class SSLClient {
    public static void main(String[] args){

        // Client exit when incorrect number of arguments entered at start-up.
        if (args.length != 2) {
            System.out.println("Correct Command: ./startClient.sh <hostname> <port>");
            System.exit(0);
        }

        // Variables to store command line arguments.
        String hostname = args[0];
        String port = args[1];

        // System exit if port argument is not an integer.
        if (!port.matches("[0-9]+")) {
            System.out.println( "Port number must be an integer." );
            System.exit(0);
        }

        try {
            // System.setProperty("javax.net.debug", "all");

            // Create keystore and truststore passwords.
            char[] password = "aabbcc".toCharArray();
            char[] password2 = "abcdefg".toCharArray();

            // Initialise the keystore with PKCS12 file format.
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            // Load the client certificate generated with OpenSSL.
            keyStore.load(new FileInputStream("resources/client/client-certificate.p12"), password);

            // Create KeyManagerFactory object that uses the SunX509 algorithm from SunJSSE.
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
            // Initiate key manager factory with keystore with certificate and password.
            keyManagerFactory.init(keyStore, password);

            // Instantiate empty X509KeyManager.
            X509KeyManager x509KeyManager = null;

            // Loop over the keyManagerFactory and identify an keyManager that is a X509 type.
            for (KeyManager keyManager : keyManagerFactory.getKeyManagers()) {
                if (keyManager instanceof X509KeyManager) {
                    // Assign the keyManager in the factory that is a X509 type to the x509KeyManager variable.
                    x509KeyManager = (X509KeyManager) keyManager;
                    break;
                }
            }
            // Throw exception if key manager in factory of X509 type cannot be found.
            if (x509KeyManager == null) throw new NullPointerException();

            // Initialise the truststore with PKCS12 file format to 'trust' the client.
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            // Load the server certificate generated with OpenSSL.
            trustStore.load(new FileInputStream("resources/server/server-certificate.p12"), password2);

            // Create TrustManagerFactory object that uses the PKIX algorithm from SunJSSE.
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX", "SunJSSE");

            // Initiate trust manager factory with truststore with certificate.
            trustManagerFactory.init(trustStore);

            // Instantiate empty X509TrustManager.
            X509TrustManager x509TrustManager = null;

            // Loop over the trustManagerFactory and identify a trustManager that is a X509 type.
            for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
                if (trustManager instanceof X509TrustManager) {
                    // Assign the trustManager in the factory that is a X509 type to the x509TrustManager variable.
                    x509TrustManager = (X509TrustManager) trustManager;
                    break;
                }
            }
            // Throw exception if trust manager in factory of X509 type cannot be found.
            if (x509TrustManager == null) throw new NullPointerException();

            // Instantiate SSLContext object to be used by the SSLSocketFactory to obtain relevant state information.
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // Initialise the SSLContext with the keystore and truststore details of the x509 certificate key pairs.
            sslContext.init(new KeyManager[]{x509KeyManager}, new TrustManager[]{x509TrustManager}, null);

            // Generate socket factory to create secure client sockets with the SSLContext.
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();

            // Generate client SSLSocket object with TLSv1.2 protocol using the socket factory.
            SSLSocket socket = (SSLSocket) socketFactory.createSocket(hostname, Integer.parseInt(port));
            socket.setEnabledProtocols(new String[]{"TLSv1.2"});

            // Input and output streams to read from and write to the server.
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Stream to read input from the client/user.
            BufferedReader clientIn = new BufferedReader(new InputStreamReader(System.in));

            // String variable for user input from console.
            String userInput;

            // Variable to make menu option selections.
            boolean menuOptionSelected = false;

            while (!socket.isClosed()) {
                // Loop over the menu when an option is not selected.
                while (true) {

                    // Variable to store user chosen option.
                    String menuOption;
                    // Call the menu function to display menu options.
                    menuOption = Menu();

                    // Menu Options: 1 - DNA optimisation, 2 - System Exit.
                    if (menuOption.equalsIgnoreCase("1")) {
                        menuOptionSelected = true;
                        String startServer = "start DNA";
                        toServer.println(startServer.toUpperCase());
                        break;
                    } else if (menuOption.equalsIgnoreCase("2")) {
                        // Send disconnect message to server and exit client side.
                        userInput = "Disconnect";
                        toServer.println(userInput.toUpperCase());

                        // Call closeSocket function to close streams and socket and exit the system.
                        closeSocket(socket, toServer, fromServer);
                        break;
                    }

                    // If an valid menu option selected user prompted to re-enter option.
                    if (!menuOption.equalsIgnoreCase("1") || !menuOption.equalsIgnoreCase("2")) {
                        System.out.println("Invalid Menu Option Entered - only option 1 and 2 available.");
                    }
                }

                // Variable to store if the server has acknowledged the "START DNA" message.
                boolean serverConfirm = false;

                /* Loop to validate user input and send client input to server for optimisation. */
                while (menuOptionSelected) {
                    String acknowledge = fromServer.readLine();
                    if(acknowledge.equalsIgnoreCase("SERVER READY")){

                        // Set serverConfirm to true, server confirmed ready.
                        serverConfirm = true;
                    }
                    if (serverConfirm) {
                        System.out.println("");
                        System.out.println("Please enter DNA Sequence");
                        System.out.println("");

                        // Get user DNA sequence input.
                        System.out.print("User DNA sequence: ");
                        userInput = clientIn.readLine();

                        // If server is closing,then client closes.
                        if(userInput.equalsIgnoreCase("CLOSE SERVER")){
                            closeSocket(socket, toServer, fromServer);
                        }
                        // Validate that the user input is a correct DNA sequence.
                        if (userInput.length() % 3 != 0 || !(userInput.matches("^[ATCGatcg]+$"))) {

                            if (userInput.length() % 3 != 0) {
                                System.out.println("");
                                System.out.println("**Error: Sequences must be divisible by 3**");
                                System.out.println("");
                            } else if (!(userInput.matches("^[ATCGatcg]+$"))) {
                                System.out.println("");
                                System.out.println("**Error: Sequence must contain DNA nucleotides(A,T,G,C)**");
                                System.out.println("");
                            }
                            break;
                        }

                        // Send the user input DNA sequence converted to uppercase to the server.
                        toServer.println(userInput.toUpperCase());
                        // Assign server optimised DNA to variable.
                        String optimisedDNA = fromServer.readLine();

                        // If server optimised DNA invalid, close the socket and exit.
                        if (optimisedDNA.length() % 3 != 0 || !(optimisedDNA.matches("^[ATCGatcg]+$"))) {
                            System.out.println("Invalid input");
                            toServer.println("DISCONNECT");
                            closeSocket(socket, toServer, fromServer);
                        } else {
                            // Output the validated optimised DNA sequence into the console.
                            System.out.println("");
                            System.out.println("Optimised DNA: " + optimisedDNA);
                            System.out.println("");

                            // Reset menu selection to return to the menu.
                            menuOptionSelected = false;
                            //Reset serverConfirm, to ensure confirmation before next optimisation.
                            serverConfirm = false;
                        }
                    }
                }
            }
        }catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException
                | NoSuchProviderException | KeyStoreException | KeyManagementException e){
            e.printStackTrace();
        }
    }

    /**
     * Function:       Menu()
     * Description:    Function that provides a user interface for the program.
     *
     * @return the menu option selected by the user.
     */
    private static String Menu() {
        //User Interface
        //Variable to record menu selection
        String selection;
        Scanner inputMenu = new Scanner(System.in);
        System.out.println("Select a Menu Item: ");
        System.out.println("Option 1 - Optimise DNA");
        System.out.println("Option 2 - Exit");
        System.out.print("Select Option: ");
        selection = inputMenu.next();
        return selection;
    }

    /**
     * Function:        closeSocket()
     * Description:     Function that closes the client socket and streams and exits the system.
     *
     * @param socket - variable that represents the client side socket.
     * @param pw     - printwriter for sending client input to server.
     * @param br     - bufferreader for receiving output from the server.
     */
    private static void closeSocket(SSLSocket socket, PrintWriter pw, BufferedReader br) throws IOException {
        pw.close();
        br.close();
        socket.close();
        System.out.println("Client shutting down.");
        System.exit(0);
    }
}
