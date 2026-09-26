package loo2.plp.orientadaObjetos2.excecao;

import loo2.plp.expressions2.expression.Id;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;

/**
 * Lancada quando um protocolo e declarado com um nome ja usado por outra
 * classe ou protocolo. Classes e protocolos compartilham o mesmo espaco de
 * nomes, por isso esta excecao estende ClasseJaDeclaradaException e segue
 * pelos mesmos caminhos de tratamento.
 */
public class ProtocoloJaDeclaradoException extends ClasseJaDeclaradaException {

	private final Id id;

	public ProtocoloJaDeclaradoException(Id id) {
		super(id);
		this.id = id;
	}

	@Override
	public String getMessage() {
		return "Protocolo " + id + " ja declarado.";
	}
}
