package Nodo.rmi.main;

import Nodo.rmi.ClienteRMI;
import Nodo.rmi.controlador.ControladorClienteRMI;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorClienteRMI
{
    private final int puerto;
    private final String nombreRMI;
    private Registry registro;
    private ClienteRMI clienteRMI;

    public ServidorClienteRMI(int puerto, String nombreRMI)
    {
        super();
        this.puerto = puerto;
        this.nombreRMI = nombreRMI;
        try
        {
            this.clienteRMI = new ControladorClienteRMI();
            registro = LocateRegistry.createRegistry(puerto);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void detenerRMI()
    {
        try
        {
            registro.unbind(nombreRMI);
            System.out.println("Los recursos ya NO se estan compartiendo");
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
    }

    public void iniciarServidor()
    {
        try
        {
            registro.bind(nombreRMI,this.clienteRMI);
            System.out.println("Los recursos ya se estan compartiendo");
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(ServidorClienteRMI.class.getName()).log(Level.SEVERE, null,ex);
            ex.printStackTrace();
        }
        catch (AlreadyBoundException ex)
        {
            Logger.getLogger(ServidorClienteRMI.class.getName()).log(Level.SEVERE, null,ex);
            ex.printStackTrace();
        }
    }
}
