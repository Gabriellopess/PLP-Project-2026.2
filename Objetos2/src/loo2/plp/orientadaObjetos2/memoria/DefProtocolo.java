package loo2.plp.orientadaObjetos2.memoria;

import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos2.declaracao.protocolo.Assinatura;
import loo2.plp.orientadaObjetos2.declaracao.protocolo.ListaAssinatura;

/**
 * Definicao de um protocolo guardada no ambiente de compilacao: o nome do
 * protocolo e o conjunto de assinaturas que uma classe precisa possuir para
 * satisfaze-lo.
 */
public class DefProtocolo {

	/**
	 * Nome do protocolo.
	 */
	private Id idProtocolo;

	/**
	 * Assinaturas exigidas pelo protocolo.
	 */
	private ListaAssinatura assinaturas;

	public DefProtocolo(Id idProtocolo, ListaAssinatura assinaturas) {
		this.idProtocolo = idProtocolo;
		this.assinaturas = assinaturas;
	}

	public Id getIdProtocolo() {
		return idProtocolo;
	}

	public ListaAssinatura getAssinaturas() {
		return assinaturas;
	}

	/**
	 * Recupera a assinatura do metodo de nome dado.
	 *
	 * @throws ProcedimentoNaoDeclaradoException quando o protocolo nao exige
	 *         metodo com esse nome.
	 */
	public Assinatura getAssinatura(Id nomeMetodo) throws ProcedimentoNaoDeclaradoException {
		return assinaturas.getAssinatura(nomeMetodo);
	}
}
