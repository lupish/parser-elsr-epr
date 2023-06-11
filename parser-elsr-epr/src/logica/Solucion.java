package logica;

public class Solucion {
    /*****************
     Estructura de solucion del Tabu Search

     Atributos de la clase:
     - strTira: 1 remanufactura, 0 en caso contrario
     - xM: cantidad a manufacturar
     - xR: cantidad a remanufacturar
     - costo: costo total
     - costoXR: costo de remanufacturar
     - costoXM: costo de manufacturar
     *****************/

    /*** ATRIBUTOS ***/
    // costos
    public float costo; // costo final
    public float costoM, costoR; // costos de produccion
    public float costoStockU, costoStockS, costoStockUL, costoStockSL; // costos de stock
    public float costoTransportarS, costoTransportarU, costoViaje; // costos de viaje
    public float costoEntrega, costoRemanu, costoManu;

    // variables de decision
    public int[] xm, xr, Is, Iu;
    public int[][] xs, xu, IsL, IuL;
    public boolean[] ym, yr;
    public boolean[][] yv;

    // tiras
    public String r;
    public String [] v;
    public String strTira;
}
