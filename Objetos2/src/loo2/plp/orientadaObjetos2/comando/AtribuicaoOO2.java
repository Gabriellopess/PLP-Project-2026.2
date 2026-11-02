package loo2.plp.orientadaObjetos2.comando;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.comando.Atribuicao;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.expressao.Expressao;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.LeftExpression;
import loo2.plp.orientadaObjetos1.expressao.valor.Valor;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos2.excecao.ErroDuckTypingException;
import loo2.plp.orientadaObjetos2.util.CompatibilidadeTipos;
import loo2.plp.orientadaObjetos2.util.TipoDinamico;

public class AtribuicaoOO2 extends Atribuicao {

	/**
	 * Tipo de x, guardado pelo checaTipo quando e e dyn e x nao e: a regra R7
	 * precisa ser conferida no executar, com o valor real. O no da AST nao
	 * guarda tipos estaticos, e o checaTipo sempre roda antes do executar.
	 */
	private Tipo tipoConferido = null;

	public AtribuicaoOO2(LeftExpression av, Expressao expressao) {
		super(av, expressao);
	}
	
	/**
	 * A atribuicao x := e esta bem tipada se o tipo de e for compativel com o
	 * tipo de x (igualdade, null, heranca, protocolos e dyn). O lado esquerdo
	 * e verificado antes de se ler o seu tipo, para que um atributo
	 * inexistente, ou acessado por dyn ou protocolo, seja erro de tipo.
	 */
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente) throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
    	if (!av.checaTipo(ambiente) || !expressao.checaTipo(ambiente)) {
    		return false;
    	}
    	Tipo tipoExpressao = expressao.getTipo(ambiente);
    	Tipo tipoVariavel = av.getTipo(ambiente);
    	tipoConferido = (tipoExpressao instanceof TipoDinamico && !(tipoVariavel instanceof TipoDinamico))
    			? tipoVariavel : null;
    	return CompatibilidadeTipos.ehCompativel(tipoExpressao, tipoVariavel, ambiente);
    }

    public AmbienteExecucaoOO1 executar(AmbienteExecucaoOO1 ambiente)
    		throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
    		ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException {
    	if (tipoConferido == null) {
    		return super.executar(ambiente);
    	}
    	Valor valor = expressao.avaliar(ambiente);
    	if (!CompatibilidadeTipos.valorCompativel(valor, tipoConferido, ambiente)) {
    		throw new ErroDuckTypingException("valor de " + av.getId() + ": "
    				+ CompatibilidadeTipos.tipoDoValor(valor, ambiente) + " n\u00e3o satisfaz " + tipoConferido);
    	}
    	return new Atribuicao(av, valor).executar(ambiente);
    }

}
