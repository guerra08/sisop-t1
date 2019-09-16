import java.util.concurrent.*;


public class Jantar {
    static int num_canibais;
    static int num_porcoes_por_travessa;
    static volatile int travessa;

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
                    }
                    travessa--;
                    System.out.println("Canibal " + tid + " comendo a porcao. Restam " + travessa + " na travessa.");
                    mutex.release();
                    
                }catch(InterruptedException e){}
            }
        }
    }

    public static void main(String[] args) {
        if(args.length < 2){
            System.out.println("Argumentos necessários. Utilize: \njava jantar n_canibais m_porcoes\nFinalizando o programa.");
            System.exit(0);
        }
        num_canibais = Integer.parseInt(args[0]);
        num_porcoes_por_travessa = Integer.parseInt(args[1]);
        travessa = num_porcoes_por_travessa;


        /*
        Mutex para controlar qual canibal está comendo a travessa.
        */
        Semaphore mutex = new Semaphore(1);


        /*
        /Responsável por controlar as refeições dos canibais. 
        Cada vez que um canibal come, o valor desse semafóro é decrementado. 
        Quando a travessa está vazia e o cozinheiro é chamado, o mesmo tem seu valor restaurado.
        */
        Semaphore controllerComida = new Semaphore(num_porcoes_por_travessa + 1); 

        /*
        Juntamente com o blockThread abaixo, é responsável por controlar quando o cozinheiro será chamado.
        Tem o seu valor começado em 0. Quando a travessa esvaziar, recebe o valor 1, liberando a thread do cozinheiro para
        que encha a travessa. Ao finalizar de encher, recebe um down, travando o cozinheiro.
        */
        Semaphore blockCozinheiro = new Semaphore(0);

        /*
        Responsável por fazer o canibal esperar enquanto a thread do cozinheiro realiza a ação de encher a travessa.
        Ao encher, é liberada para que os canibais voltem a comer.
        */
        Semaphore blockCanibal = new Semaphore(0); 


        Thread p = new Thread(new cozinheiro(mutex, controllerComida, blockCozinheiro, blockCanibal));
        p.start();
        for (int i = 1; i <= num_canibais; i++) {
            Thread c = new Thread(new canibal(i, mutex, controllerComida, blockCozinheiro, blockCanibal));
            c.start();
        }
    }
}