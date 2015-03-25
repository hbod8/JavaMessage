import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class MessageClient {
    public static final int PORT = 1234;
    public static void main(String[] args) {
        Socket socket = null;
        System.out.println("-- Message System Started --");
        System.out.println("INFO:Opening port " + PORT);
        try {
            InetAddress ip = InetAddress.getByName("24.17.105.4");
            socket = new Socket(ip, PORT);
        } catch(ConnectException e) {
            System.err.println("Server is down! " + e);
            System.exit(-1);
        } catch(Exception e) {
            System.err.println(e);
        }
        SwingUtilities.invokeLater(new MessageGUI(socket));
    }
}

class MessageGet extends Thread {
    DataInputStream receive = null;
    JTextArea content = null;
    public MessageGet(Socket socket, JTextArea txta) {
        try {
            receive = new DataInputStream(socket.getInputStream());
        } catch(Exception e) {
            System.err.println(e);
        }
        content = txta;
    }
    public void run() {
        int a;
        try {
            while ((a = receive.read()) != -1) content.append(new Character((char)a).toString());
        } catch(Exception e) {
            System.err.println(e);
        }
    }
}

class MessageSend {
    DataOutputStream send = null;
    public MessageSend(Socket socket) {
        try {
            send = new DataOutputStream(socket.getOutputStream());
        } catch(Exception e) {
            System.err.println(e);
        }
    }
    public void send(String data) {
        try {
            send.writeBytes(data + "\n");
        } catch(Exception e) {
            System.err.println(e);
        }
    }
}

class MessageGUI implements Runnable {
    Socket socket = null;
    public MessageGUI(Socket soc) {
        socket = soc;
    }
    public void run() {
//        Setup the window.
        JFrame window = new JFrame("Message");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(500, 300);
        window.setLayout(new BorderLayout());
        window.setVisible(true);
//        Setup the JTextArea for message output.
        JTextArea content = new JTextArea();
        content.setEditable(false);
        new MessageGet(socket, content).start();
        window.add(content, BorderLayout.CENTER);
//        Setup the text input.
        JTextField input = new JTextField();
        window.add(input, BorderLayout.PAGE_END);
        input.addActionListener(new MessageField(input, socket));
    }
}

class MessageField implements ActionListener {
    Socket socket = null;
    JTextField input = null;
    public MessageField(JTextField in, Socket soc) {
        input = in;
        socket = soc;
    }
    public void actionPerformed(ActionEvent ae) {
        new MessageSend(socket).send(System.getProperty("user.name") + ":" + input.getText());
        input.setText("");
    }
}