package loo2.plp.orientadaObjetos2.declaracao.protocolo;

import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos2.declaracao.DecOO;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloJaDeclaradoException;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo2.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;
import loo2.plp.orientadaObjetos2.memoria.DefProtocolo;

/**
 * Declaracao de um protocolo: um nome e as assinaturas dos metodos que uma
 * classe precisa ter para satisfaze-lo (duck typing estatico).
 *
 * DecProtocolo ::= "protocolo" Id "{" ListaAssinatura "}"
 */
public class DecProtocolo implements DecOO {

	/**
	 * Nome do protocolo.
	 */
	private Id nome;

	/**
	 * Assinaturas dos metodos do protocolo.
	 */
	private ListaAssinatura assinaturas;

	/**
	 * Construtor.
	 *
	 * @param nome nome do protocolo.
	 * @param assinaturas assinaturas dos metodos do protocolo.
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

	/**
	 * Registra o protocolo no ambiente de execucao, onde ele e consultado
	 * pelas checagens de duck typing feitas durante a execucao.
	 *
	 * @param ambiente o ambiente de execucao.
	 * @return o ambiente modificado pela declaracao do protocolo.
	 */
	public AmbienteExecucaoOO2 elabora(AmbienteExecucaoOO2 ambiente)
			throws ProtocoloJaDeclaradoException {
		ambiente.mapProtocolo(nome, new DefProtocolo(nome, assinaturas));
		return ambiente;
	}

	/**
	 * Registra o protocolo e verifica as suas assinaturas: os tipos dos
	 * parametros devem ser validos e nenhum nome de metodo pode se repetir.
	 *
	 * O protocolo e registrado antes de as assinaturas serem verificadas
	 * (como DecClasseSimplesOO2 faz com a classe), para que uma assinatura
	 * possa usar o proprio protocolo como tipo de parametro.
	 *
	 * @param ambiente o ambiente de compilacao.
	 * @return <code>true</code> se as assinaturas estiverem bem tipadas;
	 *         <code>false</code> caso contrario.
	 */
	public boolean checaTipo(AmbienteCompilacaoOO2 ambiente)
			throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException,
			ProtocoloJaDeclaradoException {
		ambiente.mapProtocolo(nome, new DefProtocolo(nome, assinaturas));
		return assinaturas.checaTipo(ambiente);
	}
}
