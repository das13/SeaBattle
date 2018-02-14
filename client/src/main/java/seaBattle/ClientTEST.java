package seaBattle;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Symonenko Oleksandr
 */

public class ClientTEST {

    private static String name;
    private static String password;

    public static void main(String[] args) throws InterruptedException {

// запускаем подключение сокета по известным координатам и нициализируем приём сообщений с консоли клиента
        try(Socket socket = new Socket("localhost", 3345);
            BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
            DataInputStream ois = new DataInputStream(socket.getInputStream())){

            System.out.println("CLIENT. Client connected to socket. Working in thread. Write your nickname and password for LOG IN/REGISTER");
            System.out.println("CLIENT. Nickname:");
            name = br.readLine();
            System.out.println("CLIENT. Password:");
            password = br.readLine();
            oos.writeUTF(name);
            oos.flush();
            oos.writeUTF(password);
            oos.flush();
            System.out.println("CLIENT. Name and password was sent to server successfully.");

            // проверяем живой ли канал и работаем если живой
            while(!socket.isOutputShutdown()){

                // ждём консоли клиента на предмет появления в ней данных
                if(br.ready()){

                    // данные появились - работаем
                    System.out.println("CLIENT. Client start writing in channel...");
                    String clientCommand = br.readLine();

                    // пишем данные с консоли в канал сокета для сервера
                    oos.writeUTF(clientCommand);
                    oos.flush();
                    System.out.println("CLIENT. Client sent message " + clientCommand + " to server.");

                    // ждём чтобы сервер успел прочесть сообщение из сокета и ответить

                    // проверяем условие выхода из соединения
                    if(clientCommand.equalsIgnoreCase("quit")){

                        // если условие выхода достигнуто разъединяемся
                        System.out.println("CLIENT. Client kill connections");

                        // смотрим что нам ответил сервер на последок перед закрытием ресурсов
                        if(ois.read() > -1)     {
                            System.out.println("CLIENT. reading...");
                            String in = ois.readUTF();
                            System.out.println(in);
                        }

                        // после предварительных приготовлений выходим из цикла записи чтения
                        break;
                    }

                    // если условие разъединения не достигнуто продолжаем работу
                    System.out.println("CLIENT. Client sent message & start waiting for data from server...");

                    // проверяем, что нам ответит сервер на сообщение(за предоставленное ему время в паузе он должен был успеть ответить)

                    // если успел забираем ответ из канала сервера в сокете и сохраняем её в ois переменную,  печатаем на свою клиентскую консоль
                    System.out.println("CLIENT. reading...");
                    String in = ois.readUTF();
                    System.out.println(in);

                }
            }
            // на выходе из цикла общения закрываем свои ресурсы
            System.out.println("CLIENT. Closing connections & channels on clentSide - DONE.");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}