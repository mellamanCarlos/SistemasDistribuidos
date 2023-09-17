#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

int main(int argc, char *argv[])
{
    int clienteSocket, puerto, read_size;
    struct sockaddr_in serverAddr;
    char buffer[2000], direccionIPServ[20];

    strcpy(direccionIPServ, argv[1]);
    puerto = atoi(argv[2]);

    if ((clienteSocket = socket(AF_INET, SOCK_STREAM, 0)) < 0)
    {
        perror("Error al crear el socket");
        exit(EXIT_FAILURE);
    }

    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(puerto); // Puerto del servidor en Java
    serverAddr.sin_addr.s_addr = inet_addr(direccionIPServ);

    if (connect(clienteSocket, (struct sockaddr *)&serverAddr, sizeof(serverAddr)) < 0)
    {
        perror("Error al conectar con el servidor");
        exit(EXIT_FAILURE);
    }

    char mensaje[200];

    printf("Teclea el mensaje que quieres enviar: ");
    fgets(mensaje, sizeof(mensaje), stdin); // read string
    send(clienteSocket, mensaje, strlen(mensaje), 0);

    while ((read_size = recv(clienteSocket, buffer, sizeof(buffer), 0)) > 0) // Para recibir una respuesta del servidor:
    {
        printf("Respuesta del servidor: %s\n", buffer);

        printf("Teclea el mensaje que quieres enviar: ");
        fgets(mensaje, sizeof(mensaje), stdin); // read string
        send(clienteSocket, mensaje, strlen(mensaje), 0);
    }

    if (read_size == 0)
    {
        puts("Servidor desconectado");
        fflush(stdout);
    }
    else if (read_size == -1)
    {
        perror("recv fallo");
    }

    close(clienteSocket);

    return 0;
}