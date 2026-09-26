package loo2.plp.orientadaObjetos2.declaracao.protocolo;

import java.util.HashSet;
import java.util.Set;

import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.imperative1.util.Lista;
import loo2.plp.orientadaObjetos1.declaracao.procedimento.DecParametro;
import loo2.plp.orientadaObjetos1.declaracao.procedimento.ListaDeclaracaoParametro;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;

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

	/**
	 * Uma assinatura esta bem tipada se os nomes dos parametros sao distintos
	 * e todos os tipos dos parametros sao validos (primitivos, dyn, classes
	 * ou protocolos declarados).
	 */
	public boolean checaTipo(AmbienteCompilacaoOO2 ambiente)
			throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
		Set<String> nomesParametros = new HashSet<String>();
		Lista<DecParametro> lista = parametros;
		while (lista != null && lista.getHead() != null) {
			if (!nomesParametros.add(lista.getHead().getId().getIdName())) {
				return false;
			}
			lista = lista.getTail();
		}
		return parametros.checaTipo(ambiente);
	}
}
