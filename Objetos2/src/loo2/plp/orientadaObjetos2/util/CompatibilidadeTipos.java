package loo2.plp.orientadaObjetos2.util;

import loo2.plp.orientadaObjetos1.declaracao.procedimento.ListaDeclaracaoParametro;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.expressao.valor.Valor;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorBooleano;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorInteiro;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorNull;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorRef;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorString;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.DefClasse;
import loo2.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos1.util.TipoClasse;
import loo2.plp.orientadaObjetos1.util.TipoPrimitivo;
import loo2.plp.orientadaObjetos2.excecao.execucao.ChamadaMetodoInvalidaException;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo2.plp.orientadaObjetos2.memoria.DefClasseOO2;

/**
 * Regras de compatibilidade entre tipos de OO2, centralizadas em um unico
 * ponto para que declaracao, atribuicao, <code>new</code> e passagem de
 * parametros se comportem da mesma forma.
 *
 * Um valor de tipo <code>real</code> pode ser usado onde se espera um valor
 * de tipo <code>esperado</code> quando:
 * <ul>
 * <li><code>esperado</code> e <code>dyn</code> e <code>real</code> e um tipo
 * de referencia (classe, <code>null</code> ou <code>dyn</code>);</li>
 * <li>os tipos sao iguais;</li>
 * <li><code>esperado</code> e uma classe e <code>real</code> e
 * <code>null</code> ou uma subclasse dela.</li>
 * </ul>
 * Um valor <code>dyn</code> nunca e aceito onde se espera um tipo estatico:
 * nao ha como garantir a compatibilidade antes da execucao.
 */
public class CompatibilidadeTipos {

	/**
	 * Verifica, em tempo de compilacao, se um valor de tipo <code>real</code>
	 * pode ser usado onde se espera o tipo <code>esperado</code>.
	 */
	public static boolean ehCompativel(Tipo esperado, Tipo real, AmbienteCompilacaoOO1 ambiente)
			throws ClasseNaoDeclaradaException {
		if (esperado instanceof TipoDinamico) {
			return ehTipoReferencia(real);
		}
		if (real instanceof TipoDinamico) {
			return false;
		}
		if (esperado.equals(real)) {
			return true;
		}
		if (esperado instanceof TipoClasse) {
			if (real.equals(TipoClasse.TIPO_NULL)) {
				return true;
			}
			if (real instanceof TipoClasse && ambiente instanceof AmbienteCompilacaoOO2) {
				return HierarquiaUtils.ehSubTipo(real, esperado, (AmbienteCompilacaoOO2) ambiente);
			}
		}
		return false;
	}

	/**
	 * Indica se o tipo pode referenciar um objeto (classe, null ou dyn).
	 */
	public static boolean ehTipoReferencia(Tipo tipo) {
		return tipo instanceof TipoClasse || tipo instanceof TipoDinamico;
	}

	/**
	 * Verifica, em tempo de execucao, se um valor e compativel com o tipo
	 * formal de um parametro. Usado nas chamadas de metodo sobre valores
	 * <code>dyn</code>, que nao passam pela verificacao estatica.
	 */
	public static boolean ehCompativelEmExecucao(Tipo formal, Valor valor, AmbienteExecucaoOO1 ambiente)
			throws ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException {
		if (formal instanceof TipoPrimitivo) {
			TipoPrimitivo primitivo = (TipoPrimitivo) formal;
			return (primitivo.eInteiro() && valor instanceof ValorInteiro)
					|| (primitivo.eBooleano() && valor instanceof ValorBooleano)
					|| (primitivo.eString() && valor instanceof ValorString);
		}
		if (valor instanceof ValorNull) {
			return true;
		}
		if (!(valor instanceof ValorRef)) {
			return false;
		}
		if (formal instanceof TipoDinamico) {
			return true;
		}
		// Tipo classe: a classe real do objeto deve ser a esperada ou uma subclasse.
		Id classe = ambiente.getObjeto((ValorRef) valor).getClasse();
		Id esperada = formal.getTipo();
		while (classe != null) {
			if (classe.equals(esperada)) {
				return true;
			}
			DefClasse def = ambiente.getDefClasse(classe);
			classe = (def instanceof DefClasseOO2) ? ((DefClasseOO2) def).getNomeSuperClasse() : null;
		}
		return false;
	}

	/**
	 * Verifica, em tempo de execucao, se os valores dos argumentos de uma
	 * chamada sao compativeis com os parametros formais do metodo.
	 *
	 * @throws ChamadaMetodoInvalidaException quando o numero de argumentos ou
	 *         o tipo de algum deles nao corresponde ao esperado.
	 */
	public static void checaArgumentosEmExecucao(Id nomeMetodo, ListaDeclaracaoParametro formais,
			ListaValor valores, AmbienteExecucaoOO1 ambiente)
			throws ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException {
		if (formais.length() != valores.length()) {
			throw new ChamadaMetodoInvalidaException("Metodo " + nomeMetodo + " espera "
					+ formais.length() + " argumento(s), mas recebeu " + valores.length() + ".");
		}
		int posicao = 1;
		while (valores.length() > 0) {
			Tipo formal = formais.getHead().getTipo();
			Valor valor = valores.getHead();
			if (!ehCompativelEmExecucao(formal, valor, ambiente)) {
				throw new ChamadaMetodoInvalidaException("Argumento " + posicao + " de " + nomeMetodo
						+ " e incompativel: esperado " + formal + ", recebido " + valor + ".");
			}
			formais = (ListaDeclaracaoParametro) formais.getTail();
			valores = (ListaValor) valores.getTail();
			posicao++;
		}
	}
}
