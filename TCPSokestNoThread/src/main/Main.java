/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import client.client;
import java.util.Scanner;
import server.server;

/**
 *
 * @author Roman
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner in = new Scanner(System.in);

		System.out.println("Запустить программу в режиме сервера или клиента? (S(erver) / C(lient))");
		while (true) {
			char answer = Character.toLowerCase(in.nextLine().charAt(0));
			if (answer == 's') {
				new server();
				break;
			} else if (answer == 'c') {
				new client();
				break;
			} else {
				System.out.println("Некорректный ввод. Повторите.");
			}
		}
    }
    
}
