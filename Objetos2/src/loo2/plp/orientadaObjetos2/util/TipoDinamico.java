package loo2.plp.orientadaObjetos2.util;

import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.util.Tipo;

/**
 * Tipo dinamico ("dyn"): referencia a qualquer objeto, cujas chamadas de
 * metodo sao verificadas em tempo de execucao.
 *
 * TipoDinamico ::= "dyn"
 */
public class TipoDinamico implements Tipo {

	/**
	 * Unica instancia do tipo dinamico.
	 */
	public static final TipoDinamico DYN = new TipoDinamico();

	private static final Id ID_DYN = new Id("dyn");

	private TipoDinamico() {
	}

	public Id getTipo() {
		return ID_DYN;
	}

	/**
	 * O tipo dinamico e sempre valido: nao depende de nenhuma declaracao.
	 */
	public boolean eValido(AmbienteCompilacaoOO1 ambiente) {
		return true;
	}

	public boolean equals(Object obj) {
		return obj instanceof TipoDinamico;
	}

	public int hashCode() {
		return ID_DYN.hashCode();
	}

	public String toString() {
		return "dyn";
	}
}
