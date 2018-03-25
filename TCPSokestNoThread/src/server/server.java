/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Roman
 */
public class server {
    public server(){
        
     int port = 6666; // случайный порт (может быть любое число от 1025 до 65535)
       try {
         ServerSocket ss = new ServerSocket(port); // создаем сокет сервера и привязываем его к вышеуказанному порту
         System.out.println("Ожидание клиента...");

         Socket socket = ss.accept(); // заставляем сервер ждать подключений и выводим сообщение когда кто-то связался с сервером
         System.out.println("Клиент присоеденился...");
         System.out.println();

 // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиенту. 
         InputStream sin = socket.getInputStream();
         OutputStream sout = socket.getOutputStream();

 // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
         DataInputStream in = new DataInputStream(sin);
         DataOutputStream out = new DataOutputStream(sout);

         String line = null;
         while(true) {
           try{
           line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
           }catch(Exception e){
               System.out.println("Server stoping...");
               break;
           }
           String [] arrLine = line.split(" ");
           double[] doubleArray = new double[arrLine.length];
           if(arrLine.length != 3){
                System.out.println("Это не 3 числа: " + line);
                out.writeUTF("Это не 3 числа!"); // отсылаем клиенту обратно ту самую строку текста.
           }else{
               try{
                    for(int i = 0; i < arrLine.length; i++) {

                         doubleArray[i] = Double.parseDouble(arrLine[i]);

                         System.out.println(doubleArray[i]);
                     }
                    out.writeUTF(Equation(doubleArray));
                
                }catch(Exception e){
                    out.writeUTF("Нужно ввести числа!!!");
                    }
           }
           
           
//           System.out.println("Запрос клиента : " + line);
//           out.writeUTF(line); // отсылаем клиенту обратно ту самую строку текста.
           out.flush(); // заставляем поток закончить передачу данных.
           System.out.println("Ждем следующий запрс...");
           System.out.println();
         }
      } catch(Exception x) { x.printStackTrace(); }
    }
    
    
    
    // метод для решения кубического уравнения
    public static String Equation(double []doubleArr) {
        double a = doubleArr[0];
        double b = doubleArr[1];
        double c = doubleArr[2];
        double D;
        
        D = b * b - 4 * a * c;
        if (D > 0) {
            double x1, x2;
            x1 = (-b - Math.sqrt(D)) / (2 * a);
            x2 = (-b + Math.sqrt(D)) / (2 * a);
            System.out.println("x1 = " + x1 + ", x2 = " + x2);
            return ("Корни уравнения: x1 = " + x1 + ", x2 = " + x2);
        }
        else if (D == 0) {
            double x;
            x = -b / (2 * a);
            System.out.println("x = " + x);
            return ("Уравнение имеет единственный корень: x = " + x);
        }
        else {
            System.out.println("Нет корней!");
            return ("Нет корней!");
        }
    }
}
