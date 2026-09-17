package loo3.plp.orientadaObjetos3.excecao.declaracao;

import loo3.plp.expressions2.expression.Id;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;

/**
 * Excecao lancada quando um protocolo e declarado mais de uma vez.
 * Estende ClasseJaDeclaradaException para que os pontos do interpretador que
 * ja tratam redeclaracao de classes tratem tambem a de protocolos.
 */
public class ProtocoloJaDeclaradoException extends ClasseJaDeclaradaException {

	public ProtocoloJaDeclaradoException(Id id) {
		super("Protocolo " + id + " ja declarado.");
	}
}
