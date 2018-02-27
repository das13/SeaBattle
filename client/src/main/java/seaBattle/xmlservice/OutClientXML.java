package seaBattle.xmlservice;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.net.Socket;

public class OutClientXML {
    private XMLOutputFactory factory;
    private XMLStreamWriter writer;
    //второй для контроля в консоли
    private XMLStreamWriter writer2;
    private Socket socket;

    public OutClientXML(Socket socket){
        this.socket = socket;
        factory = XMLOutputFactory.newInstance();
        try {
            writer = factory.createXMLStreamWriter(socket.getOutputStream());
        //console check
            writer2 = factory.createXMLStreamWriter(System.out);
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAuthorization(String login, String password) throws XMLStreamException {
        writer.writeStartDocument("1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("key");
        writer.writeCharacters("AUTHORIZATION");
        writer.writeEndElement();
        writer.writeStartElement("value");
        writer.writeCharacters(login);
        writer.writeEndElement();
        writer.writeStartElement("value");
        writer.writeCharacters(password);
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();

        //console check
        writer2.writeStartDocument("1.0");
        writer2.writeStartElement("root");
        writer2.writeStartElement("key");
        writer2.writeCharacters("AUTHORIZATION");
        writer2.writeEndElement();
        writer2.writeStartElement("value");
        writer2.writeCharacters(login);
        writer2.writeEndElement();
        writer2.writeStartElement("value");
        writer2.writeCharacters(password);
        writer2.writeEndElement();
        writer2.writeEndElement();
        writer2.writeEndDocument();
        writer2.flush();
    }


    public XMLOutputFactory getFactory() {
        return factory;
    }
    public void setFactory(XMLOutputFactory factory) {
        this.factory = factory;
    }
    public XMLStreamWriter getWriter() {
        return writer;
    }
    public void setWriter(XMLStreamWriter writer) {
        this.writer = writer;
    }
    public XMLStreamWriter getWriter2() {
        return writer2;
    }
    public void setWriter2(XMLStreamWriter writer2) {
        this.writer2 = writer2;
    }
}
