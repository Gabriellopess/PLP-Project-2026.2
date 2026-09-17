package loo2.plp.orientadaObjetos2.declaracao.protocolo;

import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.imperative1.util.Lista;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoJaDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;

/**
 * Lista (nao vazia) de assinaturas de um protocolo.
 *
 * <pre>
 * ListaAssinatura ::= Assinatura | Assinatura "," ListaAssinatura
 * </pre>
 */
public class ListaAssinatura extends Lista<Assinatura> {

	public ListaAssinatura() {
	}

	public ListaAssinatura(Assinatura assinatura) {
		super(assinatura, new ListaAssinatura());
	}

	public ListaAssinatura(Assinatura assinatura, ListaAssinatura lista) {
		super(assinatura, lista);
	}

	/**
	 * Recupera a assinatura do metodo de nome dado.
	 *
	 * @throws ProcedimentoNaoDeclaradoException quando o protocolo nao possui
	 *         metodo com esse nome.
	 */
	public Assinatura getAssinatura(Id nome) throws ProcedimentoNaoDeclaradoException {
		ListaAssinatura atual = this;
		while (atual != null && atual.getHead() != null) {
			if (atual.getHead().getNome().equals(nome)) {
				return atual.getHead();
			}
			atual = (ListaAssinatura) atual.getTail();
		}
		throw new ProcedimentoNaoDeclaradoException(nome);
	}

	/**
	 * Indica se existe assinatura com o nome dado.
	 */
	public boolean contem(Id nome) {
		try {
			getAssinatura(nome);
			return true;
		} catch (ProcedimentoNaoDeclaradoException e) {
			return false;
		}
	}

	/**
	 * Verifica se os tipos dos parametros de todas as assinaturas sao validos
	 * e se nao ha dois metodos com o mesmo nome.
	 *
	 * @throws ProcedimentoJaDeclaradoException quando um nome se repete.
	 */
	public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
			throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException,
			ProcedimentoJaDeclaradoException {
		boolean resposta = true;
		ListaAssinatura atual = this;
		while (resposta && atual != null && atual.getHead() != null) {
			Assinatura assinatura = atual.getHead();
			ListaAssinatura resto = (ListaAssinatura) atual.getTail();
			if (resto != null && resto.contem(assinatura.getNome())) {
				throw new ProcedimentoJaDeclaradoException(assinatura.getNome());
			}
			resposta = assinatura.checaTipo(ambiente);
			atual = resto;
		}
		return resposta;
	}
}
