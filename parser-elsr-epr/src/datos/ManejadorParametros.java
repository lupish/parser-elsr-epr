package datos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import logica.Parametros;
import logica.TSExcepcion;

public class ManejadorParametros {
    
    /*** ATRIBUTOS ***/
    public Parametros params;

    /*** CONSTRUCTOR ***/
    public ManejadorParametros() {
        InputStream configInput;
        Properties config = new Properties();

        String paramRoot = System.getProperty("user.dir") + "\\src\\datos\\paramModelo.properties";
        System.out.println(paramRoot);
        try {
            configInput = new FileInputStream(paramRoot);
            config.load(configInput);

            // Cargar parametros
            int nT = Integer.parseInt(config.getProperty("cantPeriodos"));
            int nL = Integer.parseInt(config.getProperty("cantClientes"));
            params = new Parametros(nL, nT);
            params.setCantPeriodos(nT);
            params.setCantClientes(nL);
            params.setDatArchivo(config.getProperty("pathArchivo"),config.getProperty("nomArchivo"));
            params.setTamListaTabu(Integer.parseInt(config.getProperty("tamListaTabu")));
            params.cantItMax = Integer.parseInt(config.getProperty("cantItMax"));
            params.version = Integer.parseInt(config.getProperty("version"));
            params.tasas = Integer.parseInt(config.getProperty("tasas"));
            params.esMasivo = Integer.parseInt(config.getProperty("esMasivo"));
            params.outPath = config.getProperty("pathOutput");
            params.nomArchivo = config.getProperty("nomArchivo");
            params.pathArchivo = config.getProperty("pathArchivo");
            params.tasas_valor = Float.parseFloat(config.getProperty("tasas_valor"));

            params.huL_pond = Integer.parseInt(config.getProperty("huL_pond"));

            params.saltos = Integer.parseInt(config.getProperty("saltos"));
            params.saltosMAX = Integer.parseInt(config.getProperty("saltos_max"));
            
            params.v3_v2it = Integer.parseInt(config.getProperty("v3_v2it"));
        } catch(Exception e){
            System.out.println("Error = " + e.getMessage());
            e.printStackTrace();
        }

    }

    /*** METODOS ***/
    // Validaciones
    private void validarNT_L(String linea, int valor, int excCodigo) throws TSExcepcion {
        int largo;

        // elimino punto y coma
        largo = linea.length();
        linea = linea.substring(0, largo-1);

        //divido los valores
        String [] arrStrValores = linea.split("=");
        int nT = Integer.parseInt(arrStrValores[1].trim());
        if (nT != valor) {
            throw new TSExcepcion(3, params.datArchivo);
        }
    }

    // Calcular int
    private int calcularInt(String linea) {
        int largo;

        // elimino punto y coma
        largo = linea.length();
        linea = linea.substring(0, largo-1);

        //divido los valores
        String [] arrStrValores = linea.split("=");
        return(Integer.parseInt(arrStrValores[1].trim()));
    }

    private float calcularFloat(String linea) {
        int largo;

        // elimino punto y coma
        largo = linea.length();
        linea = linea.substring(0, largo-1);

        //divido los valores
        String [] arrStrValores = linea.split("=");
        return(Float.parseFloat(arrStrValores[1].trim()));
    }

    // Calcular int[]
    private int[] calcularArrayInt(String linea, int arrLen) {
        int[] arrValores = new int[arrLen];
        int largo;

        // elimino punto y coma
        largo = linea.length();
        linea = linea.substring(0, largo-1);

        //divido los valores
        String [] strDemanda = linea.split("=")[1].split(",");
        largo = strDemanda.length;
        for(int i = 0; i < largo; i++) {
            arrValores[i] = Integer.parseInt(strDemanda[i].trim().split(" ")[1]);
        }

        return arrValores;
    }

    // Calcular int[]
    private float[] calcularArrayFloat(String linea, int arrLen) {
        float[] arrValores = new float[arrLen];
        int largo;

        // System.out.println(linea);

        // elimino punto y coma
        largo = linea.length();
        linea = linea.substring(0, largo-1);

        //divido los valores
        String [] strDemanda = linea.split("=")[1].split(",");
        largo = strDemanda.length;
        for(int i = 0; i < largo; i++) {
            arrValores[i] = Float.parseFloat(strDemanda[i].trim().split(" ")[1]);
        }

        return arrValores;
    }

