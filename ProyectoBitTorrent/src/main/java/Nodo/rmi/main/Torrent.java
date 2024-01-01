package Nodo.rmi.main;

import DAO.TorrentDAO;
import DAO.archivo.ArchivoTorrentDAO;
import Nodo.controlador.AdministradorCliente;
import modelo.Nodo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class Torrent extends TimerTask
{
    private Properties props;
    List<DescargarClienteRMI> hilosDescargas;


    public Torrent()
    {
        hilosDescargas = new ArrayList<>();
        props = new Properties();

        try(FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\propiedadesBitTorrent.properties"))
        {
            props.load(fis);
        }
        catch (Exception ex)
        {
            System.exit(1);
        }

        Timer timer = new Timer();
        timer.schedule(this, Integer.parseInt(props.getProperty("iniciarTorrent")), Integer.parseInt(props.getProperty("intervaloTorrent")));

        props = new Properties();

        try(FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\propiedadesBitTorrent.properties"))
        {
            props.load(fis);
        }
        catch (Exception ex)
        {
            System.exit(1);
        }
    }

    public void reconstruirArchivos()
    {
        String directorio = System.getProperty("user.dir") + props.getProperty("directorioEstaticoCliente") + props.getProperty("directorioTorrents");
        File direc = new File(directorio);
        String [] archivoLista=direc.list();

        for(String f: archivoLista)
        {
            int tamanioArchivo = 0;
            int fragmentoDescargado = 0;
            TorrentDAO torrentDAO = new ArchivoTorrentDAO(f);
            List<Nodo> nodos = torrentDAO.lista();

            Nodo nodo = nodos.get(0);
            if(tamanioArchivo == 0)
            {
                tamanioArchivo = (int) Math.ceil(nodo.getArchivos().get(0).getTamanio());
            }

            byte [] buffer = new byte[(int) tamanioArchivo];

            int tamanioPedazo = (int)Math.ceil((double)tamanioArchivo/Double.parseDouble(props.getProperty("divisionFragmentos")));
            int offset = 0;
            for(int i =0;i<Integer.parseInt(props.getProperty("divisionFragmentos"));i++)
            {
                String nombreFragmento = System.getProperty("user.dir") + props.getProperty("directorioEstaticoCliente") + props.getProperty("directorioFragmentos") + "\\" + AdministradorCliente.obtenerFragmento(nodo.getArchivos().get(0).getNombreArchivo(), i + 1);

                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(nombreFragmento)))
                {
                    int bytesLeidos = 0;
                    int auxTamanioPedazo = tamanioPedazo * (i + 1) > tamanioArchivo ? tamanioArchivo - (tamanioPedazo) * i : tamanioPedazo;

                    bytesLeidos = bis.read(buffer, offset, (int) auxTamanioPedazo);
                    offset += bytesLeidos;
                    fragmentoDescargado++;
                } catch (Exception ex)
                {

                }
            }
                if (fragmentoDescargado >= Integer.parseInt(props.getProperty("divisionFragmentos"))-1)
                {
                    String nombreArchivo = System.getProperty("user.dir")  + props.getProperty("directorioEstaticoCliente") + props.getProperty("directorioArchivos") + "\\" + nodo.getArchivos().get(0).getNombreArchivo();
                    try(FileOutputStream fos = new FileOutputStream(nombreArchivo))
                    {
                        fos.write(buffer);
                    }
                    catch (Exception ex)
                    {
                    }
                    torrentDAO.eliminarArchivo();
                }

                else
                {
                    for(Nodo n: nodos)
                    {
                        String nombreNodo = n.getIp() + "_" + nodo.getArchivos().get(0).getNombreArchivo();
                        boolean e = false;

                        for(int a =0; a< hilosDescargas.size();a++)
                        {
                            if(!hilosDescargas.get(a).isAlive())
                            {
                                hilosDescargas.remove(a);
                                a--;
                            }
                            else if(hilosDescargas.get(a).getName().equals(nombreNodo))
                            {
                                e = true;
                                break;
                            }
                        }
                        if(!e)
                        {
                            DescargarClienteRMI descargar = new DescargarClienteRMI(n);
                            descargar.setName(nombreNodo);
                            hilosDescargas.add(descargar);
                            hilosDescargas.get(hilosDescargas.size()-1).start();
                        }
                    }
                }
        }
    }



    @Override
    public void run()
    {
        reconstruirArchivos();
    }
}
