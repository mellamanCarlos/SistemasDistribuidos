package Nodo;

import DAO.TorrentDAO;
import DAO.archivo.ArchivoTorrentDAO;
import Nodo.controlador.AdministradorCliente;
import Nodo.rmi.main.ServidorClienteRMI;
import Nodo.rmi.main.Torrent;
import Servidor.rmi.informarEsElTracker;
import modelo.Nodo;
import modelo.informacionArchivo;

import java.net.UnknownHostException;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class NodoPrincipal
{

    private static ServidorClienteRMI servidor = null;

    private static Torrent torrent;

    private static String estado = "Leecher";


    public static void main(String[] args) throws UnknownHostException
    {
        String opcion;
        Scanner scan = new Scanner(System.in);
        AdministradorCliente adminCliente;

        Properties props = new Properties();
        try(FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"\\propiedadesBitTorrent.properties"))
        {
            props.load(fis);
        }
        catch (Exception ex)
        {
            System.exit(1);
        }

        do
        {
            torrent = new Torrent();
            System.out.println("Proyecto BitTorrent");

            do
            {
                System.out.println("\n");
                System.out.println("Eres un: " + estado);
                System.out.println("Selecciona una opcion");
                System.out.println("a. Ver archivos descargados");
                System.out.println("b. Descargar un archivo");
                System.out.println("c. Compartir archivos");
                System.out.println("d. Dejar de compartir recursos");
                System.out.println("e. Salir");

                opcion = scan.nextLine();

                if(opcion.isEmpty())
                {
                    System.out.println("Opcion NO valida");
                    opcion= String.valueOf(' ');
                }
                else if(opcion.equalsIgnoreCase("a"))
                {
                    imprimirArchivosDescargados();
                }
                else if(opcion.equalsIgnoreCase("b"))
                {
                    try
                    {
                        Registry registro = LocateRegistry.getRegistry(props.getProperty("ipTracker"), Integer.parseInt(props.getProperty("puertoTracker")));
                        informarEsElTracker informaTracker = (informarEsElTracker) registro.lookup(props.getProperty("busquedaTracker"));
                        InetAddress ipHost = InetAddress.getLocalHost();
                        String direccionHost = ipHost.getHostAddress();
                        List<informacionArchivo> archivosLista = informaTracker.ListarArchivos(direccionHost);
                        imprimirLista(archivosLista);

                        System.out.println("Escribe el nombre de un archivo o cancelar para regresar al menu anterior");
                        String nombre = scan.nextLine();

                        if(!nombre.equalsIgnoreCase("cancelar"))
                        {
                            List<Nodo> nodos = informaTracker.enviarIP(nombre, direccionHost);

                            if(nodos.isEmpty())
                            {
                                System.out.println("No se encontro el archivo");
                            }
                            else
                            {
                                TorrentDAO torrent = new ArchivoTorrentDAO(nodos.get(0).getArchivos().get(0).getNombreArchivo() + props.getProperty("extensionTorrent"));
                                torrent.guardarArchivo(nodos);
                            }
                        }
                    }
                    catch(Exception ex)
                    {
                    }
                }
                else if(opcion.equalsIgnoreCase("c"))
                {
                    System.out.println("\n");
                    System.out.println("¿Quieres compartir tus archivos (volverte seeder)?");
                    System.out.println("Pulsa s para SI o cualquier otra tecla para NO");

                    String opcion2 = scan.nextLine();

                    if(opcion2.equalsIgnoreCase("s"))
                    {
                        estado = "Seeder";
                        adminCliente = new AdministradorCliente();
                        List<informacionArchivo> archivos = adminCliente.getArchivosParaCompartir();
                        Nodo nodo = adminCliente.crearNodoParaCompartir(archivos);
                        try
                        {
                            System.setProperty("sun.rmi.transport.connectionTimeout","40000");
                            Registry registro = LocateRegistry.getRegistry(props.getProperty("ipTracker"), Integer.parseInt(props.getProperty("puertoTracker")));

                            informarEsElTracker informaTracker = (informarEsElTracker) registro.lookup(props.getProperty("busquedaTracker"));
                            informaTracker.CompartirIP(nodo);
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        if(servidor == null)
                        {
                            servidor = new ServidorClienteRMI(Integer.parseInt(props.getProperty("puertoCliente")), props.getProperty("busquedaCliente"));
                        }
                            servidor.iniciarServidor();
                    }

                    else
                    {
                        estado = "Leecher";
                        if(servidor!=null)
                        {
                            servidor.detenerRMI();
                        }
                    }
                }
                else if(opcion.equalsIgnoreCase("d"))
                {
                    if(estado=="Seeder")
                    {
                        estado = "Leecher";
                        servidor.detenerRMI();
                    }
                    else if(estado=="Leecher")
                    {
                        System.out.println("\n");
                        System.out.println("Actualmente NO estas compartiendo recursos");
                    }
                }
            }while (!opcion.equalsIgnoreCase("e"));
        }while (!opcion.equalsIgnoreCase("e"));

        try
        {
            torrent.cancel();
            if(estado=="Seeder")
            {
                servidor.detenerRMI();
            }
            System.exit(0);
        }
        catch (Exception ex)
        {
        }
    }

    public static void imprimirArchivosDescargados()
    {
        AdministradorCliente adminCliente = new AdministradorCliente();
        List<informacionArchivo> archivos = adminCliente.getArchivosParaCompartir();

        System.out.println("Nombre\t\t\tTamanio\t\t\tPorcentaje");

        for(informacionArchivo archivo: archivos)
        {
            System.out.println(archivo.getNombreArchivo() + "\t\t\t" + archivo.getTamanio() + " bytes\t\t\t" + archivo.getPorcentaje() + "%");
            System.out.println("\n");
        }
    }

    public static void imprimirLista(List<informacionArchivo> archivos)
    {
        System.out.println("Nombre\t\t\tTamanio");
        for(informacionArchivo archivo: archivos)
        {
            System.out.println(archivo.getNombreArchivo() + "\t\t\t" + archivo.getTamanio() + " bytes");
            System.out.println("\n");
        }
    }

}
