package loo2.plp.orientadaObjetos2.declaracao.variavel;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.comando.ChamadaProcedimento;
import loo2.plp.orientadaObjetos1.comando.Procedimento;
import loo2.plp.orientadaObjetos1.declaracao.variavel.DecVariavelObjeto;
import loo2.plp.orientadaObjetos1.declaracao.variavel.SimplesDecVariavel;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoJaDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.expressao.ListaExpressao;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorNull;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos1.util.TipoClasse;
import loo2.plp.orientadaObjetos2.comando.NewOO2;
import loo2.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;
import loo2.plp.orientadaObjetos2.memoria.AmbienteProtocolosOO2;
import loo2.plp.orientadaObjetos2.memoria.DefClasseOO2;
import loo2.plp.orientadaObjetos2.util.CompatibilidadeTipos;
import loo2.plp.orientadaObjetos2.util.TipoDinamico;

public class DecVariavelObjetoOO2 extends DecVariavelObjeto {

	private ListaExpressao parametrosReais;

	public DecVariavelObjetoOO2(Tipo tipo, Id objeto, Id classe,
			ListaExpressao parametrosReais) {
		super(tipo, objeto, classe);
		this.parametrosReais = parametrosReais;
	}

	public AmbienteExecucaoOO1 elabora(AmbienteExecucaoOO1 ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
			ObjetoNaoDeclaradoException, ObjetoJaDeclaradoException,
			ClasseNaoDeclaradaException {

		AmbienteExecucaoOO2 aux = (AmbienteExecucaoOO2) new SimplesDecVariavel(
				getTipo(), getObjeto(), new ValorNull()).elabora(ambiente);

		try {
			aux = new NewOO2(getObjeto(), getClasse(), parametrosReais)
					.executar(aux);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return aux;
	}

	/**
	 * T x := new C(...) esta bem tipada se C for uma classe (um protocolo nao
	 * pode ser instanciado), C for compativel com T e os argumentos servirem
	 * ao construtor de C.
	 *
	 * Se T for dyn ou um protocolo, a variavel fica com o tipo declarado T;
	 * se T for uma classe, mantem-se o comportamento original de OO2 (a
	 * variavel fica com o tipo da classe instanciada).
	 */
	public boolean checaTipo(AmbienteCompilacaoOO1 ambiente) throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException, ClasseJaDeclaradaException, ClasseNaoDeclaradaException {

		if (ehProtocolo(ambiente, this.getClasse())) {
			return false;
		}

		boolean booleanSuper = false;
		Tipo tpClasse = new TipoClasse(this.getClasse());
		if (tpClasse.eValido(ambiente) && this.getTipo().eValido(ambiente)) {
			booleanSuper = CompatibilidadeTipos.ehCompativel(tpClasse, this.getTipo(), ambiente);
			boolean tipoDuck = this.getTipo() instanceof TipoDinamico
					|| ehProtocolo(ambiente, this.getTipo().getTipo());
			ambiente.map(this.getObjeto(), tipoDuck ? this.getTipo() : tpClasse);
		}
		
		DefClasseOO2 defClasse = (DefClasseOO2) ambiente.getDefClasse(this.getClasse());
		
		Procedimento metodo = defClasse.getConstrutor().getProcedimento();
		
		boolean resposta = false;
		
		if (metodo != null) {
			try {
				ambiente.incrementa();
				resposta = new ChamadaProcedimento(metodo, parametrosReais).checaTipo(ambiente);
				ambiente.restaura();
			} catch (ProcedimentoNaoDeclaradoException e) {
				throw new RuntimeException("Construtor nao declarado.");
			}
		}
		
		return booleanSuper && resposta;
	}

	private static boolean ehProtocolo(AmbienteCompilacaoOO1 ambiente, Id id) {
		return ambiente instanceof AmbienteProtocolosOO2
				&& ((AmbienteProtocolosOO2) ambiente).ehProtocolo(id);
	}
}
