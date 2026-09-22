package loo2.plp.orientadaObjetos2.declaracao.protocolo;

import loo2.plp.imperative1.util.Lista;

/**
 * ListaAssinatura ::= Assinatura | Assinatura "," ListaAssinatura
 */
public class ListaAssinatura extends Lista<Assinatura> {

	/**
	 * Construtor da lista vazia.
	 */
	public ListaAssinatura() {
	}

	/**
	 * Construtor.
	 * @param assinatura Unica assinatura da lista.
	 */
	public ListaAssinatura(Assinatura assinatura) {
		super(assinatura, new ListaAssinatura());
	}

	/**
	 * Construtor.
	 * @param assinatura Primeira assinatura da lista.
	 * @param lista Restante da lista.
	 */
	public ListaAssinatura(Assinatura assinatura, ListaAssinatura lista) {
		super(assinatura, lista);
	}
}
