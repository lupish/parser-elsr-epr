package logica;

import java.util.Arrays;

public class WagnerWhitin {
    /*****************
     Manejador de Wagner-Whitin

     Atributos de la clase:
     - n: cantidad de periodos
     - kM: costo de setup en manufacturacion
     - kTechoM: costo ecologico de setup en manufacturacion
     - hS: costo unitario de stock de productos finales
     - hTechoS: costo ecologico unitario de stock de productos finales
     - d: demanda por periodo
     - optimo: costo de la solucion hallada
     - xM: cantidad a remanufacturar
     - iS: stock de productos finales
     - niveloger: indica si se debe loguear info de ejecucion
     *****************/

    /*** ATRIBUTOS ***/
    Parametros params;
    
    private int n, l;
    private int [] d, xM, kM, hS;
    private int [] iS;
    private String nivelLogger;
    private float optimo;

    /*** CONSTRUCTOR ***/
    public WagnerWhitin(Parametros params) {
        this.params = params;

    }

    public WagnerWhitin(Parametros params, int l) {
        this.params = params;
        this.l = l;

        this.n = params.nT;
        xM = new int[n];
        this.iS = new int[n];
        Arrays.fill(this.iS, 0);

        this.nivelLogger = "PROD";

    }

    public WagnerWhitin(int cantPeriodos ,int cantClientes,int [] econSetupManu
                        ,int [] econStockProdFinal, int [][] demandaL, int [] iS
                        ,int [] econManu, int[][] xs, int [] xr
                        ,String nivelLogger) {
        this.n = cantPeriodos;
        this.kM = econSetupManu;
        this.hS = econStockProdFinal;
        // this.cm = econManu;

        // System.out.println("****** WagnerWhitin ******");
        d = new int[cantPeriodos];
        for (int i = 0 ; i < cantPeriodos; i++) {
            d[i] = 0;
            for (int l = 0 ; l < cantClientes; l++) {
                //d[i] += demandaL[l][i];
                d[i] += xs[l][i];
            }
            if (d[i] > xr[i]) {
                d[i] -= xr[i];
            } else {
                d[i] = 0;
            }
        }

        this.iS = iS;
        this.nivelLogger = nivelLogger;

        xM = new int[n];

    }

    public WagnerWhitin(int cantPeriodos ,int cantClientes,int [] econSetupManu
                        ,int [] econStockProdFinal, int [] demandaL
                        ,int [] econManu) {
        this.n = cantPeriodos;
        this.kM = econSetupManu;
        this.hS = econStockProdFinal;
        // this.cm = econManu;

        d = demandaL;

        this.iS = new int[n];
        Arrays.fill(this.iS, 0);
        this.nivelLogger = "PROD";

        xM = new int[n];

    }

    /*** METODOS ***/
    /////// OBTENTER ///////
    public float obtenerOptimo() {
        return optimo;
    }
    public int[] obtenerXM() {
        return xM;
    }

    /////// CALCULOS ///////
    private void getOrderAmount(int[] order_periods, int[] demand, int n) {
        int cant = 0;
        for(int i = 0; i < n; i ++) {
            if (order_periods[i] != -1) {
                cant++;
            }
            xM[i] = 0;
        }
        if (nivelLogger.equals("TEST")) {
            System.out.print("\niS:");
            for (int s: iS) {
                System.out.print(s + " ");
            }
            System.out.println("\ncant = " + cant);
        }

        for(int i = 0; i < cant; i ++) {
            int num = order_periods[i];
            if ((i + 1) < cant) {
                for(int j = num; j < order_periods[i + 1]; j ++) {
                    if (demand[j] < iS[j]) {
                        iS[j+1] = iS[j] + iS[j+1];
                    } else {
                        xM[num] += (demand[j] - iS[j]);
                    }
                }
            } else {
                for(int j = num; j < n; j ++) {
                    if (demand[j] < iS[j]) {
                        if (j == (n-1)) {
                            // no se hace nada
                        } else {
                            iS[j+1] = iS[j] + iS[j+1];
                        }
                    } else {
                        xM[num] += (demand[j] - iS[j]);
                    }
                }
            }
        }
    }

    private static int [] findOrderPeriods(int[][][] orderMatrix, int n) {
        int[] orderPeriods = new int[n];

        for(int j = 0; j < n; j ++) {
            orderPeriods[j] = -1;
        }

        orderPeriods[0] = 0;
        int next = orderMatrix[0][n - 1][1];
        orderPeriods[1] = next;
        int aux = orderMatrix[next][n - 1][1];
        int i = 2;
        while (aux != 0) {
            orderPeriods[i] = orderMatrix[next][n - 1][1];
            next = orderMatrix[next][n - 1][1];
            i++;
            aux = orderMatrix[next][n - 1][1];
        }

        return orderPeriods;
    }

