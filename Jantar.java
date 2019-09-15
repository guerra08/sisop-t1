import java.util.concurrent.*;

public class Jantar {
    static int num_canibais = 20;
    static int num_porcoes_por_travessa = 5;
    static volatile int travessa = 5;

    static class cozinheiro implements Runnable {
        Semaphore mutex;
        Semaphore comida;
        Semaphore cozinha;
        Semaphore enchendo;

        public cozinheiro(Semaphore mutex, Semaphore comida, Semaphore enchendo, Semaphore cozinha) {
            this.mutex = mutex;
            this.comida = comida;
            this.cozinha = cozinha;
            this.enchendo = enchendo;
        }

        public void run(){
            while(true){
                try{
                    System.out.println("Cozinheiro acordado!! Enchendo travessa");
                    cozinha.acquire();
                    travessa = num_porcoes_por_travessa;
                    for (int i = 0; i < num_porcoes_por_travessa; i++){
                        comida.release();
                    }
                    enchendo.release();
                }catch(InterruptedException e){}
            }
        }
    }


    static class canibal implements Runnable {
        Semaphore mutex;
        Semaphore comida;
        Semaphore cozinha;
        Semaphore enchendo;
        private int tid;

        public canibal(int id, Semaphore mutex, Semaphore comida, Semaphore enchendo, Semaphore cozinha) {
            this.mutex = mutex;
            this.comida = comida;
            this.cozinha = cozinha;
            this.enchendo = enchendo;
            this.tid = id;
        }

        public void run() {
            while (true) {
                try{
                    comida.acquire();
                    mutex.acquire();
                    if (travessa == 0) {
                            System.out.println("Canibal " + tid + " acordando o cozinheiro.");
                            cozinha.release();
                            enchendo.acquire();
                            travessa = num_porcoes_por_travessa;
                    }
                    travessa--;
                    System.out.println("Canibal " + tid + " comendo a porcao. Restam " + travessa + " na travessa.");
                    mutex.release();
                }catch(InterruptedException e){}
            }
        }
    }

    public static void main(String[] args) {
        Semaphore mutex = new Semaphore(1);
        Semaphore comida = new Semaphore(num_canibais);
        Semaphore cozinha = new Semaphore(0);
        Semaphore enchendo = new Semaphore(0);
        int i;

        Thread p = new Thread(new cozinheiro(mutex, comida, enchendo, cozinha));
        p.start();
        for (i = 1; i <= num_canibais; i++) {
            Thread c = new Thread(new canibal(i, mutex, comida, enchendo, cozinha));
            c.start();
        }
    }
}