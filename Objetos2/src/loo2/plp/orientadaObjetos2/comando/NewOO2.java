package loo2.plp.orientadaObjetos2.comando;

import java.util.HashMap;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.comando.Atribuicao;
import loo2.plp.orientadaObjetos1.comando.ChamadaProcedimento;
import loo2.plp.orientadaObjetos1.comando.Comando;
import loo2.plp.orientadaObjetos1.comando.Procedimento;
import loo2.plp.orientadaObjetos1.declaracao.variavel.DecVariavel;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoJaDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoJaDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.execucao.EntradaInvalidaException;
import loo2.plp.orientadaObjetos1.expressao.ListaExpressao;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.LeftExpression;
import loo2.plp.orientadaObjetos1.expressao.valor.Valor;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorRef;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.ContextoExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.ContextoObjeto;
import loo2.plp.orientadaObjetos1.memoria.Objeto;
import loo2.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo2.plp.orientadaObjetos1.util.TipoClasse;
import loo2.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;
import loo2.plp.orientadaObjetos2.memoria.ContextoExecucaoOO2;
import loo2.plp.orientadaObjetos2.memoria.AmbienteProtocolosOO2;
import loo2.plp.orientadaObjetos2.memoria.DefClasseOO2;
import loo2.plp.orientadaObjetos2.util.CompatibilidadeTipos;

/**
 * Comando x := new C(...): cria o objeto com os atributos de C e das
 * superclasses, atribui a referencia a x e executa o construtor de C.
 *
 * Nao herda de New (OO1): o executar de New nao declara as excecoes do
 * construtor, entao nao poderia ser sobrescrito para chama-lo. Antes, o
 * comando acabava executando o New de OO1, que nao roda o construtor.
 */
public class NewOO2 implements Comando {

	private LeftExpression av;

	private Id classe;

	private ListaExpressao parametrosReais;

	public NewOO2(LeftExpression av, Id classe, ListaExpressao parametrosReais) {
		this.av = av;
		this.classe = classe;
		this.parametrosReais = parametrosReais;
	}

	public LeftExpression getAv() {
		return av;
	}

	public Id getClasse() {
		return classe;
	}

	/**
	 * Acrescenta ao objeto os atributos da classe e das superclasses que
	 * ainda nao estao no seu estado.
	 */
	private void extendsClasse(AmbienteExecucaoOO1 ambiente, DefClasseOO2 classe, Objeto objeto) throws ClasseNaoDeclaradaException,
				VariavelNaoDeclaradaException, VariavelJaDeclaradaException, ObjetoNaoDeclaradoException,
				ClasseJaDeclaradaException, ObjetoJaDeclaradoException {
		if (classe.getNomeSuperClasse() != null) {
			DefClasseOO2 classeMae = (DefClasseOO2) ambiente.getDefClasse(classe.getNomeSuperClasse());
			this.extendsClasse(ambiente, classeMae, objeto);
		}

		DecVariavel decVariavel = classe.getDecVariavel();
		AmbienteExecucaoOO1 aux = decVariavel.elabora(new ContextoExecucaoOO1(ambiente));
		HashMap<loo2.plp.expressions2.expression.Id, Valor> estadoObj = aux.getPilha().pop();

		for (loo2.plp.expressions2.expression.Id id : estadoObj.keySet()) {
			// Se a variavel nao havia sido declarada adicione
			if (!objeto.getEstado().containsKey(id) ) {
				objeto.getEstado().put(id, estadoObj.get(id));
			}
		}
	}

