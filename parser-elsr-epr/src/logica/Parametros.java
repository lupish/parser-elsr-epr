package logica;

public class Parametros {
    /*****************
        Contiene los parametros del modelo

        Atributos de la clase:
        - ...

    *****************/

    /*** ATRIBUTOS ***/
    // params
    public int nT, nL, M, U_0, STU, tamListaTabu, cantItMax, version, esMasivo, tasas, huL_pond, saltos, saltosMAX, v3_v2it, all_configs, saltos_swap_odd, init_config;
    public int[] cm, cr, Km, Kr, hs, cvs, cvu;
    public int[][] D, U, Kv, hsL;
    public float[][] huL;
    public float alpha, beta, tasas_valor;
    public float[] hu;

    // variables de decision
    public int[] xm, xr, Is, Iu;
    public int[][] xs, IsL, IuL;
    public boolean[] ym, yr;
    public boolean[][] yv;

    // otros
    public String datArchivo, outPath, nomArchivo, pathArchivo, all_configs_path;

    /*** CONSTRUCTOR ***/
    public Parametros(int nL, int nT) {
        D = new int[nL][nT];
        U = new int[nL][nT];
        Kv = new int[nL][nT];
        hsL = new int[nL][nT];
        huL = new float[nL][nT];

        cm = new int[nT];
        cr = new int[nT];
        Km = new int[nT];
        Kr = new int[nT];
        hs = new int[nT];
        hu = new float[nT];
        cvs = new int[nT];
        cvu = new int[nT];

    };

    public Parametros(
        int nT, int nL, int M, int U_0, int STU, int tamListaTabu,
        int[] cm, int[] cr, int[] Km, int[] Kr, int[] hs, float[] hu, int[] cvs, int[] cvu,
        int[][] D, int[][] U, int[][] Kv, int[][] hsL, float[][] huL,
        float alpha, float beta ) {

        // int
        this.nT = nT;
        this.nL = nL;
        this.M = M;
        this.U_0 = U_0;
        this.STU= STU;
        this.tamListaTabu = tamListaTabu;
        
        // int[]
        this.cm = cm;
        this.cr = cr;
        this.Km = Km;
        this.Kr = Kr;
        this.hs = hs;
        this.hu = hu;
        this.cvs = cvs;
        this.cvu = cvu;

        // int[][]
        this.D = D;
        this.U = U;
        this.Kv = Kv;
        this.hsL = hsL;
        this.huL = huL;

        // float
        this.alpha = alpha;
        this.beta = beta;

    }

    public void setCantPeriodos(int nT) {
        this.nT = nT;
    }

    public void setCantClientes(int nL) {
        this.nL = nL;
    }

    public void setTamListaTabu(int tamListaTabu) {
        this.tamListaTabu = tamListaTabu;
    }

    public void setDatArchivo(String pathArchivo, String nomArchivo) {
        this.datArchivo = pathArchivo + nomArchivo;
    }

    public void setM(int M) {
        this.M = M;
    }

    public void setU0(int U0) {
        this.U_0 = U0;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }

    public void setSTU(int STU) {
        this.STU = STU;
    }

    public void setCM(int[] cm) {
        this.cm = cm;
    }

    public void setCR(int[] cr) {
        this.cr = cr;
    }
    
    public void setKM(int[] Km) {
        this.Km = Km;
    }

    public void setKR(int[] Kr) {
        this.Kr = Kr;
    }
    
    public void setHS(int[] hs) {
        this.hs = hs;
    }

    public void setHU(float[] hu) {
        this.hu = hu;
    }

    public void setCVS(int[] cvs) {
        this.cvs = cvs;
    }

    public void setCVU(int[] cvu) {
        this.cvu = cvu;
    }

    public void setDFila(int arrFila, int[] dFila) {
        this.D[arrFila] = dFila;
    }

    public void setUFila(int arrFila, int[] dFila) {
        this.U[arrFila] = dFila;
    }

    public void setKvFila(int arrFila, int[] dFila) {
        this.Kv[arrFila] = dFila;
    }
    
    public void setHsLFila(int arrFila, int[] dFila) {
        this.hsL[arrFila] = dFila;
    }

    public void setHuLFila(int arrFila, float[] dFila) {
        this.huL[arrFila] = dFila;
    }

    /*** IMPRIMIR ***/
    public void printInt(int valor, String strValor) {
        System.out.print(strValor + " = ");
        System.out.println(valor);
    }

    public void printFloat(float valor, String strValor) {
        System.out.print(strValor + " = ");
        System.out.println(valor);
    }

    public void printArray(int[] valor, String strValor) {
        if (strValor == "\t") {
            System.out.print(strValor);
        } else {
            System.out.print(strValor + " = ");
        }
        for (int i = 0; i < valor.length; i++) {
            System.out.print(valor[i]);
            System.out.print(" ");
        }
        System.out.println("");
    }

    public void printArrayD(float[] valor, String strValor) {
        if (strValor == "\t") {
            System.out.print(strValor);
        } else {
            System.out.print(strValor + " = ");
        }
        for (int i = 0; i < valor.length; i++) {
            System.out.print(valor[i]);
            System.out.print(" ");
        }
        System.out.println("");
    }

    public void printMatriz(int[][] valor, String strValor) {
        System.out.println(strValor + " = ");
        for (int i = 0; i < valor.length; i++) {
            printArray(valor[i], "\t");
        }
    }

    public void printMatrizFloat(float[][] valor, String strValor) {
        System.out.println(strValor + " = ");
        for (int i = 0; i < valor.length; i++) {
            printArrayD(valor[i], "\t");
        }
    }


}
