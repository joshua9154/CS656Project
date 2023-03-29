package com.company;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Properties;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;


public class Client extends JFrame {
    private JTextField enterField; // inputs message from user
    private JPanel panel; // display information to user
    private ObjectOutputStream output; // output stream to client
    private ObjectInputStream input; // input stream from client
    private ServerSocket server; // server socket
    private Socket connection; // connection to client
    private int counter = 1; // counter of number of connections
    private Color currentColor = Color.BLACK;
    private String chatServer; // host server for this application
    private Socket client; // socket to communicate with server
    Timer timer;
    private Point ball = new Point(700, 500), delta = new Point(0, 0), paddle = new Point(1150, 500), compPaddle = new Point(650, 500);
    private int ballSize = 30, scoreOne = 0, scoreTwo = 0, paddleTrajectory = 0, speed = 50;

    public Client(String Host) {

        super("Client");

        panel = new JPanel(); // create panel
        JLabel player1Score = new JLabel("Player1: ");
        JLabel player2Score = new JLabel("Player2:");
        JLabel highScore = new JLabel("High Score:");
        JLabel playerOneScore = new JLabel(getScoreOne());
        JLabel playerTwoScore = new JLabel(getScoreTwo());
        JLabel highScorer = new JLabel(getScoreHigh());

        JButton newGame = new JButton("New game");
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ball.x = 900;
                ball.y = 570;
                delta.x = 3;
                delta.y = 3;
                scoreOne = 0;
                scoreTwo = 0;
                playerOneScore.setText(getScoreOne());
                playerTwoScore.setText(getScoreTwo());
                highScorer.setText(getScoreHigh());
                timer.start();
                double i = 4.4;
                sendData(i);
            }
        });
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(2, 5));
        scorePanel.add(player1Score);
        scorePanel.add(player2Score);
        scorePanel.add(highScore);
        scorePanel.add(playerOneScore);
        scorePanel.add(playerTwoScore);
        scorePanel.add(highScorer);

        add(scorePanel, BorderLayout.NORTH);
        panel.setBackground(Color.BLACK);
        panel.add(newGame);
        add(panel, BorderLayout.CENTER);
        //  add(newGame, BorderLayout.SOUTH);
        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent m) {
                int val = m.getWheelRotation();
                if (paddleTrajectory + val < 5 && paddleTrajectory + val > -5)
                    paddleTrajectory = paddleTrajectory + val;
            }

        });

        add(panel, BorderLayout.CENTER);
        setTitle("Player 2");
        setSize(800, 600); // set size of window
        setVisible(true);


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {


                Properties appProps = new Properties();
                try {
                    InputStream is = new FileInputStream("appsProperties.bin");
                    appProps.load(is);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Point p = new Point();
                p.x = Integer.parseInt(appProps.getProperty(WindowLocationX));
                p.y = Integer.parseInt(appProps.getProperty(WindowLocationY));
                Client.this.setLocation(p);
                System.out.println(p);
            }

            final String WindowLocationX = "WindowLocationX";
            final String WindowLocationY = "WindowLocationY";

            @Override
            public void windowClosing(WindowEvent e) {


                Point p = Client.this.getLocation();
                Properties appProps = new Properties();
                appProps.setProperty(WindowLocationX, p.x + "");
                appProps.setProperty(WindowLocationY, p.y + "");
                try {
                    OutputStream os = new FileOutputStream("appsProperties.bin");
                    appProps.store(os, "cool comment");
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.out.println(p);
                System.exit(0);
            }

        });

        timer = new Timer(speed, new ActionListener() {
            int counter = 0;

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    if (updateBall() == 1) {

                        playerOneScore.setText(getScoreOne());
                    }

                    if (updateBall() == 2) {
                        sendData(1.1);
                        highScorer.setText(getScoreHigh());
                        sendData(2.2);
                        playerTwoScore.setText(getScoreTwo());
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                sendData(compPaddle.y);
                repaint();

            }
        });
    } // end Server constructor

    private int updateBall() throws IOException {
        int scored = 0;
        if (ball.y > 770) {
            sound();
            delta.y = -delta.y;
        }
        if (ball.y < 400) {
            sound();
            delta.y = -delta.y;
        }
        if (ball.x > 1120 && ball.y > (paddle.y - 10) && ball.y < (paddle.y + 70)) {
            if (ball.x < 1125) {
                scored = 1;
                scoreOne++;
                sound();
                delta.x = -delta.x;
            }

        }
        if (ball.x < 660 && ball.y > (compPaddle.y - 10) && ball.y < (compPaddle.y + 70)) {
            if (ball.x > 655) {
                scored = 2;
                scoreTwo++;
                sound();
                delta.x = -delta.x;
            }
        }

        if (ball.x > 1167) {
            ball.x = 900;
            ball.y = 570;
            sendData(3.3);
            timer.stop();
            delta.x = 0;
            delta.y = 0;


        }
        if (ball.x < 600) {
            ball.x = 900;
            ball.y = 570;
            sendData(3.3);
            timer.stop();

            delta.x = 0;
            delta.y = 0;


        }

        ball.x += delta.x;
        ball.y += delta.y;
        if ((compPaddle.getY() + paddleTrajectory) < 742 && (compPaddle.getY() + paddleTrajectory) > 399)
            compPaddle.y = compPaddle.y + paddleTrajectory;
        return scored;
    }


    public void sound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("Pew_Pew-DKnight556-1379997159.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public String getScoreOne() {
        String ones = "" + scoreOne;
        return ones;
    }

    public String getScoreTwo() {
        String twos = "" + scoreTwo;
        return twos;

    }


    public String getScoreHigh() {

        Client.highScore object1 = new highScore();
        String filename = "clientScores.ser";

        try {

            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);


            object1 = (Client.highScore) in.readObject();

            in.close();
            file.close();

        } catch (IOException ex) {
            System.out.println("IOException is caught");
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException is caught");
        }

        if (object1.getLowestHighScore() < scoreOne) {
            int i = 0;
            String name = null;
            while (i == 0) {
                i = 1;
                name = JOptionPane.showInputDialog("Enter your initials.");
                if (name == null || name.length() > 3) {
                    JOptionPane.showMessageDialog(null, "Please enter your initials with using no more than three letters.");
                    i = 0;
                }
            }

            object1.setLinks(scoreOne, name);

        }

        try {

            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(object1);

            out.close();
            file.close();

        } catch (IOException ex) {
            System.out.println("IOException is caught");
        }

        StringBuilder word = new StringBuilder();
        word.append("The High Scores are : ");
        for (int i = 0; i < object1.getLength(); i++) {
            {
                word.append(object1.getName(i) + " " + object1.getScore()[i] + " |");
            }
        }
        return word.toString();
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(new Color(0, 255, 0));
        g.drawRect(paddle.x, paddle.y, 10, 60);
        g.drawRect(compPaddle.x, compPaddle.y, 10, 60);
        g.drawRect(600, 400, 600, 400);
        g.drawRect(550, 350, 700, 500);
        g.fillOval(ball.x, ball.y, ballSize, ballSize);
    }


    public static class highScore implements Serializable {
        private int numberOfWinners = 10;
        private java.util.List<Integer> linkedNum = new LinkedList<>();
        private java.util.List<String> linkedName = new LinkedList<>();

        highScore() {
            linkedNum.add(0);
            linkedName.add("");
        }

        public String getName(int i) {
            return linkedName.get(i);
        }

        public int getLength() {
            return linkedNum.size();
        }

        public Object[] getScore() {

            return linkedNum.toArray();
        }

        public int getLowestHighScore() {

            int low = 0;
            if (linkedNum.size() < numberOfWinners)
                return 0;
            for (int i = 0; i < linkedNum.size(); i++) {
                if (linkedNum.get(i) < linkedNum.get(low))
                    low = i;
            }
            return linkedNum.get(low);
        }

        public void sort() {
            for (int j = 0; j < linkedNum.size() - 1; j++) {
                for (int i = 0; i < linkedNum.size() - 1; i++) {
                    if (linkedNum.get(i) < linkedNum.get(i + 1)) {
                        int temp = linkedNum.get(i + 1);
                        String tempName = linkedName.get(i + 1);
                        linkedNum.set(i + 1, linkedNum.get(i));
                        linkedName.set(i + 1, linkedName.get(i));
                        linkedNum.set(i, temp);
                        linkedName.set(i, tempName);
                    }
                }
            }
        }


        public void setLinks(int newScore, String newName) {
            int low = 0;
            if (linkedNum.get(0) == 0) {
                linkedNum.set(0, newScore);
                linkedName.set(0, newName);
            } else if (linkedNum.size() < numberOfWinners) {

                linkedNum.add(newScore);
                linkedName.add(newName);
                sort();
            } else {
                for (int i = 0; i < numberOfWinners; i++) {
                    if (linkedNum.get(i) < linkedNum.get(low))
                        low = i;
                }
                linkedNum.set(low, newScore);
                linkedName.set(low, newName);
                sort();
            }

        }

    }

    public void runClient() {
        try // connect to server, get streams, process connection
        {
            connectToServer(); // create a Socket to make connection
            getStreams(); // get the input and output streams
            processConnection(); // process connection
        } // end try
        catch (EOFException eofException) {
            displayMessage("\nClient terminated connection");
        } // end catch
        catch (IOException ioException) {
            ioException.printStackTrace();
        } // end catch
        finally {
            closeConnection(); // close connection
        } // end finally
    } // end method runClient

    // connect to server
    private void connectToServer() throws IOException {
        displayMessage("Attempting connection\n");

        // create Socket to make connection to server
        client = new Socket(InetAddress.getByName(chatServer), 12345);

        // display connection information
        displayMessage("Connected to: " +
                client.getInetAddress().getHostName());
    } // end method connectToServer

    // get streams to send and receive data
    private void getStreams() throws IOException {
        // set up output stream for objects
        output = new ObjectOutputStream(client.getOutputStream());
        output.flush(); // flush output buffer to send header information

        // set up input stream for objects
        input = new ObjectInputStream(client.getInputStream());

        displayMessage("\nGot I/O streams\n");
    } // end method getStreams


    // process connection with client
    private void processConnection() throws IOException {
        // enable enterField so server user can send messages
        setTextFieldEditable(true);

        do // process messages sent from client
        {
            try // read message and display it
            {
                Object o = input.readObject();
                if (o instanceof Integer) {
                    int p = (Integer) o;
                    paddle.y = p;
                } else if (o instanceof Double) {
                    double p = (double) o;

                    if (p == 1.1) {
                        timer.stop();

                    }
                    if (p == 2.2) {
                        timer.start();
                    }
                    if (p == 3.3) {
                        ball.x = 900;
                        ball.y = 570;
                        delta.x = -delta.x;
                        delta.y = -delta.y;
                        timer.stop();
                    }
                    if (p == 4.4) {
                        ball.x = 900;
                        ball.y = 570;
                        delta.x = 3;
                        delta.y = 3;
                        scoreOne = 0;
                        scoreTwo = 0;
                        timer.start();
                    }
                }

            } // end try
            catch (ClassNotFoundException classNotFoundException) {
                displayMessage("\nUnknown object type received");
            } // end catch

        } while (true);
    } // end method processConnection

    // close streams and socket
    private void closeConnection() {
        displayMessage("\nTerminating connection\n");
        setTextFieldEditable(false); // disable enterField

        try {
            output.close(); // close output stream
            input.close(); // close input stream
            connection.close(); // close socket
        } // end try
        catch (IOException ioException) {
            ioException.printStackTrace();
        } // end catch
    } // end method closeConnection

    // send message to client
    private void sendData(Object o) {
        try // send object to client
        {
            output.writeObject(o);
            output.flush(); // flush output to client
        } // end try
        catch (IOException ioException) {
        } // end catch
    } // end method sendData

    // manipulates panel in the event-dispatch thread
    private void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() // updates panel
                    {

                    } // end method run
                } // end anonymous inner class
        ); // end call to SwingUtilities.invokeLater
    } // end method displayMessage

    // manipulates enterField in the event-dispatch thread
    private void setTextFieldEditable(final boolean editable) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() // sets enterField's editability
                    {
//                        enterField.setEditable(editable);
                    } // end method run
                }  // end inner class
        ); // end call to SwingUtilities.invokeLater
    } // end method setTextFieldEditable

} // end class Server
