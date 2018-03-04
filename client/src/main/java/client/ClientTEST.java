package client;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import client.xmlservice.InClientXML;
import client.xmlservice.OutClientXML;

public class ClientTEST {
    private InClientXML inClientXML;
    private OutClientXML outClientXML;
    private Scanner scanner;
    private Socket socket;

    public ClientTEST() throws IOException, XMLStreamException {
        this.socket = new Socket("localhost", 9001);
        inClientXML = new InClientXML(socket);
        outClientXML = new OutClientXML(socket);
        //console check
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) throws  Exception{
        ClientTEST clientTEST = new ClientTEST();
        clientTEST.run();
    }

    public void run() throws IOException, XMLStreamException {
        while (!socket.isClosed()){
            System.out.println("\nwrite key");
            String key = scanner.nextLine();
            System.out.println("write value1:");
            String value1 = scanner.nextLine();
            System.out.println("write value2:");
            String value2 = scanner.nextLine();
            System.out.println("SENDING:");
            outClientXML.send(key,value1,value2);
            System.out.println("\n\nSERVER THREAD ANSWER:");

            inClientXML.setReader(inClientXML.getFactory().createXMLStreamReader(inClientXML.getFileReader()));
            XMLStreamReader reader = inClientXML.getReader();
            while (reader.hasNext()) {
                System.out.println("key = \"" + inClientXML.checkValue(reader) +"\"");
                System.out.println("value = \"" + inClientXML.checkValue(reader) +"\"");
                reader.next();
                if (reader.isEndElement() && "root".equals(reader.getName().toString())) {
                    break;
                }else{
                    reader.next();
                }
            }
        }
        System.out.println("\n\nexit");
        outClientXML.getWriter().close();
        outClientXML.getWriter2().close();
    }
}


