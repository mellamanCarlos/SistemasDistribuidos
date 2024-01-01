package modelo;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class informacionArchivo implements Serializable
{
    private String nombreArchivo;
    private double tamanio;
    private double porcentaje;
    private List<Integer> fragmentos;

    public informacionArchivo(String nombreArchivo, double tamanio, double porcentaje, List<Integer> fragmentos)
    {
        this.nombreArchivo = nombreArchivo;
        this.tamanio = tamanio;
        this.porcentaje = porcentaje;
        this.fragmentos = fragmentos;
    }

    public String getNombreArchivo()
    {
        return nombreArchivo;
    }
    public void setNombreArchivo(String nombreArchivo)
    {
        this.nombreArchivo = nombreArchivo;
    }
    public double getTamanio()
    {
        return tamanio;
    }
    public void setTamanio(double tamanio)
    {
        this.tamanio = tamanio;
    }
    public double getPorcentaje()
    {
        return porcentaje;
    }
    public void setPorcentaje(double porcentaje)
    {
        this.porcentaje=porcentaje;
    }
    public List<Integer> getFragmentos()
    {
        return fragmentos;
    }
    public void setFragmentos(List<Integer> fragmentos)
    {
        this.fragmentos=fragmentos;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.nombreArchivo);
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.tamanio) ^ (Double.doubleToLongBits(this.tamanio) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.porcentaje) ^ (Double.doubleToLongBits(this.porcentaje) >>> 32));
        hash = 29 * hash + Objects.hashCode(this.fragmentos);
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
        final informacionArchivo otro = (informacionArchivo) obj;
        if(Double.doubleToLongBits(this.tamanio) != Double.doubleToLongBits(otro.tamanio))
        {
            return false;
        }
        if (Double.doubleToLongBits(this.porcentaje) != Double.doubleToLongBits(otro.porcentaje))
        {
            return false;
        }
        if (!Objects.equals(this.nombreArchivo,otro.nombreArchivo))
        {
            return false;
        }
        if (!Objects.equals(this.fragmentos,otro.fragmentos))
        {
            return false;
        }
        return true;
    }

    public String pasarAString()
    {
        return "InformacionArchivo{" + "nombreArchivo=" + nombreArchivo + ", tamanio=" + tamanio + ", porcentaje=" + porcentaje + ", fragmentos=" + fragmentos + "}";
    }
}
