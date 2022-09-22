package logica;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class TabuSearch {
    /*****************
        Contiene la logica de cada iteracion del Tabu Search

        Atributos de la clase:

    *****************/

    public enum ModoPrint {
        VARIABLES_DECISION
        ,GENERAL
        ,VD__POR_PLAN
        ,VD__POR_TIPO
    };

    /*** ATRIBUTOS ***/
    CircularFifoQueue<String> listaTabu;
    int[] xr, xm, Iu, Is;
    int[][] xs, xu, IuL, IsL;
    Parametros params;
    int cantSolAnalizadas, cantSolYaVisitadas, cantSolNoFactibles;

    // costos
    private float costoM, costoR; // costos de produccion
    private float costoStockU, costoStockUL, costoStockSL; // costos de stock
    private float costoTransportarS, costoTransportarU, costoViaje; // costos de viaje
    

    /*** CONSTRUCTOR ***/
    public TabuSearch(Parametros params, int version) {

        // CircularFifoQueue<String>
        listaTabu = new CircularFifoQueue<String>(params.tamListaTabu); // VER EL OBJETIVO QUE LE PASO!
        this.params = params;

        this.cantSolAnalizadas = 0;
        this.cantSolYaVisitadas = 0;
        this.cantSolNoFactibles = 0;

        if (version >= 2) {
            this.xs = new int[params.nL][params.nT];
            this.xu = new int[params.nL][params.nT];
            // this.IuL = new int[params.nL][params.nT];
        }
    }

    public TabuSearch() {

    }

    /*** METODOS ***/

    public void calcularXS(String[] v) {
        int dL, j;

        costoViaje = 0;
        costoTransportarS = 0;
        costoStockSL = 0;

        for (int l = 0; l < params.nL; l ++) {
            for (int i = 0; i < params.nT; i ++) {
                if (v[l].charAt(i) == '1') {
                    dL = params.D[l][i];
                    j = i + 1;
                    while (j < params.nT && v[l].charAt(j) == '0') {
                        dL += params.D[l][j];

                        j ++;
                    }

                    // Costo fijo por viajar
                    costoViaje += params.Kv[l][i];
                } else {
                    dL = 0;
                }
                xs[l][i] = dL;

                // Costo variable por transportar prod finales
                costoTransportarS += xs[l][i] * params.cvs[l];

                // Stock
                if (i == 0) {
                    IsL[l][i] = xs[l][i] - params.D[l][i];
                } else {
                    IsL[l][i] = IsL[l][i-1] + xs[l][i] - params.D[l][i];
                }

                // Costo variable por stock de prod finales
                costoStockSL += IsL[l][i] * params.hsL[l][i];
            }
        }
    }

    public void calcularXU(String[] v, Solucion s) {
        int uL, i, stockL;

        costoTransportarU = 0;
        costoStockUL = 0;

        for (int l = 0; l < params.nL; l ++) {
            for (int j = (params.nT - 1); j >= 0; j --) {
                if (v[l].charAt(j) == '1') {
                    uL = params.U[l][j];
                    i = j - 1;
                    while (i >= 0 && v[l].charAt(i) == '0') {
                        uL += params.U[l][i];

                        i --;
                    }

                    if (xs[l][j] == 0) {
                        // Costo fijo por viajar
                        costoViaje += params.Kv[l][i];
                    }

                    s.IuL[l][j] = 0;
                } else {
                    uL = 0;

                    i = j;
                    stockL = 0;
                    while (i >= 0 && v[l].charAt(i) == '0') {
                        stockL += params.U[l][i];

                        i --;
                    }
                    s.IuL[l][j] = stockL;
                }
                xu[l][j] = uL;

                // Costo variable por transportar retornos
                costoTransportarU += xu[l][j] * params.cvu[l];

                // Stock
                /*if (j == 0) {
                    s.IuL[l][j] = params.U[l][j] - xu[l][j];
                } else {
                    s.IuL[l][j] = s.IuL[l][j-1] + params.U[l][j] - xu[l][j];

                    if (j == 4 && s.IuL[l][j] < 0) {
                        System.out.println("s.IuL[" + l +"][" + j + "] < 0");
                        System.out.println("s.IuL[l][j-1] = " + s.IuL[l][j-1]);
                        System.out.println("params.U[l][j] = " + params.U[l][j]);
                        System.out.println("xu[l][j] = " + xu[l][j]);
                    }
                }*/
                Iu[j] += xu[l][j];

                // Costo variable por stock de prod finales
                costoStockUL += s.IuL[l][j] * params.huL[l][j];
            }
        }

        // printMatriz(IuL, "sol_IsU");
    }

    public void calcularXU_v2(Solucion s) {
        int uL, i, stockL, costoViajeU;
        boolean condRecol;

        // System.out.println("*** calcularXU_v2 ***");
        // printMatriz(s.xs, "xs");

        costoTransportarU = 0;
        costoStockUL = 0;
        costoViajeU = 0;
        

        Iu = new int[params.nT];
        s.Iu = Iu;
        IuL = new int[params.nL][params.nT];
        s.IuL = IuL;

        for (int l = 0; l < params.nL; l ++) {
            // tasa de recoleccion 1: se debe recolectar en el penultimo periodo
            if (s.xs[l][params.nT-params.STU-1] == 0 && params.beta == 1) {
                costoViajeU += params.Kv[l][params.nT-params.STU-1];
            }

            for (int j = (params.nT - 1); j >= 0; j --) {
                if (s.xs[l][j] > 0 || (params.beta == 1 && j == (params.nT-params.STU-1))) {
                    uL = params.U[l][j];
                    i = j - 1;
                    condRecol = true;
                    if (i == (params.nT-params.STU-1) && params.beta == 1) {
                        condRecol = false;
                    }
                    while (i >= 0 && s.xs[l][i] == 0 && condRecol) {
                        uL += params.U[l][i];

                        i --;
                    }

                    s.IuL[l][j] = 0;
                } else {
                    uL = 0;

                    i = j;
                    stockL = 0;
                    condRecol = true;
                    while (i >= 0 && s.xs[l][i] == 0 && condRecol) {
                        stockL += params.U[l][i];

                        i --;
                        
                        if (i == (params.nT-params.STU-1) && params.beta == 1) {
                            condRecol = false;
                        }
                    }
                    // System.out.println("s.IuL[" + l + "][" + j + "] = " + stockL);
                    s.IuL[l][j] = stockL;
                }
                
                xu[l][j] = uL;

                // Costo variable por transportar retornos
                costoTransportarU += xu[l][j] * params.cvu[l];

                Iu[j] += xu[l][j];

                // Costo variable por stock de prod finales
                costoStockUL += s.IuL[l][j] * params.huL[l][j];
            }
        }
        
        s.costoTransportarU = costoTransportarU;
        s.costoStockUL = costoStockUL;
        s.costoViaje = costoViajeU;

        
        
        /*System.out.println("----> prints in calcularXU_v2");
        System.out.println("s.costoTransportarU = " + s.costoTransportarU);
        System.out.println("s.costoStockUL = " + s.costoStockUL);
        System.out.println("s.costoViaje = " + s.costoViaje);
        System.out.println("costoViajeU = " + costoViajeU);
        printMatriz(s.IuL, "sol_IuL");
        printArray(Iu, "sol_Iu");
        printMatriz(xu, "sol_xu");*/
        
        
    }

    public void calcularXU_v2_ret(Solucion s) {
        int uL, i, stockL, costoCliente; //, costoViajeU;
        float costoViajeU;
        boolean condRecol;

        // System.out.println("*** calcularXU_v2_ret ***");
        // printMatriz(s.xs, "xs");

        costoTransportarU = 0;
        costoViajeU = 0;

        Iu = new int[params.nT];
        s.Iu = Iu;
        IuL = new int[params.nL][params.nT];
        s.IuL = IuL;

        for (int l = 0; l < params.nL; l ++) {
            // System.out.println("------> CLIENTE l = " + l);
            // tasa de recoleccion 1: se debe recolectar en el penultimo periodo
            if (s.xs[l][params.nT-params.STU-1] == 0 && params.beta == 1) {
                costoViajeU += params.Kv[l][params.nT-params.STU-1];
            }
            // si costoViajeU es mayor a 0, hay q calcular el costo de stock cliente

            for (int j = (params.nT - 1); j >= 0; j --) {
                if (s.xs[l][j] > 0 || (params.beta == 1 && j == (params.nT-params.STU-1))) {
                    uL = params.U[l][j];
                    i = j - 1;
                    condRecol = true;
                    if (i == (params.nT-params.STU-1) && params.beta == 1) {
                        condRecol = false;
                    }
                    while (i >= 0 && s.xs[l][i] == 0 && condRecol) {
                        uL += params.U[l][i];

                        i --;
                    }

                    if (s.xs[l][j] == 0 && params.beta == 1 && j == (params.nT-params.STU-1)) {
                        i++;
                        
                        /*
                        System.out.println("se viaje en el ultimo retorno, se quitan los stockeos");
                        System.out.println("\ti = " + i);
                        System.out.println("\tj = " + j);
                        System.out.println("\tcostoViajeU antes = " + costoViajeU);
                        */
                        for(int w = i; w < (j+1); w++) {
                            // se viaje en el ultimo retorno, se quitan los stockeos dado q voy en el antepenultimo
                            // System.out.println("\t\tw = " + w);
                            costoViajeU -= (params.huL[l][j] * params.U[l][w]);
                            if (s.xs[l][j+1] == 0) {
                                costoViajeU -= (params.huL[l][j+1] * params.U[l][w]);
                            }
                        }
                        // System.out.println("\tcostoViajeU desp = " + costoViajeU);
                    }

                    s.IuL[l][j] = 0;
                } else {
                    uL = 0;

                    i = j;
                    stockL = 0;
                    condRecol = true;
                    while (i >= 0 && s.xs[l][i] == 0 && condRecol) {
                        stockL += params.U[l][i];

                        i --;

                        if (i == (params.nT-params.STU-1) && params.beta == 1) {
                            condRecol = false;
                        }
                    }
                    // System.out.println("s.IuL[" + l + "][" + j + "] = " + stockL);
                    s.IuL[l][j] = stockL;
                }
                
                xu[l][j] = uL;

                // Costo variable por transportar retornos
                // costoTransportarU += xu[l][j] * params.cvu[l];

                Iu[j] += xu[l][j];

                // Costo variable por stock de prod finales
                // costoStockUL += s.IuL[l][j] * params.huL[l][j];
                // System.out.println("CLIENTE l = " + l + "<------");
            }
        }

        s.costoTransportarU = costoTransportarU;
        s.costoStockUL = costoStockUL;
        s.costoViaje = costoViajeU;

        
        /*
        System.out.println("----> prints in calcularXU_v2_ret");
        System.out.println("s.costoTransportarU = " + s.costoTransportarU);
        System.out.println("s.costoStockUL = " + s.costoStockUL);
        printMatriz(s.IuL, "sol_IuL");
        printArray(Iu, "sol_Iu");
        printMatriz(xu, "sol_xu");
        */
        
        
    }

    public void calcularXR(String r, Solucion s) {
        int stock, prodFinalesLlevar, retornosL;

        costoR = 0;
        costoStockU = 0;

        /*
        System.out.println("*** calcularXR ***");
        System.out.println("--> r = " + r);
        printMatriz(s.xs, "s.xs");
        printMatriz(s.xu, "s.xu");
        */

        /*for (int t = 0; t < params.nT; t++) {
            System.out.print("Iu[t] = ");
            System.out.println(Iu[t]); 
        }*/

        for (int i = 0; i < params.nT; i++) {

            /*** Calcular cantidad a remanufacturar ***/
            if (r.charAt(i) == '1') {
                if (i > (params.STU-1)) {
                    stock = s.Iu[i - params.STU];

                    // System.out.print("Iu[i - params.STU] = ");
                    // System.out.println(Iu[i - params.STU]); 
                } else {
                    stock = params.U_0;
                    for (int k = 0; k < i; k++) {
                        stock -= xr[k];
                    }
                }

                // xr[i] = Math.min(stock, demandas);
                xr[i] = stock;

                // Costo fijo por remanufacturar
                costoR += params.Kr[i];
            } else {
                xr[i] = 0;
            }

            // Costo variable por remanufacturar
            costoR += xr[i] * params.cr[i];

            /*** Calcular stock de retornos ***/
            retornosL = 0;
            for (int l = 0; l < params.nL; l ++) {
                retornosL += s.xu[l][i];
            }
            if (i == 0) {
                // s.Iu[i] = s.Iu[i] + params.U_0 - xr[i] <-- antes;
                s.Iu[i] = retornosL + params.U_0 - xr[i];
            } else {
                // s.Iu[i] = s.Iu[i] + Iu[i-1] - xr[i];
                s.Iu[i] = retornosL + Iu[i-1] - xr[i];
            }
            //System.out.print("update Iu[i] = ");
            //System.out.println(Iu[i]); 

            // Costo stock de retornos
            costoStockU += s.Iu[i] * params.hu[i];
            
            /*** Calcular stock de productos finales ***/
            prodFinalesLlevar = 0;
            for (int l = 0; l < params.nL; l++) {
                prodFinalesLlevar += s.xs[l][i]; // sin s
            }
            // Input de WW
            if (xr[i] > prodFinalesLlevar) {
                s.Is[i] = xr[i] - prodFinalesLlevar;
            } else {
                s.Is[i] = 0;
            }

            // Costo stock de prod finales se calcula luego de WW
        }

        // printArray(s.Iu, "s.Iu");
        // System.out.println("costoStockU = " + costoStockU);
    }

    public void calcularXM(String r, Solucion s) {
        WagnerWhitin ww = new WagnerWhitin(params.nT,params.nL,params.Km
                                            ,params.hs,params.D,Is
                                            ,params.cm, s.xs, s.xr
                                            ,"PROD");
        ww.wagnerWhitin();

        xm = ww.obtenerXM();
        costoM = ww.obtenerOptimo();
    }

    public float calcularXSWW(int cliente) {
        WagnerWhitin ww = new WagnerWhitin(params.nT,params.nL,params.Kv[cliente]
                                            ,params.hsL[cliente],params.D[cliente]
                                            ,params.cm);
        ww.wagnerWhitin();

        xs[cliente] = ww.obtenerXM();
        return(ww.obtenerOptimo());
    }

    public float calcularXSWW_ret(int cliente) {
        WagnerWhitin ww = new WagnerWhitin(params, cliente);
        ww.wagnerWhitin_ret();

        xs[cliente] = ww.obtenerXM();
        return(ww.obtenerOptimo());
    }

    public Solucion generarSolucion(String[] v, String r) {
        Solucion s = new Solucion();
        s.costo = 0;
        s.strTira = convertirTuplaStr(v, r);
        s.v = v;
        s.r = r;
        
        /*** Inicializar variables de decision ***/
        // Produccion
        xs = new int[params.nL][params.nT];
        xu = new int[params.nL][params.nT];
        xr = new int[params.nT];
        xm = new int[params.nT];
        // Stock
        Iu = new int[params.nT];
        s.Iu = Iu;
        Is = new int[params.nT];
        s.Is = Is;
        IuL = new int[params.nL][params.nT];
        s.IuL = IuL;
        IsL = new int[params.nL][params.nT];
        
        /*** Plan de entrega ***/
        calcularXS(v);
        calcularXU(v, s);
        s.xs = xs;
        s.xu = xu;
        s.costoViaje = costoViaje;
        s.costoTransportarS = costoTransportarS;
        s.costoTransportarU = costoTransportarU;
        s.costoStockSL = costoStockSL;
        s.costoStockUL = costoStockUL;
        s.costoEntrega = costoViaje + costoTransportarS + costoTransportarU + costoStockSL + costoStockUL;
        s.costo = s.costoEntrega; // += s.costoViaje + s.costoTransportarS + s.costoTransportarU + s.costoStockSL + s.costoStockUL;

        /*** Plan de remanufacturacion ***/
        calcularXR(r, s);
        s.xr = xr;
        // s.Iu = Iu;
        s.costoR = costoR;
        s.costoStockU = costoStockU;
        s.costoRemanu = costoR + costoStockU;
        s.costo += s.costoRemanu; //s.costoR + s.costoStockU;

        /*** Plan de manufacturacion ***/
        calcularXM(r, s);
        s.xm = xm;
        s.Is = Is;
        s.costoM = costoM;
        // s.costoStockS = costoStockS;
        s.costoManu = s.costoM;
        s.costo += s.costoManu; //s.costoM; // + s.costoStockS;

        return s;
    }

    public Solucion generarSolucion(String r, Solucion solInicial) {
        Solucion s = new Solucion();
        s.costo = -1;
        s.r = r;
        s.strTira = r;
        
        /*** Inicializar variables de decision ***/
        // Produccion
        xr = new int[params.nT];
        xm = new int[params.nT];
        // Stock
        Iu = new int[params.nT];
        s.Iu = Iu;
        Is = new int[params.nT];
        s.Is = Is;
        IuL = new int[params.nL][params.nT];
        s.IuL = IuL;
        IsL = new int[params.nL][params.nT];

        // VEEER
        // Is = new int[params.nT];
        // s.Is = Is;
        // IsL = new int[params.nL][params.nT];

        
        /*** Plan de entrega ***/
        s.xs = solInicial.xs;
        s.xu = solInicial.xu;
        s.costoTransportarU = solInicial.costoTransportarU;
        s.costoStockUL = solInicial.costoStockUL;
        s.costoEntrega = solInicial.costoEntrega;
        s.costo = solInicial.costoEntrega;

        /*** Plan de remanufacturacion ***/
        calcularXR(r, s);
        // printArray(s.Iu, "generarSolucion. s.Iu");
        s.xr = xr;
        s.costoR = costoR;
        s.costoStockU = costoStockU;
        s.costoRemanu = costoR + costoStockU;
        s.costo += s.costoRemanu;

        /*** Plan de manufacturacion ***/
        calcularXM(r, s);
        s.xm = xm;
        s.Is = Is;
        s.costoM = costoM;
        s.costoManu = s.costoM;
        s.costo += s.costoManu;

        return s;
    }

    public String convertirTuplaStr(String[] v, String r) {
        String strTupla = "";
        for (String i:v) {
            strTupla += i;
        }
        strTupla += r;

        return strTupla;
    }
    
    public void guardarListaTabu(String[] v, String r){
        // transformo la tupla en str
        String strTupla = convertirTuplaStr(v, r);
        // System.out.println("strTira = " + strTupla);

        // guardo la tupla si no existe
        if (!listaTabu.contains(strTupla)) {
            listaTabu.add(strTupla);
        }

    }

    public String[] copiaLimpiaV(String[] v, int swapF, int swapC) {
        /*** swapeo la tupla de visitas en la posicion ['swapF', 'swapC'] ***/

        String[] vLimpio = new String[params.nL];
        for(int f = 0; f < params.nL; f ++) {
            vLimpio[f] = "";
            for(int c = 0; c < params.nT; c ++) {
                if (f == swapF && c == swapC) {
                    if (v[f].charAt(c) == '0') {
                        vLimpio[f] += '1';
                    } else {
                        vLimpio[f] += '0';
                    }
                } else {
                    vLimpio[f] += v[f].charAt(c);
                }
            }
        }

        return vLimpio;
    }

    public String copiaLimpiaR(String r, int swap) {
        /*** swapeo la tupla de remanufacturación en la posicion 'swap' ***/
        String rLimpio = "";
        Character periodo_swap;
        if (r.charAt(swap) == '0') {
            periodo_swap = '1';
        } else {
            periodo_swap = '0';
        }

        if (swap == 0) {
            rLimpio = periodo_swap + r.substring(swap + 1, r.length());   
        } else {
            rLimpio = r.substring(0, swap) + periodo_swap + r.substring(swap + 1, r.length());
        }

        return rLimpio;
    }

    public ArrayList<Object[]> hallarVecindario(String[] v, String r) {
        ArrayList<Object[]> vecinas = new ArrayList<>();

        String[] vLimpio;
        String rLimpio;

        // no swapeo primer periodo para cumplir la demanda
        // no swapeo ultimo periodo para cumplir tasa de recoleccion, generalizo beta 1
        for (int cV = 1; cV < (params.nT-1); cV++) { // itero por periodos
            for (int fV = 0; fV < params.nL; fV ++) { // itero por cliente
                vLimpio = copiaLimpiaV(v, fV, cV); // swap v[fV, cV]
                
                for (int cR = 0; cR < params.nT; cR++) { // itero por periodos
                    rLimpio = copiaLimpiaR(r, cR); // swap r[cR]
                    Object[] tupla = {vLimpio, rLimpio};
                    vecinas.add(tupla);
                }
            }
        }


        return vecinas;
    }

    public List hallarVecindario(String r) {
        List<String> vecinas = new ArrayList<String>();

        String rLimpio;

        for (int cR = 0; cR < params.nT; cR++) { // itero por periodos
            rLimpio = copiaLimpiaR(r, cR); // swap r[cR]
            
            vecinas.add(rLimpio);
        }


        return vecinas;
    }

    public Solucion TS(String[] v, String r) {
        String[] vActual;
        String rActual;
        String strTupla;

        Solucion solVecina;
        Solucion solVecinaOptima = new Solucion();
        solVecinaOptima.costo = -1;

        // genero la solucion
        Solucion solActual = generarSolucion(v, r);
        // VEEEEEEEEER
        // solVecinaOptima = solActual;
        // System.out.println("TS. solActual = " + solActual.costo);

        // guardo en la lista tabu
        guardarListaTabu(v, r);

        // exploro el vecindario
        ArrayList<Object[]> vecinas = hallarVecindario(v, r);
        for (int i = 0; i < vecinas.size(); i++) {
            vActual = (String[]) vecinas.get(i)[0];
            rActual = (String) vecinas.get(i)[1];
            strTupla = convertirTuplaStr(vActual, rActual);

            if (!listaTabu.contains(strTupla)) {
                solVecina = generarSolucion(vActual, rActual);

                if (validarFactibilidad(solVecina)) {
                    // solucion factible
                    if (solVecinaOptima.costo == -1 || solVecinaOptima.costo > solVecina.costo) {
                        solVecinaOptima = solVecina;
                    }
                } else {
                    // System.out.println("Solucion no factible");
                }

                /*if (solVecinaOptima.costo == -1 || solVecinaOptima.costo > solVecina.costo) {
                    solVecinaOptima = solVecina;
                }*/
            }
        }

        if (solVecinaOptima.strTira == null) {
            solVecinaOptima = solActual;
        } else {
            listaTabu.add(solVecinaOptima.strTira);
        }
        return solVecinaOptima;
    }

    public Solucion TS(String r, Solucion solInicial) {
        String rActual;

        // System.out.println("--- TS r = " + r + "---");

        Solucion solVecina;
        Solucion solVecinaOptima = new Solucion();
        solVecinaOptima.costo = solInicial.costoEntrega;
        solVecinaOptima.costoEntrega = solInicial.costoEntrega;

        // genero la solucion
        Solucion solActual = generarSolucion(r, solInicial);
        // printArray(solActual.Iu, "TS.1. s.Iu");

        // guardo en la lista tabu
        if (!listaTabu.contains(r)) {
            listaTabu.add(r);
        }

        // exploro el vecindario
        List<String> vecinas = hallarVecindario(r);
        for (int i = 0; i < vecinas.size(); i++) {
            rActual = vecinas.get(i);
            // System.out.println("rActual = " + rActual);

            if (!listaTabu.contains(rActual)) {
                solVecina = generarSolucion(rActual, solActual);
                // System.out.println("solVecina.costo = " + solVecina.costo + "- r = " + rActual);
                
                /*if (solVecinaOptima.costo == solInicial.costoEntrega || solVecinaOptima.costo > solVecina.costo) {
                    solVecinaOptima = solVecina;
                }*/

                if (validarFactibilidad(solVecina)) {
                    // solucion factible
                    if (solVecinaOptima.costo == solInicial.costoEntrega || solVecinaOptima.costo > solVecina.costo) {
                        solVecinaOptima = solVecina;
                    }
                } else {
                    // System.out.println("Solucion no factible");
                }
            }
        }

        // System.out.println("solVecinaOptima.strTira = " + solVecinaOptima.strTira + " - r = " + solVecinaOptima.r);
        if (solVecinaOptima.strTira == null) {
            solVecinaOptima = solActual;
        } else {
            listaTabu.add(solVecinaOptima.strTira);
        }

        // System.out.println("TS. solVecinaOptima.costo = "+ solVecinaOptima.costo);
        return solVecinaOptima;
    }

    public boolean validarFactibilidad(Solucion s) {
        //System.out.println("\nvalidarTasaRecoleccion = " + validarTasaRecoleccion(s));
        //System.out.println("validarTasaRemanu = " + validarTasaRemanu(s));
        //System.out.println("validarDemandaCliente = " + validarDemandaCliente(s));

        return (validarTasaRecoleccion(s) && validarTasaRemanu(s) && validarDemandaCliente(s));
        
        // alpha = 0
        // recorro vecindario asegurando tasa de recol
        //return validarDemandaCliente(s);
    }

    public boolean validarTasaRecoleccion(Solucion s) {
        int cant_recolectada = 0;
        int cant_retornos = 0;
        
        for (int l = 0; l < params.nL; l++) {
            for (int t = 0; t < (params.nT - params.STU); t++) {
                cant_recolectada += s.xu[l][t];
                cant_retornos += params.U[l][t];
            }
        }

        // System.out.println("cant_recolectada = " + cant_recolectada + " - cant_retornos*b = " + (params.beta * cant_retornos));
        return (cant_recolectada >= (params.beta * cant_retornos));

    }

    public boolean validarDemandaCliente(Solucion s) {
        
        for (int l = 0; l < params.nL; l++) {
            for (int t = 0; t < params.nT; t++) {
                if (IsL[l][t] < 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean validarTasaRemanu(Solucion s) {
        int cant_remanu = 0;
        int cant_retornos_recol = 0;
        
        for (int t = 0; t < params.nT; t++) {
            cant_remanu += s.xr[t];
            for (int l = 0; l < params.nL; l++) {
                if (t < (params.nT - params.STU)) {
                    cant_retornos_recol += s.xu[l][t];
                }
            }
        }

        return (cant_remanu >= (params.alpha * cant_retornos_recol));

    }

    public boolean validarTasaRemanuPRINT(Solucion s) {
        System.out.println("\n*** VALIDAR TASA REMANU ***");
        int cant_remanu = 0;
        int cant_retornos_recol = 0;
        
        for (int t = 0; t < params.nT; t++) {
            cant_remanu += s.xr[t];
            for (int l = 0; l < params.nL; l++) {
                if (t < (params.nT - params.STU)) {
                    cant_retornos_recol += s.xu[l][t];
                }
            }
        }

        System.out.println(cant_remanu >= (params.alpha * cant_retornos_recol));
        System.out.println(cant_remanu);
        System.out.println(params.alpha);
        System.out.println(cant_retornos_recol);
        System.out.println(params.alpha * cant_retornos_recol);

        return (cant_remanu >= (params.alpha * cant_retornos_recol));

    }

    public boolean validarSolucion(Solucion s) throws TSExcepcion {
        boolean sol_ok = true;
        String error_desc = "";
        float costoTotal = 0;
        float costoEntrega = 0, costoViaje = 0, costoTransportarS = 0, costoTransportarU = 0, costoStockSL = 0, costoStockUL = 0;
        float costoRemanu = 0, costoR = 0, costoStockU = 0;
        float costoManu = 0;

        // printMatriz(s.IuL, "\nsolOptima_IuL INSIDE.");

        // validar factibilidad
        if (!validarFactibilidad(s)) {
            sol_ok = false;
            error_desc = "\n\tno factible";
            System.out.println("Fail 0");
        }

        int[] Is = new int[params.nT];
        int[] Iu = new int[params.nT];
        int[][] IsL = new int[params.nL][params.nT];
        int[][] IuL = new int[params.nL][params.nT];
        for (int t = 0; t < params.nT; t++) {
            if (t == 0) {
                Is[t] = s.xm[t] + s.xr[t];
                Iu[t] = params.U_0 - s.xr[t];
            } else {
                Is[t] = Is[t-1] + s.xm[t] + s.xr[t];
                Iu[t] = Iu[t-1] - s.xr[t];
            }
            for (int c = 0; c < params.nL; c++) {
                Is[t] -= s.xs[c][t];
                Iu[t] += s.xu[c][t];

                if (t == 0) {
                    IsL[c][t] = s.xs[c][t] - params.D[c][t];
                    IuL[c][t] = params.U[c][t] - s.xu[c][t];
                } else {
                    IsL[c][t] = IsL[c][t-1] + s.xs[c][t] - params.D[c][t];
                    IuL[c][t] = IuL[c][t-1] + params.U[c][t] - s.xu[c][t];
                }

                // costos
                if (s.xs[c][t] > 0 || s.xu[c][t] > 0) {
                    costoViaje += params.Kv[c][t];
                }
                costoTransportarS += params.cvs[c] * s.xs[c][t];
                costoTransportarU += params.cvu[c] * s.xu[c][t];

                costoStockSL += params.hsL[c][t] * IsL[c][t];
                costoStockUL += params.huL[c][t] * IuL[c][t];
                
                // validaciones
                if (IsL[c][t] < 0) {
                    error_desc += "\n\tIsL[" + c +"][" + t + "] negativo (" + IsL[c][t] + ")";
                    sol_ok = false;
                    System.out.println("Fail 1");
                }
                if (IuL[c][t] < 0) {
                    error_desc += "\n\tIuL[" + c +"][" + t + "] negativo (" + IuL[c][t] + ")";
                    sol_ok = false;
                    System.out.println("Fail 2");
                }

                /*if ((s.xs[c][t] + s.xu[c][t]) > 0 && s.yv[c][t]) {
                    error_desc += "\n\terror de activacion de yv[" + c +"][" + t + "]";
                    sol_ok = false;
                }*/
            }

            // costos
            if (s.xm[t] > 0) {
                costoManu += params.Km[t];
            }
            if (s.xr[t] > 0) {
                costoRemanu += params.Kr[t];
                costoR += params.Kr[t];
            }

            costoR += params.cr[t] * xr[t];
            costoStockU += params.hu[t] * Iu[t];

            costoManu += params.cm[t] * xm[t] + params.hs[t] * Is[t];

            // validaciones
            if (Is[t] < 0) {
                error_desc += "\n\tIs[" + t + "] negativo (" + Is[t] + ")";
                sol_ok = false;
                System.out.println("Fail 3");
            }
            if (Iu[t] < 0) {
                error_desc += "\n\tIu[" + t + "] negativo (" + Iu[t] + ")";
                sol_ok = false;
                System.out.println("Fail 4");
            }

            /*if (s.xm[t] > 0 && s.ym[t]) {
                error_desc += "\n\tactivacion de ym[" + t + "]";
                sol_ok = false;
            }

            if (s.xr[t] > 0 && s.yr[t]) {
                error_desc += "\n\terror de activacion de yr[" + t + "]";
                sol_ok = false;
            }*/

        }

        // costos
        costoEntrega = costoViaje + costoTransportarS + costoTransportarU + costoStockSL + costoStockUL;
        costoRemanu = costoR + costoStockU;
        costoTotal = costoEntrega + costoRemanu + costoManu;
        
        if ((int)(s.costo) != (int)(costoTotal) && Math.round(s.costo) != Math.round(costoTotal)) {
            error_desc += "\n\tcosto de la solucion (" + s.costo + ") no coincide con el de la validacion (" + costoTotal + ")";
            
            // Plan de entrega
            error_desc += "\n\t\tsol_costoEntrega = " + s.costoEntrega + " vs val_costoEntrega = " + costoEntrega;
            error_desc += "\n\t\t\tsol_costoViaje = " + s.costoViaje + " vs val_costoViaje = " + costoViaje;
            error_desc += "\n\t\t\tsol_costoTransportarS = " + s.costoTransportarS + " vs val_costoTransportarS = " + costoTransportarS;
            error_desc += "\n\t\t\tsol_costoTransportarU = " + s.costoTransportarU + " vs val_costoTransportarU = " + costoTransportarU;
            error_desc += "\n\t\t\tsol_costoStockSL = " + s.costoStockSL + " vs val_costoStockSL = " + costoStockSL;
            error_desc += "\n\t\t\tsol_costoStockUL = " + s.costoStockUL + " vs val_costoStockUL = " + costoStockUL;

            // Plan de Remanufacturacion
            error_desc += "\n\t\tsol_costoRemanu = " + s.costoRemanu + " vs val_costoRemanu = " + costoRemanu;
            error_desc += "\n\t\t\tsol_costoR = " + s.costoR + " vs val_costoR = " + costoR;
            error_desc += "\n\t\t\tsol_costoStockU = " + s.costoStockU + " vs val_costoStockU = " + costoStockU;

            // Plan de Remanufacturacion
            error_desc += "\n\t\tsol_costoManu = " + s.costoManu + " vs val_costoManu = " + costoManu;

            sol_ok = false;
            System.out.println("Fail 5");
        }

        if (!sol_ok) {
            throw new TSExcepcion(13, error_desc);
        }

        return sol_ok;
    }


    /*** IMPRIMIR ***/
    public void imprimirVecindario(ArrayList<Object []> vecinas) {
        System.out.println("\n********************** VECINAS **********************");
        for (int i = 0; i < vecinas.size(); i ++) {
            System.out.print("vistas = ");
            for(int f = 0; f < vecinas.get(0).length; f++) {
                System.out.print(((String[]) vecinas.get(i)[0])[f] + " - ");
            }
            System.out.println("remanu = " + vecinas.get(i)[1]);
        }

        System.out.println("*****************************************************\n");
    }

    public void printArray(int[] valor, String strValor) {
        if (strValor == "\t") {
            System.out.print(strValor);
        } else {
            System.out.print(strValor + " =  \t");
        }
        for (int i = 0; i < valor.length; i++) {
            System.out.print(valor[i]);
            System.out.print("\t");
        }
        System.out.println("");
    }

    public void printMatriz(int[][] valor, String strValor) {
        System.out.println(strValor + " = ");
        for (int i = 0; i < valor.length; i++) {
            printArray(valor[i], "\t");
        }
    }

    public void printSolucion(Solucion s, ModoPrint mp) {
        if (mp == ModoPrint.GENERAL) {
            System.out.println("COSTO TOTAL = " + s.costo);
        }
        
        if (mp == ModoPrint.VARIABLES_DECISION || mp == ModoPrint.VD__POR_PLAN || mp == ModoPrint.VD__POR_TIPO) {
            // Imprimir variables de decision
            printMatriz(s.xs, "xs");
            printMatriz(s.xu, "xu");
            printArray(s.xr, "xr");
            printArray(s.xm, "xm");

            printArray(s.Iu, "Iu");
            printArray(s.Is, "Is");
        }

        if (mp == ModoPrint.VD__POR_PLAN) {
            System.out.print("\nCOSTOS = ");
            System.out.println(s.costo);
            System.out.println("\tPlan de Entrega = \t\t" + s.costoEntrega);
            System.out.println("\tPlan de Remanufacturación = \t" + s.costoRemanu);
            System.out.println("\tPlan de Manufacturación = \t" + s.costoManu);
        }

        if (mp == ModoPrint.VD__POR_TIPO) {
            System.out.println("\nCOSTOS = ");
            System.out.println("\tManufacturar = \t\t" + s.costoM);
            System.out.println("\tReanufacturar = \t" + s.costoR);
            System.out.println("\tTransporte = \t\t" + (s.costoViaje + costoTransportarS + costoTransportarU));
            System.out.println("\tStock = \t\t" + (s.costoStockS + s.costoStockU + s.costoStockSL + s.costoStockUL));
        }
    }
}
