//Server for JavaMessage project.
//
//Issues:
//
//MessageThread needs to share a InputStream between threads for messaging.

import java.net.*;
import java.io.*;
import java.util.*;

public class MessageServer {

    static final int PORT = 1234;

    public static void main(String args[]) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (Exception e) {
            System.err.println(e);
        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (Exception e) {
                System.err.println(e);
            }
            // new thread for a client
            new MessageThread(socket).start();
        }
    }
}

class MessageThread extends Thread {
    protected Socket socket;
    public MessageThread(Socket clientSocket) {
        this.socket = clientSocket;
    }
    public void run() {
        InputStream inp = null;
        BufferedReader brinp = null;
        DataOutputStream out = null;
        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
        } catch (Exception e) {
            System.err.println("USER left");
            return;
        }
        String line;
        while (true) {
            try {
                line = brinp.readLine();
                System.out.print(line + "\n");
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {
                    out.writeBytes(line + "\n\r");
                    out.flush();
                }
            } catch (IOException e) {
                return;
            }
        }
    }
}