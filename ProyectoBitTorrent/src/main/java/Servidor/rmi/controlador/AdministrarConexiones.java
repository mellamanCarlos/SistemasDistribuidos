package Servidor.rmi.controlador;

import DAO.DAO;
import DAO.archivo.ArchivoNodoDAO;
import Nodo.rmi.ClienteRMI;
import modelo.Nodo;

import java.io.FileInputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class AdministrarConexiones extends TimerTask
{
    Properties props;

    public AdministrarConexiones()
    {
        props = new Properties();
        try(FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"\\propiedadesBitTorrent.properties"))
        {
            props.load(fis);
        }
        catch(Exception ex)
        {
            System.exit(1);
        }
        Timer temporizador = new Timer();
        temporizador.scheduleAtFixedRate(this, Integer.parseInt(props.getProperty("validacionInicial")), Integer.parseInt(props.getProperty("intervaloValidacion")));
    }

    private void administrarSeeders()
    {
        DAO archivoNodoDAO= new ArchivoNodoDAO();
        List<Nodo> listaNodo = archivoNodoDAO.lista();
        if (listaNodo != null)
        {
            for (int i =0; i<listaNodo.size() ; i++)
            {
                try
                {
                    Registry registro = LocateRegistry.getRegistry(listaNodo.get(i).getIp(),listaNodo.get(i).getPuerto());
                    ClienteRMI cliente = (ClienteRMI) registro.lookup("transferenciaArchivos");
                    System.out.println("Confirmando conexion con el nodo: " +listaNodo.get(i).getIp());

                    if(cliente.conexionACK())
                    {
                        System.out.println("Esta compartiendo recursos el nodo: " + listaNodo.get(i).getIp());
                        System.out.println("\n");
                    }
                }
                catch (Exception e)
                {
                    System.out.println("El nodo: " + listaNodo.get(i).getIp() + " ya NO esta compartiendo recursos");
                    System.out.println("\n");
                    archivoNodoDAO.eliminar(listaNodo.get(i).getIp());
                }
            }
        }
    }

    @Override
    public void run()
    {
        administrarSeeders();
    }
}
