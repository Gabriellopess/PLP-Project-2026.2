package loo3.plp.orientadaObjetos3.declaracao;

import loo3.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo3.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoJaDeclaradoException;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo3.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo3.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;
import loo3.plp.orientadaObjetos2.declaracao.ConstrutorNaoDeclaradoException;

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
