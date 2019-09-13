#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>

#define N_CANIBAIS	5
#define N_PORCOES	10

sem_t mutex, db;
int rc = 0, reads = 0, writes = 0;

void *canibal(void *arg){

    long int i;

	i = (long int)arg;
	while(1){
		sem_wait(&mutex);
		printf("Estou comendo");
		sem_post(&mutex);
		printf("Tá bom");
	}

} 

void *cozinheiro(void *arg){
	long int i;

	i = (long int)arg;
	while(1){
		printf("Cozinheiro foi acordado");
		sem_wait(&db);
		printf("Cozinheiro foi dormir");
		sem_post(&db);
	}
}

int main(void){
	long int i;
	pthread_t canibais[N_CANIBAIS];
	
	sem_init(&mutex, 0, 1);

	for(i = 0; i < N_CANIBAIS; i++)
		pthread_create(&canibais[i], NULL, canibal, (void *)i);

	pthread_exit(NULL);	
	return(0);
}