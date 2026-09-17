package loo3.plp.orientadaObjetos3.declaracao.protocolo;

import loo3.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoJaDeclaradoException;
import loo3.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo3.plp.orientadaObjetos3.declaracao.DecOO;
import loo3.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo3.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;
import loo3.plp.orientadaObjetos3.memoria.DefProtocolo;

/**
 * Declaracao de um protocolo: um conjunto de assinaturas de metodos que uma
 * classe satisfaz estruturalmente, sem precisar declarar isso.
 *
 * <pre>
 * DecProtocolo ::= "protocolo" Id "{" ListaAssinatura "}"
 * </pre>
 */
public class DecProtocolo implements DecOO {

	/**
	 * Nome do protocolo.
	 */
	private Id nome;

	/**
	 * Assinaturas exigidas.
	 */
	private ListaAssinatura assinaturas;

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
	 * Registra o protocolo no ambiente de execucao. Ele nao participa da
	 * execucao normal, mas e consultado na checagem em tempo de execucao das
	 * chamadas sobre valores <code>dyn</code> cujos parametros sao protocolos.
	 */
	public AmbienteExecucaoOO2 elabora(AmbienteExecucaoOO2 ambiente) throws ClasseJaDeclaradaException {
		ambiente.mapDefProtocolo(nome, new DefProtocolo(nome, assinaturas));
		return ambiente;
	}

	/**
	 * Registra o protocolo no ambiente de compilacao e verifica se as
	 * assinaturas estao bem formadas (tipos validos, nomes sem repeticao).
	 *
	 * @throws ClasseJaDeclaradaException quando ja existe classe ou protocolo
	 *         com o mesmo nome.
	 */
	public boolean checaTipo(AmbienteCompilacaoOO2 ambiente)
			throws VariavelNaoDeclaradaException, ClasseJaDeclaradaException,
			ClasseNaoDeclaradaException, ProcedimentoJaDeclaradoException {
		// Classes e protocolos compartilham o espaco de nomes dos tipos.
		boolean classeExiste;
		try {
			ambiente.getDefClasse(nome);
			classeExiste = true;
		} catch (ClasseNaoDeclaradaException e) {
			classeExiste = false;
		}
		if (classeExiste) {
			throw new ClasseJaDeclaradaException(nome);
		}

		// Registra antes de checar as assinaturas, para que um protocolo possa
		// referenciar a si mesmo como tipo de parametro.
		ambiente.mapDefProtocolo(nome, new DefProtocolo(nome, assinaturas));

		return assinaturas.checaTipo(ambiente);
	}
}
