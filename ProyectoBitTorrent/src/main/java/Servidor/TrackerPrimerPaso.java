package Servidor;

import DAO.DAO;
import DAO.archivo.ArchivoNodoDAO;
import Servidor.rmi.controlador.AdministrarConexiones;
import Servidor.rmi.informarEsElTracker;
import modelo.Nodo;
import modelo.informacionArchivo;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TrackerPrimerPaso extends UnicastRemoteObject implements informarEsElTracker
{
    public TrackerPrimerPaso() throws RemoteException
    {
        super();
    }

    @Override
    public void CompartirIP(Nodo nodo) throws IOException
    {
        System.out.println("La IP: " + nodo.getIp() + " se ha conectado al tracker");
        DAO archivoNodo = new ArchivoNodoDAO();
        archivoNodo.crear(nodo);
    }

    @Override
    public ArrayList<Nodo> enviarIP(String nombreArchivo, String IP) throws RemoteException, IOException
    {
        System.out.println("El nodo " +IP+ " ha solicitado el archivo " +nombreArchivo);
        DAO archivoNodo = new ArchivoNodoDAO();
        ArrayList<Nodo> nodos = (ArrayList<Nodo>) archivoNodo.lista();
        if(nodos.equals(null) != true)
        {
            for(int i =0; i<nodos.size();i++)
            {
                List<informacionArchivo> infoArchivo = new ArrayList<>();
                List<informacionArchivo> archivos = nodos.get(i).getArchivos();

                for(informacionArchivo archivo: archivos)
                {
                    if(archivo.getNombreArchivo().equals(nombreArchivo))
                    {
                        infoArchivo.add(archivo);
                        break;
                    }
                }

                if(infoArchivo.isEmpty())
                {
                    nodos.remove(i);
                    i--;
                }

                else
                {
                    Nodo nuevoNodo = nodos.get(i);
                    nuevoNodo.setArchivos(infoArchivo);
                    nodos.set(i,nuevoNodo);
                }
            }
        }
        return nodos;
    }

    @Override
    public ArrayList<informacionArchivo> ListarArchivos(String IP)
    {
        System.out.println("El nodo: " +IP+ " quiere conocer los archivos en la red");
        DAO nodoDAO = new ArchivoNodoDAO();
        List<Nodo> nodos = nodoDAO.lista();
        ArrayList<informacionArchivo> archivos = new ArrayList<>();
        for(Nodo nodo: nodos)
        {
            for(informacionArchivo archivo : nodo.getArchivos())
            {
                boolean existe = false;
                for(informacionArchivo auxArchivo : archivos)
                {
                    if(auxArchivo.getNombreArchivo().equals(archivo.getNombreArchivo()))
                    {
                        existe = true;
                        break;
                    }
                }

                if(!existe)
                {
                    archivos.add(archivo);
                }
            }
        }
        return archivos;
    }

    public static void main (String[] args) throws RemoteException, Exception
    {
        Properties props = new Properties();

        try(FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"\\propiedadesBitTorrent.properties"))
        {
            props.load(fis);
        }
        catch (Exception ex)
        {
            System.exit(1);
        }

        try
        {
            TrackerPrimerPaso servidor = new TrackerPrimerPaso();
            AdministrarConexiones nuevo = new AdministrarConexiones();
            System.out.println(props.getProperty("busquedaTracker"));
            Registry registro = LocateRegistry.createRegistry(Integer.parseInt(props.getProperty("puertoTracker")));
            registro.bind(props.getProperty("busquedaTracker"),servidor);
            System.out.println("El servidor esta listo");
        }
        catch (Exception e)
        {
            System.err.println("Excepcion de servidor: " +e.toString());
            e.printStackTrace();
        }
    }
}
