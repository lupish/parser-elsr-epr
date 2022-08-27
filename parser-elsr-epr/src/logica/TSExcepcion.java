package logica;

public class TSExcepcion extends Exception {

    private int codigoError;
    private String nomArchivo;
    private int vecindario, modo;
    private double tasaRetorno;

    public TSExcepcion(int codigoError) {
        super();
        this.codigoError = codigoError;
    }

    public TSExcepcion(int codigoError, String nomArchivo) {
        super();
        this.codigoError = codigoError;
        this.nomArchivo = nomArchivo;
    }

    public TSExcepcion(int codigoError, String nomArchivo, int vecindario) {
        super();
        this.codigoError = codigoError;
        this.nomArchivo = nomArchivo;
        this.vecindario = vecindario;
    }

    public TSExcepcion(int codigoError, int modo) {
        super();
        this.codigoError = codigoError;
        this.modo = modo;
    }

    public TSExcepcion(int codigoError, String nomArchivo, double tasaRetorno) {
        super();
        this.codigoError = codigoError;
        this.nomArchivo = nomArchivo;
        this.tasaRetorno = tasaRetorno;
    }

    @Override
    public String getMessage() {
        String error;
        switch (codigoError) {
            case 1:
                error = "No se puede abrir el archivo DAT (" + nomArchivo + ").";
                break;
            case 2:
                error = "No se puede leer el archivo DAT (" + nomArchivo + ").";
                break;
            case 3:
                error = "La cantidad de periodos del DAT (" + nomArchivo + ") no concuerda con el properties.";
                break;
            case 4:
                error = "Archivo DAT (" + nomArchivo + ") mal formado.";
                break;
            case 5:
                error = "No existe el vecindario " + vecindario;
                break;
            case 6:
                error = "No existe el modo " + modo;
                break;
            case 7:
                error = "Error al hallar la tasa de retornos (" + tasaRetorno + ") para el archivo DAT (" + nomArchivo
                        + ").";
                break;
            case 8:
                error = "Error al hallar el costo de la solucion.";
                break;
            case 9:
                error = "Error al hallar el balance de flujo de la solucion.";
                break;
            case 10:
                error = "Error al hallar el balance de stock de la solucion.";
                break;
            case 11:
                error = "En el ultimo periodo se tiene stock de prod finales.";
                break;
            case 12:
                error = "La cantidad de clientes del DAT (" + nomArchivo + ") no concuerda con el properties.";
                break;
            case 13:
                error = "Solucion erronea:" + nomArchivo;
                break;
            case 14:
                error = "No existe la version seleccionada:" + nomArchivo;
                break;
            default:
                error = "Error desconocido.";
                break;
        }
        return error;
    }
}
