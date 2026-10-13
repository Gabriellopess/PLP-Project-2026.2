package loo2.plp.orientadaObjetos2.comando;

import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.comando.Atribuicao;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.Expressao;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.LeftExpression;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos2.util.CompatibilidadeTipos;

public class AtribuicaoOO2 extends Atribuicao {

	public AtribuicaoOO2(LeftExpression av, Expressao expressao) {
		super(av, expressao);
	}
	
	/**
	 * A atribuicao x := e esta bem tipada se o tipo de e for compativel com o
	 * tipo de x (igualdade, null, heranca, protocolos e dyn).
	 */
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente) throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
    	return expressao.checaTipo(ambiente)
    			&& CompatibilidadeTipos.ehCompativel(expressao.getTipo(ambiente), av.getTipo(ambiente), ambiente);
    }

}
