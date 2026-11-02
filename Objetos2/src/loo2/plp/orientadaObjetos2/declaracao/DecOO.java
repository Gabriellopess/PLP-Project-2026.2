package loo2.plp.orientadaObjetos2.declaracao;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoJaDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloJaDeclaradoException;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo2.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;

/**
 * Declaracao de topo de um programa OO2: uma classe ou um protocolo.
 *
 * DecOO ::= DecClasse | DecProtocolo
 */
public interface DecOO {

	/**
	 * Registra a declaracao no ambiente de execucao.
	 *
	 * @param ambiente o ambiente de execucao.
	 * @return o ambiente modificado pela declaracao.
	 */
	public AmbienteExecucaoOO2 elabora(AmbienteExecucaoOO2 ambiente)
			throws ClasseJaDeclaradaException, ClasseNaoDeclaradaException,
			ConstrutorNaoDeclaradoException, ProtocoloJaDeclaradoException;

	/**
	 * Registra a declaracao no ambiente de compilacao e verifica se ela esta
	 * bem tipada.
	 *
	 * @param ambiente o ambiente de compilacao.
	 * @return <code>true</code> se a declaracao estiver bem tipada;
	 *         <code>false</code> caso contrario.
	 */
	public boolean checaTipo(AmbienteCompilacaoOO2 ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
			ClasseJaDeclaradaException, ClasseNaoDeclaradaException,
			ProcedimentoNaoDeclaradoException, ProcedimentoJaDeclaradoException,
			ConstrutorNaoDeclaradoException, ProtocoloJaDeclaradoException;
}
