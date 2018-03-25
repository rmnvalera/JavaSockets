package server;

import client.Client;
import java.io.*;
import java.net.*;

import common.Const;
import common.DataX;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private final ServerSocket socket;
    private final ExecutorService executor;
    
    private boolean active = true;

    public Server(ExecutorService executor) throws IOException {
        this.executor = executor;
        this.socket = new ServerSocket(Const.Port);
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
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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

        public ServeOneJabber(Socket s) throws IOException {
            this.socket = s;
        }

        @Override
        public void run() {
            DataX dataX = null;
            try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));) {

                while (isActive() && Thread.currentThread().isAlive()) {
                    try {
                        dataX = (DataX) in.readObject();
                    } catch (ClassNotFoundException e) {
                        System.out.println("Invalid receiving data" + e.getMessage());
                    }
                    if (dataX != null) {
                        System.out.println("Запрос: a = " + dataX.a + "; b = " + dataX.b + "; c = " + dataX.c + ";");
                        String result = Equation(dataX);
                        out.println(result);
                        out.flush();
                    }

                    System.out.println("Ждем следующий запрс...");
                }
                System.out.println("closing...");
            } catch (IOException e) {
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Socket not closed");
                }
            }
        }

        public String Equation(DataX dataX) {
            double a = dataX.a;
            double b = dataX.b;
            double c = dataX.c;
            double D;

            D = b * b - 4 * a * c;
            if (D > 0) {
                double x1, x2;
                x1 = (-b - Math.sqrt(D)) / (2 * a);
                x2 = (-b + Math.sqrt(D)) / (2 * a);
                System.out.println("x1 = " + x1 + ", x2 = " + x2);
                return ("Корни уравнения: x1 = " + x1 + ", x2 = " + x2);
            } else if (D == 0) {
                double x;
                x = -b / (2 * a);
                System.out.println("x = " + x);
                return ("Уравнение имеет единственный корень: x = " + x);
            } else {
                System.out.println("Нет корней!");
                return ("Нет корней!");
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

    }

    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(8);

        Server server = new Server(executor);
        executor.submit(server);
        
        try(Scanner in = new Scanner(System.in))
        {
            String line = in.nextLine();
        }
        //server.setActive(false);
        executor.shutdown();
    }
}
