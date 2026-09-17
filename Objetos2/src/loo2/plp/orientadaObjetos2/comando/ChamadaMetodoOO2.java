package loo2.plp.orientadaObjetos2.comando;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.comando.ChamadaMetodo;
import loo2.plp.orientadaObjetos1.comando.Procedimento;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoJaDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoJaDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.execucao.EntradaInvalidaException;
import loo2.plp.orientadaObjetos1.expressao.Expressao;
import loo2.plp.orientadaObjetos1.expressao.ListaExpressao;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.expressao.valor.Valor;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorRef;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.ContextoExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.DefClasse;
import loo2.plp.orientadaObjetos1.memoria.Objeto;
import loo2.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos2.excecao.execucao.ChamadaMetodoInvalidaException;
import loo2.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;
import loo2.plp.orientadaObjetos2.memoria.ContextoExecucaoOO2;
import loo2.plp.orientadaObjetos2.memoria.DefClasseOO2;
import loo2.plp.orientadaObjetos2.util.CompatibilidadeTipos;
import loo2.plp.orientadaObjetos2.util.HierarquiaUtils;
import loo2.plp.orientadaObjetos2.util.TipoDinamico;

/**
 * Chamada de metodo em OO2.
 *
 * Em tempo de execucao o metodo e sempre procurado na classe real do objeto
 * (e em suas superclasses). Em tempo de compilacao a verificacao depende do
 * tipo estatico do receptor:
 * <ul>
 * <li>classe: o metodo deve existir na classe declarada ou em uma superclasse
 * (tipagem nominal, como em OO2 original);</li>
 * <li><code>dyn</code>: nada e verificado sobre o metodo; apenas os argumentos
 * precisam estar bem tipados. Existencia do metodo e compatibilidade dos
 * argumentos sao checadas em tempo de execucao (duck typing dinamico).</li>
 * </ul>
 */
public class ChamadaMetodoOO2 extends ChamadaMetodo {

	public ChamadaMetodoOO2(Expressao expressao, Id nomeMetodo, ListaExpressao parametrosReais) {
		super(expressao, nomeMetodo, parametrosReais);
	}

	public AmbienteExecucaoOO1 executar(AmbienteExecucaoOO1 ambiente) throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
		ProcedimentoNaoDeclaradoException, ProcedimentoJaDeclaradoException, ObjetoJaDeclaradoException,
		ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException, ClasseJaDeclaradaException, EntradaInvalidaException {

		Valor receptor = expressao.avaliar(ambiente);
		if (!(receptor instanceof ValorRef)) {
			// Possivel apenas com receptor dyn: a verificacao estatica nao pode impedir.
			throw new ChamadaMetodoInvalidaException("Chamada de " + nomeMetodo + " sobre " + receptor
					+ ", que nao e um objeto.");
		}
		ValorRef vr = (ValorRef) receptor;                     // recupera o id do objeto
		Objeto objeto = ambiente.getObjeto(vr);                // recupera o objeto
		Id idClasse = objeto.getClasse();                      // recupera a classe real do objeto
		DefClasse defClasse = ambiente.getDefClasse((loo2.plp.expressions2.expression.Id) idClasse);
		// Procura o metodo na classe real e em suas superclasses. Se nao existir,
		// ProcedimentoNaoDeclaradoException sinaliza o erro (em tempo de execucao,
		// no caso de receptor dyn).
		Procedimento metodo = HierarquiaUtils.getProcedimentoHierarquia(ambiente, (DefClasseOO2) defClasse, nomeMetodo);

		ListaValor valoresDosParametros = parametrosReais.avaliar(ambiente);
		// Para receptores dyn esta e a unica verificacao dos argumentos; para os
		// demais ela apenas confirma o que o verificador de tipos ja garantiu.
		CompatibilidadeTipos.checaArgumentosEmExecucao(nomeMetodo, metodo.getParametrosFormais(),
				valoresDosParametros, ambiente);

		// cria um novo ambiente para a execucao, pois
		// nao deve levar em conta as variaveis definidas na main
		AmbienteExecucaoOO1 aux = (ambiente instanceof AmbienteExecucaoOO2)
				? new ContextoExecucaoOO2((AmbienteExecucaoOO2) ambiente)
				: new ContextoExecucaoOO1(ambiente);
		aux.changeValor(new Id("this"), vr);
		new ChamadaProcedimentoOO2(metodo, parametrosReais, valoresDosParametros).executar(aux);
		return ambiente;
	}

	public boolean checaTipo(AmbienteCompilacaoOO1 ambiente) throws VariavelNaoDeclaradaException,
				VariavelJaDeclaradaException, ClasseNaoDeclaradaException {
		if (!expressao.checaTipo(ambiente)) {
			return false;
		}
		Tipo tipoReceptor = expressao.getTipo(ambiente);

		if (tipoReceptor instanceof TipoDinamico) {
			// Duck typing dinamico: o metodo so sera procurado em tempo de execucao.
			return ChamadaProcedimentoOO2.checaTipoArgumentos(parametrosReais, ambiente);
		}

		boolean resposta;
		// Tipagem nominal: o metodo deve existir na classe declarada ou em uma
		// superclasse; caso contrario ProcedimentoNaoDeclaradoException e lancada
		// e checaTipo retorna false.
		DefClasse defClasse = ambiente.getDefClasse(tipoReceptor.getTipo());
		try {
			Procedimento metodo = HierarquiaUtils.getProcedimentoHierarquia(ambiente, (DefClasseOO2) defClasse, nomeMetodo);
			ambiente.incrementa();
			ambiente.map(new Id("this"), tipoReceptor);
			resposta = new ChamadaProcedimentoOO2(metodo, parametrosReais).checaTipo(ambiente);
			ambiente.restaura();
		} catch (ProcedimentoNaoDeclaradoException e) {
			resposta = false;
		}
		return resposta;
	}
}
