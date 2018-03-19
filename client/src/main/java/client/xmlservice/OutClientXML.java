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

    /**
     * <code>OutClientXML</code> initializes to connect
     * with <code>PlayerController</code> thread to send XML data
     * @param socket - linked PlayerController thread socket
     */
    public OutClientXML(Socket socket){
        this.socket = socket;
        factory = XMLOutputFactory.newInstance();
        try {
            writer = factory.createXMLStreamWriter(socket.getOutputStream());
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <code>send</code> sends XML data with specific key and 1 value1
     * @param key - key
     * @param value1 - value
     */
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

    /**
     * <code>send</code> sends XML data with specific key and 2 values - value1 and value2
     * @param key - key
     * @param value1 - value 1
     * @param value2 - value 2
     * @throws XMLStreamException
     */
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

    /**
     * <code>send</code> sends XML data with specific key and 4 values, used for indicate ship location
     * @param key - key
     * @param y1 - y1 coordinate
     * @param x1 - x1 coordinate
     * @param y2 - y2 coordinate
     * @param x2 - x2 coordinate
     * @throws XMLStreamException
     */
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
