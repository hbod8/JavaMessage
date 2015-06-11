import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MessageClient {
    public static void main(String[] args) {
        System.out.println("Starting Message System...");
        Scanner in = new Scanner(System.in);
        MessageClient mc = new MessageClient();
        String input;
        System.out.println(":System Started, type help for help.");
        System.out.print(":");
        while (true) {
            input = in.nextLine();
            if (input.equalsIgnoreCase("HELP")) {
                mc.printHelp();
                System.out.print(":");
            } else if (input.equalsIgnoreCase("QUIT")) {
                System.exit(0);
            } else if (input.equalsIgnoreCase("CONNECT")) {
                mc.connect(in);
                in.nextLine();
                System.out.print(":");
            } else {
                System.out.print("No command found.\n:");
            }
        }
    }
    public static void printHelp() {
        System.out.println("help\tShow this prompt\nconnect\tStarts a new connection\nquit\tQuit the program\nexit\tExit a connection");
    }
    public void connect(Scanner in) {
        Socket soc = null;
        InetAddress addr = null;
        System.out.print("IP_ADDRESS/HOST:");
        String ip = in.nextLine();
        System.out.print("PORT:");
        int port = in.nextInt();
        try {
            System.out.println("Attempting to connect to HOST:\'" + ip + "\' on PORT:\'" + port + "\'");
            addr = InetAddress.getByName(ip);
            soc = new Socket(addr, port);
        } catch(Exception e) {
            System.out.println("Error connecting to server: " + e.getLocalizedMessage());
            return;
        }
        SwingUtilities.invokeLater(new MessageGUI(ip + ":" + port, soc));
    }
}

class MessageGUI implements Runnable {
    public MessageGUI(String windowName, Socket server) {
        JFrame window = new JFrame(windowName);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setSize(500, 300);
        window.setLayout(new BorderLayout());
        window.setVisible(true);
        
        MessageReceive mr = new MessageReceive(server);
        mr.setEditable(false);
        mr.setBackground(new Color(0, 0, 0));
        mr.setForeground(new Color(0, 255, 0));
        mr.setVisible(true);
        new Thread(mr).start();
        window.add(mr, BorderLayout.CENTER);
        
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(server.getOutputStream());
        } catch(Exception e) {
            System.out.println("Error creating output stream to server: " + e.getLocalizedMessage());
        }
        
        JTextField input = new JTextField();
        input.addActionListener(new MessageSend(server, input, dos));
        input.setBackground(new Color(0, 0, 0));
        input.setForeground(new Color(0, 255, 0));
        window.add(input, BorderLayout.PAGE_END);
        
        System.out.println("Displaying connection.");
    }
    public void run() {}
}

class MessageReceive extends JTextArea implements Runnable {
    protected Socket server;
    public MessageReceive(Socket server) {
        this.server = server;
    }
    public void run() {
        DataInputStream dis = null;
        int bytes;
        try {
            dis = new DataInputStream(server.getInputStream());
        } catch(Exception e) {
            System.out.println("Error connecting server: " + e.getLocalizedMessage());
        }
        this.append("Connected.\n");
        while (true) {
            try {
                while ((bytes = dis.read()) != -1) this.append(String.valueOf((char) bytes));
            } catch(Exception e) {
                System.out.println("Error reading from server: " + e.getLocalizedMessage());
                return;
            }
        }
    }
}

class MessageSend implements ActionListener {
    protected Socket server;
    protected JTextField input;
    protected DataOutputStream dos = null;
    public MessageSend(Socket server, JTextField input, DataOutputStream dos) {
        this.server = server;
        this.input = input;
        this.dos = dos;
    }
    public void actionPerformed(ActionEvent ae) {
        try {
            dos.writeBytes(input.getText() + "\n");
            input.setText("");
        } catch(Exception e) {
            System.out.println("Error writing to server output stream: " + e.getLocalizedMessage());
        }
    }
}