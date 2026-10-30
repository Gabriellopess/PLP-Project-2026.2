package loo2.plp.orientadaObjetos2.expressao;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.expressao.Expressao;
import loo2.plp.orientadaObjetos1.expressao.valor.Valor;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos1.util.TipoPrimitivo;
import loo2.plp.orientadaObjetos2.excecao.ErroDuckTypingException;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo2.plp.orientadaObjetos2.util.CompatibilidadeTipos;
import loo2.plp.orientadaObjetos2.util.TipoDinamico;

/**
 * Cast explicito: <code>cast(T, e)</code> trata o valor de e como sendo do
 * tipo T (classe ou protocolo).
 *
 * Na compilacao, o cast e aceito se T e o tipo de e podem se relacionar:
 * T &lt;= tipo de e, tipo de e &lt;= T, ou T protocolo (uma subclasse do tipo
 * de e pode satisfaze-lo). O tipo da expressao e T.
 * Na execucao, a classe real do objeto precisa ser compativel com T
 * (CompatibilidadeTipos.valorCompativel); caso contrario, e lancado
 * ErroDuckTypingException.
 *
 * ExpCast ::= "cast" "(" Tipo "," Expressao ")"
 */
public class ExpCast implements Expressao {

	private final Tipo alvo;
	private final Expressao expressao;

	public ExpCast(Tipo alvo, Expressao expressao) {
		this.alvo = alvo;
		this.expressao = expressao;
	}

	public Valor avaliar(AmbienteExecucaoOO1 ambiente) throws VariavelNaoDeclaradaException,
			VariavelJaDeclaradaException, ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException {
		Valor valor = expressao.avaliar(ambiente);
		if (!CompatibilidadeTipos.valorCompativel(valor, alvo, ambiente)) {
			throw new ErroDuckTypingException("cast: " + CompatibilidadeTipos.tipoDoValor(valor, ambiente)
					+ " não satisfaz " + alvo);
		}
		return valor;
	}

	public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
			throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
		if (!expressao.checaTipo(ambiente) || !alvo.eValido(ambiente)
				|| !(ambiente instanceof AmbienteCompilacaoOO2)) {
			return false;
		}
		Tipo origem = expressao.getTipo(ambiente);
		if (origem instanceof TipoPrimitivo || alvo instanceof TipoPrimitivo) {
			return false;
		}
		if (origem instanceof TipoDinamico || alvo instanceof TipoDinamico) {
			return true;
		}
		boolean alvoProtocolo = ((AmbienteCompilacaoOO2) ambiente).ehProtocolo(alvo.getTipo());
		return alvoProtocolo
				|| CompatibilidadeTipos.ehCompativel(origem, alvo, ambiente)
				|| CompatibilidadeTipos.ehCompativel(alvo, origem, ambiente);
	}

	public Tipo getTipo(AmbienteCompilacaoOO1 ambiente) {
		return alvo;
	}
}
