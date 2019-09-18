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

public class JantarSemMutexEx2 {
    static int nCanibais;
    static int mPorcoesPorTravessa;
    static volatile int travessaCount;
    
    static Peterson peterson;
  

    public static void main(String[] args) {
        if(args.length < 2){
            System.out.println("Argumentos necessários. Utilize: \njava jantar n_canibais m_porcoes\nFinalizando o programa.");
            System.exit(0);
        }
    
    
        nCanibais = Integer.parseInt(args[0]);
        mPorcoesPorTravessa = Integer.parseInt(args[1]);
        travessaCount = mPorcoesPorTravessa;
        

        /*
        Instancia da classe Peterson, onde estão implementados os métodos lock e unlock
        para a questão 2, na qual substitui-se os métodos acquire e release do Mutex com os métodos
        implementados na classe.
        */
        peterson = new Peterson(nCanibais);    
    

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
        Thread threadCozinheiro = new Thread(new cozinheiro(controllerComida, blockCozinheiro, blockCanibal));
        threadCozinheiro.start();

        /*
        Instanciação e iniciação de cada canibal, com a quantidade indicada na execução.
        */
        for (int i = 0; i < nCanibais; i++) {
            Thread threadCanibal = new Thread(new canibal(i,controllerComida, blockCozinheiro, blockCanibal));
            threadCanibal.start();
        }
    }

    static class cozinheiro implements Runnable {
        Semaphore mutex;
        Semaphore controllerComida;
        Semaphore blockCozinheiro;
        Semaphore blockCanibal;

        public cozinheiro(Semaphore controllerComida, Semaphore blockCanibal, Semaphore blockCozinheiro) {
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
                    blockCozinheiro.acquire();
                    travessaCount = mPorcoesPorTravessa;
                    System.out.println("Cozinheiro acordado!! Enchendo a travessa");
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

        public canibal(int id, Semaphore controllerComida, Semaphore blockCanibal, Semaphore blockCozinheiro) {
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
                    peterson.lock(tid);
                    if (travessaCount == 0) {
                            System.out.println("Canibal " + tid + " acordando o cozinheiro.");
                            blockCozinheiro.release();
                            blockCanibal.acquire();                            
                    }
                    travessaCount--;
                    System.out.println("Canibal " + tid + " comendo a porcao. Restam " + travessaCount + " na travessa.");
                    Thread.sleep(500);
                    peterson.unlock(tid);
                }catch(InterruptedException e){}
            }
        }
    }

    
}