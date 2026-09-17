package loo2.plp.orientadaObjetos2.comando;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.imperative1.util.Lista;
import loo2.plp.orientadaObjetos1.comando.ChamadaProcedimento;
import loo2.plp.orientadaObjetos1.comando.Procedimento;
import loo2.plp.orientadaObjetos1.declaracao.procedimento.ListaDeclaracaoParametro;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.expressao.Expressao;
import loo2.plp.orientadaObjetos1.expressao.ListaExpressao;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo2.plp.orientadaObjetos1.util.ListaTipo;
import loo2.plp.orientadaObjetos2.util.CompatibilidadeTipos;

/**
 * Chamada de procedimento (metodo ou construtor) em OO2. A verificacao dos
 * argumentos usa as regras de compatibilidade de OO2 em vez de exigir tipos
 * identicos: um argumento pode ser uma subclasse do parametro, ou qualquer
 * objeto quando o parametro e <code>dyn</code>.
 */
public class ChamadaProcedimentoOO2 extends ChamadaProcedimento {

	public ChamadaProcedimentoOO2(Procedimento procedimento, ListaExpressao parametrosReais,
			ListaValor valoresParametros) {
		super(procedimento, parametrosReais, valoresParametros);
	}

	public ChamadaProcedimentoOO2(Procedimento procedimento, ListaExpressao parametrosReais) {
		super(procedimento, parametrosReais);
	}

	/**
	 * Verifica se todas as expressoes de argumento estao bem tipadas.
	 */
	public static boolean checaTipoArgumentos(ListaExpressao parametrosReais, AmbienteCompilacaoOO1 ambiente)
			throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
		Lista<Expressao> atual = parametrosReais;
		while (atual != null && atual.getHead() != null) {
			if (!atual.getHead().checaTipo(ambiente)) {
				return false;
			}
			atual = atual.getTail();
		}
		return true;
	}

	public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException,
			ProcedimentoNaoDeclaradoException, ClasseNaoDeclaradaException {
		if (!checaTipoArgumentos(parametrosReais, ambiente)) {
			return false;
		}
		boolean resposta;
		ambiente.incrementa();
		ListaDeclaracaoParametro formais = procedimento.getParametrosFormais();
		ListaTipo reais = parametrosReais.getTipos(ambiente);
		if (reais.length() == formais.length()) {
			resposta = true;
			while (resposta && reais.head() != null && formais.getHead() != null) {
				resposta = CompatibilidadeTipos.ehCompativel(formais.getHead().getTipo(), reais.head(), ambiente);
				reais = reais.tail();
				formais = (ListaDeclaracaoParametro) formais.getTail();
				if (reais == null || formais == null) {
					break;
				}
			}
		} else {
			resposta = false;
		}
		ambiente.restaura();
		return resposta;
	}
}
