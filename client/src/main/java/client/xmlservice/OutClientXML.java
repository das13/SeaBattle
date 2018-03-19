package client.xmlservice;


import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.net.Socket;

public class OutClientXML {
    private XMLOutputFactory factory;
    private XMLStreamWriter writer;
    private Socket socket;

    public OutClientXML(Socket socket){
        this.socket = socket;
        factory = XMLOutputFactory.newInstance();
        try {
            writer = factory.createXMLStreamWriter(socket.getOutputStream());
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
    }

    //send XML with 1 value
    public synchronized void send(String key, String value1) throws XMLStreamException {
        writer.writeStartDocument("1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("key");
        writer.writeCharacters(key);
        writer.writeEndElement();
        writer.writeStartElement("value");
        writer.writeCharacters(value1);
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
    }

    //send XML with 2 values
    public synchronized void send(String key, String value1, String value2) throws XMLStreamException {
        writer.writeStartDocument("1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("key");
        writer.writeCharacters(key);
        writer.writeEndElement();
        writer.writeStartElement("value");
        writer.writeCharacters(value1);
        writer.writeEndElement();
        writer.writeStartElement("value");
        writer.writeCharacters(value2);
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
    }

    //XML with many values
    public synchronized void send(String key, String[] list) throws XMLStreamException {
        writer.writeStartDocument("1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("key");
        writer.writeCharacters(key);
        writer.writeEndElement();

        for (String value : list) {
            writer.writeStartElement("value");
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
    }

    public synchronized void send(String key, int y1, int x1, int y2, int x2) throws XMLStreamException {
        writer.writeStartDocument("1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("key");
        writer.writeCharacters(key);
        writer.writeEndElement();
        writer.writeStartElement("value");
        writer.writeCharacters(String.valueOf(y1));
        writer.writeEndElement();
        writer.writeStartElement("value");
        writer.writeCharacters(String.valueOf(x1));
        writer.writeEndElement();
        writer.writeStartElement("value");
        writer.writeCharacters(String.valueOf(y2));
        writer.writeEndElement();
        writer.writeStartElement("value");
        writer.writeCharacters(String.valueOf(x2));
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
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
}
