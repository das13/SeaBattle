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
            System.out.println("authorization loop");
            System.out.println("\nwrite login:");
            String login = scanner.nextLine();
            System.out.println("write password:");
            String password = scanner.nextLine();
            outClientXML.sendAuthorization(login,password);
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

