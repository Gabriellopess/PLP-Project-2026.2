package loo3.plp.orientadaObjetos2.util;

import loo3.plp.expressions2.expression.Id;
import loo3.plp.expressions2.memory.Ambiente;
import loo3.plp.orientadaObjetos1.comando.Procedimento;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo3.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo3.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo3.plp.orientadaObjetos2.memoria.DefClasseOO2;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo3.plp.orientadaObjetos1.util.Tipo;
import loo3.plp.orientadaObjetos1.util.TipoPrimitivo;
import loo3.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;

public class HierarquiaUtils {

	/**
	 * Retorna <code>true</code> se <code>tipoFilho</code> eh um subtipo de <code>tipoPai</code>.
	 * Se <code>tipoFilho == tipoPai</code> OU <code>tipoFilho.equals(tipoPai)</code> forem <code>true</code>,
	 * este metodo retorna <code>false</code>.
	 * @param tipoFilho
	 * @param tipoPai
	 * @param ambiente
	 * @return
	 * @throws ClasseNaoDeclaradaException
	 */
	public static boolean ehSubTipo(Tipo tipoFilho, Tipo tipoPai, AmbienteCompilacaoOO2 ambiente) throws ClasseNaoDeclaradaException {
		if(tipoFilho instanceof TipoPrimitivo || tipoPai instanceof TipoPrimitivo){
			return false;
		}
		
		boolean ehSubTipo = false;
		Id idPai;
		SuperClasseMap superClasseMap = ambiente.getSuperClasse(tipoFilho.getTipo());
		
		if(superClasseMap != null){
			idPai = superClasseMap.getSuperClasse();
			
			while(idPai != null){
				if(idPai.equals(tipoPai.getTipo())){
					ehSubTipo = true;
					break;
				}
				SuperClasseMap superClassePai = ambiente.getSuperClasse(idPai);
				
				if(superClassePai != null){
					idPai = superClassePai.getSuperClasse();
				}else {
					idPai = null;
				}
			}
		}
		
		return ehSubTipo;
	}

	/**
	 * Procura o metodo <code>nomeMetodo</code> na classe dada e, se nao o
	 * encontrar, nas suas superclasses. Funciona tanto com o ambiente de
	 * compilacao quanto com o de execucao.
	 * @throws ProcedimentoNaoDeclaradoException quando nenhuma classe da
	 *         hierarquia declara o metodo.
	 */
	public static Procedimento getProcedimentoHierarquia(Ambiente ambiente, DefClasseOO2 defClasse, Id nomeMetodo)
			throws ClasseNaoDeclaradaException, ProcedimentoNaoDeclaradoException {
		Procedimento metodo = null;
		try {
			metodo = defClasse.getMetodo((loo3.plp.orientadaObjetos1.expressao.leftExpression.Id) nomeMetodo);
		} catch (ProcedimentoNaoDeclaradoException e) {
			if (defClasse.getNomeSuperClasse() != null) {
				DefClasseOO2 defClasseMae = null;
				if (ambiente instanceof AmbienteCompilacaoOO1) {
					defClasseMae = (DefClasseOO2) ((AmbienteCompilacaoOO1) ambiente).getDefClasse(defClasse.getNomeSuperClasse());
				} else if (ambiente instanceof AmbienteExecucaoOO1) {
					defClasseMae = (DefClasseOO2) ((AmbienteExecucaoOO1) ambiente).getDefClasse(defClasse.getNomeSuperClasse());
				}
				if (defClasseMae != null) {
					metodo = getProcedimentoHierarquia(ambiente, defClasseMae, nomeMetodo);
				}
			}
		}
		if (metodo == null) {
			throw new ProcedimentoNaoDeclaradoException(nomeMetodo);
		}
		return metodo;
	}
}
