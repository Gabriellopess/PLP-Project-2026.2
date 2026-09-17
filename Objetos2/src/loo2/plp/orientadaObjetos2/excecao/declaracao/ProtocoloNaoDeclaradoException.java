package loo2.plp.orientadaObjetos2.excecao.declaracao;

import loo2.plp.expressions2.expression.Id;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;

/**
 * Excecao lancada quando se procura um protocolo que nao foi declarado.
 * Estende ClasseNaoDeclaradaException para que os pontos do interpretador que
 * ja tratam classes nao declaradas tratem tambem protocolos.
 */
public class ProtocoloNaoDeclaradoException extends ClasseNaoDeclaradaException {

	public ProtocoloNaoDeclaradoException(Id id) {
		super("Protocolo " + id + " nao declarado.");
	}
}
