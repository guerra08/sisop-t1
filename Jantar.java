import java.util.concurrent.*;


/**
 * Programa feito para resolução da questão 1 do trabalho 1 da disciplina de Sistemas Operacionais,
 * ministrada pelo professor Sergio Johann Filho, no segundo semestre de 2019. 
 *  
 * @author Bruno Guerra e Eduardo Lessa
 * 
 * @param args[0] Representa o N (número de canibais) existentes.
 * @param args[1] Representa o M (número de porções por travessas) existentes.
 * Ambos os argumentos de linha de comando são necessários para a execução do programa. 
 * 
 * 
 * Exemplo de execução:
 * javac Jantar.java -> para compilar
 * java Jantar 10 20 -> Um jantar com 10 canibais e com capacidade de 20 porções na travessa.
 */

public class Jantar {
    static int nCanibais;
    static int mPorcoesPorTravessa;
    static volatile int travessaCount;
    
    public static void main(String[] args) {
        if(args.length < 2){
            System.out.println("Argumentos necessários. Utilize: \njava jantar n_canibais m_porcoes\nFinalizando o programa.");
            System.exit(0);
        }
    
    
        nCanibais = Integer.parseInt(args[0]);
        mPorcoesPorTravessa = Integer.parseInt(args[1]);
        travessaCount = mPorcoesPorTravessa;
    
    
        /*
        Mutex para controlar qual canibal está comendo a travessa.
        */
        Semaphore mutex = new Semaphore(1);
    

        /*
        /Responsável por controlar as refeições dos canibais. 
        Cada vez que um canibal come, o valor desse semafóro é decrementado. 
        Quando a travessa está vazia e o cozinheiro é chamado, o mesmo tem seu valor restaurado.
        */
        Semaphore controllerComida = new Semaphore(mPorcoesPorTravessa + 1); 
    
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
    
        
        /*
        Thread responsável pelo cozinheiro.
        */
        Thread threadCozinheiro = new Thread(new cozinheiro(mutex, controllerComida, blockCozinheiro, blockCanibal));
        threadCozinheiro.start();

        /*
        Instanciação e iniciação de cada canibal, com a quantidade indicada na execução.
        */
        for (int i = 1; i <= nCanibais; i++) {
            Thread threadCanibal = new Thread(new canibal(i, mutex, controllerComida, blockCozinheiro, blockCanibal));
            threadCanibal.start();
        }
    }

    static class cozinheiro implements Runnable {
        Semaphore mutex;
        Semaphore controllerComida;
        Semaphore blockCozinheiro;
        Semaphore blockCanibal;

        public cozinheiro(Semaphore mutex, Semaphore controllerComida, Semaphore blockCanibal, Semaphore blockCozinheiro) {
            this.mutex = mutex;
            this.controllerComida = controllerComida;
            this.blockCozinheiro = blockCozinheiro;
            this.blockCanibal = blockCanibal;
        }

        /*
        Método run implementado da classe Runnable.
        */

        @Override
        public void run(){
            while(true){
                try{
                    System.out.println("Cozinheiro acordado!! Enchendo a travessa");
                    blockCozinheiro.acquire();
                    travessaCount = mPorcoesPorTravessa;
                    for (int i = 0; i < mPorcoesPorTravessa; i++){
                        controllerComida.release();
                    }
                    blockCanibal.release();
                }catch(InterruptedException e){}
            }
        }
    }


    static class canibal implements Runnable {
        Semaphore mutex;
        Semaphore controllerComida;
        Semaphore blockCozinheiro;
        Semaphore blockCanibal;
        private int tid;

        public canibal(int id, Semaphore mutex, Semaphore controllerComida, Semaphore blockCanibal, Semaphore blockCozinheiro) {
            this.mutex = mutex;
            this.controllerComida = controllerComida;
            this.blockCozinheiro = blockCozinheiro;
            this.blockCanibal = blockCanibal;
            this.tid = id;
        }

        /*
        Método run implementado da classe Runnable.
        */

        @Override
        public void run() {
            while (true) {
                try{
                    controllerComida.acquire();
                    mutex.acquire();
                    if (travessaCount == 0) {
                            System.out.println("Canibal " + tid + " acordando o cozinheiro.");
                            blockCozinheiro.release();
                            blockCanibal.acquire();                            
                    }
                    travessaCount--;
                    System.out.println("Canibal " + tid + " comendo a porcao. Restam " + travessaCount + " na travessa.");
                    mutex.release();
                    
                }catch(InterruptedException e){}
            }
        }
    }
}