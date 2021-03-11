package client;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ChatClient {

    public final static String TARGET_SERVER = "127.0.0.1";
    public final static int TARGET_PORT = 2000;
    private final Frame f;
    private final TextField chatTextField;
    private final TextArea chatTextArea;
    private final Button buttonSend;
    private final Button buttonQuit;
    private final Choice choice;
    private final MenuBar mb;
    private final Menu m1;
    private final Menu m2;
    private final MenuItem mi1;
    private final MenuItem mi2;
    private final Dialog d;
    private final Label d1;
    private final Label d2;
    private final Label d3;
    private final Label d4;
    private final Button db;
    private BufferedReader serverIn;
    private PrintStream serverOut;

    private ChatClient() {
        f = new Frame("Chat Room");
        chatTextField = new TextField(50);
        chatTextArea = new TextArea(10, 50);
        buttonSend = new Button("Send");
        buttonQuit = new Button("Quit");

        choice = new Choice();
        choice.addItem("Arkadiusz");
        choice.addItem("Bryan");
        choice.addItem("Dave");
        choice.addItem("Emily");

        mb = new MenuBar();
        m1 = new Menu("File");
        m2 = new Menu("Help");
        mi1 = new MenuItem("Quit");
        mi2 = new MenuItem("About");

        d = new Dialog(f,"About",true);
        d1 = new Label("Author: A. Galas");
        d2 = new Label("Compilation date: 11/03/2021");
        d3 = new Label("Version: 1.00");
        d4 = new Label("Compilation No.: 5");
        db = new Button("OK");
    }

    private void launchFrame() {

        f.add(chatTextField, BorderLayout.SOUTH);
        f.add(chatTextArea, BorderLayout.WEST);

        d.setLayout(new GridLayout(5, 1));
        d.add(d1);
        d.add(d2);
        d.add(d3);
        d.add(d4);
        d.add(db);
        d.pack();

        buttonSend.addActionListener(new SendButtonHandler());
        buttonQuit.addActionListener(new QuitButtonHandler());
        f.addWindowListener(new CloseWidgetHandler());
        chatTextField.addKeyListener(new EnterButtonHandler());
        choice.addItemListener(new ChoiceMenuHandler());
        mi1.addActionListener(new QuitMenuHandler());
        mi2.addActionListener(new AboutMenuHandler());
        d.addWindowListener(new CloseDialogHandler());
        db.addActionListener(new DialogButtonHandler());

        Panel p1 = new Panel();
        p1.add(buttonSend);
        p1.add(buttonQuit);
        p1.add(choice);

        //Add the button panel to the center
        f.add(p1, BorderLayout.CENTER);

        //Add menu items and menu bar to Frame
        m1.add(mi1);
        m2.add(mi2);
        mb.add(m1);
        mb.setHelpMenu(m2);
        f.setMenuBar(mb);

        f.setSize(440, 210);
        f.setVisible(true);
        doConnect();
    }

    private void doConnect() {
        try {
            Socket connection = new Socket(TARGET_SERVER,TARGET_PORT);
            serverIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            serverOut = new PrintStream(connection.getOutputStream());
            Thread t = new Thread(new RemoteReader());
            t.start();
        }
        catch(Exception e) {
            System.out.println("Unable to connect to server!");
            e.printStackTrace();
        }
    }

    class RemoteReader implements Runnable {
        public void run() {
            while (true) {
                try {
                    chatTextArea.append(serverIn.readLine());
                    chatTextArea.append("\n");
                } catch (IOException e) {
                    break;
                }
            }
        }
    }

    class SendButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String s = choice.getSelectedItem()+": "+chatTextField.getText() + "\n";
            serverOut.print(s);
        }
    }

    static class QuitButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    static class CloseWidgetHandler implements WindowListener {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
        public void windowActivated(WindowEvent e) {}
        public void windowDeactivated(WindowEvent e) {}
        public void windowIconified(WindowEvent e) {}
        public void windowDeiconified(WindowEvent e) {}
        public void windowClosed(WindowEvent e) {}
        public void windowOpened(WindowEvent e) {}
    }

    class EnterButtonHandler implements KeyListener {
        public void keyTyped(KeyEvent e){}
        public void keyReleased(KeyEvent e) {}
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                String s = choice.getSelectedItem()+": "+chatTextField.getText() + "\n";
                serverOut.print(s);
            }
        }
    }

    static class ChoiceMenuHandler implements ItemListener {
        public void itemStateChanged(ItemEvent ev) {}
    }

    static class QuitMenuHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    class AboutMenuHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {d.setVisible(true);}
    }

    class CloseDialogHandler implements WindowListener {
        public void windowClosing(WindowEvent e) {d.setVisible(false);}
        public void windowActivated(WindowEvent e) {}
        public void windowDeactivated(WindowEvent e) {}
        public void windowIconified(WindowEvent e) {}
        public void windowDeiconified(WindowEvent e) {}
        public void windowClosed(WindowEvent e) {}
        public void windowOpened(WindowEvent e) {}
    }

    class DialogButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {d.setVisible(false);}
    }

    public static void main(String[] args) {
        ChatClient gui = new ChatClient();
        gui.launchFrame();
    }
}
