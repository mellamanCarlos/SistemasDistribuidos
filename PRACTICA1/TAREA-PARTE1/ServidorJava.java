import java.net.*; // paquete que contienen clases de red , todo lo necesario para comunicarme en red
import java.io.*; // paquete que contienen clases para E/S teclado y monitor

public class ServidorJava 
{
    public static void main(String[] args) throws IOException 
    {
       
        if (args.length != 1) 
        {
            System.err.println("Uso desde consola:  <numero puerto>");
            System.exit(1);
        }
       
        int numeroPuerto = Integer.parseInt(args[0]);// convertimos el numero de puerto

        try 
        {
            // Crear un socket de servidor en el puerto especificado
            ServerSocket serverSocket = new ServerSocket(numeroPuerto);
            System.out.println("Servidor escuchando en el puerto " + numeroPuerto);

            // Aceptar conexiones entrantes de clientes
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado desde " + clientSocket.getInetAddress());

            // Obtener flujos de entrada y salida
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();
           
            PrintWriter escritor = new PrintWriter(out, true);                   
            BufferedReader lector = new BufferedReader(new InputStreamReader(in));
            
            String linealeida;
            while ((linealeida = lector.readLine()) != null) 
            {
                escritor.println(linealeida + ",¿como estas? Soy el servidor\0");
            }
            
            // Cierra la conexión con el cliente
            clientSocket.close();

            // Cierra el servidor
            serverSocket.close();
        } 
        catch (IOException e) 
        {
            System.out.println(" ocurrio una excepcion cuando intentamos escuchar "+ numeroPuerto + " o esperando por una conexicon");
            System.out.println(e.getMessage());
        }
    }
}