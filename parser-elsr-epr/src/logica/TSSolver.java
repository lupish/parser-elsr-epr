package logica;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import datos.ManejadorParametros;
import logica.TabuSearch.ModoPrint;

public class TSSolver {
    /*****************
        Contiene la logica de Tabu Search para TSv1, TSv2, TSv3, TSv4 y TSv5

    *****************/

    public static void ejecucionMasiva(ManejadorParametros mp, Parametros params, int versionMasivo) throws Exception {
        String fileName = "", output = "";
        
        // set version
        params.version = versionMasivo;

        fileName = "OUT";
        if (params.tasas == 1) {
            String tasas_str = String.valueOf(params.tasas_valor).replace(".", "");
            fileName += "_tasas" + tasas_str;
        }
        fileName += "_v" + params.version + "_it" + params.cantItMax;
        if (params.saltos == 1) {
            if (params.saltos_swap_odd == 1) {
                fileName += "_saltosOdd" + String.valueOf(params.saltosMAX);
            } else {
                fileName += "_saltos" + String.valueOf(params.saltosMAX);
            }
        }
        fileName += "_datos_nT" + params.nT + "_nL" + params.nL +".out";

        output = params.outPath + fileName;
        System.out.println("output = "+ output);
        PrintStream fileStream = new PrintStream(output);
        System.setOut(fileStream);

        System.out.println("*************************************");
        System.out.println("\tCANTIDAD CLIENTES = " + params.nL);
        System.out.println("\tCANTIDAD PERIODOS = " + params.nT);
        System.out.println("\tCANTIDAD IT MAXIMAS = " + params.cantItMax);
        System.out.println("\tTAMANO LISTA TABU = " + params.tamListaTabu);
        System.out.println("\tVERSION = " + params.version);
        System.out.println("\thuL POND = " + params.huL_pond);
        if (params.tasas == 1) {
            System.out.println("\tTASAS = " + params.tasas_valor);
        }
        System.out.println("*************************************\n");

        File carpetaMasiva = new File(params.pathArchivo);
        File[] archivos = carpetaMasiva.listFiles();
        Arrays.sort(archivos);

        if (params.version == 1) {
            System.out.println("****************************** VERSION 1 ******************************");
        }
        if (params.version == 2) {
            System.out.println("****************************** VERSION 2 ******************************");    
        }
        if (params.version == 3) {
            System.out.println("****************************** VERSION 3 ******************************");
        }
        if (params.version == 4) {
            System.out.println("****************************** VERSION 2 retornos ******************************");
        }
        if (params.version == 5) {
            System.out.println("****************************** VERSION 3 retornos ******************************");
        }
        if (params.version <= 0 || params.version > 5) {
            throw new TSExcepcion(14, params.version);
        }

        Solucion solMetah;
        String [] v = new String[params.nL];

        for (int i = 0; i < archivos.length; i++) {
            if (archivos[i].isFile() && archivos[i].getName().contains(".dat")) {
                params.nomArchivo = archivos[i].getName();
                params.setDatArchivo(params.pathArchivo, params.nomArchivo);
                
                params = mp.leerArchivo();

                if (params.tasas == 1) {
                    // sobreescribir tasas
                    params.alpha = params.tasas_valor;
                    params.beta = params.tasas_valor;
                }

                // System.out.println("params.nomArchivo = " + params.nomArchivo);

                if (params.version == 1) {
                    ejecucionUnitaria(params, null, null);
                }
                if (params.version == 2) {
                    ejecucionUnitaria_v2(params);
                }
                if (params.version == 3) {
                    // 
                    Integer cantItV3 = params.cantItMax;
                    params.cantItMax = params.v3_v2it;
                    solMetah = ejecucionUnitaria_v2(params);
                    System.out.println("\nDONE v2");

                    for (int l = 0; l < params.nL; l++) {
                        v[l] = "";
                        for (int t = 0; t < params.nT; t++) {
                            if (solMetah.xs[l][t] > 0 || solMetah.xu[l][t] > 0) {
                                v[l] += "1";
                            } else {
                                v[l] += "0";
                            }
                        }
                    }
                    params.cantItMax = cantItV3;
                    ejecucionUnitaria(params, v, solMetah.r);
                }
                if (params.version == 4) {
                    ejecucionUnitaria_v2_ret(params);
                }
                if (params.version == 5) {
                    // 
                    // params.cantItMax = 50;
                    solMetah = ejecucionUnitaria_v2_ret(params);
                    System.out.println("\nDONE v2");

                    for (int l = 0; l < params.nL; l++) {
                        v[l] = "";
                        for (int t = 0; t < params.nT; t++) {
                            if (solMetah.xs[l][t] > 0 || solMetah.xu[l][t] > 0) {
                                v[l] += "1";
                            } else {
                                v[l] += "0";
                            }
                        }
                    }
                    // params.cantItMax = 25;
                    ejecucionUnitaria(params, v, solMetah.r);
                }

                System.out.println("\n");
            }
        }
    }
    
