package loo2.plp.orientadaObjetos2.declaracao.variavel;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
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
import loo2.plp.orientadaObjetos2.comando.ChamadaProcedimentoOO2;
import loo2.plp.orientadaObjetos2.comando.NewOO2;
import loo2.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;
import loo2.plp.orientadaObjetos2.memoria.DefClasseOO2;
import loo2.plp.orientadaObjetos2.util.CompatibilidadeTipos;

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
	 * <code>Tipo id := new C(args)</code> esta bem tipado quando C existe, e
	 * compativel com o tipo declarado (mesma classe, subclasse ou <code>dyn</code>)
	 * e os argumentos casam com o construtor. A variavel fica associada ao
	 * tipo declarado, e nao a classe concreta instanciada.
	 */
	public boolean checaTipo(AmbienteCompilacaoOO1 ambiente) throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException, ClasseJaDeclaradaException, ClasseNaoDeclaradaException {
		boolean resposta = false;
		Tipo tpClasse = new TipoClasse(this.getClasse());
		if (tpClasse.eValido(ambiente) && this.getTipo().eValido(ambiente)
				&& CompatibilidadeTipos.ehCompativel(this.getTipo(), tpClasse, ambiente)) {
			DefClasseOO2 defClasse = (DefClasseOO2) ambiente.getDefClasse(this.getClasse());
			Procedimento construtor = defClasse.getConstrutor().getProcedimento();
			try {
				ambiente.incrementa();
				resposta = new ChamadaProcedimentoOO2(construtor, parametrosReais).checaTipo(ambiente);
				ambiente.restaura();
			} catch (ProcedimentoNaoDeclaradoException e) {
				throw new RuntimeException("Construtor nao declarado.");
			}
		}
		if (resposta) {
			ambiente.map(this.getObjeto(), this.getTipo());
		}
		return resposta;
	}
}
