package seaBattle.controller;

import seaBattle.xmlservice.InClientXML;
import seaBattle.xmlservice.OutClientXML;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientController {
    private InClientXML inClientXML;
    private OutClientXML outClientXML;
    private Scanner scanner;
    private Socket socket;

    public ClientController() throws IOException, XMLStreamException {
        socket = new Socket("localhost", 9001);
        inClientXML = new InClientXML(socket);
        outClientXML = new OutClientXML(socket);
        //console check
        this.scanner = new Scanner(System.in);
    }

    public void run() throws IOException, XMLStreamException {

        while (!socket.isClosed()){
            System.out.println("\nwrite key");
            String key = scanner.nextLine();
            System.out.println("write value1:");
            String value1 = scanner.nextLine();
            System.out.println("write value2:");
            String value2 = scanner.nextLine();
            System.out.println("sending:");
            outClientXML.send(key,value1,value2);
            System.out.println("\nserver xml answer:\n");

            inClientXML.setReader(inClientXML.getFactory().createXMLStreamReader(inClientXML.getFileReader()));
            XMLStreamReader reader = inClientXML.getReader();
            while (reader.hasNext()) {
                inClientXML.printEvent(reader);
                if (reader.isEndElement() && "root".equals(reader.getName().toString())) {
                    break;
                }else{
                    reader.next();
                }
//            while (true) {
//                while (true){
//                    System.out.println("idle loop");
//                    условие выхода с цикла ожидания
//                    break;
//                }
//                while (true) {
//                    System.out.println("game loop");
//                    условие выхода с цикла игры
//                    break;
//                }
            }


//            inClientXML.printEvent(inClientXML.getReader());


        }
        System.out.println("\n\nPress somth to exit");
        outClientXML.getWriter().close();
        outClientXML.getWriter2().close();
    }
}

