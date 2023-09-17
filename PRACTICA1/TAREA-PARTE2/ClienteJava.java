import java.io.*;
import java.net.*;
import java.util.Arrays;

public class ClienteJava 
{
    public static void main(String[] args) throws IOException 
    {

        if (args.length != 2) 
        {

            System.err.println("Uso desde consola: java ClienteJava <Direccion IP servidor> <numero puerto>");
            System.exit(1);
        }

        String nombreHost = args[0];
        int numeroPuerto = Integer.parseInt(args[1]);

        try 
        {
            // Establece una conexión con el servidor en el puerto designado por el usuario
            Socket socket = new Socket(nombreHost, numeroPuerto);
            System.out.println("Conexion al servidor establecida\n");

            // Obtiene los flujos de entrada y salida
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            String usuarioEscribio;
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

            while ((usuarioEscribio = teclado.readLine()) != null) 
            {
                if (usuarioEscribio.equals("0") == true) 
                {
                    // Cierra la conexión
                    socket.close();
                    break;
                }

                // Envía datos al servidor
                out.write(usuarioEscribio.getBytes());

                // Recibe datos del servidor
                byte[] buffer = new byte[20000];
                Arrays.fill(buffer, (byte) 0); // Llena el búfer con ceros
                int bytesRead = in.read(buffer);

                String respuesta = new String(buffer, 0, bytesRead);
                System.out.println("Respuesta del servidor: " + respuesta);

            }
        } 
        catch (UnknownHostException e) 
        {
            System.err.println("No conozco al host " + nombreHost);
            System.exit(1);
        } 
        catch (IOException e) 
        {

            System.err.println("no se pudo obtener E/S para la conexion " + nombreHost);
            System.exit(1);
        }
    }
}