package DAO;

import modelo.Nodo;
public interface TorrentDAO extends DAO<Nodo>
{
    public void eliminarArchivo();
}
