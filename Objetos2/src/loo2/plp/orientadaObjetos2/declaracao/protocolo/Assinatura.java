package loo2.plp.orientadaObjetos2.declaracao.protocolo;

import loo2.plp.orientadaObjetos1.declaracao.procedimento.ListaDeclaracaoParametro;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;

/**
 * Assinatura de um metodo dentro de um protocolo: nome e parametros, sem corpo.
 *
 * Assinatura ::= "proc" Id "(" [ ListaDeclaracaoParametro ] ")"
 */
public class Assinatura {

	/**
	 * Nome do metodo.
	 */
	private Id nome;

	/**
	 * Parametros formais do metodo.
	 */
	private ListaDeclaracaoParametro parametros;

	/**
	 * Construtor.
	 * @param nome Nome do metodo.
	 * @param parametros Parametros formais (lista vazia se nao houver).
	 */
	public Assinatura(Id nome, ListaDeclaracaoParametro parametros) {
		this.nome = nome;
		this.parametros = parametros;
	}

	public Id getNome() {
		return nome;
	}

	public ListaDeclaracaoParametro getParametros() {
		return parametros;
	}
}
