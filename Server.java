import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

/**
 * Created by William on 2017-02-04.
 */
public class Server {

    private static final int BUFFER_SIZE = 1024;

    private void ohce(PrintWriter out) throws IOException {
        DatagramSocket udpSocket = new DatagramSocket(0);
        int rPort = udpSocket.getLocalPort();
        out.print(rPort);
        out.flush();
        out.close();

        byte[] inputData = new byte[BUFFER_SIZE];
        DatagramPacket inputPacket = new DatagramPacket(inputData, inputData.length);
        udpSocket.receive(inputPacket);

        InetAddress ipAddress = inputPacket.getAddress();
        int remotePort = inputPacket.getPort();

        String message = new String(inputPacket.getData()); // encoding?
        String egassem = new StringBuilder(message).reverse().toString();
        byte[] outputData = egassem.getBytes();

        DatagramPacket outputPacket = new DatagramPacket(outputData, outputData.length, ipAddress, remotePort);
        udpSocket.send(outputPacket);
    }

    private ServerSocket newServerSocketWithEphemeralPort() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        int nPort = serverSocket.getLocalPort();
        System.out.printf("SERVER_PORT=%d\n", nPort);
        return serverSocket;
    }

    public Server(int requestCode) {
        ServerSocket serverSocket = this.newServerSocketWithEphemeralPort();
        while (true) {
            try (
                    Socket tcpSocket = serverSocket.accept();
                    Scanner in = new Scanner(tcpSocket.getInputStream());
                    PrintWriter out = new PrintWriter(tcpSocket.getOutputStream())
            ) {
                int receivedCode = in.nextInt();
                if (receivedCode == requestCode) {
                    this.ohce(out);
                } else {
                    System.err.println("Incorrect request code.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length  != 1) throw new IllegalArgumentException();
            int requestCode = Integer.parseInt(args[0]);
            new Server(requestCode);
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            System.err.println("Please enter a valid request code only.");
            System.exit(1);
        }
    }
}
