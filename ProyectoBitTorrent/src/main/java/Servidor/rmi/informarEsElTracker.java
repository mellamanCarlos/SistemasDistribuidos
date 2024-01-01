package Servidor.rmi;

import modelo.Nodo;
import modelo.informacionArchivo;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface informarEsElTracker extends Remote
{
    void CompartirIP(Nodo nodo) throws RemoteException, IOException;
    List<Nodo> enviarIP(String nombreArchivo, String IP) throws RemoteException,IOException;
    List <informacionArchivo> ListarArchivos(String IP) throws RemoteException;
}
