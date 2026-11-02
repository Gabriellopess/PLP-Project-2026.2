package loo2.plp.orientadaObjetos2.declaracao;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.imperative1.util.Lista;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoJaDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloJaDeclaradoException;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo2.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;

/**
 * Lista das declaracoes de topo do programa (classes e protocolos), na
 * ordem em que aparecem.
 *
 * ListaDeclaracaoOO ::= DecOO | DecOO "," ListaDeclaracaoOO
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
	 * @param DeclaracaoOO
	 *            DeclaracaoOO que compoe a tail.
	 */
	public ListaDeclaracaoOO(DecOO decOO) {
		super(decOO, new ListaDeclaracaoOO());
	}

	/**
	 * Construtor.
	 * 
	 * @param declaracaoOO
	 *            Primeira declaracao da tail.
	 * @param listaDeclaracaoOO
	 *            Restante da tail de declaracoes.
	 */
	public ListaDeclaracaoOO(DecOO decOO, ListaDeclaracaoOO lista) {
		super(decOO, lista);
	}

	public AmbienteExecucaoOO2 elabora(AmbienteExecucaoOO2 ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
			ClasseJaDeclaradaException, ClasseNaoDeclaradaException,
			ProcedimentoNaoDeclaradoException, ProcedimentoJaDeclaradoException, ConstrutorNaoDeclaradoException,
			ProtocoloJaDeclaradoException {
		
		getHead().elabora(ambiente);
		if (length() > 1) {
			((ListaDeclaracaoOO)getTail()).elabora(ambiente);
		}
		
		return ambiente;
	}

	public boolean checaTipo(AmbienteCompilacaoOO2 ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
			ClasseJaDeclaradaException, ClasseNaoDeclaradaException,
			ProcedimentoNaoDeclaradoException, ProcedimentoJaDeclaradoException, ConstrutorNaoDeclaradoException,
			ProtocoloJaDeclaradoException {
		
		boolean ret = getHead().checaTipo(ambiente);
		if (ret && length() > 1) {
			ret = ((ListaDeclaracaoOO)getTail()).checaTipo(ambiente);
		}
		
		return ret;
	}
}