    // Calcular int[][]
    private int[] calcularMatrizFila(String linea, int arrLen) {
        int largo;
        int[] arrValores = new int[arrLen];

        // elimino punto y coma
        if(linea.contains(";")) {
            largo = linea.length();
            linea = linea.substring(0, largo-1);
        }

        //divido los valores
        String [] arrStrValores = linea.split("\t");
        largo = arrStrValores.length;
        for(int i = 0; i < largo; i ++) {
            arrValores[i] = Integer.parseInt(arrStrValores[i]);
        }

        return arrValores;
    }

    private int[] calcularMatrizFilaPond(String linea, int arrLen, int pond) {
        int largo;
        int[] arrValores = new int[arrLen];

        // elimino punto y coma
        if(linea.contains(";")) {
            largo = linea.length();
            linea = linea.substring(0, largo-1);
        }

        //divido los valores
        String [] arrStrValores = linea.split("\t");
        largo = arrStrValores.length;
        for(int i = 0; i < largo; i ++) {
            arrValores[i] = Integer.parseInt(arrStrValores[i]) * pond;
        }

        return arrValores;
    }

    // Calcular int[][]
    private float[] calcularMatrizFilaFloat(String linea, int arrLen) {
        int largo;
        float[] arrValores = new float[arrLen];

        // elimino punto y coma
        if(linea.contains(";")) {
            largo = linea.length();
            linea = linea.substring(0, largo-1);
        }

        //divido los valores
        String [] arrStrValores = linea.split("\t");
        largo = arrStrValores.length;
        for(int i = 0; i < largo; i ++) {
            arrValores[i] = Float.parseFloat(arrStrValores[i]);
        }

        return arrValores;
    }

    private float[] calcularMatrizFilaFloatPond(String linea, int arrLen, int pond) {
        int largo;
        float[] arrValores = new float[arrLen];

        // elimino punto y coma
        if(linea.contains(";")) {
            largo = linea.length();
            linea = linea.substring(0, largo-1);
        }

        //divido los valores
        String [] arrStrValores = linea.split("\t");
        largo = arrStrValores.length;
        for(int i = 0; i < largo; i ++) {
            arrValores[i] = Float.parseFloat(arrStrValores[i]) * pond;
        }

        return arrValores;
    }

    private void calcularMatrices(String linea) {
        String lineaNums = linea.split("=")[1];
        String [] arrValores = lineaNums.split("-");
        String valorCliente;

        String StrCliente;
        int clienteIt = 0;
        for(int i = 0; i < arrValores.length; i++) {
            if (!arrValores[i].isEmpty()) {
                StrCliente = String.valueOf(arrValores[i].trim().charAt(0));
                valorCliente = arrValores[i].split(StrCliente, 2)[1].trim();

                // Se asigna al parametro correspondiente
                if (linea.contains("D")) {
                    params.setDFila(clienteIt, calcularMatrizFila(valorCliente, params.nT));
                }
                if (linea.contains("U")) {
                    params.setUFila(clienteIt, calcularMatrizFila(valorCliente, params.nT));
                }
                if (linea.contains("Kv")) {
                    params.setKvFila(clienteIt, calcularMatrizFila(valorCliente, params.nT));
                }
                if (linea.contains("hsL")) {
                    // params.setHsLFila(clienteIt, calcularMatrizFila(valorCliente, params.nT));

                    if (params.huL_pond > 0) {
                        params.setHsLFila(clienteIt, calcularMatrizFilaPond(valorCliente, params.nT, params.huL_pond));
                    } else {
                        params.setHsLFila(clienteIt, calcularMatrizFila(valorCliente, params.nT));
                    }
                }
                if (linea.contains("huL")) {
                    if (params.huL_pond > 0) {
                        params.setHuLFila(clienteIt, calcularMatrizFilaFloatPond(valorCliente, params.nT, params.huL_pond));
                    } else {
                        params.setHuLFila(clienteIt, calcularMatrizFilaFloat(valorCliente, params.nT));
                    }
                    
                }
                clienteIt ++;
            }
        }

    }

