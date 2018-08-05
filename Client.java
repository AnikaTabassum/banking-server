package classes;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    PrintStream streamToServer;
    BufferedReader streamFromServer;
    Socket toServer;
    public Client()
    {
    	System.out.println("Welcome to Client");
        connectToServer();
    }
    private void connectToServer()
    {
        try{
            String name;
            toServer = new Socket("10.100.102.42",3333);
            streamFromServer = new BufferedReader(new InputStreamReader((toServer.getInputStream())));
            streamToServer = new PrintStream(toServer.getOutputStream());
            System.out.println("Enter Connection Name");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            name = reader.readLine();   
            streamToServer.println(name);
            String str = streamFromServer.readLine();
            System.out.println("The Server Says "+str);
           /* while(true)
            {
	            Scanner sc = new Scanner(System.in);
	            String str3 = sc.nextLine();
	            streamToServer.println(str3);
	            String str2 = streamFromServer.readLine();
	            if(str2.equals("Closing server"))
	            	break;
            	System.out.println("The Server Says: " + str2);
            }*/
            Login l = new Login();
            l.init();
        }
        catch(Exception e)
        {
                System.out.println("Exception "+e);
        }       
    }
    public static void main(String args[])
    {
        new Client();
    }}