package httpserver;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Roman
 */
public class HttpServer implements Runnable{

    static final int PORT = 5557;
    private final ServerSocket socket;
    private final ExecutorService executor;
    
    private boolean active = true;
   
    public HttpServer(ExecutorService executor) throws IOException {
        this.executor = executor;
        this.socket = new ServerSocket(PORT);
    }
    
    @Override
    public void run() {
        System.out.println("Server Started");
        try (ServerSocket s = this.socket) {
            while (isActive() && Thread.currentThread().isAlive()) {
                Socket clientSocket = s.accept();
                executor.submit(new ServeOneJabber(clientSocket));
            }
        } catch (IOException ex) {
            if (this.socket.isClosed())
            {
                System.out.println("Server stopped");
            } else {
                Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) throws IOException {
            this.active = active;
            if (!this.active && this.socket != null)
            {
                this.socket.close();
            }
        }

   
   class ServeOneJabber implements Runnable {
   private boolean active = true;
   private final Socket socket;
   private BufferedReader in;
   private PrintWriter out;
   
   public ServeOneJabber(Socket s) throws IOException {
      this.socket = s;
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
   }
   
   @Override
   public void run() {
      try {
         while (isActive() && Thread.currentThread().isAlive()) {
            getRecive recive;
            String str = in.readLine();
                    
            System.out.println("Echoing: " + str);
            if(str.indexOf("GET /result?",0) != -1){
                recive = geterRecive(str);
                String res = Equation(recive);
                writeResponse(res);
            }else{
                writeResponse();
            }
            
         }
         System.out.println("closing...");
      }
      catch (IOException e) {
      }
      finally {
         try {
            socket.close();
         }
         catch (IOException e) {
            System.err.println("Socket not closed");
         }
      }
   }
   
    public boolean isActive() {
         return active;
     }

    public void setActive(boolean active) {
             this.active = active;
         }
    

    private void writeResponse(){
             String body = indexHTML();

             String response =   "HTTP/1.1 200 OK\r\n" +
                                 "Content-Length: " + body.length() + "\r\n" +            
                                 "Content-Type: text/html\r\n\r\n";
             String result = response + body;
             out.println(result);
             out.close();
         }

    private void writeResponse(String str){
             String body = indexHTML() + str;

             String response =   "HTTP/1.1 200 OK\r\n" +
                                 "Content-Length: " + body.length() + "\r\n" +            
                                 "Content-Type: text/html\r\n\r\n";
             String result = response + body;
             out.println(result);
             out.close();
         }
    

    private getRecive geterRecive(String reciv) {
         getRecive result = new getRecive();
         //const char * ñ = reciv;
         String sX1 = "x1=";
         String sX2 = "x2=";
         String sX3 = "x3=";
         String XX = " HTTP/1.1";
         int PosA;
         int PosB;
         int PosC;
         int endX;


         PosA = getPosX(sX1, reciv) + 3;
         PosB = getPosX(sX2, reciv) + 3;
         PosC = getPosX(sX3, reciv) + 3;
         endX = getPosX(XX, reciv);

         result.a = getX(PosA, PosB-4, reciv);
         result.b = getX(PosB, PosC-4, reciv);
         result.c = getX(PosC, endX, reciv);

         System.out.println("x1 = " + result.a);
         System.out.println("x2 = " + result.b);
         System.out.println("x3 = " + result.c);
         return result;
    }

    private int getPosX(String param, String reciv){
        int pos;
        pos = reciv.indexOf(param);
    //       System.out.println(param + " " + pos);
        return pos;
    }

    private double getX(int PosStart, int PosEnd, String reciv){
        String result = "";
        double res;
        for(;PosStart < PosEnd; PosStart++){
             result += reciv.charAt(PosStart);
         }

         res = Double.parseDouble(result);
    //        System.out.println(res);
        return res;
    }
    

    private String Equation(getRecive rec) {
        double a = rec.a;
        double b = rec.b;
        double c = rec.c;
        double D;

        D = b * b - 4 * a * c;
        if (D > 0) {
            double x1, x2;
            x1 = (-b - Math.sqrt(D)) / (2 * a);
            x2 = (-b + Math.sqrt(D)) / (2 * a);
            System.out.println("x1 = " + x1 + ", x2 = " + x2);
            return ("two roots: x1 = " + x1 + ", x2 = " + x2);
        }
        else if (D == 0) {
            double x;
            x = -b / (2 * a);
            System.out.println("x = " + x);
            return ("one root: x = " + x);
        }
        else {
            System.out.println("Нет корней!");
            return ("Not root!");
        }
    }

    
    private String indexHTML(){
        String result = "";
        try(FileReader reader = new FileReader("D:\\Java\\HttpServer\\Index.html"))
        {       
            int c;
            while((c=reader.read())!=-1){
                result += (char)c;
//                System.out.print((char)c);
            } 
        }
        catch(IOException ex){    
            System.out.println(ex.getMessage());
        }   
        
        return result;
    }
  
   
   
}
   
   
   public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(8);

        HttpServer server = new HttpServer(executor);
        executor.submit(server);
        
        try(Scanner in = new Scanner(System.in))
        {
            String line = in.nextLine();
        }
        //server.setActive(false);
        executor.shutdown();
    }
}