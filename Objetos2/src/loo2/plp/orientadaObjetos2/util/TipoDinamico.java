package loo2.plp.orientadaObjetos2.util;

import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.util.Tipo;

/**
 * Tipo dinamico (<code>dyn</code>): uma variavel desse tipo pode referenciar
 * qualquer objeto, e as chamadas de metodo sobre ela sao verificadas apenas
 * em tempo de execucao (duck typing dinamico).
 *
 * <pre>
 * TipoDinamico ::= "dyn"
 * </pre>
 */
public class TipoDinamico implements Tipo {

	/**
	 * Identificador textual do tipo.
	 */
	public static final Id TIPO_ID_dyn = new Id("dyn");

	/**
	 * Instancia unica do tipo dinamico.
	 */
	public static final Tipo TIPO_DYN = new TipoDinamico();

	private TipoDinamico() {
	}

	public Id getTipo() {
		return TIPO_ID_dyn;
	}

	/**
	 * O tipo dinamico e sempre valido.
	 */
	public boolean eValido(AmbienteCompilacaoOO1 ambiente) {
		return true;
	}

	public boolean equals(Object obj) {
		return obj instanceof TipoDinamico;
	}

	public int hashCode() {
		return TIPO_ID_dyn.hashCode();
	}

	public String toString() {
		return "dyn";
	}
}