    public static void masterEjecucion() throws Exception {
        /*** Leer archivo DAT y configuracion ***/
        ManejadorParametros mp = new ManejadorParametros();
        Parametros params = mp.leerArchivo();

        if (params.esMasivo == 1) {
            if (params.all_configs == 1) {
                PrintStream console = System.out;
                int init_config = params.init_config;
                for (int i = init_config; i <= 10; i ++) {
                    params.pathArchivo = params.all_configs_path + "config" + i + "\\";
                    params.outPath = params.all_configs_path + "config" + i + "\\OUT\\";
                    System.out.println("CONFIG = " + params.pathArchivo + " - OUT = " + params.outPath);

                    ejecucionMasiva(mp, params, params.version);

                    // back to console
                    System.setOut(console);
                }
            } else { 
                if (params.version == 0) {
                    // all versions
                    int max_versions = 5;
                    PrintStream console = System.out;
                    for (int i = 1; i <= max_versions; i++) {
                        System.out.println("RUN VERSION = " + i);
                        ejecucionMasiva(mp, params, i);
                        
                        // back to console
                        System.setOut(console);
                    }
                }
                else {
                    ejecucionMasiva(mp, params, params.version);
                }
            }
        } else {
            System.out.println("*************************************");
            System.out.println("\tCANTIDAD CLIENTES = " + params.nL);
            System.out.println("\tCANTIDAD PERIODOS = " + params.nT);
            System.out.println("\tCANTIDAD IT MAXIMAS = " + params.cantItMax);
            System.out.println("\tTAMANO LISTA TABU = " + params.tamListaTabu);
            System.out.println("\tVERSION = " + params.version);
            System.out.println("\thuL POND = " + params.huL_pond);
            if (params.tasas == 1) {
                System.out.println("\tTASAS = " + params.tasas_valor);
            }
            System.out.println("*************************************\n");

            if (params.tasas == 1) {
                // sobreescribir tasas
                params.alpha = params.tasas_valor;
                params.beta = params.tasas_valor;
            }
            
            if (params.version == 1) {
                /*** TSv1 ***/
                System.out.println("****************************** VERSION 1 ******************************");
                ejecucionUnitaria(params, null, null);
            }
            if (params.version == 2) {
                /*** TSv2 ***/
                System.out.println("****************************** VERSION 2 ******************************");
                ejecucionUnitaria_v2(params);
            }
            if (params.version == 3) {
                /*** TSv3: TSv2 + TSv1 ***/
                String [] v = new String[params.nL];
                
                // hallar sol de TSv2 como input para TSv1
                Integer cantItV3 = params.cantItMax;
                params.cantItMax = params.v3_v2it;
                Solucion solMetah = ejecucionUnitaria_v2(params);
                System.out.println("\nDONE v2");

                // tomar matriz v de la sol de TSv2
                for (int l = 0; l < params.nL; l++) {
                    v[l] = "";
                    for (int t = 0; t < params.nT; t++) {
                        if (solMetah.xs[l][t] > 0 || solMetah.xu[l][t] > 0) {
                            v[l] += "1";
                        } else {
                            v[l] += "0";
                        }
                    }
                }

                // ejecutar TSv1
                params.cantItMax = cantItV3;
                ejecucionUnitaria(params, v, solMetah.r);
            }
            if (params.version == 4) {
                /*** TSv4 ***/
                System.out.println("****************************** VERSION 2 retornos ******************************");
                System.out.println("tasa alpha = " + params.alpha);
                System.out.println("tasa beta = " + params.beta);
                Solucion solMetah= ejecucionUnitaria_v2_ret(params);
                System.out.println("\nv4 ( "+ solMetah.costo + " )");
            }
            if (params.version == 5) {
                /*** TSv5: TSv4 + TSv1 ***/
                Solucion solMetah;
                
                // hallar sol de TSv4 como input para TSv1
                String [] v = new String[params.nL];
                solMetah = ejecucionUnitaria_v2_ret(params);
                System.out.println("\nDONE v2 ( "+ solMetah.costo + " )");

                // tomar matriz v de la sol de TSv4
                for (int l = 0; l < params.nL; l++) {
                    v[l] = "";
                    for (int t = 0; t < params.nT; t++) {
                        if (solMetah.xs[l][t] > 0 || solMetah.xu[l][t] > 0) {
                            v[l] += "1";
                        } else {
                            v[l] += "0";
                        }
                    }
                }
                
                // ejecutar TSv1
                ejecucionUnitaria(params, v, solMetah.r);
            }
        }
    }
    
