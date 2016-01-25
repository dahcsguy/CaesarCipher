import java.net.*;
import java.io.*;	
import java.util.*;
/**
*  @author     Wayne Zhang
*  Class:      4002-219
*  Assignment: Caesar Cipher
*  Purpose:    To encode and decode text using a client/server
*/
public class CaesarServer
{
   //socket
   private static Socket s;
   //shift constant is 3
   private static int charShift = CaesarConstants.DEFAULT_SHIFT;
   public static void main(String [] args)
   {
      try {
         try
         {
            //if the user enter something to the command line
            if(args[0]!=null)
            {
               //if the user enter a number less than 1 or greater than 25
               if(Integer.parseInt(args[0])<1||Integer.parseInt(args[0])>25)
               {
               }
               //user enter a number between 1 and 25
               else
               {
                  //clearShift equals to the number the user entered
                  charShift = Integer.parseInt(args[0]);
               }
            }
         }
         //catches exception like user entering letters, etc
         catch(Exception nfe)
         {  
         }
         //ss = 16789
         ServerSocket ss = new ServerSocket(CaesarConstants.PORT_NUMBER);
         
         //stay open
         while(true)
         {
            //connection with client
            s = ss.accept();
            
            //create thread
            Thread t = new Thread(new Client(s));
            //start thread
            t.start();
         }
      }
      //error in connecting
      catch(IOException ioe){
         System.out.println("Error in server startup");
         ioe.printStackTrace();
      }
   }
   /**
   *  Encodes the text you input
   *  @param word    the message you want to encode
   *  @return        the encoded message
   */   
   public static String encode(String word) 
   {
      //if the word is null
      if(word == null)
      {
         //return a empty string
         return "";
      }
      //put word to a char array - all lower case
      char[] ori = word.toLowerCase().toCharArray();
      for(int i = 0; i < ori.length; i++) 
      {
         //if the letters are not between 'a' and 'z', ignore them
         if(ori[i] < 'a' || ori[i] > 'z')
         {
            continue;
         }
         //add the number of shift
         ori[i] += charShift;
         //wrap around if over 'z'
         if(ori[i] > 'z') 
         {
            ori[i] -= 'z';
            ori[i] += ('a' - 1);			
         }
      }
      //return the encoded message
      return new String(ori);
   }
   /**
   *  Decodes the text you input
   *  @param word    the message you want to decode
   *  @return        the decoded message
   */   
   public static String decode(String word)
   {
      //if the word is null
      if(word == null)
      {
         return "";
      }
      //put word to a char array - all lower case
      char[] ori = word.toLowerCase().toCharArray();
      for(int i = 0; i < ori.length; i++) {
         //if the letters are not between 'a' and 'z', ignore them
         if(ori[i] < 'a' || ori[i] > 'z') 
         {
            continue;
         }
         //subtract the number of shifts
         ori[i] -= charShift;
         //wrap around if letter less than a
         if(ori[i] < 'a') 
         {
            ori[i] += 'z';
            ori[i] -= ('a' - 1);			
         }
      }
      //return decoded message
      return new String(ori);
   }
}
/**
*  @author    Wayne Zhang
*  Purpose:   To read the line and return the encode/decoded message
*/   
class Client implements Runnable
{
   //socket
   private Socket sock;
   //BufferedReader
   private BufferedReader br = null;
   //PrintWriter
   private PrintWriter pw = null;
   //parameterized constructor
   public Client(Socket _sock)
   {
      sock = _sock;
   }  
   /*
   *  Run the Client Class
   */
   public synchronized void run()
   {
      try
      {
         //
         br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
         pw = new PrintWriter(sock.getOutputStream());
         //give the client "OK" message
         pw.println("OK");
         //client gives ENCRYPT, DECRYPT or ERROR
         String a = br.readLine();            
         while(true)
         {
            //if client sends ENCRYPT
            if(a.equals("ENCRYPT"))
            {
               //flush out the stream
               pw.flush();
               //read the first line of the message
               String text = br.readLine();
               //send the encoded text
               pw.println(CaesarServer.encode(text));
               //flush out the stream
               pw.flush();
            }
            //if client sends DECRYPT
            else if(a.equals("DECRYPT"))
            {
               //flush out the stream
               pw.flush();
               //read the first line of the message
               String text = br.readLine();
               //send the decoded text
               pw.println(CaesarServer.decode(text));
               //flush out the stream
               pw.flush();
            }
            else
            {
               //flush out the stream
               pw.flush();
               //give the user error message
               pw.println("ERROR - YOU PRESS THE WRONG BUTTON");
               //flush out the stream
               pw.flush();
               //get out of the loop
               break;
            }
         }
      }
      catch(Exception e)
      {
         
      }
   }
}