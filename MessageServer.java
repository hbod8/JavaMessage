import java.io.*;
import java.net.*;
import java.util.*;

public class MessageServer {
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        MessageServer msgsrv = new MessageServer();
        System.out.println("Starting server on port " + port + "...");
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
        } catch(Exception e) {
            System.out.println("Error creating server: " + e.getLocalizedMessage());
            System.exit(0);
        }
        System.out.println("Created server port, now waiting for users...");
        Socket client = null;
        MulticastSocket ms = null;
        try {
            ms = new MulticastSocket(4);
            ms.joinGroup(InetAddress.getByName("225.65.65.65"));
        } catch(Exception e) {
            System.out.println("IN:Error creating Datagram Server: " + e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(0);
        }
        while (true) {
            try {
                client = ss.accept();
                System.out.println("Connecting user: " + client.getInetAddress().toString());
            } catch(Exception e) {
                System.out.println("Error on server: " + e.getLocalizedMessage());
            }
            new MessageConnectionIn(client, ms).start();
            new MessageConnectionOut(client, ms).start();
        }
    }
}

class MessageConnectionOut extends Thread {
    protected Socket client;
    public MulticastSocket ms;
    public MessageConnectionOut(Socket client, MulticastSocket ms) {
        this.client = client;
        this.ms = ms;
    }
    public void run() {
        this.setName(client.getInetAddress().getHostAddress() + ":OUT");
        try {
            System.out.println("OUT:User connected.");
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            while (true) {
                byte[] outgoing = new byte[4096];
                DatagramPacket dp = new DatagramPacket(outgoing, outgoing.length);
                ms.receive(dp);
                dos.writeChars(new String(outgoing) + "\n");
                System.out.println("SENT_TO:" + this.getName());
            }
        } catch(Exception e) {
            System.out.println("OUT:Error connecting " + this.getName() + ": " + e.getLocalizedMessage());
            return;
        }
    }
}

class MessageConnectionIn extends Thread {
    protected Socket client;
    public MulticastSocket ms;
    public MessageConnectionIn(Socket client, MulticastSocket ms) {
        this.client = client;
        this.ms = ms;
    }
    public void run() {
        this.setName(client.getInetAddress().getHostAddress() + ":IN");
        try {
            System.out.println("IN:User connected.");
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (true) {
                String lineIn = br.readLine();
                byte[] input = lineIn.getBytes();
                System.out.println(lineIn);
                byte[] output = new byte[4096];
                for (int c = 0; c < output.length; c++) output[c] = 0x0;
                for (int i = 0; i < input.length && i < output.length; i++) output[i] = input[i];
                DatagramPacket dp = new DatagramPacket(output, output.length, InetAddress.getLocalHost(), 4);
                ms.send(dp);
            }
        } catch(Exception e) {
            System.out.println("IN:Error connecting to " + this.getName() + ": " + e.getLocalizedMessage());
            return;
        }
    }
}