package loo2.plp.orientadaObjetos2.util;

import java.util.ArrayList;
import java.util.List;

import loo2.plp.orientadaObjetos1.comando.Procedimento;
import loo2.plp.orientadaObjetos1.declaracao.procedimento.ListaDeclaracaoParametro;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.expressao.valor.Valor;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorBooleano;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorInteiro;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorRef;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorString;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos1.util.TipoClasse;
import loo2.plp.orientadaObjetos1.util.TipoPrimitivo;
import loo2.plp.orientadaObjetos2.declaracao.protocolo.Assinatura;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloNaoDeclaradoException;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo2.plp.orientadaObjetos2.memoria.AmbienteProtocolosOO2;
import loo2.plp.orientadaObjetos2.memoria.DefClasseOO2;
import loo2.plp.orientadaObjetos2.memoria.DefProtocolo;

/**
 * Relacao de compatibilidade de tipos T &lt;= U ("um valor do tipo T pode ser
 * usado onde se espera U"), que substitui as comparacoes por igualdade:
 *
 * R1  T &lt;= T
 * R2  null &lt;= U, se U e classe, protocolo ou dyn
 * R3  C &lt;= D, se a classe C herda (transitivamente) da classe D
 * R4  C &lt;= P, se a classe C satisfaz o protocolo P
 * R5  P &lt;= Q, se o protocolo P tem todas as assinaturas de Q
 * R6  T &lt;= dyn, para todo T nao primitivo
 * R7  dyn &lt;= U, para todo U nao primitivo (conferido na execucao)
 *
 * Tipos primitivos so sao compativeis consigo mesmos.
 */
public class CompatibilidadeTipos {

	private CompatibilidadeTipos() {
	}

	/**
	 * Indica se um valor do tipo t pode ser usado onde se espera o tipo u.
	 */
	public static boolean ehCompativel(Tipo t, Tipo u, AmbienteCompilacaoOO1 ambiente)
			throws ClasseNaoDeclaradaException {
		if (t.equals(u)) {
			return true;                                         // R1
		}
		if (t instanceof TipoPrimitivo || u instanceof TipoPrimitivo) {
			return false;
		}
		if (u instanceof TipoDinamico || t instanceof TipoDinamico) {
			return true;                                         // R2 e R6 (u dyn), R7 (t dyn)
		}
		if (t.equals(TipoClasse.TIPO_NULL)) {
			return true;                                         // R2
		}
		if (!(ambiente instanceof AmbienteCompilacaoOO2)) {
			return false;
		}
		AmbienteCompilacaoOO2 amb = (AmbienteCompilacaoOO2) ambiente;
		boolean tProtocolo = amb.ehProtocolo(t.getTipo());
		boolean uProtocolo = amb.ehProtocolo(u.getTipo());
		if (!tProtocolo && !uProtocolo) {
			return HierarquiaUtils.ehSubTipo(t, u, amb);         // R3
		}
		if (!tProtocolo && uProtocolo) {
			return satisfaz(t.getTipo(), u.getTipo(), amb);      // R4
		}
		if (tProtocolo && uProtocolo) {
			return contem(t.getTipo(), u.getTipo(), amb);        // R5
		}
		return false;  // protocolo -> classe: nao ha como garantir estaticamente
	}

	/**
	 * Tipo de um valor em tempo de execucao: o tipo primitivo, null, ou a
	 * classe real do objeto referenciado. Nunca devolve dyn nem protocolo.
	 */
	public static Tipo tipoDoValor(Valor valor, AmbienteExecucaoOO1 ambiente)
			throws ObjetoNaoDeclaradoException {
		if (valor instanceof ValorInteiro) {
			return TipoPrimitivo.TIPO_INTEIRO;
		}
		if (valor instanceof ValorBooleano) {
			return TipoPrimitivo.TIPO_BOOLEANO;
		}
		if (valor instanceof ValorString) {
			return TipoPrimitivo.TIPO_STRING;
		}
		if (valor instanceof ValorRef) {
			return new TipoClasse(ambiente.getObjeto((ValorRef) valor).getClasse());
		}
		return TipoClasse.TIPO_NULL;
	}

	/**
	 * A relacao de compatibilidade na execucao: tipoDoValor(valor) &lt;= u.
	 * Como o tipo do valor e sempre primitivo, null ou uma classe concreta,
	 * bastam R1, R2, R3, R4 e R6. E aqui que a regra R7 e conferida.
	 */
	public static boolean valorCompativel(Valor valor, Tipo u, AmbienteExecucaoOO1 ambiente)
			throws ObjetoNaoDeclaradoException {
		Tipo t = tipoDoValor(valor, ambiente);
		if (t.equals(u)) {
			return true;                                         // R1
		}
		if (t instanceof TipoPrimitivo || u instanceof TipoPrimitivo) {
			return false;
		}
		if (u instanceof TipoDinamico || t.equals(TipoClasse.TIPO_NULL)) {
			return true;                                         // R2 e R6
		}
		Definicoes defs = definicoes(ambiente);
		if (defs.defProtocolo(u.getTipo()) != null) {
			return satisfaz(t.getTipo(), u.getTipo(), defs);     // R4
		}
		return ehSubclasse(t.getTipo(), u, defs);                // R3
	}

