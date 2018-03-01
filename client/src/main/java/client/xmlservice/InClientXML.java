package client.xmlservice;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;

public class InClientXML {
    private XMLStreamReader reader;
    private XMLInputFactory factory;
    private Reader fileReader;
    private Socket socket;

    public InClientXML(Socket socket){
        this.socket = socket;
        this.factory = XMLInputFactory.newInstance();
        try {
            this.fileReader = new InputStreamReader(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String checkValue(XMLStreamReader reader){
        StringBuilder sb = new StringBuilder();
        String value = "";
        try {
            while (reader.getEventType() != 4){
                reader.next();
            }
            while (reader.getEventType() == 4){
                sb.append(reader.getText());
                value = sb.toString();
                reader.next();
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return value;
    }

    public void printEvent(XMLStreamReader reader) {
        switch (reader.getEventType()) {
            case XMLStreamConstants.START_ELEMENT:
                System.out.print("<");
                printName(reader);
                printNamespaces(reader);
                printAttributes(reader);
                System.out.print(">");
                break;
            case XMLStreamConstants.END_ELEMENT:
                System.out.print("</");
                printName(reader);
                System.out.print(">");
                break;
            case XMLStreamConstants.SPACE:
            case XMLStreamConstants.CHARACTERS:
                int start = reader.getTextStart();
                int length = reader.getTextLength();
                System.out.print(new String(reader.getTextCharacters(),
                        start,
                        length));
                break;
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                System.out.print("<?");
                if (reader.hasText())
                    System.out.print(reader.getText());
                System.out.print("?>");
                break;
            case XMLStreamConstants.CDATA:
                System.out.print("<![CDATA[");
                start = reader.getTextStart();
                length = reader.getTextLength();
                System.out.print(new String(reader.getTextCharacters(),
                        start,
                        length));
                System.out.print("]]>");
                break;
            case XMLStreamConstants.COMMENT:
                System.out.print("<!--");
                if (reader.hasText())
                    System.out.print(reader.getText());
                System.out.print("-->");
                break;
            case XMLStreamConstants.ENTITY_REFERENCE:
                System.out.print(reader.getLocalName()+"=");
                if (reader.hasText())
                    System.out.print("["+reader.getText()+"]");
                break;
            case XMLStreamConstants.START_DOCUMENT:
                System.out.print("<?xml");
                System.out.print(" version='"+reader.getVersion()+"'");
                System.out.print(" encoding='"+reader.getCharacterEncodingScheme()+"'");
                if (reader.isStandalone())
                    System.out.print(" standalone='yes'");
                else
                    System.out.print(" standalone='no'");
                System.out.print("?>");
                break;
            case XMLStreamConstants.END_DOCUMENT:
                System.out.print("</END");
                printName(reader);
                System.out.print("END>");
                break;
        }
        System.out.println("");
    }
    private static void printName(XMLStreamReader reader){
        if(reader.hasName()){
            String prefix = reader.getPrefix();
            String uri = reader.getNamespaceURI();
            String localName = reader.getLocalName();
            printName(prefix,uri,localName);
        }
    }
    private static void printName(String prefix, String uri, String localName) {
        if (uri != null && !("".equals(uri)) ) System.out.print("['"+uri+"']");
        //if (uri != null && !("".equals(uri)) ) System.out.print("['"+uri+"']:");
        if (prefix != null) System.out.print(prefix+"");
        //if (prefix != null) System.out.print(prefix+":");
        if (localName != null) System.out.print(localName);
    }
    private static void printAttributes(XMLStreamReader reader){
        for (int i=0; i < reader.getAttributeCount(); i++) {
            printAttribute(reader,i);
        }
    }
    private static void printAttribute(XMLStreamReader reader, int index) {
        String prefix = reader.getAttributePrefix(index);
        String namespace = reader.getAttributeNamespace(index);
        String localName = reader.getAttributeLocalName(index);
        String value = reader.getAttributeValue(index);
        System.out.print(" ");
        printName(prefix,namespace,localName);
        System.out.print("='"+value+"'");
    }
    private static void printNamespaces(XMLStreamReader reader){
        for (int i=0; i < reader.getNamespaceCount(); i++) {
            printNamespace(reader,i);
        }
    }
    private static void printNamespace(XMLStreamReader reader, int index) {
        String prefix = reader.getNamespacePrefix(index);
        String uri = reader.getNamespaceURI(index);
        System.out.print(" ");
        if (prefix == null)
            System.out.print("xmlns='"+uri+"'");
        else
            System.out.print("xmlns"+prefix+"='"+uri+"'");
//            System.out.print("xmlns:"+prefix+"='"+uri+"'");
    }

    //getters & setters
    public XMLStreamReader getReader() {
        return reader;
    }
    public void setReader(XMLStreamReader reader) {
        this.reader = reader;
    }
    public XMLInputFactory getFactory() {
        return factory;
    }
    public void setFactory(XMLInputFactory factory) {
        this.factory = factory;
    }
    public Reader getFileReader() {
        return fileReader;
    }
    public void setFileReader(Reader fileReader) {
        this.fileReader = fileReader;
    }
}