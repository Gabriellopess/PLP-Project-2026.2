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
     * Uma atribuicao esta bem tipada quando o tipo da expressao e compativel
     * com o tipo declarado do lado esquerdo: mesmo tipo, subclasse, null em
     * variavel de classe, ou qualquer objeto em variavel <code>dyn</code>.
     */
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente) throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
    	boolean retorno = false;
    	if (av.checaTipo(ambiente) && expressao.checaTipo(ambiente)) {
    		retorno = CompatibilidadeTipos.ehCompativel(av.getTipo(ambiente), expressao.getTipo(ambiente), ambiente);
    	}
    	return retorno;
    }
}
