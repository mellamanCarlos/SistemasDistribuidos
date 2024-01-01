package Nodo.rmi.controlador;

import modelo.informacionArchivo;
import Nodo.controlador.AdministradorCliente;
import Nodo.rmi.ClienteRMI;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Properties;

public class ControladorClienteRMI extends UnicastRemoteObject implements ClienteRMI
{
    Properties props;

    public ControladorClienteRMI() throws RemoteException
    {
        super();
        props = new Properties();
        try(FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\propiedadesBitTorrent.properties"))
        {
            props.load(fis);
        }
        catch (Exception ex)
        {
            System.exit(1);
        }
    }
    @Override
    public byte[] transferirArchivo(String nombreArchivo, int fragmento, String ip) throws RemoteException
    {
       System.out.println("El nodo " + ip + " ha solicitado el fragmento " + fragmento + " del archivo " + nombreArchivo);

       byte [] datos = null;

        AdministradorCliente adminCliente = new AdministradorCliente();

        List<informacionArchivo> listaArchivos = adminCliente.getArchivosParaCompartir();
        informacionArchivo archivo = null;
        for(informacionArchivo f: listaArchivos)
        {
            if(f.getNombreArchivo().equals(nombreArchivo))
            {
                if(f.getFragmentos().contains(fragmento))
                {
                    archivo = f;
                    break;
                }
            }
        }

        try( BufferedInputStream bis = new BufferedInputStream(new FileInputStream(System.getProperty("user.dir")  + props.getProperty("directorioEstaticoCliente") + props.getProperty("directorioFragmentos") + "\\" + AdministradorCliente.obtenerFragmento(nombreArchivo,fragmento))) )
        {
            byte [] buffer = new byte[(int) Math.ceil(archivo.getTamanio()/  (Double.parseDouble(props.getProperty("divisionFragmentos"))))];
            int bytesRead=0;
            bytesRead = bis.read(buffer);
            datos = buffer;
            bis.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return datos;
    }

    @Override
    public boolean conexionACK() throws RemoteException
    {
        System.out.println("Mensaje por parte del tracker para verificar si sigues conectado");
        return true;
    }
}
