package seaBattle.xmlservice;

import org.apache.log4j.Logger;
import seaBattle.controller.PlayerController;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.net.Socket;

/**
 * <code>OutServerXML</code>
 * provides sending keys/values with XMLStreamWriter
 * @author Oleksandr Symonenko
 */
public class OutServerXML {
    private XMLOutputFactory factory;
    private XMLStreamWriter writer;
    private Socket socket;

    private final static Logger logger = Logger.getLogger(PlayerController.class);

    /**
     * <code>OutServerXML</code> initializes for every <code>PlayerController</code> thread
     * to send XML data to specific Client linked to this <code>PlayerController</code>
     * @param socket - linked Client socket
     */
    public OutServerXML(Socket socket){
        this.socket = socket;
        factory = XMLOutputFactory.newInstance();
        try {
            writer = factory.createXMLStreamWriter(socket.getOutputStream());
        } catch (XMLStreamException | IOException e) {
            logger.error("XMLStreamException or IOE when creating XMLStreamWriter", e);
        }
    }

    /**
     * <code>send</code> sends XML data with specific key and 1 value
     * @param key - key
     * @param value - value
     */
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
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error("InterruptedException when sending", e);
            }
        } catch (XMLStreamException e) {
            logger.error("XMLStreamException when sending", e);
        }
    }

    /**
     * <code>send</code> sends XML data with specific key, shoot result and 2 values
     * @param key - key
     * @param shootResult - result of shooting
     * @param value1 - y1 coordinate
     * @param value2 - x1 coordinate
     */
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
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error("InterruptedException when sending", e);
            }
        } catch (XMLStreamException e) {
            logger.error("XMLStreamException when sending", e);
        }
    }

    /**
     * <code>send</code> sends XML data with specific key and list of String values
     * @param key - key
     * @param list - list of String values
     */
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

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error("InterruptedException when sending", e);
            }
        } catch (XMLStreamException e) {
            logger.error("XMLStreamException when sending", e);
        }
    }

    //getters & setters
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
