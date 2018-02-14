package seaBattle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Symonenko Oleksandr
 */

public class Server {

    private static ExecutorService executeIt = Executors.newFixedThreadPool(3);
    private static int threadsNumber = 0;

    public static void main(String[] args) {

        // стартуем сервер на порту 3345 и инициализируем переменную для обработки консольных команд с самого сервера
        try (ServerSocket server = new ServerSocket(3345);
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("SERVER. main.java.seaBattle.Server socket created");

            // работа пока серверный сокет не закрыт
            while (!server.isClosed()) {

                // режим ожидания
                // подключения к сокету общения под именем - "clientDialog" на серверной стороне
                Socket client = server.accept();

                // после получения запроса на подключение сервер создаёт сокет
                // для общения с клиентом и отправляет его в отдельную нить
                // монопоточную нить = моно сервер который продолжает общение от лица сервера
                System.out.print("SERVER. NEW CONNECTIONS ACCEPTED. THREADS NUMBER NOW IS " + ++threadsNumber + "\n" );
                executeIt.execute(new Thread(client));
            }

            // закрытие пула нитей после завершения работы всех нитей
            executeIt.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getThreadsNumber() {
        return threadsNumber;
    }
}