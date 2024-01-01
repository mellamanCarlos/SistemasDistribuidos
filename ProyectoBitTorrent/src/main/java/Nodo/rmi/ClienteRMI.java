package Nodo.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClienteRMI extends Remote
{
    public byte[] transferirArchivo(String nombreArchivo,int fragmento,String ip) throws RemoteException;

    public boolean conexionACK() throws RemoteException;
}