	public AmbienteExecucaoOO1 executar(AmbienteExecucaoOO1 ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException, ClasseJaDeclaradaException, ClasseNaoDeclaradaException,
			ObjetoJaDeclaradoException, ObjetoNaoDeclaradoException, ProcedimentoNaoDeclaradoException,
			ProcedimentoJaDeclaradoException, EntradaInvalidaException {

		DefClasseOO2 defClasse = (DefClasseOO2) ambiente.getDefClasse(classe);

		// Cria o objeto com os atributos da propria classe
		AmbienteExecucaoOO1 auxAtributos = defClasse.getDecVariavel().elabora(new ContextoExecucaoOO1(ambiente));
		Objeto objeto = new Objeto(classe, new ContextoObjeto(auxAtributos.getPilha().pop()));

		// Acrescenta os atributos herdados
		if (defClasse.getNomeSuperClasse() != null) {
			DefClasseOO2 classeMae = (DefClasseOO2) ambiente.getDefClasse(defClasse.getNomeSuperClasse());
			this.extendsClasse(ambiente, classeMae, objeto);
		}

		ValorRef vr = ambiente.getProxRef();
		ambiente.mapObjeto(vr, objeto);

		// Os argumentos sao avaliados antes de x mudar de valor
		ListaValor valoresDosParametros = parametrosReais.avaliar(ambiente);
		Procedimento construtor = defClasse.getConstrutor().getProcedimento();
		CompatibilidadeTipos.confereArgumentos(classe, construtor.getParametrosFormais(), valoresDosParametros, ambiente);
		ambiente = new Atribuicao(av, vr).executar(ambiente);

		// Executa o construtor com this apontando para o novo objeto. Fora de
		// OO2 (ambiente de OO1), usa um contexto de OO1.
		AmbienteExecucaoOO1 aux = (ambiente instanceof AmbienteExecucaoOO2)
				? new ContextoExecucaoOO2((AmbienteExecucaoOO2) ambiente)
				: new ContextoExecucaoOO1(ambiente);
		aux.changeValor(new Id("this"), vr);
		new ChamadaProcedimento(construtor, parametrosReais, valoresDosParametros).executar(aux);

		return ambiente;
	}

	/**
	 * Usado pela declaracao T x := new C(...) (DecVariavelObjetoOO2).
	 */
	public AmbienteExecucaoOO2 executar(AmbienteExecucaoOO2 ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException, ClasseJaDeclaradaException, ClasseNaoDeclaradaException,
			ObjetoJaDeclaradoException, ObjetoNaoDeclaradoException, ProcedimentoNaoDeclaradoException,
			ProcedimentoJaDeclaradoException, EntradaInvalidaException {
		return (AmbienteExecucaoOO2) executar((AmbienteExecucaoOO1) ambiente);
	}

	/**
	 * O comando x := new C(...) esta bem tipado se C for uma classe declarada
	 * (um protocolo nao pode ser instanciado) e C for compativel com o tipo
	 * de x (igualdade, heranca, protocolos e dyn), e se os argumentos
	 * servirem ao construtor de C, como na declaracao T x := new C(...).
	 */
	public boolean checaTipo(AmbienteCompilacaoOO1 ambiente) throws VariavelNaoDeclaradaException,
			VariavelJaDeclaradaException, ClasseJaDeclaradaException, ClasseNaoDeclaradaException {
		TipoClasse tpClasse = new TipoClasse(getClasse());
		boolean ehProtocolo = ambiente instanceof AmbienteProtocolosOO2
				&& ((AmbienteProtocolosOO2) ambiente).ehProtocolo(getClasse());
		return getAv().checaTipo(ambiente)
				&& !ehProtocolo
				&& tpClasse.eValido(ambiente)
				&& CompatibilidadeTipos.ehCompativel(tpClasse, getAv().getTipo(ambiente), ambiente)
				&& checaTipoConstrutor(ambiente);
	}

	private boolean checaTipoConstrutor(AmbienteCompilacaoOO1 ambiente) throws VariavelNaoDeclaradaException,
			VariavelJaDeclaradaException, ClasseNaoDeclaradaException {
		DefClasseOO2 defClasse = (DefClasseOO2) ambiente.getDefClasse(getClasse());
		Procedimento construtor = defClasse.getConstrutor().getProcedimento();
		try {
			return new ChamadaProcedimento(construtor, parametrosReais).checaTipo(ambiente);
		} catch (ProcedimentoNaoDeclaradoException e) {
			return false;
		}
	}

}
