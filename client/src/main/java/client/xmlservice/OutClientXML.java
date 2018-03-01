package client.xmlservice;


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

    //send XML with 1 value
    public void send(String key, String value1) throws XMLStreamException {
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

//        writer2.writeStartDocument("1.0");
//        writer2.writeStartElement("root");
//        writer2.writeStartElement("key");
//        writer2.writeCharacters(key);
//        writer2.writeEndElement();
//        writer2.writeStartElement("value");
//        writer2.writeCharacters(value1);
//        writer2.writeEndElement();
//        writer2.writeEndElement();
//        writer2.writeEndDocument();
//        writer2.flush();
    }

    //send XML with 2 values
    public void send(String key, String value1, String value2) throws XMLStreamException {
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

        //console check
        writer2.writeStartDocument("1.0");
        writer2.writeStartElement("root");
        writer2.writeStartElement("key");
        writer2.writeCharacters(key);
        writer2.writeEndElement();
        writer2.writeStartElement("value");
        writer2.writeCharacters(value1);
        writer2.writeEndElement();
        writer2.writeStartElement("value");
        writer2.writeCharacters(value2);
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
