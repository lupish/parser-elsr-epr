# Parser del problema ELSR para EPR

Contiene los algoritmos basados en Tabu Search para resolver el problema de planificación de la producción en donde se puede manufacturar y remanufacturar, y se tiene una red de clientes que consumen productos y generan retornos. En donde el problema cumple con la regulación de la responsabiidad extendida del productor.

Algoritmos generados:
* TSv1: 
  * Fase de inicialización: v[l, 1] = v[l, nT-1] = v[l, nT] = 1, r[nT] = 1.
  * Vecindario: swap v -> swap r.
  * Fase de generación: (xs, xu) = plan de delivery, xr = plan de remanuacturacion, xm = plan de manufacturacion. 
* TSv2:
  * Fase de inicialización: xs se calcula con WW segun demanda, delivery setup, stock de prod finales del cliente.
  * Vecindario: swap r.
  * Fase de generación: xr = plan de remanuacturacion, xm = plan de manufacturacion.
* TSv3:
  * Fase de inicialización: (v, r) = TSv2.
  * Vecindario: igual a TSv1.
  * Fase de generación: igual a TSv1.
* TSv4:
  * Fase de inicialización: xs se calcula con WW segun demanda, delivery setup, stock de prod finales y stock de retornos del cliente.
  * Vecindario: igual que TSv2.
  * Fase de generación: igual que TSv2.
* TSv5:
  * Fase de inicialización: (v, r) = TSv4.
  * Vecindario: igual a TSv1.
  * Fase de generación: igual a TSv1.


Trabajo realizado en el marco de la Maestría de Investigación de Operaciones de Facultad de Ingeniería UdelaR de Luciana Vidal.
