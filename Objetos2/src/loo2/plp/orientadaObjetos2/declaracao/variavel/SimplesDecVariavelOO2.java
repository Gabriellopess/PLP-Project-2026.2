package loo2.plp.orientadaObjetos2.declaracao.variavel;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.declaracao.variavel.SimplesDecVariavel;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.Expressao;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos2.util.CompatibilidadeTipos;

/**
 * Declaracao <code>Tipo Id = Expressao</code> em OO2. A verificacao de tipos
 * usa as regras de compatibilidade de OO2 (subclasses, <code>dyn</code>,
 * protocolos) em vez da igualdade estrita de tipos de OO1.
 */
public class SimplesDecVariavelOO2 extends SimplesDecVariavel {

	public SimplesDecVariavelOO2(Tipo tipo, Id id, Expressao expressao) {
		super(tipo, id, expressao);
	}

	public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException,
			ClasseNaoDeclaradaException {
		boolean resposta = false;
		if (tipo.eValido(ambiente) && expressao.checaTipo(ambiente)) {
			resposta = CompatibilidadeTipos.ehCompativel(tipo, expressao.getTipo(ambiente), ambiente);
		}
		if (resposta) {
			ambiente.map(id, tipo);
		}
		return resposta;
	}
}
