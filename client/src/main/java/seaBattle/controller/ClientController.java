package seaBattle.controller;

import seaBattle.xmlservice.InClientXML;
import seaBattle.xmlservice.OutClientXML;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientController {
    InClientXML inClientXML;
    OutClientXML outClientXML;
    Scanner scanner;
    Socket socket;

    public ClientController() throws IOException, XMLStreamException {
        outClientXML = new OutClientXML();
        socket = new Socket("localhost", 9001);
        outClientXML.setFactory(XMLOutputFactory.newInstance());
        outClientXML.setWriter(outClientXML.getFactory().createXMLStreamWriter(socket.getOutputStream()));
        outClientXML.setWriter2(outClientXML.getFactory().createXMLStreamWriter(System.out));
        this.scanner = new Scanner(System.in);
    }

    public void run() throws IOException, XMLStreamException {

        String input;

        while (!socket.isClosed()){
            System.out.println("\nwaiting. write login");
            input = scanner.nextLine();
            outClientXML.sendAuthorization(input);

        }
        System.out.println("\n\nPress somth to exit");
        outClientXML.getWriter().close();
        outClientXML.getWriter2().close();
    }
}
