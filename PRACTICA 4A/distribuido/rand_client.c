/*
 * This is sample code generated by rpcgen.
 * These are only templates and you can use them
 * as a guideline for developing your own functions.
 */

#include "rand.h"

void rand_prog_1(char *host, long semi, int iter)
{
	CLIENT *clnt;
	void *result_1;
	long inicializa_random_1_arg = semi;
	int *result_2;
	char *obtiene_siguiente_random_1_arg;
	int itera = iter;
	int i;

#ifndef DEBUG
	clnt = clnt_create(host, RAND_PROG, RAND_VERS, "udp");
	if (clnt == NULL)
	{
		clnt_pcreateerror(host);
		exit(1);
	}
#endif /* DEBUG */

	result_1 = inicializa_random_1(&inicializa_random_1_arg, clnt);
	if (result_1 == (void *)NULL)
	{
		clnt_perror(clnt, "call failed");
	}
	for (i = 0; i < itera; i++)
	{
		result_2 = obtiene_siguiente_random_1((void *)&obtiene_siguiente_random_1_arg, clnt);
		if (result_2 == (int *)NULL)
		{
			clnt_perror(clnt, "call failed");
		}
		else
		{

			printf("%d : %d\n", i, *result_2);
		}
	}

#ifndef DEBUG
	clnt_destroy(clnt);
#endif /* DEBUG */
}

int main(int argc, char *argv[])
{
	char *host;
	int misemilla, itera;
	if (argc != 4)
	{
		printf("Uso: %s server_host semilla iteraciones\n", argv[0]);
		exit(1);
	}
	host = argv[1];
	misemilla = (long)atoi(argv[2]);
	itera = atoi(argv[3]);
	rand_prog_1(host, misemilla, itera);
	exit(0);
}