	/**
	 * Indica se a classe, ou uma de suas superclasses, e o tipo u.
	 */
	private static boolean ehSubclasse(Id classe, Tipo u, Definicoes defs) {
		DefClasseOO2 defClasse = defs.defClasse(classe);
		while (defClasse != null) {
			Id superClasse = defClasse.getNomeSuperClasse();
			if (superClasse == null) {
				return false;
			}
			if (new TipoClasse(superClasse).equals(u)) {
				return true;
			}
			defClasse = defs.defClasse(superClasse);
		}
		return false;
	}

	/**
	 * Indica se a classe satisfaz o protocolo: para cada assinatura do
	 * protocolo, a classe (ou uma superclasse) tem um metodo com o mesmo nome
	 * e exatamente os mesmos tipos de parametros, na mesma ordem.
	 */
	public static boolean satisfaz(Id classe, Id protocolo, AmbienteCompilacaoOO2 ambiente) {
		return satisfaz(classe, protocolo, definicoes(ambiente));
	}

	/**
	 * Indica se o protocolo p tem todas as assinaturas do protocolo q, com os
	 * mesmos tipos de parametros.
	 */
	public static boolean contem(Id p, Id q, AmbienteCompilacaoOO2 ambiente) {
		Definicoes defs = definicoes(ambiente);
		DefProtocolo defP = defs.defProtocolo(p);
		DefProtocolo defQ = defs.defProtocolo(q);
		if (defP == null || defQ == null) {
			return false;
		}
		for (Assinatura assinaturaQ : defQ.getAssinaturas()) {
			Assinatura assinaturaP = defP.getAssinatura(assinaturaQ.getNome());
			if (assinaturaP == null
					|| !mesmosTipos(assinaturaP.getParametros(), assinaturaQ.getParametros())) {
				return false;
			}
		}
		return true;
	}

	private static boolean satisfaz(Id classe, Id protocolo, Definicoes defs) {
		DefProtocolo defProtocolo = defs.defProtocolo(protocolo);
		if (defProtocolo == null) {
			return false;
		}
		for (Assinatura assinatura : defProtocolo.getAssinaturas()) {
			Procedimento metodo = buscaMetodo(classe, assinatura.getNome(), defs);
			if (metodo == null
					|| !mesmosTipos(metodo.getParametrosFormais(), assinatura.getParametros())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Procura o metodo na classe e, se nao achar, nas superclasses (a mesma
	 * busca que ChamadaMetodoOO2 faz). Retorna null se nao encontrar ou se
	 * a classe nao estiver declarada.
	 */
	private static Procedimento buscaMetodo(Id classe, Id nomeMetodo, Definicoes defs) {
		DefClasseOO2 defClasse = defs.defClasse(classe);
		while (defClasse != null) {
			try {
				return defClasse.getMetodo(nomeMetodo);
			} catch (ProcedimentoNaoDeclaradoException e) {
				Id superClasse = defClasse.getNomeSuperClasse();
				defClasse = (superClasse == null) ? null : defs.defClasse(superClasse);
			}
		}
		return null;
	}

	/**
	 * Compara duas listas de parametros pelos tipos (invariancia); os nomes
	 * dos parametros nao importam.
	 */
	private static boolean mesmosTipos(ListaDeclaracaoParametro a, ListaDeclaracaoParametro b) {
		List<Tipo> tiposA = tipos(a);
		List<Tipo> tiposB = tipos(b);
		return tiposA.equals(tiposB);
	}

	private static List<Tipo> tipos(ListaDeclaracaoParametro lista) {
		List<Tipo> resposta = new ArrayList<Tipo>();
		ListaDeclaracaoParametro atual = lista;
		while (atual != null && atual.getHead() != null) {
			resposta.add(atual.getHead().getTipo());
			atual = (ListaDeclaracaoParametro) atual.getTail();
		}
		return resposta;
	}

	/**
	 * Consulta de classes e protocolos. Ha uma implementacao para o ambiente
	 * de compilacao e outra para o de execucao, para que satisfaz e
	 * buscaMetodo sirvam nas duas fases sem duplicar codigo.
	 */
	private interface Definicoes {
		/** Definicao da classe, ou null se nao estiver declarada. */
		DefClasseOO2 defClasse(Id classe);

		/** Definicao do protocolo, ou null se nao estiver declarado. */
		DefProtocolo defProtocolo(Id protocolo);
	}

	private static Definicoes definicoes(final AmbienteCompilacaoOO2 ambiente) {
		return new Definicoes() {
			public DefClasseOO2 defClasse(Id classe) {
				try {
					return (DefClasseOO2) ambiente.getDefClasse(classe);
				} catch (ClasseNaoDeclaradaException e) {
					return null;
				}
			}

			public DefProtocolo defProtocolo(Id protocolo) {
				try {
					return ambiente.getDefProtocolo(protocolo);
				} catch (ProtocoloNaoDeclaradoException e) {
					return null;
				}
			}
		};
	}

	private static Definicoes definicoes(final AmbienteExecucaoOO1 ambiente) {
		return new Definicoes() {
			public DefClasseOO2 defClasse(Id classe) {
				try {
					return (DefClasseOO2) ambiente.getDefClasse(classe);
				} catch (ClasseNaoDeclaradaException e) {
					return null;
				}
			}

			public DefProtocolo defProtocolo(Id protocolo) {
				if (!(ambiente instanceof AmbienteProtocolosOO2)) {
					return null;
				}
				try {
					return ((AmbienteProtocolosOO2) ambiente).getDefProtocolo(protocolo);
				} catch (ProtocoloNaoDeclaradoException e) {
					return null;
				}
			}
		};
	}
}
