package loo2.plp.orientadaObjetos2.declaracao;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoJaDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo2.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;

/**
 * Declaracao de alto nivel de um programa OO2: uma classe ou um protocolo.
 *
 * <pre>
 * DecOO ::= DecClasse | DecProtocolo
 * </pre>
 */
public interface DecOO {

	/**
	 * Elabora a declaracao no ambiente de execucao.
	 *
	 * @param ambiente o ambiente de execucao.
	 * @return o ambiente modificado pela declaracao.
	 */
	public AmbienteExecucaoOO2 elabora(AmbienteExecucaoOO2 ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
			ClasseJaDeclaradaException, ClasseNaoDeclaradaException,
			ProcedimentoNaoDeclaradoException, ProcedimentoJaDeclaradoException,
			ConstrutorNaoDeclaradoException;

	/**
	 * Verifica se a declaracao esta bem tipada, registrando-a no ambiente de
	 * compilacao.
	 *
	 * @param ambiente o ambiente de compilacao.
	 * @return <code>true</code> se a declaracao esta bem tipada;
	 *         <code>false</code> caso contrario.
	 */
	public boolean checaTipo(AmbienteCompilacaoOO2 ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
			ClasseJaDeclaradaException, ClasseNaoDeclaradaException,
			ProcedimentoNaoDeclaradoException, ProcedimentoJaDeclaradoException,
			ConstrutorNaoDeclaradoException;
}
