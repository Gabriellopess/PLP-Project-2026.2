package loo3.plp.orientadaObjetos2.declaracao;

import loo3.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo3.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo3.plp.imperative1.util.Lista;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoJaDeclaradoException;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo3.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo3.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;
import loo3.plp.orientadaObjetos3.declaracao.DecOO;

/**
 * Lista de declaracoes de alto nivel (classes e protocolos) de um programa.
 *
 * <pre>
 * ListaDeclaracaoOO ::= DecOO | DecOO "," ListaDeclaracaoOO
 * </pre>
 */
public class ListaDeclaracaoOO extends Lista<DecOO> {
	/**
	 * Construtor.
	 */
	public ListaDeclaracaoOO() {
	}

	/**
	 * Construtor.
	 * 
	 * @param decOO
	 *            Declaracao unica da lista.
	 */
	public ListaDeclaracaoOO(DecOO decOO) {
		super(decOO, new ListaDeclaracaoOO());
	}

	/**
	 * Construtor.
	 * 
	 * @param decOO
	 *            Primeira declaracao da lista.
	 * @param lista
	 *            Restante das declaracoes.
	 */
	public ListaDeclaracaoOO(DecOO decOO, ListaDeclaracaoOO lista) {
		super(decOO, lista);
	}

	public AmbienteExecucaoOO2 elabora(AmbienteExecucaoOO2 ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
			ClasseJaDeclaradaException, ClasseNaoDeclaradaException,
			ProcedimentoNaoDeclaradoException, ProcedimentoJaDeclaradoException, ConstrutorNaoDeclaradoException {
		
		if (length() == 1) {
			getHead().elabora(ambiente);
		} else {
			getHead().elabora(ambiente);
			((ListaDeclaracaoOO)getTail()).elabora(ambiente);
		}
		
		return ambiente;
	}

	public boolean checaTipo(AmbienteCompilacaoOO2 ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
			ClasseJaDeclaradaException, ClasseNaoDeclaradaException,
			ProcedimentoNaoDeclaradoException, ProcedimentoJaDeclaradoException, ConstrutorNaoDeclaradoException {
		
		boolean ret = false;
		if (length() == 1) {
			ret = getHead().checaTipo(ambiente);
		} else {
			ret = getHead().checaTipo(ambiente);
			if (ret)
				ret = ((ListaDeclaracaoOO)getTail()).checaTipo(ambiente);
		}
		
		return ret;
	}
}
