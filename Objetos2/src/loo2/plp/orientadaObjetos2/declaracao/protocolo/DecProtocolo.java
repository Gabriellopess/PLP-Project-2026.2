package loo2.plp.orientadaObjetos2.declaracao.protocolo;

import java.util.HashSet;
import java.util.Set;

import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.imperative1.util.Lista;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos2.declaracao.DecOO;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloJaDeclaradoException;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo2.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;
import loo2.plp.orientadaObjetos2.memoria.DefProtocolo;

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

	/**
	 * Registra o protocolo no ambiente de execucao. Protocolos nao executam
	 * nada, mas a verificacao dinamica de dyn (regra R7) precisa consulta-los.
	 */
	public AmbienteExecucaoOO2 elabora(AmbienteExecucaoOO2 ambiente) throws ClasseJaDeclaradaException {
		ambiente.mapProtocolo(nome, new DefProtocolo(nome, assinaturas));
		return ambiente;
	}

	/**
	 * Um protocolo esta bem tipado se seu nome ainda nao foi usado por outra
	 * classe ou protocolo, se nao repete nomes de metodo e se cada
	 * assinatura esta bem tipada.
	 *
	 * O protocolo e registrado antes de verificar as assinaturas, para que
	 * elas possam citar o proprio protocolo como tipo de parametro.
	 */
	public boolean checaTipo(AmbienteCompilacaoOO2 ambiente)
			throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
		try {
			ambiente.mapProtocolo(nome, new DefProtocolo(nome, assinaturas));
		} catch (ProtocoloJaDeclaradoException e) {
			return false;
		}

		Set<String> nomesMetodos = new HashSet<String>();
		Lista<Assinatura> lista = assinaturas;
		while (lista != null && lista.getHead() != null) {
			Assinatura assinatura = lista.getHead();
			if (!nomesMetodos.add(assinatura.getNome().getIdName()) || !assinatura.checaTipo(ambiente)) {
				return false;
			}
			lista = lista.getTail();
		}
		return true;
	}
}
