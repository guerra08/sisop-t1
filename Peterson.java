
/**
 * Classe Peterson generalizada para N processos desenvolvida para substituição do Mutex na questão 
 * do trabalho 1 da disciplina de Sistemas Operacionais.
 * @author Bruno Guerra e Eduardo Lessa
*/


public class Peterson {
    private static volatile int[] flag;
    private static volatile int[] lastExecuted;
    
    private int nCanibais;


    /**
     * 
     * @param nCanibais - Indica a quantidade de canibais existentes.
     * 
     * Classe construtora para a inicialização das variáveis globais.
     */
    Peterson(int nCanibais) {
        this.nCanibais = nCanibais;
        flag = new int[nCanibais];
        lastExecuted = new int[nCanibais];
    }


    /**
     * 
     * @param id Indica o id da Thread do canibal.
     * 
     * Esse método é o equivalente ao acquire, da classe Semaphore. 
     */
    void lock(int id) {
        for (int j = 1; j < nCanibais; j++) {
            flag[id] = j;
            lastExecuted[j] = id;

            for (int k = 0; k < nCanibais; k++) {
                while (flag[k] >= j && lastExecuted[j] == id && id != k);
            }
        }
    }


    /**
     * 
     * @param id Indica o id da Thread do canibal. 
     * 
     * Esse método é o equivalente ao release, da classe Semaphore. 
     */
    void unlock(int id){
        flag[id] = 0;
    }

}
