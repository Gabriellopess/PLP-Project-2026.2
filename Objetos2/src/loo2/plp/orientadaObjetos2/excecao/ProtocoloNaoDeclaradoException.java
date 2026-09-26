package loo2.plp.orientadaObjetos2.excecao;

import loo2.plp.expressions2.expression.Id;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;

/**
 * Lancada quando se busca um protocolo que nao foi declarado. Estende
 * ClasseNaoDeclaradaException pelo mesmo motivo de
 * ProtocoloJaDeclaradoException: o espaco de nomes de tipos e unico.
 */
public class ProtocoloNaoDeclaradoException extends ClasseNaoDeclaradaException {

	private final Id id;

	public ProtocoloNaoDeclaradoException(Id id) {
		super(id);
		this.id = id;
	}

	@Override
	public String getMessage() {
		return "Protocolo " + id + " nao declarado.";
	}
}
