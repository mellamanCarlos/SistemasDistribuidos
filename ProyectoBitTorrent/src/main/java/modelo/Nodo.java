package modelo;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Nodo implements Serializable
{
    private String ip;
    private int puerto;
    private List<informacionArchivo> archivos;

    public Nodo (String ip, int puerto, List<informacionArchivo> archivos)
    {
        this.ip = ip;
        this.puerto=puerto;
        this.archivos=archivos;
    }

    public String getIp()
    {
        return ip;
    }
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    public int getPuerto()
    {
        return puerto;
    }
    public void setPuerto(int puerto)
    {
        this.puerto=puerto;
    }
    public List<informacionArchivo> getArchivos()
    {
        return archivos;
    }
    public void setArchivos(List<informacionArchivo> archivos)
    {
        this.archivos=archivos;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.ip);
        hash = 17 * hash + this.puerto;
        hash = 17 * hash + Objects.hashCode(this.archivos);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Nodo otro = (Nodo) obj;
        if(this.puerto != otro.puerto)
        {
            return false;
        }
        if(!Objects.equals(this.ip, otro.ip))
        {
            return false;
        }
        if(!Objects.equals(this.archivos,otro.archivos))
        {
            return false;
        }
        return true;
    }

    public String pasarAString()
    {
        return "Nodo{" + "ip=" + ip + ", puerto=" + puerto + ", archivos=" + archivos + "}";
    }
}
