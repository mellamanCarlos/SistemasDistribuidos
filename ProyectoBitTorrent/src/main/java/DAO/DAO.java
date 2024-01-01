package DAO;

import java.util.List;

public interface DAO<Object>
{
    public void crear(Object object);

    public void actualizar (Object object);

    public void eliminar(String s);

    public List<Object> lista();

    public void guardarArchivo(List<Object> objetoLista);
}