    public Parametros leerArchivo() throws TSExcepcion {
        String archivo = params.datArchivo;
        System.out.println(archivo);

        String linea;  
        String lineaMatriz = "";

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(archivo);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            linea = bufferedReader.readLine();
            while(linea != null) {
                // System.out.println(linea);

                // Validar valores que viene del properties
                if (linea.contains("param nT")) {
                    validarNT_L(linea, params.nT, 3);
                }
                if (linea.contains("param nL")) {
                    validarNT_L(linea, params.nL, 12);
                }

                // Leer int & float
                if (linea.contains("param M ")) {
                    params.setM(calcularInt(linea));
                }
                if (linea.contains("param U0 ")) {
                    params.setU0(calcularInt(linea));
                }

                if (linea.contains("param alpha ")) {
                    params.setAlpha(calcularFloat(linea));
                }
                if (linea.contains("param beta ")) {
                    params.setBeta(calcularFloat(linea));
                }
                if (linea.contains("param STU ")) {
                    params.setSTU(calcularInt(linea));
                }

                // Leer int[]
                if (linea.contains("param cm ")) {
                    params.setCM(calcularArrayInt(linea, params.nT));
                }
                if (linea.contains("param cr ")) {
                    params.setCR(calcularArrayInt(linea, params.nT));
                }
                if (linea.contains("param Km ")) {
                    params.setKM(calcularArrayInt(linea, params.nT));
                }
                if (linea.contains("param Kr ")) {
                    params.setKR(calcularArrayInt(linea, params.nT));
                }
                if (linea.contains("param hs ")) {
                    params.setHS(calcularArrayInt(linea, params.nT));
                }
                if (linea.contains("param hu ")) {
                    params.setHU(calcularArrayFloat(linea, params.nT));
                }
                if (linea.contains("param cvs ")) {
                    params.setCVS(calcularArrayInt(linea, params.nL));
                }
                if (linea.contains("param cvu ")) {
                    params.setCVU(calcularArrayInt(linea, params.nL));
                }

                // Leer int[][]
                if ((!linea.isEmpty() && linea.charAt(0) != '#') && !linea.contains("end")) {
                    // Se filtran las lineas vacias, los comentarios y el end del final
                    if (!linea.contains(";"))  {
                        lineaMatriz += linea + "-";
                        while (!linea.contains(";")) {
                            linea = bufferedReader.readLine();
                            lineaMatriz += linea + "-";
                        }
                        // System.out.println("lineaMatriz = " + lineaMatriz);
                        calcularMatrices(lineaMatriz);

                        lineaMatriz = "";
                    }
                }

                linea = bufferedReader.readLine();
            }

            // imprimirParametros();

            // Always close files.
            bufferedReader.close();
        }
        catch (TSExcepcion e) {
            throw e;
        }
        catch(FileNotFoundException ex) {
            throw new TSExcepcion(1, archivo);
        }
        catch(IOException ex) {
            throw new TSExcepcion(2, archivo);
        }
        catch (Exception e) {
            e.getStackTrace();
            throw new TSExcepcion(4, archivo);
        }

        return params;
    }

    /*** IMPRIMIR ***/
    public void imprimirParametros() {
        System.out.println("************** PARAMS **************");

        // int & float
        params.printInt(params.nT, "nT");
        params.printInt(params.nL, "nL");
        params.printInt(params.M, "M");
        params.printInt(params.U_0, "U0");
        params.printFloat(params.alpha, "alpha");
        params.printFloat(params.beta, "beta");
        params.printInt(params.STU, "STU");
        
        // int[]
        params.printArray(params.cm, "cm");
        params.printArray(params.cr, "cr");
        params.printArray(params.Km, "Km");
        params.printArray(params.Kr, "Kr");
        params.printArray(params.hs, "hs");
        params.printArrayD(params.hu, "hu");
        params.printArray(params.cvs, "cvs");
        params.printArray(params.cvu, "cvu");

        // int[][]
        params.printMatriz(params.D, "D");
        params.printMatriz(params.U, "U");
        params.printMatriz(params.Kv, "Kv");
        params.printMatriz(params.hsL, "hsL");
        params.printMatrizFloat(params.huL, "huL");
    }
}
