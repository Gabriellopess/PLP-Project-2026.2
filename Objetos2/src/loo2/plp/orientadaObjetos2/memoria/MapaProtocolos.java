package loo2.plp.orientadaObjetos2.memoria;

import java.util.HashMap;

import loo2.plp.expressions2.expression.Id;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloJaDeclaradoException;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloNaoDeclaradoException;

/**
 * Tabela de protocolos declarados, compartilhada pelos contextos de
 * compilacao e de execucao de OO2.
 *
 * A chave e o nome do protocolo (String) e nao o Id, porque Id.equals nao e
 * simetrico entre as subclasses de Id usadas pelo parser e pelos ambientes.
 */
public class MapaProtocolos {

	private final HashMap<String, DefProtocolo> protocolos = new HashMap<String, DefProtocolo>();

	public void map(Id nome, DefProtocolo defProtocolo) throws ProtocoloJaDeclaradoException {
		if (protocolos.containsKey(nome.getIdName())) {
			throw new ProtocoloJaDeclaradoException(nome);
		}
		protocolos.put(nome.getIdName(), defProtocolo);
	}

	public DefProtocolo get(Id nome) throws ProtocoloNaoDeclaradoException {
		DefProtocolo defProtocolo = protocolos.get(nome.getIdName());
		if (defProtocolo == null) {
			throw new ProtocoloNaoDeclaradoException(nome);
		}
		return defProtocolo;
	}

	public boolean contem(Id nome) {
		return protocolos.containsKey(nome.getIdName());
	}
}