    /*** TSv2: 
     * Fase de inicializacion: xs se calcula con WW segun demanda, delivery setup y stock de prod del cliente.
     * Vecindario: swap r.
     * Fase de generacion: xr = plan de remanuacturacion, xm = plan de manufacturacion.
    ***/
    public static Solucion ejecucionUnitaria_v2(Parametros params) throws Exception {
        /*** Leer archivo DAT y configuracion ***/
        TabuSearch ts = new TabuSearch(params, params.version);

        /*** Fase de inicializacion: sol factible ***/
        Solucion solOptima = new Solucion();

        // calcular xs
        float costoXS = 0;
        for (int l = 0; l < params.nL; l++) {
            // WW para productos finales
            costoXS += ts.calcularXSWW(l);
        }
        solOptima.xs = ts.xs;

        // calcular xu
        ts.calcularXU_v2(solOptima);
        solOptima.xu = ts.xu;

        if (!ts.validarTasaRecoleccion(solOptima)) {
            for (int l = 0; l < params.nL; l++) {
                if (solOptima.xs[l][params.nT-1] == 0) {
                    solOptima.xu[l][params.nT-1] = params.U[l][params.nT-1];

                    solOptima.costoViaje += params.Kv[l][params.nT-1];
                    solOptima.costoStockUL -= params.U[l][params.nT-1] * params.huL[l][params.nT-1];
                }
            }
        }

        solOptima.costoEntrega = costoXS + solOptima.costoTransportarU + solOptima.costoStockUL + solOptima.costoViaje;
        solOptima.costo = solOptima.costoEntrega;
        
        String rInicial = "";
        for(int i = 0; i < params.nT; i ++) {
            if (i == (params.nT - 1)) {
                rInicial += "1";
            }
            else {
                rInicial += "0";
            }  
        }

        // se inicia el timer
        double startTime = System.currentTimeMillis();

        /*** Fase de exploracion: iterar por vecindarios y quedarse con la mejor sol ***/
        int cantItMax = params.cantItMax;
        Solucion solActual;
        int cantIt = 0;
        while (cantIt < cantItMax) {
            solActual = ts.TS(rInicial, solOptima);
            rInicial = solActual.r;

            if (solOptima.costo == solOptima.costoEntrega || solOptima.costo >= solActual.costo) {
                // actualizar mejor sol
                solOptima = solActual;
            }

            cantIt ++;
        }
        
        // se finaliza el timer
        double endTime = (System.currentTimeMillis() - startTime);
        if (params.version == 2) {
            ts.printSolucion(solOptima, ModoPrint.VD__POR_PLAN);
            System.out.print("\nTIEMPO (ms): " + endTime);
        }

        ts.validarSolucion(solOptima);

        return solOptima;
    }

    /*** TSv4: 
     * Fase de inicializacion: xs se calcula con WW segun demanda, delivery setup, stock de prod y stock de retornos del cliente.
     * Vecindario: swap r.
     * Fase de generacion: xr = plan de remanuacturacion, xm = plan de manufacturacion.
    ***/
    public static Solucion ejecucionUnitaria_v2_ret(Parametros params) throws Exception {
        /*** Leer archivo DAT y configuracion ***/
        TabuSearch ts = new TabuSearch(params, params.version);

        /*** Fase de inicializacion: sol factible ***/
        Solucion solOptima = new Solucion();

        // calcular xs
        float costoXS = 0;
        for (int l = 0; l < params.nL; l++) {
            // WW para productos finales
            costoXS += ts.calcularXSWW_ret(l);
        }
        solOptima.xs = ts.xs;

        // calcular xu
        ts.calcularXU_v2_ret(solOptima);
        solOptima.xu = ts.xu;
        solOptima.costoEntrega = costoXS + solOptima.costoTransportarU + solOptima.costoStockUL + solOptima.costoViaje;
        solOptima.costo = solOptima.costoEntrega;
        
        String rInicial = "";
        for(int i = 0; i < params.nT; i ++) {
            if (i == (params.nT - 1)) {
                rInicial += "1";
            }
            else {
                rInicial += "0";
            }
        }

        // se inicia el timer
        double startTime = System.currentTimeMillis();

        /*** Fase de exploracion: iterar por vecindarios y quedarse con la mejor sol ***/
        int cantItMax = params.cantItMax;
        Solucion solActual;
        int cantIt = 0;
        while (cantIt < cantItMax) {
            solActual = ts.TS(rInicial, solOptima);
            rInicial = solActual.r;

            if (solOptima.costo == solOptima.costoEntrega || solOptima.costo >= solActual.costo) {
                // actualizar mejor sol
                solOptima = solActual;
            }

            cantIt ++;
        }

        // se finaliza el timer
        double endTime = (System.currentTimeMillis() - startTime);
        if (params.version == 4) {
            ts.printSolucion(solOptima, ModoPrint.VD__POR_PLAN);
            System.out.print("\nTIEMPO (ms): " + endTime);
        }

        ts.validarSolucion(solOptima);

        return solOptima;
    }
    
