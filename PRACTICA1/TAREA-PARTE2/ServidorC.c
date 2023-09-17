#include <netinet/in.h> //estructura para almacenar informacion de la direccion
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/socket.h> //for socket APIs
#include <unistd.h>     //write

int main(int argc, char const *argv[])
{

    int c, read_size, puerto;
    char mensajeCliente[2000], respuesta[20000], direccionIPServ[20];

    puerto = atoi(argv[1]);

    // Se crea un socket para el servidor
    int servSocket = socket(AF_INET, SOCK_STREAM, 0);

    if (servSocket == -1)
    {
        printf("No se pudo crear el socket\n");
    }
    printf("Socket creado\n");

    // define server address
    struct sockaddr_in servAddr, client;

    servAddr.sin_family = AF_INET;
    servAddr.sin_port = htons(puerto);
    servAddr.sin_addr.s_addr = INADDR_ANY;

    // bind socket to the specified IP and port
    if (bind(servSocket, (struct sockaddr *)&servAddr, sizeof(servAddr)) < 0)
    {
        // print the error message
        perror("bind fallo. Error");
        return 1;
    }
    printf("Bind realizado correctamente\n");

    // listen for connections
    listen(servSocket, 3);

    // Accept and incoming connection
    printf("Esperando por conexión entrante...\n");
    c = sizeof(struct sockaddr_in);

    // accept connection from an incoming client
    int clientSocket = accept(servSocket, (struct sockaddr *)&client, (socklen_t *)&c);
    if (clientSocket < 0)
    {
        perror("accept fallo. Conexion NO establecida");
        return 1;
    }
    printf("Conexion del cliente aceptada\n");

    // Receive a message from client
    while ((read_size = recv(clientSocket, mensajeCliente, 2000, 0)) > 0)
    {
        printf("Mensaje recibido del cliente: %.*s\n", read_size, mensajeCliente);
        
        int numero = atoi(mensajeCliente);
        numero++;  // Incrementa el número

        snprintf(respuesta, sizeof(respuesta), "%d", numero);

        printf("Mensaje que se va a enviar al cliente: %.*s\n", (int)strlen(respuesta), respuesta);

        // Send the message back to client
        write(clientSocket, respuesta, strlen(respuesta)+1); // Añade +1 para incluir el carácter nulo
        memset(mensajeCliente, 0, sizeof(mensajeCliente));// Limpia el búfer antes de recibir un nuevo mensaje
    }

    if (read_size == 0)
    {
        puts("Cliente desconectado");
        fflush(stdout);
    }
    else if (read_size == -1)
    {
        perror("recv fallo");
    }

    return 0;
}