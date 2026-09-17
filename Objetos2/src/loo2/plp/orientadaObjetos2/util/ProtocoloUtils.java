package loo2.plp.orientadaObjetos2.util;

import loo2.plp.expressions2.expression.Id;
import loo2.plp.expressions2.memory.Ambiente;
import loo2.plp.orientadaObjetos1.comando.Procedimento;
import loo2.plp.orientadaObjetos1.declaracao.procedimento.ListaDeclaracaoParametro;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo2.plp.orientadaObjetos2.declaracao.protocolo.Assinatura;
import loo2.plp.orientadaObjetos2.declaracao.protocolo.ListaAssinatura;
import loo2.plp.orientadaObjetos2.excecao.declaracao.ProtocoloNaoDeclaradoException;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo2.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;
import loo2.plp.orientadaObjetos2.memoria.DefClasseOO2;
import loo2.plp.orientadaObjetos2.memoria.DefProtocolo;

/**
 * Tipagem estrutural de protocolos: uma classe satisfaz um protocolo quando
 * possui (ela mesma ou por heranca) todos os metodos exigidos, com o mesmo
 * nome e a mesma lista de tipos de parametros. Nada precisa ser declarado
 * na classe.
 */
public class ProtocoloUtils {

	/**
	 * Recupera a definicao de um protocolo, seja no ambiente de compilacao
	 * seja no de execucao, ou <code>null</code> se o nome nao for de protocolo.
	 */
	public static DefProtocolo getDefProtocolo(Id nome, Ambiente ambiente) {
		try {
			if (ambiente instanceof AmbienteCompilacaoOO2) {
				return ((AmbienteCompilacaoOO2) ambiente).getDefProtocolo(nome);
			}
			if (ambiente instanceof AmbienteExecucaoOO2) {
				return ((AmbienteExecucaoOO2) ambiente).getDefProtocolo(nome);
			}
		} catch (ProtocoloNaoDeclaradoException e) {
			// nao e protocolo
		}
		return null;
	}

	/**
	 * Indica se o identificador nomeia um protocolo declarado.
	 */
	public static boolean ehProtocolo(Id nome, Ambiente ambiente) {
		return getDefProtocolo(nome, ambiente) != null;
	}

	/**
	 * Verifica se a classe satisfaz o protocolo: para cada assinatura exigida
	 * deve existir, na classe ou em suas superclasses, um metodo de mesmo
	 * nome e mesmos tipos de parametros.
	 */
	public static boolean classeSatisfaz(DefClasseOO2 classe, DefProtocolo protocolo, Ambiente ambiente)
			throws ClasseNaoDeclaradaException {
		ListaAssinatura exigidas = protocolo.getAssinaturas();
		while (exigidas != null && exigidas.getHead() != null) {
			Assinatura assinatura = exigidas.getHead();
			try {
				Procedimento metodo = HierarquiaUtils.getProcedimentoHierarquia(ambiente, classe, assinatura.getNome());
				if (!mesmosParametros(metodo.getParametrosFormais(), assinatura.getParametrosFormais())) {
					return false;
				}
			} catch (ProcedimentoNaoDeclaradoException e) {
				return false;
			}
			exigidas = (ListaAssinatura) exigidas.getTail();
		}
		return true;
	}

	/**
	 * Verifica se o protocolo <code>real</code> satisfaz o protocolo
	 * <code>esperado</code>: toda assinatura exigida por <code>esperado</code>
	 * deve estar em <code>real</code> com os mesmos tipos de parametros.
	 */
	public static boolean protocoloSatisfaz(DefProtocolo real, DefProtocolo esperado) {
		ListaAssinatura exigidas = esperado.getAssinaturas();
		while (exigidas != null && exigidas.getHead() != null) {
			Assinatura assinatura = exigidas.getHead();
			try {
				Assinatura oferecida = real.getAssinatura(assinatura.getNome());
				if (!mesmosParametros(oferecida.getParametrosFormais(), assinatura.getParametrosFormais())) {
					return false;
				}
			} catch (ProcedimentoNaoDeclaradoException e) {
				return false;
			}
			exigidas = (ListaAssinatura) exigidas.getTail();
		}
		return true;
	}

	/**
	 * Duas listas de parametros formais casam quando tem o mesmo tamanho e,
	 * posicao a posicao, o mesmo tipo (os nomes dos parametros nao importam).
	 */
	private static boolean mesmosParametros(ListaDeclaracaoParametro a, ListaDeclaracaoParametro b) {
		if (a.length() != b.length()) {
			return false;
		}
		while (a != null && a.getHead() != null && b != null && b.getHead() != null) {
			if (!a.getHead().getTipo().equals(b.getHead().getTipo())) {
				return false;
			}
			a = (ListaDeclaracaoParametro) a.getTail();
			b = (ListaDeclaracaoParametro) b.getTail();
		}
		return true;
	}
}
