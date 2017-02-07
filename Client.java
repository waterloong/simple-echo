import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

/**
 * Created by William on 2017-02-05.
 */
public class Client {

    public static final int BUFFER_SIZE = 1024;
    private InetAddress serverAddress;
    private int nPort;
    private int requestCode;
    private String message;

    private int getRPort() {
        try (
                Socket tcpSocket = new Socket(this.serverAddress, this.nPort);
                Scanner in = new Scanner(tcpSocket.getInputStream());
                PrintWriter out = new PrintWriter(tcpSocket.getOutputStream())
        ) {
            out.println(this.requestCode);
            out.flush();
            int rPort = in.nextInt();
            return rPort;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return -1; // should never reach
    }

    private void sendMessage(int rPort) {
        try {
            DatagramSocket udpSocket = new DatagramSocket(0);

            byte[] outputData = this.message.getBytes();
            DatagramPacket outputPacket = new DatagramPacket(outputData, outputData.length, this.serverAddress, rPort);
            udpSocket.send(outputPacket);

            byte[] inputData = new byte[BUFFER_SIZE];
            DatagramPacket inputPacket = new DatagramPacket(inputData, inputData.length);
            udpSocket.receive(inputPacket);
            System.out.println(new String(inputData));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Client(InetAddress serverAddress, int nPort, int requestCode, String message) {
        this.serverAddress = serverAddress;
        this.nPort = nPort;
        this.requestCode = requestCode;
        this.message = message;
        int rPort = this.getRPort();
        this.sendMessage(rPort);
    }

    public static void main(String[] args) {
        try {
            // <server_address> , <n_port>, <req_code>, and <msg>
            if (args.length  != 4) throw new IllegalArgumentException();
            InetAddress serverAddress = InetAddress.getByName(args[0]);
            int nPort = Integer.parseInt(args[1]);
            int requestCode = Integer.parseInt(args[2]);
            String message = args[3];
            new Client(serverAddress, nPort, requestCode, message);
        } catch (Exception e) {
            System.err.println("The input parameters do not meet the specification");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
