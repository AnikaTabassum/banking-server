package classes;

import java.net.*;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.*;

public class Server implements Runnable{ 
    ServerSocket serverSocket;
    PrintStream streamToClient;
    BufferedReader streamFromClient;
    Socket fromClient;
    static int count = 0;
    Thread thread;
    public Server()
    {
    	System.out.println("Welcome to Server");
        try{
            serverSocket = new ServerSocket(3350);
        }
        catch(Exception e)
        {
            System.out.println("Socket could not be created"+e);
        }
        thread = new Thread(this);
        thread.start();
    }
    public void run()
    {
        try{
            while(true)
            {
                fromClient = serverSocket.accept();
                count++;    
                System.out.println("Client connection number "+count);
                streamFromClient = new BufferedReader(new InputStreamReader((fromClient.getInputStream())));
                streamToClient = new PrintStream(fromClient.getOutputStream());
                String str = streamFromClient.readLine();
                System.out.println("Client connection name "+str);
                streamToClient.println("Welcome "+str);
                String str2=streamFromClient.readLine();
                String str3[]=str2.split(" ");
                String username=str3[0];
                String passw=str3[1];
                /*while(true)
                {
                	String str2 = streamFromClient.readLine();
                	System.out.println("Client says: "+str2);
                	if(str2.equals("exit"))
                	{
                		streamToClient.println("Closing server");
                		break;
                	}
                	Scanner sc = new Scanner(System.in);
                	String str3 = sc.nextLine();
                	streamToClient.println(str3);
                }*/
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(false);
				factory.setValidating(false);
				try {
					DocumentBuilder builder = factory.newDocumentBuilder();
					File file = new File("signup.xml"); // XML file to read
					Document document = builder.parse(file);
					Element catalog = document.getDocumentElement();
					
					NodeList books = catalog.getChildNodes();
					boolean flag = false;
					for (int i = 0, ii = 0, n = books.getLength() ; i < n ; i++) {
					  Node child = books.item(i);
					  if ( child.getNodeType() != Node.ELEMENT_NODE )
					    continue;
					  Element book = (Element)child;
					  ii++;
					  
					  String gotusername = getCharacterData(findFirstNamedElement(child,"Username"));
					  String gotpassw = getCharacterData(findFirstNamedElement(child,"Password"));
					  if((gotusername.equals(username)) && (gotpassw.equals(passw)))
					  {
						  
						  streamToClient.println("Login successful!!");
						  //JOptionPane.showMessageDialog(null, "Login successful!!");
						  flag = true;
						  break;
					  }
					}
					if(flag == false)
					{
						 streamToClient.println("Failed to login...");
						//JOptionPane.showMessageDialog(null, "Failed to login...");
					}
					
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
				str2=streamFromClient.readLine();
				System.out.println(str2);
				String str4[]=str2.split(" ");
				System.out.println(str4[0]+" "+str4[1]);
				/*DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
				factory1.setNamespaceAware(false);
				factory1.setValidating(false);
				DocumentBuilder builder = null;
				try {
					builder = factory1.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				File file = new File("signup.xml"); // XML file to read
				Document document;
				try {
					document = builder.parse(file);
					
					Element catalog = document.getDocumentElement();
					
					Element book = document.createElement("user");
					
					String[] elnames = {"Username","Email", "Password","Balance"};
					
					String[] elvalues = {username,email,passw};
					for (int i =0; i < elnames.length; i++) {
					  Element el = createElement(document, elnames[i], elvalues[i]);
					  book.appendChild(el);
					}
					catalog.appendChild(book);
					writeXMLToFile(document);
					JOptionPane.showMessageDialog(null, "Signup successful");
					frame.setVisible(false);
					Login hello = new Login();
					hello.frame.setVisible(true);
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

            }
        }
        catch(Exception e){
            System.out.println("Exception "+e);         
        }
        finally{
            try{
                fromClient.close();
            }
            catch(Exception e)
            {
                System.out.println("could not close connection "+e);
            }
        }
    }
	public Node findFirstNamedElement(Node parent,String tagName)
	{
	  NodeList children = parent.getChildNodes();
	  for (int i = 0, in = children.getLength() ; i < in ; i++) {
	    Node child = children.item(i);
	    if ( child.getNodeType() != Node.ELEMENT_NODE )
	      continue;
	    if ( child.getNodeName().equals(tagName) )
	      return child;
	  }
	  return null;
	}

	public String getCharacterData(Node parent)
	{
	  StringBuilder text = new StringBuilder();
	  if ( parent == null )
	    return text.toString();
	  NodeList children = parent.getChildNodes();
	  for (int k = 0, kn = children.getLength() ; k < kn ; k++) {
	    Node child = children.item(k);
	    if ( child.getNodeType() != Node.TEXT_NODE )
	      break;
	    text.append(child.getNodeValue());
	  }
	  return text.toString();
	}
	public Element createElement(Document document, String elName,String elValue)
	{
		
		Element el = document.createElement(elName);
		el.setTextContent(elValue);
		//javax.xml.soap.Text text = (javax.xml.soap.Text) document.createTextNode(elValue);
		//el.appendChild(text);
		return el;
	}

	public void writeXMLToFile(Document document)
	{
		TransformerFactory tfact = TransformerFactory.newInstance();
		Transformer tform;
		try {
			tform = tfact.newTransformer();
			tform.setOutputProperty(OutputKeys.INDENT, "yes");
			tform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			tform.transform(new DOMSource(document), new StreamResult(new File("signup.xml")));
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void main(String args[])  
	{
	    Server s = new Server();
	    s.run();
	}
}
