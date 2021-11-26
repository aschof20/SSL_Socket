
import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;


public class SSLServer {
    public static void main(String[] args) {

        // Server exit if port omitted at start-up or incorrect number of arguments added.
        if (args.length < 1){
            System.out.println( "Correct Command: ./startServer.sh <port>" );
            System.exit(0);
        }

        // String to store port argument.
        String port = args[0];

        // System exit if port argument is not an integer.
        if (!port.matches("[0-9]+")) {
            System.out.println( "Port number must be an integer." );
            System.exit(0);
        }

        System.out.println("Connected with port: " + port);

        try {
            // System.setProperty("javax.net.debug", "all");

            // Create keystore and truststore passwords.
            char[] password = "abcdefg".toCharArray();
            char[] password2 = "aabbcc".toCharArray();

            // Initialise the keystore with PKCS12 file format.
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            // Load the server certificate generated with OpenSSL.
            keyStore.load(new FileInputStream("resources/server/server-certificate.p12"), password);

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
            // Load the client certificate generated with OpenSSL.
            trustStore.load(new FileInputStream("resources/client/client-certificate.p12"), password2);

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

            // Generate socket factory to create secure server sockets with the SSLContext.
            SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();

            // Generate SSLServerSocket object with TLSv1.2 protocol using the socket factory.
            SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(Integer.parseInt(port));
            serverSocket.setEnabledProtocols(new String[]{"TLSv1.2"});

            // Enable server socket to accept incoming transmissions from the client.
            SSLSocket socket = (SSLSocket) serverSocket.accept();

            // Require authentication of client prior to server accepting client transmissions.
            socket.setNeedClientAuth(true);

            // Input and output streams to read from and write to the client.
            PrintWriter writer= new PrintWriter(socket.getOutputStream(), true);
            BufferedReader clientInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Variable to store user input from client.
            String sequence;

            // Server keeps listening to the client until socket closed.
            while (true) {

                // Assign the client input sequence to sequence variable.
                sequence = clientInput.readLine();

                // Print the input from the client.
                System.out.println("CLIENT: " + sequence);

                // If the disconnect message is received, close the socket and exit.
                if (sequence.equalsIgnoreCase("disconnect")) {
                    // Call the closeSocket function to close socket and exit.
                    closeSocket(socket, writer, clientInput);
                    break;
                }

                /* If the start RNA message is received proceed to accepting input DNA sequence, otherwise
                close the socket if an invalid message was receieved.*/
                if (sequence.equalsIgnoreCase("start dna")) {
                    writer.println("SERVER READY");
                    continue;
                } else if (!sequence.matches("^[ATCGatcg]+$") || sequence.length() % 3 != 0) {
                    writer.println("CLOSE SERVER");
                    closeSocket(socket, writer, clientInput);
                    break;
                }
                // Call the optimiseDNA function to optimise DNA sequence for optimal RNA output.
                String optimalSequence = optimiseDNA(sequence);

                // Send the optimal sequence to the client.
                writer.println(optimalSequence);
            }
        }catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException |
                NoSuchProviderException | KeyStoreException | KeyManagementException e){
            e.printStackTrace();
        }
    }

    /**
     * Function:        getCodons()
     * Description:     Function that takes a DNA string and partitions it into codons and adds
     *                  the codons to a list.
     * @param str       - DNA string to be partitioned.
     * @return          List of codons from the client DNA string.
     */
    private static List<String> getCodons(String str) {
        List<String> parts = new ArrayList<>();
        int len = str.length();
        for (int i=0; i<len; i+= 3)
        {
            parts.add(str.substring(i, Math.min(len, i + 3)));
        }
        return parts;
    }

    /**
     * Function:     opimtiniseDNA()
     * Description:  A function that breaks DNA into codons, identifies the associated amino acid and
     *               retrieves the optimal codon for the amino acid.
     * @param str    - DNA oinput string from client.
     * @return       Optimised DNA sequence
     */
    private static String optimiseDNA(String str){

        // Call the getCodons function to partition string into codons stored in a list.
        List<String> codonList = getCodons(str);

        // String buffer to store to optimised sequence.
        StringBuilder optimisedSequence = new StringBuilder();

        // Empty string array to store optimal codon conversion.
        String[] optimalCodonList = new String[codonList.size()];

        /* Loop over codon list and identify the associated amino acid and the optimal codon
           representing that amino acid.*/
        for (int i = 0; i < codonList.size(); i++) {

            // Convert codon into optimal codon.
            String optimalCodon = OptimalCodons.valueOf(AminoAcids.valueOf(codonList.get(i)).toString()).toString();

            // Add optimal codon to a list of codons representing the sequence received from the client.
            optimalCodonList[i] = String.valueOf(optimalCodon);
        }

        // Loop over the optimal codon list and add to the stringbuffer to create the optimal string.
        for (String s : optimalCodonList) optimisedSequence.append(s);

        return optimisedSequence.toString();
    }

    private static void closeSocket(SSLSocket socket, PrintWriter pw, BufferedReader br) throws IOException {
        pw.close();
        br.close();
        socket.close();
        System.out.println("Server shutting down.");
        System.exit(0);
    }
}
