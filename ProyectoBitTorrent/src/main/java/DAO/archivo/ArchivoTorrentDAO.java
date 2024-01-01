package DAO.archivo;

import DAO.TorrentDAO;
import modelo.Nodo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ArchivoTorrentDAO implements TorrentDAO
{
    private String directorioEstatico;
    private String nombreArchivo;

    public ArchivoTorrentDAO(String nombreArchivo)
    {
        Properties props = new Properties();
        try(FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"\\propiedadesBitTorrent.properties"))
        {
            props.load(fis);
            directorioEstatico = props.getProperty("directorioEstaticoCliente") + props.getProperty("directorioTorrents");
            this.nombreArchivo = directorioEstatico + "\\" + nombreArchivo;
        }
        catch(Exception ex)
        {
            directorioEstatico="";
            System.exit(1);
        }
    }

    @Override
    public void crear(Nodo object)
    {
        //No hace nada
    }

    @Override
    public void actualizar(Nodo object)
    {
        //No hace nada
    }

    @Override
    public void eliminar(String s)
    {
        //No hace nada
    }

    @Override
    public List<Nodo> lista()
    {
        List<Nodo> nuevaLista=null;
        try(FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + nombreArchivo))
        {
            try(ObjectInputStream ois = new ObjectInputStream(fis))
            {
                nuevaLista = new ArrayList<>();
                nuevaLista = (List<Nodo>) ois.readObject();
            }
            catch(Exception e)
            {
            }
            finally
            {
                fis.close();
            }
        }
        catch(Exception e)
        {
        }
        return nuevaLista;
    }

    @Override
    public void guardarArchivo(List<Nodo> objetoLista)
    {
        try(FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + nombreArchivo))
        {
            try(ObjectOutputStream oos = new ObjectOutputStream(fos))
            {
                oos.writeObject(objetoLista);
            }
            catch (Exception e)
            {
            }
            finally
            {
                fos.close();
            }
        }
        catch (Exception e)
        {
        }
    }

    @Override
    public void eliminarArchivo()
    {
        File file = new File(System.getProperty("user.dir") + this.nombreArchivo);

        if(file.delete())
        {
            System.out.println("Archivo eliminado");
        }
        else
        {
            System.out.println("Falla al momento de eliminar el archivo");
        }
    }
}