    public void wagnerWhitin_ret() {
        // cost[o][f] costo de producir en el periodo o y stocker hasta periodo f
        // sin stock inicial

        float [][] cost = new float[n][n];

        for(int i = 0; i < n; i ++) {
            for(int j = i; j < n; j ++) {
                cost[i][j] += params.Kv[l][i];
                
                for(int k = (i+1); k < (j+1); k ++) {
                    for(int w = (i); w < k; w++) {
                        cost[i][j] += params.hsL[l][w] * params.D[l][k];
                    }

                    for(int w = (i+1); w < (k+1); w++) {
                        cost[i][j] += params.huL[l][k] * params.U[l][w];
                    }
                }
            }
        }

        float[][] F = new float[n][n];
        int[][][] orderM = new int[n][n][2];
        for(int i = 0; i < n; i ++) {
            for(int j = 0; j < n; j ++) {
                orderM[i][j][0] = j;
            }
        }

        for(int j = 0; j < n; j ++) {
            for(int i = 0; i < (j+1); i ++) {
                F[j - i][j] = cost[j - i][j];
                for(int k = (j-i); k < j; k ++) {
                    if (cost[j - i][k] + F[k + 1][j] < F[j - i][j]) {
                        F[j - i][j] = cost[j - i][k] + F[k + 1][j];
                        orderM[j - i][j][0] = j-i;
                        orderM[j - i][j][1] = k+1;
                    }
                }
            }
        }

        int lenF = F.length;
        int largoD = params.D[l].length;
        optimo = F[0][lenF - 1];

        int[] orderPeriods = findOrderPeriods(orderM, largoD);

        getOrderAmount(orderPeriods, params.D[l], n);
    }
    
    public void wagnerWhitin() {
        // cost[o][f] costo de producir en el periodo o y stocker hasta periodo f

        float [][] cost = new float[n][n];

        int demandas;
        int stocks;
        int [] stockActualizado;
        int stockValor = 0;

        float costoExterno = 0;
        for (int i = 0; i < n; i++) {
            if (iS[i] > 0 && d[i] == 0) {
                costoExterno += hS[i] * iS[i];

                int stockAntiguo = iS[i];
                iS[i] = 0;
                boolean seguir = true;
                for(int j = i + 1; j < n && seguir; j++) {
                    if (d[j] > 0) {
                        if ((stockAntiguo - d[j]) > 0) {
                            stockAntiguo = stockAntiguo - d[j];

                            costoExterno += hS[j] * stockAntiguo;

                            d[j] = 0;
                        } else {
                            d[j] = d[j] - stockAntiguo;
                            seguir = false;
                        }
                    } else {
                        stockAntiguo += iS[j];
                        iS[j] = 0;
                        costoExterno += hS[j] * stockAntiguo;
                    }
                }
            }
        }

        for(int i = 0; i < n; i ++) {
            stockActualizado = new int[n];
            stockActualizado[i] = iS[i];

            for(int j = i; j < n; j ++) {
                demandas = 0;
                stocks = 0;

                for(int a = i; a <= j; a ++) {
                    demandas += d[a];
                    stocks += iS[a];
                }

                if (demandas > stocks) {
                    cost[i][j] += kM[i];
                } else {
                    // no hace nada
                }

                // Stock del periodo i
                cost[i][j] += hS[i] * stockActualizado[i];

                for(int k = (i+1); k < (j+1); k ++) {
                    if (d[k] <= (stockActualizado[k-1] + iS[k])) {
                        stockActualizado[k] = iS[k] + stockActualizado[k-1] - d[k];
                        stockValor = stockActualizado[k];
                    } else {
                        stockActualizado[k] = 0;
                        stockValor = d[k];
                    }

                    for(int w = (i); w < k; w++) {
                        cost[i][j] += hS[w] * stockValor;
                    }

                    cost[i][j] += hS[k] * stockActualizado[k];
                }
            }
        }

        float[][] F = new float[n][n];
        int[][][] orderM = new int[n][n][2];
        for(int i = 0; i < n; i ++) {
            for(int j = 0; j < n; j ++) {
                orderM[i][j][0] = j;
            }
        }

        for(int j = 0; j < n; j ++) {
            for(int i = 0; i < (j+1); i ++) {
                F[j - i][j] = cost[j - i][j];
                for(int k = (j-i); k < j; k ++) {
                    if (cost[j - i][k] + F[k + 1][j] < F[j - i][j]) {
                        F[j - i][j] = cost[j - i][k] + F[k + 1][j];
                        orderM[j - i][j][0] = j-i;
                        orderM[j - i][j][1] = k+1;
                    }
                }
            }
        }

        int lenF = F.length;
        int largoD = d.length;
        optimo = F[0][lenF - 1] + costoExterno;

        int[] orderPeriods = findOrderPeriods(orderM, largoD);

        getOrderAmount(orderPeriods, d, n);

    }

    public void imprimirWW() {
        if (nivelLogger.equals("TEST")) {
            System.out.println("\n********* Wagner & Whitin *********");
            System.out.print("\nK: ");
            for(int i = 0; i < n; i ++) {
                System.out.print(kM[i] + " ");
            }
            System.out.print("\nh: ");
            for(int i = 0; i < n; i ++) {
                System.out.print(hS[i] + " ");
            }
            System.out.print("\nd: ");
            for(int i = 0; i < n; i ++) {
                System.out.print(d[i] + " ");
            }

            System.out.println("\nORDER");
            for(int i = 0; i < xM.length; i++) {
                System.out.println("xM[" + (i+1) + "] = " + xM[i] + " - d = " + d[i]);
            }
            System.out.println("\nOPTIMO = " + optimo);
            System.out.println("\n***********************************");
        }
    }

}
