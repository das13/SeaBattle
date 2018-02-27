package seaBattle.controller;

import seaBattle.xmlservice.InServerXML;
import seaBattle.xmlservice.OutServerXML;
import seaBattle.xmlservice.SaveLoadServerXML;

import java.net.Socket;

public class ServerController {
    Socket socket;

    InServerXML inServerXML = new InServerXML(socket);
    OutServerXML outServerXML = new OutServerXML(socket);
    SaveLoadServerXML saveLoadServerXML = new SaveLoadServerXML();

}
