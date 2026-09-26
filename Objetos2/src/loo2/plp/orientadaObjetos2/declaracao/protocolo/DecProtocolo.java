package loo2.plp.orientadaObjetos2.declaracao.protocolo;

import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos2.declaracao.DecOO;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo2.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;

/**
 * Declaracao de um protocolo: um conjunto de assinaturas de metodos que
 * qualquer classe satisfaz estruturalmente, sem declarar isso.
 *
 * DecProtocolo ::= "protocolo" Id "{" ListaAssinatura "}"
 */
public class DecProtocolo implements DecOO {

	/**
	 * Nome do protocolo.
	 */
	private Id nome;

	/**
	 * Assinaturas exigidas pelo protocolo.
	 */
	private ListaAssinatura assinaturas;

	/**
	 * Construtor.
	 * @param nome Nome do protocolo.
	 * @param assinaturas Assinaturas exigidas pelo protocolo.
	 */
	public DecProtocolo(Id nome, ListaAssinatura assinaturas) {
		this.nome = nome;
		this.assinaturas = assinaturas;
	}

	public Id getNome() {
		return nome;
	}

	public ListaAssinatura getAssinaturas() {
		return assinaturas;
	}

	// TODO etapa 3: registrar o protocolo no ambiente de execucao.
	public AmbienteExecucaoOO2 elabora(AmbienteExecucaoOO2 ambiente) {
		throw new UnsupportedOperationException(
				"Protocolo " + nome + ": elaboracao de protocolos ainda nao implementada");
	}

	// TODO etapa 3: validar as assinaturas e registrar o protocolo no ambiente de compilacao.
	public boolean checaTipo(AmbienteCompilacaoOO2 ambiente) {
		throw new UnsupportedOperationException(
				"Protocolo " + nome + ": verificacao de protocolos ainda nao implementada");
	}
}
