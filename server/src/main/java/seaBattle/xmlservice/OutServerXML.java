package seaBattle.xmlservice;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.net.Socket;

public class OutServerXML {
    private XMLOutputFactory factory;
    private XMLStreamWriter writer;
    //второй для контроля в консоли
    private XMLStreamWriter writer2;
    private Socket socket;

    public OutServerXML(Socket socket){
        this.socket = socket;
        factory = XMLOutputFactory.newInstance();
        try {
            writer = factory.createXMLStreamWriter(socket.getOutputStream());
            writer2 = factory.createXMLStreamWriter(System.out);
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String key, String value){
        try {
        writer.writeStartDocument("1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("key");
        writer.writeCharacters(key);
        writer.writeEndElement();
        writer.writeStartElement("value");
        writer.writeCharacters(value);
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
        writer2.writeCharacters(value);
        writer2.writeEndElement();
        writer2.writeEndElement();
        writer2.writeEndDocument();
        writer2.flush();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void send(String key,String shootResult, String value1, String value2){
        try {
            writer.writeStartDocument("1.0");
            writer.writeStartElement("root");
            writer.writeStartElement("key");
            writer.writeCharacters(key);
            writer.writeEndElement();
            writer.writeStartElement("value");
            writer.writeCharacters(shootResult);
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
            writer2.writeCharacters(shootResult);
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
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    //XML with many values
    public void send(String key, String[] list) {
        try {
        writer.writeStartDocument("1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("key");
        writer.writeCharacters(key);
        writer.writeEndElement();

            for (String aList : list) {
                writer.writeStartElement("value");
                writer.writeCharacters(aList);
                writer.writeEndElement();
            }
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();

        //console check
        writer2.writeStartDocument("1.0");
        writer2.writeStartElement("root");
        writer2.writeStartElement("key");
        writer2.writeCharacters(key);
        writer2.writeEndElement();

            for (String aList : list) {
                writer2.writeStartElement("value");
                writer2.writeCharacters(aList);
                writer2.writeEndElement();
            }
        writer2.writeEndElement();
        writer2.writeEndDocument();
        writer2.flush();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
//    public void send(String key, SortedMap <login, rank>){
//
//    }


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
