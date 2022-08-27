package logica;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import datos.ManejadorParametros;
import logica.TabuSearch.ModoPrint;

public class TSSolver {

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
            fileName += "_saltos" + String.valueOf(params.saltosMAX);
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

        // String fileName = "OUT_v" + params.version + "_it" + params.cantItMax + "_datos_nT" + params.nT + "_nL" + params.nL +".out";
        //String fileName = "OUT_tasas1_v" + params.version + "_it" + params.cantItMax + "_datos_nT" + params.nT + "_nL" + params.nL +".out";

        if (params.esMasivo == 1) {
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
            } else {
                //for (int i = 1; i <= 10; i++) {

                //}

                ejecucionMasiva(mp, params, params.version);
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

            // sobreescribir tasas
            // params.alpha = 1;
            // params.beta = 1;

            if (params.tasas == 1) {
                // sobreescribir tasas
                params.alpha = params.tasas_valor;
                params.beta = params.tasas_valor;
            }
            
            if (params.version == 1) {
                System.out.println("****************************** VERSION 1 ******************************");
                ejecucionUnitaria(params, null, null);
            }
            if (params.version == 2) {
                System.out.println("****************************** VERSION 2 ******************************");
                ejecucionUnitaria_v2(params);
            }
            if (params.version == 3) {
                String [] v = new String[params.nL];
                
                // params.cantItMax = 50;
                Integer cantItV3 = params.cantItMax;
                params.cantItMax = params.v3_v2it;
                Solucion solMetah = ejecucionUnitaria_v2(params);

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

                // test solver sol
                // v[0] = "100010001001";
                // v[1] = "100010001001";
                // v[2] = "100010101001";
                // solMetah.r = "000000001000";

                params.cantItMax = cantItV3;
                ejecucionUnitaria(params, v, solMetah.r);
            }
            if (params.version == 4) {
                System.out.println("****************************** VERSION 2 retornos ******************************");
                System.out.println("tasa alpha = " + params.alpha);
                System.out.println("tasa beta = " + params.beta);
                Solucion solMetah= ejecucionUnitaria_v2_ret(params);
                System.out.println("\nv4 ( "+ solMetah.costo + " )");
            }
            if (params.version == 5) {
                // 
                // params.cantItMax = 50;
                Solucion solMetah;
                String [] v = new String[params.nL];
                solMetah = ejecucionUnitaria_v2_ret(params);
                System.out.println("\nDONE v2 ( "+ solMetah.costo + " )");

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
                // params.cantItMax = 200;
                ejecucionUnitaria(params, v, solMetah.r);
            }
        }
    }
    
    public static Solucion ejecucionUnitaria_v2(Parametros params) throws Exception {
        /*** Leer archivo DAT y configuracion ***/
        TabuSearch ts = new TabuSearch(params, params.version);

        /*** Fase de inicializacion ***/
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
                    System.out.println("ACA");
                    solOptima.xu[l][params.nT-1] = params.U[l][params.nT-1];

                    solOptima.costoViaje += params.Kv[l][params.nT-1];
                    solOptima.costoStockUL -= params.U[l][params.nT-1] * params.huL[l][params.nT-1];
                    
                }
            }

        }


        solOptima.costoEntrega = costoXS + solOptima.costoTransportarU + solOptima.costoStockUL + solOptima.costoViaje;
        solOptima.costo = solOptima.costoEntrega;

        // ts.printMatriz(solOptima.xs, "solInicial xs");
        // ts.printMatriz(solOptima.xu, "solInicial xu");

        
        
        String rInicial = "";
        for(int i = 0; i < params.nT; i ++) {
            /*
            if ((i % 2) == 0) {
                rInicial += "0";
            } else {
                rInicial += "1";
            }
            */
            
            if (i == (params.nT - 1)) {
                rInicial += "1";
            }
            else {
                rInicial += "0";
            }  
        }

        // Se inicia el timer
        double startTime = System.currentTimeMillis();

        /*
        if (!ts.validarTasaRecoleccion(solOptima)) {
            // actualizar cantidad recoletada
                for (int t = 0; t < params.nT; t++) {
                    System.out.println("t = " + t);
                    System.out.println("\tU = " + params.U[0][t]);
                    if (solOptima.xs[0][t] > 0) {
                        System.out.println("\t" + solOptima.xu[0][t]);
                    }
                }
            

            
        };
        */
        

        int cantItMax = params.cantItMax;
        Solucion solActual;
        int cantIt = 0;
        while (cantIt < cantItMax) {
            // System.out.println("\n********************** ITERACION " + cantIt + " **********************");

            solActual = ts.TS(rInicial, solOptima);
            rInicial = solActual.r;
            // System.out.println("TS.Solver solActual = " + solActual + " - r = " + rInicial);

            if (solOptima.costo == solOptima.costoEntrega || solOptima.costo >= solActual.costo) {
                // System.out.println("EJ. Cambio sol--> solOptima.costo = " + solOptima.costo + "solActual.costo = " + solActual.costo);
                solOptima = solActual;
            }

            cantIt ++;
        }

        double endTime = (System.currentTimeMillis() - startTime);
        if (params.version == 2) {
            ts.printSolucion(solOptima, ModoPrint.VD__POR_PLAN);
            System.out.print("\nTIEMPO (ms): " + endTime);
        }

        ts.validarSolucion(solOptima);

        return solOptima;
    }

    public static Solucion ejecucionUnitaria_v2_ret(Parametros params) throws Exception {
        // version 2 considerando los costos de los retornos 
        
        /*** Leer archivo DAT y configuracion ***/
        TabuSearch ts = new TabuSearch(params, params.version);

        /*** Fase de inicializacion ***/
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

        // ts.printMatriz(solOptima.xs, "solInicial xs");
        // ts.printMatriz(solOptima.xu, "solInicial xu");
        
        String rInicial = "";
        for(int i = 0; i < params.nT; i ++) {
            /*
            if ((i % 2) == 0) {
                rInicial += "0";
            } else {
                rInicial += "1";
            }
            if (i == (params.nT - 1)) {
                rInicial += "1";
            }
            */
            
            if (i == (params.nT - 1)) {
                rInicial += "1";
            }
            else {
                rInicial += "0";
            }
              
        }

        // Se inicia el timer
        double startTime = System.currentTimeMillis();

        int cantItMax = params.cantItMax;
        Solucion solActual;
        int cantIt = 0;
        while (cantIt < cantItMax) {
            // System.out.println("\n********************** ITERACION " + cantIt + " **********************");

            solActual = ts.TS(rInicial, solOptima);
            rInicial = solActual.r;
            // System.out.println("TS.Solver solActual = " + solActual + " - r = " + rInicial);

            if (solOptima.costo == solOptima.costoEntrega || solOptima.costo >= solActual.costo) {
                // System.out.println("EJ. Cambio sol--> solOptima.costo = " + solOptima.costo + "solActual.costo = " + solActual.costo);
                solOptima = solActual;
            }

            cantIt ++;
        }

        double endTime = (System.currentTimeMillis() - startTime);
        if (params.version == 4) {
            ts.printSolucion(solOptima, ModoPrint.VD__POR_PLAN);
            System.out.print("\nTIEMPO (ms): " + endTime);
        }

        ts.validarSolucion(solOptima);

        return solOptima;
    }
    
    public static Solucion ejecucionUnitaria(Parametros params, String[] v_v2, String r_v2) throws Exception{
        /*** Leer archivo DAT y configuracion ***/

        TabuSearch ts = new TabuSearch(params, params.version);

        /*** Fase de inicializacion ***/
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
            //if (i == (params.nT - params.STU - 1)) {
            if (i == (params.nT - 1)) {
                rInicial += "1";
            }
            else {
                rInicial += "0";
            }   
        }

        int cantItMax = params.cantItMax;

        /*** Fase de generacion ***/
        Solucion solOptima = new Solucion();
        solOptima.costo = -1;

        // Se inicia el timer
        double startTime = System.currentTimeMillis();

        // set4ear input para v3
        if (params.version == 3 || params.version == 5) {
            vInicial = v_v2;
            rInicial = r_v2;
        }

        solOptima = ts.generarSolucion(vInicial, rInicial);
        ts.guardarListaTabu(vInicial, rInicial);

        Solucion solActual;
        int cantIt = 0;
        int cantSaltos = 0;
        while (cantIt < cantItMax) {
            // System.out.println("\n********************** ITERACION " + cantIt + " **********************");

            // System.out.println("IT = " + cantIt + " - SALTO = " + cantSaltos);
            // System.out.println("\tvInicial = " + vInicial[0] + vInicial[1] + vInicial[2]);
            // System.out.println("\trInicial = " + rInicial);
            solActual = ts.TS(vInicial, rInicial);
            vInicial = solActual.v;
            rInicial = solActual.r;

            if (solOptima.costo == -1 || solOptima.costo >= solActual.costo) {
                solOptima = solActual;
            }

            cantIt ++;

            if (params.saltos == 1 && cantIt >= cantItMax && cantSaltos < params.saltosMAX) {
                // System.out.println("\n********************** SALTOS " + cantSaltos + " **********************");
                String rNeg = "";
                String [] vNeg = new String[params.nL];

                for (int l = 0; l < params.nL; l++) {
                    vNeg[l] = "";

                    vNeg[l] += '1';
                    for (int i = 1; i < (params.nT-1); i++) {
                        /*if (solActual.v[l].charAt(i) == '0') {
                            vNeg[l] += '1';
                        } else {
                            vNeg[l] += '0';
                        }*/
                        vNeg[l] += solActual.v[l].charAt(i);

                    }
                    vNeg[l] += '1';
                    // System.out.println("Entro con v = " + solActual.v[l] + " - salgo con vNeg = " + vNeg[l]);
                }

                rNeg = "";
                for (int i = 0; i < (params.nT-1); i++) {
                    //if (solOptima.r.charAt(i) == '0') {
                    if (i % 2 == 0) {
                        rNeg += '1';
                    } else {
                        rNeg += '0';
                    }
                }
                rNeg += '1';
                // System.out.println("Entro con r = " + solOptima.r + " - salgo con rNeg = " + rNeg);

                vInicial = vNeg;
                rInicial = rNeg;

                cantSaltos ++;
                cantIt = 0;
                
            }
            
        }

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
