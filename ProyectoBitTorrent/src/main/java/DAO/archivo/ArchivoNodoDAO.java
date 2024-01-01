package DAO.archivo;

import modelo.Nodo;
import DAO.DAO;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ArchivoNodoDAO implements DAO<Nodo>
{
    private String directorioEstatico;

    private String nombreArchivo;

    public ArchivoNodoDAO()
    {
        Properties props = new Properties();
        try(FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\propiedadesBitTorrent.properties"))
        {
            props.load(fis);
            directorioEstatico = props.getProperty("directorioEstaticoServidor");
            nombreArchivo= directorioEstatico + props.getProperty("trackerDAO");
        }
        catch(Exception ex)
        {
            directorioEstatico="";
            nombreArchivo="";
            System.exit(1);
        }

    }

    @Override
    public void crear(Nodo object)
    {
        List<Nodo> nuevaLista = lista();
        if(nuevaLista==null)
        {
            nuevaLista = new ArrayList<>();
        }
        nuevaLista.add(object);
        guardarArchivo(nuevaLista);
    }

    @Override
    public void actualizar(Nodo object)
    {
        List<Nodo> listaNodos = lista();

        for (int i =0 ; i<listaNodos.size() ; i++)
        {
            if(listaNodos.get(i).getIp().equals(object.getIp()))
            {
                Nodo aux = listaNodos.get(i);
                aux.setIp(object.getIp());
                aux.setPuerto(object.getPuerto());
                aux.setArchivos(object.getArchivos());
                listaNodos.set(i,aux);
                break;
            }
        }
        guardarArchivo(listaNodos);
    }

    @Override
    public void eliminar(String s)
    {
        List<Nodo> listaNodos = lista();

        for(int i=0;i<listaNodos.size();i++)
        {
            if(listaNodos.get(i).getIp().equals(s))
            {
                listaNodos.remove(i);
                break;
            }
        }
        guardarArchivo(listaNodos);
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
}
