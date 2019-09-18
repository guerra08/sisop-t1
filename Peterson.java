
public class Peterson {
    static volatile int[] flag;
    static volatile int[] lastExecuted;
    
    private int nCanibais;


    Peterson(int nCanibais) {
        this.nCanibais = nCanibais;
        flag = new int[nCanibais];
        lastExecuted = new int[nCanibais];
    }

    void lock(int i) {
        for (int j = 1; j < nCanibais; j++) {
            flag[i] = j;
            lastExecuted[j] = i;

            for (int k = 0; k < nCanibais; k++) {
                while (flag[k] >= j && lastExecuted[j] == i && i != k);
            }
        }
    }

    void unlock(int i){
        flag[i] = 0;
    }

}
