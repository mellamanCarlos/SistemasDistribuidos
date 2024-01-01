package Nodo.controlador;

import modelo.informacionArchivo;
import modelo.Nodo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;

public class AdministradorCliente
{
    private String directorioEstatico;
    private String archivosDirectorio;
    private String fragmentosDirectorio;
    Properties props;
    public AdministradorCliente()
    {
        props = new Properties();
        try(FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+ "\\propiedadesBitTorrent.properties"))
        {
            props.load(fis);
            directorioEstatico = props.getProperty("directorioEstaticoCliente");
            archivosDirectorio = directorioEstatico + props.getProperty("directorioArchivos");
            fragmentosDirectorio = directorioEstatico + props.getProperty("directorioFragmentos");
        }
        catch (Exception ex)
        {
            directorioEstatico = "";
            System.exit(1);
        }
    }

    public List<informacionArchivo> getArchivosParaCompartir()
    {
        List<informacionArchivo> archivos = new ArrayList<>();
        String directorioAbsoluto = recuperarDirectorio(archivosDirectorio);
        File directorio = new File(directorioAbsoluto);
        String [] nombresLista = directorio.list();

        for (String nombreArchivo : nombresLista)
        {
            File archivo = new File(directorioAbsoluto + "\\" + nombreArchivo);
            List <Integer> fragmentosLista = new ArrayList<>();
            for(int i = 0; i<Integer.parseInt(props.getProperty("divisionFragmentos")) ; i++)
            {
                fragmentosLista.add(i+1);
            }

            informacionArchivo archivoInfo = new informacionArchivo(nombreArchivo, archivo.length(),0.0, fragmentosLista);
            archivos.add(archivoInfo);
            try(FileInputStream fis = new FileInputStream(archivo))
            {
                archivoFragmento(fis, (int)archivo.length(),nombreArchivo);
            }
            catch (Exception ex)
            {
            }
        }
        directorioAbsoluto = recuperarDirectorio(fragmentosDirectorio);
        File direcFragmentos = new File(directorioAbsoluto);
        nombresLista = direcFragmentos.list();

        for (String nombreArchivo: nombresLista)
        {
            boolean encontrado = false;
            String[] nombreArchivoDividido = nombreArchivo.split("_");
            String[] numeroExtension = nombreArchivoDividido[1].split("[.]");
            String nombreArchivoOriginal = nombreArchivoDividido[0] + "." + numeroExtension[1];

            for (int i = 0; i < archivos.size(); i++)
            {
                if (archivos.get(i).getNombreArchivo().equals(nombreArchivoOriginal))
                {
                    informacionArchivo auxArchivo = archivos.get(i);
                    auxArchivo.setPorcentaje(auxArchivo.getPorcentaje() + 20);

                    List<Integer> listaFragmentos = auxArchivo.getFragmentos();
                    listaFragmentos.add(Integer.parseInt(numeroExtension[0]));
                    auxArchivo.setFragmentos(listaFragmentos);
                    archivos.set(i, auxArchivo);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado)
            {
                List<Integer> listaFragmentos = new ArrayList<>();
                listaFragmentos.add(Integer.parseInt(numeroExtension[0]));
                File auxiArchivo = new File(directorioAbsoluto + "\\" + nombreArchivo);
                informacionArchivo informacionExtraArchivo = new informacionArchivo(nombreArchivoOriginal, Integer.parseInt(props.getProperty("divisionFragmentos")) * auxiArchivo.length(), 20, listaFragmentos);
                archivos.add(informacionExtraArchivo);
            }
        }
            for(int i = 0; i< archivos.size();i++)
            {
                System.out.println("Compartiendo: " + archivos.get(i).getNombreArchivo());
            }
        return archivos;
    }
    public Nodo crearNodoParaCompartir(List<informacionArchivo> archivos) throws UnknownHostException
    {
        Nodo nodo;

        InetAddress ipHost = InetAddress.getLocalHost();
        String direccionHost = ipHost.getHostAddress();

        nodo = new Nodo(direccionHost,Integer.parseInt(props.getProperty("puertoCliente")),archivos);

        return nodo;
    }

    public static String obtenerFragmento(String nombreArchivo, int fragmento)
    {
        String []dividirNombre = nombreArchivo.split("[.]");
        String nombreCompleto = dividirNombre[0] + "_" + Integer.toString(fragmento) + "." + dividirNombre[1];

        return nombreCompleto;
    }
    public String recuperarDirectorio(String directorio)
    {
        String directorioActual = System.getProperty("user.dir");
        String directorioAbsoluto = directorioActual + directorio;
        return directorioAbsoluto;
    }

    private void archivoFragmento(FileInputStream fis, int tamanio, String nombreArchivo)
    {
        try(BufferedInputStream bis = new BufferedInputStream(fis))
        {
            int nuevoTamanio = (int)Math.ceil(((double)tamanio)/Double.parseDouble(props.getProperty("divisionFragmentos")));
            byte [] buffer = new byte[nuevoTamanio];
            int read=0;
            int i=0;

            while( (read = bis.read(buffer)) != -1 )
            {
                String directorio = recuperarDirectorio(getDirectorioFragmentos());
                String nombreCompleto = obtenerFragmento(nombreArchivo,i+1);
                try(FileOutputStream fos = new FileOutputStream(directorio + "\\" + nombreCompleto))
                {
                    fos.write(buffer);
                }
                catch (Exception ex)
                {

                }
                i++;
            }
        }
        catch(Exception ex)
        {
        }
    }

    public boolean verificarFragmento(String nombreArchivo, int fragmento)
    {
        boolean encontrado = false;
        String nombreCompleto = AdministradorCliente.obtenerFragmento(nombreArchivo,fragmento);
        String directorioAbsoluto = recuperarDirectorio(fragmentosDirectorio);
        File directorio = new File(directorioAbsoluto);
        String [] nombresLista = directorio.list();

        for(String nombre: nombresLista)
        {
            if(encontrado = (nombre.equals(nombreCompleto)))
            {
                break;
            }
        }
        return encontrado;
    }

    public String getArchivosDirectorio()
    {
        return archivosDirectorio;
    }

    public String getDirectorioFragmentos()
    {
        return fragmentosDirectorio;
    }
}