    /*** TSv1: 
     * Fase de inicializacion: v[l, 1] = v[l, nT-1] = v[l, nT] = 1, r[nT] = 1.
     * Vecindario: swap v -> swap r.
     * Fase de generacion: (xs, xu) = plan de delivery, xr = plan de remanuacturacion, xm = plan de manufacturacion.
    ***/
    public static Solucion ejecucionUnitaria(Parametros params, String[] v_v2, String r_v2) throws Exception{
        /*** Leer archivo DAT y configuracion ***/
        TabuSearch ts = new TabuSearch(params, params.version);

        /*** Fase de inicializacion: sol factible ***/
        String rInicial = "";
        String [] vInicial = new String[params.nL];
        String visitaCliente = "";
        for(int i = 0; i < params.nT; i ++) {
            if (i == 0 || i == (params.nT - params.STU - 1) || i == (params.nT - 1)) {
                visitaCliente += "1";
            }
            else {
                visitaCliente += "0";
            }   
        }

        Arrays.fill(vInicial, visitaCliente);
        
        for(int i = 0; i < params.nT; i ++) {
            if (i == (params.nT - 1)) {
                rInicial += "1";
            }
            else {
                rInicial += "0";
            }   
        }

        int cantItMax = params.cantItMax;

        /*** Fase de generacion: en base a la tupla factible, se genera la primer sol ***/
        Solucion solOptima = new Solucion();
        solOptima.costo = -1;

        // Se inicia el timer
        double startTime = System.currentTimeMillis();

        // setear input para v3
        if (params.version == 3 || params.version == 5) {
            vInicial = v_v2;
            rInicial = r_v2;
        }

        solOptima = ts.generarSolucion(vInicial, rInicial);
        ts.guardarListaTabu(vInicial, rInicial);

        /*** Fase de exploracion: iterar por vecindarios y quedarse con la mejor sol ***/
        Solucion solActual;
        int cantIt = 0;
        int cantSaltos = 0;
        while (cantIt < cantItMax) {
            solActual = ts.TS(vInicial, rInicial);
            vInicial = solActual.v;
            rInicial = solActual.r;

            if (solOptima.costo == -1 || solOptima.costo >= solActual.costo) {
                // actualizar mejor sol
                solOptima = solActual;
            }

            cantIt ++;

            /*** Post optimizacion: saltos ***/
            if (params.saltos == 1 && cantIt >= cantItMax && cantSaltos < params.saltosMAX) {
                String rNeg = "";
                String [] vNeg = new String[params.nL];

                for (int l = 0; l < params.nL; l++) {
                    vNeg[l] = "";

                    vNeg[l] += '1';
                    for (int i = 1; i < (params.nT-1); i++) {
                        vNeg[l] += solActual.v[l].charAt(i);
                    }
                    vNeg[l] += '1';
                }

                rNeg = "";
                for (int i = 0; i < (params.nT-1); i++) {
                    if (params.saltos_swap_odd == 1) {
                        if (i % 2 != 0) {
                            rNeg += '1';
                        } else {
                            rNeg += '0';
                        }
                    } else {
                        if (i % 2 == 0) {
                            rNeg += '1';
                        } else {
                            rNeg += '0';
                        }
                    }
                }
                rNeg += '1';

                vInicial = vNeg;
                rInicial = rNeg;

                cantSaltos ++;
                cantIt = 0;
            }
        }

        // se finaliza el timer
        double endTime = (System.currentTimeMillis() - startTime);
        ts.printSolucion(solOptima, ModoPrint.VD__POR_PLAN);
        System.out.print("\nTIEMPO (ms): " + endTime);

        ts.validarTasaRemanu(solOptima);

        return solOptima;
    }
    
    public static void main(String[] args) {
        try {
            masterEjecucion();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
