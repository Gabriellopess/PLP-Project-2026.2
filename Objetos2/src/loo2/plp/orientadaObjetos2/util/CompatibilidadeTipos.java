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
import loo2.plp.orientadaObjetos2.memoria.DefProtocolo;

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
 * <code>null</code> ou uma subclasse dela;</li>
 * <li><code>esperado</code> e um protocolo e <code>real</code> e
 * <code>null</code>, uma classe que possui todos os metodos exigidos ou um
 * protocolo que exige pelo menos esses metodos (tipagem estrutural, ver
 * {@link ProtocoloUtils}).</li>
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
			DefProtocolo protocolo = ProtocoloUtils.getDefProtocolo(esperado.getTipo(), ambiente);
			if (protocolo != null) {
				return satisfazProtocolo(real, protocolo, ambiente);
			}
			if (real instanceof TipoClasse && ambiente instanceof AmbienteCompilacaoOO2) {
				return HierarquiaUtils.ehSubTipo(real, esperado, (AmbienteCompilacaoOO2) ambiente);
			}
		}
		return false;
	}

	/**
	 * Verifica se o tipo <code>real</code> (classe ou protocolo) satisfaz
	 * estruturalmente o protocolo esperado.
	 */
	private static boolean satisfazProtocolo(Tipo real, DefProtocolo protocolo, AmbienteCompilacaoOO1 ambiente)
			throws ClasseNaoDeclaradaException {
		if (!(real instanceof TipoClasse)) {
			return false;
		}
		DefProtocolo protocoloReal = ProtocoloUtils.getDefProtocolo(real.getTipo(), ambiente);
		if (protocoloReal != null) {
			return ProtocoloUtils.protocoloSatisfaz(protocoloReal, protocolo);
		}
		DefClasse classeReal = ambiente.getDefClasse(real.getTipo());
		return classeReal instanceof DefClasseOO2
				&& ProtocoloUtils.classeSatisfaz((DefClasseOO2) classeReal, protocolo, ambiente);
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
		Id classe = ambiente.getObjeto((ValorRef) valor).getClasse();
		Id esperada = formal.getTipo();
		// Protocolo: a classe real do objeto deve possuir os metodos exigidos.
		DefProtocolo protocolo = ProtocoloUtils.getDefProtocolo(esperada, ambiente);
		if (protocolo != null) {
			DefClasse def = ambiente.getDefClasse(classe);
			return def instanceof DefClasseOO2 && ProtocoloUtils.classeSatisfaz((DefClasseOO2) def, protocolo, ambiente);
		}
		// Tipo classe: a classe real do objeto deve ser a esperada ou uma subclasse.
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
	 * Descricao legivel de um valor para mensagens de erro: objetos sao
	 * descritos pela classe, os demais pelo proprio valor.
	 */
	private static String descreve(Valor valor, AmbienteExecucaoOO1 ambiente) throws ObjetoNaoDeclaradoException {
		if (valor instanceof ValorRef) {
			return "objeto da classe " + ambiente.getObjeto((ValorRef) valor).getClasse();
		}
		return String.valueOf(valor);
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
						+ " e incompativel: esperado " + formal + ", recebido " + descreve(valor, ambiente) + ".");
			}
			formais = (ListaDeclaracaoParametro) formais.getTail();
			valores = (ListaValor) valores.getTail();
			posicao++;
		}
	}
}
