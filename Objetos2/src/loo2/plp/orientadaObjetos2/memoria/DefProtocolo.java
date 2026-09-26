package loo2.plp.orientadaObjetos2.memoria;

import loo2.plp.expressions2.expression.Id;
import loo2.plp.imperative1.util.Lista;
import loo2.plp.orientadaObjetos2.declaracao.protocolo.Assinatura;
import loo2.plp.orientadaObjetos2.declaracao.protocolo.ListaAssinatura;

/**
 * Definicao de um protocolo guardada nos ambientes de compilacao e de
 * execucao: o nome e as assinaturas que uma classe precisa ter para
 * satisfaze-lo.
 */
public class DefProtocolo {

	private Id nome;

	private ListaAssinatura assinaturas;

	public DefProtocolo(Id nome, ListaAssinatura assinaturas) {
		this.nome = nome;
		this.assinaturas = assinaturas;
	}

	public Id getNome() {
		return nome;
	}

	public ListaAssinatura getAssinaturas() {
		return assinaturas;
	}

	/**
	 * Retorna a assinatura do metodo com o nome dado, ou <code>null</code>
	 * se o protocolo nao exige esse metodo.
	 */
	public Assinatura getAssinatura(Id nomeMetodo) {
		Lista<Assinatura> lista = assinaturas;
		while (lista != null && lista.getHead() != null) {
			Assinatura assinatura = lista.getHead();
			if (assinatura.getNome().getIdName().equals(nomeMetodo.getIdName())) {
				return assinatura;
			}
			lista = lista.getTail();
		}
		return null;
	}
}
