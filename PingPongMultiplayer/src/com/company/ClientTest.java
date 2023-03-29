package com.company;


import javax.swing.*;
import java.io.IOException;

public class ClientTest
{
    public static void main( String[] args ) throws IOException {
        Client application; // declare client application

        // if no command line args
        if ( args.length == 0 )
            application = new Client( "127.0.0.1" ); // connect to localhost
        else
            application = new Client( args[ 0 ] ); // use args to connect

        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        application.runClient(); // run client application
    } // end main
} // end class ClientTest
