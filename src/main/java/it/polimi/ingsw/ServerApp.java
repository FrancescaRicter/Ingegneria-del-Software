package it.polimi.ingsw;

import it.polimi.ingsw.Connection.Server.Server;
import it.polimi.ingsw.Model.Island;

import java.io.IOException;

/**
 * Server Application
 */
public class ServerApp {
    /**
     * Main method launched when the server app is run
     */
    public static void main( String[] args ) throws IOException {
           try {
               Server server = new Server();
               server.run();
           }
           catch (IOException ex) {
               System.out.println("Server exception " + ex.getMessage() + "!");
               ex.printStackTrace();
           }
    }
}
