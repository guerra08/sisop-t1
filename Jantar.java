import java.util.concurrent.*;

public class Jantar {
    static int num_threads = 50;
    static volatile int porcoes = 0;

    static class Canibal implements Runnable {
        Semaphore mutex;
        Semaphore vazia;
        Semaphore cozinhando;
        private int tid;

        public Canibal(int id, Semaphore mtx, Semaphore vz, Semaphore cz){
            this.tid = id;
            mutex = mtx;
            vazia = vz;
            cozinhando = cz;
        }

        public void run(){
            while (true) {
                try {
                    cozinhando.acquire();
                    mutex.acquire();
                    System.out.println("Canibal " + tid + " está comendo ");
                    porcoes++;
                    if (porcoes == num_threads)
                        vazia.release();
                    mutex.release();
                }catch (InterruptedException e) {
                }
            }
        }
    }

    static class Cozinheiro implements Runnable {
        Semaphore mutex;
        Semaphore vazia;
        Semaphore cozinhando;
        private int tid;
        private int i;

        public Cozinheiro(int id, Semaphore mtx, Semaphore vz, Semaphore cz){
            this.tid = id;
            mutex = mtx;
            vazia = vz;
            cozinhando = cz;
        }

        public void run(){
            while (true) {
                try {
                    vazia.acquire();
                    mutex.acquire();
                    System.out.println("Cozinheiro está cozinhando");
                    porcoes = 0;
                    for (i = 0; i < num_threads; i++)
                        cozinhando.release();
                    mutex.release();
                }catch (InterruptedException e) {
                }
            }
        }
    }

    public static void main(String[] args) {
        Semaphore mutex = new Semaphore(1);
        Semaphore vazia = new Semaphore(0);
        Semaphore cozinhando = new Semaphore(num_threads);
        int i;

        Thread p = new Thread(new Cozinheiro(0, mutex, vazia, cozinhando));
        p.start();
        for (i = 1; i <= num_threads; i++){
            Thread t = new Thread(new Canibal(i, mutex, vazia, cozinhando));
            t.start();
        }
    }
}
