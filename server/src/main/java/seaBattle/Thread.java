package seaBattle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Symonenko Oleksandr
 */

public class Thread implements Runnable {

    private static Socket clientDialog;
    private String tName;

    public Thread(Socket client) {
        Thread.clientDialog = client;
    }

    @Override
    public void run() {

        try {
            DataOutputStream out = new DataOutputStream(clientDialog.getOutputStream());
            DataInputStream in = new DataInputStream(clientDialog.getInputStream());

            StringBuilder sb = new StringBuilder();
            sb.append(Server.getThreadsNumber());
            tName = sb.toString();

            System.out.println("THREAD #" + tName + ". Data(IN/OUT)Streams created");

            String name = in.readUTF();
            String password = in.readUTF();
            System.out.println("THREAD #" + tName + ".     Player name: " + name);
            System.out.println("THREAD #" + tName + ". Player password: " + password);

            System.out.println("THREAD #" + tName + ". 1. Checking playerList.txt/ registering new player (MAP?)");
            System.out.println("THREAD #" + tName + ". 2. Allow access / message - YOU BANNED! ");
            System.out.println("THREAD #" + tName + ". 3. Updating player list and send it to the player");
            System.out.println("THREAD #" + tName + ". 4. Allowing access to chat under player list");

            // основная рабочая часть
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // начинаем диалог с подключенным клиентом в цикле, пока сокет не закрыт клиентом
            while (!clientDialog.isClosed()) {
                System.out.println("THREAD #" + tName + ". - SERVER IS READY TO READ FROM THIS THREAD -\n");

                // серверная нить ждёт в канале чтения (inputstream) получения
                // данных клиента после получения данных считывает их
                String entry = in.readUTF();

                // и выводит в консоль
                System.out.println("THREAD #" + tName + ". READ from clientDialog message - " + entry);

                // если условие окончания работы не верно - продолжаем работу -
                // отправляем эхо обратно клиенту

                System.out.println("THREAD #" + tName + ". - SERVER IS SENDING ANSWER -");
                out.writeUTF("THREAD #" + tName + ". main.java.seaBattle.Server reply - " + entry + " - OK");
                System.out.println("THREAD #" + tName + ". main.java.seaBattle.Server Wrote message to clientDialog.");

                // освобождаем буфер сетевых сообщений
                out.flush();
            }

            // обработка выхода
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // если условие выхода - верно выключаем соединения
            System.out.println("THREAD #" + tName + ". Client disconnected");
            System.out.println("THREAD #" + tName + ". Closing connections & channels.");

            // закрываем сначала каналы сокета !
            in.close();
            out.close();

            // потом закрываем сокет общения с клиентом в нити
            clientDialog.close();

            System.out.println("THREAD #" + tName + ". Closing connections & channels - DONE.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String gettName() {
        return tName;
    }
}