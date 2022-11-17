package it.polimi.ingsw;

import it.polimi.ingsw.View.ClientCLI;
import it.polimi.ingsw.View.Colors;


import java.io.IOException;
import java.util.Scanner;

/**
 * Client Application
 */
public class App {

    /**
     * Main method launched when the client app is run
     */
    public static void main(String[] args) {
     boolean valid;
     String clientInput;
     Scanner in = new Scanner(System.in);

     System.out.println(

                     "     ░██╗░░░░░░░██╗███████╗██╗░░░░░░█████╗░░█████╗░███╗░░░███╗███████    \n  " +
                     "     ░██║░░██╗░░██║██╔════╝██║░░░░░██╔══██╗██╔══██╗████╗░████║██╔════╝     \n  " +
                     "     ░╚██╗████╗██╔╝█████╗░░██║░░░░░██║░░╚═╝██║░░██║██╔████╔██║█████╗░░     \n  " +
                     "     ░░████╔═████║░██╔══╝░░██║░░░░░██║░░██╗██║░░██║██║╚██╔╝██║██╔══╝░░     \n  " +
                     "     ░░╚██╔╝░╚██╔╝░███████╗███████╗╚█████╔╝╚█████╔╝██║░╚═╝░██║███████╗     \n  " +
                     "                                                                           \n  " +
                     "                              ████████╗░█████╗░                            \n  " +
                     "                              ╚══██╔══╝██╔══██╗                            \n  " +
                     "                              ░░░██║░░░██║░░██║                            \n  " +
                     "                              ░░░██║░░░██║░░██║                            \n  " +
                     "                              ░░░██║░░░╚█████╔╝                            \n  " +
                     "                                                                           \n  " +
                     "      ███████╗██████╗░██╗░█████╗░███╗░░██╗████████╗██╗░░░██╗░██████╗       \n  " +
                     "      ██╔════╝██╔══██╗██║██╔══██╗████╗░██║╚══██╔══╝╚██╗░██╔╝██╔════╝       \n  " +
                     "      █████╗░░██████╔╝██║███████║██╔██╗██║░░░██║░░░░╚████╔╝░╚█████╗░       \n  " +
                     "      ██╔══╝░░██╔══██╗██║██╔══██║██║╚████║░░░██║░░░░░╚██╔╝░░░╚═══██╗       \n  " +
                     "      ███████╗██║░░██║██║██║░░██║██║░╚███║░░░██║░░░░░░██║░░░██████╔╝       \n  "
     );

     valid = false;
     System.out.println("IP: ");
     String ip = in.nextLine();

     System.out.println(Colors.CYAN + "PLEASE SELECT AN OPTION:" + Colors.RESET);
     System.out.println(Colors.CYAN + "0" + Colors.RESET + ") for CLI \n"+Colors.CYAN  + Colors.CYAN + "1" +Colors.RESET+") to exit the game\n" + Colors.CYAN + "HELP" + Colors.RESET);

     while(!valid) {
         clientInput = in.nextLine().replaceAll(" ", "");
         switch (clientInput) {
             case "0":
                 System.out.println("You choose the CLI interface! ");
                 System.out.println("    ");
                 System.out.println("Trying to connect to " + ip + "...");
                 System.out.println("    ");
                 valid = true;
                 try {
                      ClientCLI clientCLI = new ClientCLI(ip,1236);
                      clientCLI.run();
                 } catch ( IOException e) {
                    System.out.println("Could not connect with the server IP");
                    System.exit(1);
                 }
                 break;

             case "1":
                 System.out.println("You choose to exit the game!\nShutting down... ");

                 System.exit(0);
                 break;

             case "HELP":
                 System.out.println(Colors.CYAN + "0" + Colors.RESET + ") for CLI \n"+Colors.CYAN  + Colors.CYAN + "1" +Colors.RESET+") to exit the game\n" + Colors.CYAN + "HELP" + Colors.RESET);
                 break;
             default:
                 System.out.println("You have typed an invalid input!\nPlease try again!");
                 break;
         }
     }
 }
}

