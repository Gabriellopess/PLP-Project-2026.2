package loo3.plp.orientadaObjetos3.declaracao.protocolo;

import loo3.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo3.plp.orientadaObjetos1.declaracao.procedimento.ListaDeclaracaoParametro;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo3.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo3.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;

/**
 * Assinatura de um metodo dentro de um protocolo: apenas nome e parametros
 * formais, sem corpo.
 *
 * <pre>
 * Assinatura ::= "proc" Id "(" [ ListaDeclaracaoParametro ] ")"
 * </pre>
 */
public class Assinatura {

	/**
	 * Nome do metodo.
	 */
	private Id nome;

	/**
	 * Parametros formais do metodo.
	 */
	private ListaDeclaracaoParametro parametrosFormais;

	public Assinatura(Id nome, ListaDeclaracaoParametro parametrosFormais) {
		this.nome = nome;
		this.parametrosFormais = parametrosFormais;
	}

	public Id getNome() {
		return nome;
	}

	public ListaDeclaracaoParametro getParametrosFormais() {
		return parametrosFormais;
	}

	/**
	 * Verifica se os tipos dos parametros formais sao validos no ambiente.
	 */
	public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
			throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
		return parametrosFormais.checaTipo(ambiente);
	}

	public String toString() {
		return "proc " + nome + "(" + parametrosFormais + ")";
	}
}
