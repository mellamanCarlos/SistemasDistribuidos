package Nodo.rmi.main;

import modelo.informacionArchivo;
import modelo.Nodo;
import Nodo.controlador.AdministradorCliente;
import Nodo.rmi.ClienteRMI;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

public class DescargarClienteRMI extends Thread
{
    private final Nodo nodo;

    Properties props;

    public DescargarClienteRMI(Nodo nodo)
    {
        super();
        this.nodo = nodo;
        props = new Properties();
        try(FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+ "\\propiedadesBitTorrent.properties"))
        {
            props.load(fis);
        }
        catch (Exception ex)
        {
            System.exit(1);
        }
    }

    private void iniciarDescarga()
    {
        try
        {
            AdministradorCliente adminCliente = new AdministradorCliente();
            Registry registro = LocateRegistry.getRegistry(nodo.getIp(),nodo.getPuerto());

            ClienteRMI cliente = (ClienteRMI) registro.lookup(props.getProperty("busquedaCliente"));
            informacionArchivo infoArchivo = nodo.getArchivos().get(0);

            for(int i = 0 ; i< infoArchivo.getFragmentos().size();i++)
            {
                if(!adminCliente.verificarFragmento(infoArchivo.getNombreArchivo(),infoArchivo.getFragmentos().get(i)))
                {
                    InetAddress ipHost = InetAddress.getLocalHost();
                    String direccionHost = ipHost.getHostAddress();
                    byte [] buffer = cliente.transferirArchivo(infoArchivo.getNombreArchivo(),infoArchivo.getFragmentos().get(i),direccionHost);

                    try(FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + props.getProperty("directorioEstaticoCliente") + props.getProperty("directorioFragmentos") + "\\" + AdministradorCliente.obtenerFragmento(infoArchivo.getNombreArchivo(), infoArchivo.getFragmentos().get(i))))
                    {
                        fos.write(buffer);
                    }
                    catch (Exception ex)
                    {
                    }
                }
            }
        }
        catch (Exception ex)
        {
        }
    }

    @Override
    public void run()
    {
        iniciarDescarga();
    }
}
