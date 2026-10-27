package loo2.plp.orientadaObjetos2.comando;

import loo2.plp.expressions2.memory.Ambiente;
import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.comando.ChamadaMetodo;
import loo2.plp.orientadaObjetos1.comando.ChamadaProcedimento;
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
import loo2.plp.orientadaObjetos1.expressao.valor.ValorRef;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.ContextoExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.DefClasse;
import loo2.plp.orientadaObjetos1.memoria.Objeto;
import loo2.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.imperative1.util.Lista;
import loo2.plp.orientadaObjetos1.comando.Skip;
import loo2.plp.orientadaObjetos2.declaracao.protocolo.Assinatura;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloNaoDeclaradoException;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo2.plp.orientadaObjetos2.memoria.DefClasseOO2;
import loo2.plp.orientadaObjetos2.util.TipoDinamico;

public class ChamadaMetodoOO2 extends ChamadaMetodo {

	public ChamadaMetodoOO2(Expressao expressao, Id nomeMetodo, ListaExpressao parametrosReais) {
		super(expressao, nomeMetodo, parametrosReais);
	}
	
	private Procedimento getProcedimentoHierarquia (Ambiente ambiente, DefClasseOO2 defClasse, Id nomeMetodo) throws ClasseNaoDeclaradaException, ProcedimentoNaoDeclaradoException {
		Procedimento metodo = null;
		
		try {
			metodo = defClasse.getMetodo(nomeMetodo);
		} catch (ProcedimentoNaoDeclaradoException e) {
			if (defClasse.getNomeSuperClasse() != null) {
				if (ambiente instanceof AmbienteCompilacaoOO1) {
					AmbienteCompilacaoOO1 ambienteCompilacao = (AmbienteCompilacaoOO1) ambiente;
					DefClasseOO2 defClasseMae = (DefClasseOO2) ambienteCompilacao.getDefClasse(defClasse.getNomeSuperClasse());
					metodo = this.getProcedimentoHierarquia(ambiente, defClasseMae, nomeMetodo);
				} else if (ambiente instanceof AmbienteExecucaoOO1) {
					AmbienteExecucaoOO1 ambienteExecucao = (AmbienteExecucaoOO1) ambiente;
					DefClasseOO2 defClasseMae = (DefClasseOO2) ambienteExecucao.getDefClasse(defClasse.getNomeSuperClasse());
					metodo = this.getProcedimentoHierarquia(ambiente, defClasseMae, nomeMetodo);
				}
			}
		}
		if (metodo == null) {
			throw new ProcedimentoNaoDeclaradoException(nomeMetodo);
		}
		return metodo;
	}
	
    public AmbienteExecucaoOO1 executar(AmbienteExecucaoOO1 ambiente) throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
    	ProcedimentoNaoDeclaradoException, ProcedimentoJaDeclaradoException, ObjetoJaDeclaradoException, 
    	ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException, ClasseJaDeclaradaException, EntradaInvalidaException {

	    ValorRef vr = (ValorRef) expressao.avaliar(ambiente);  // recupera o id do objeto
	    Objeto objeto =  ambiente.getObjeto(vr);               // recupera o objeto
	    Id idClasse = objeto.getClasse();                      // recupera o tipo do objeto
	    DefClasse defClasse = ambiente.getDefClasse((loo2.plp.expressions2.expression.Id)idClasse); // recupera a defini��o da classe
	    Procedimento metodo = this.getProcedimentoHierarquia(ambiente, (DefClasseOO2) defClasse, nomeMetodo); // recupera o procedimento
	    // cria um novo ambiente para a execucao, pois
	    // n�o deve levar em conta as vari�veis definidas na main
	    AmbienteExecucaoOO1 aux = new ContextoExecucaoOO1(ambiente);
	                                                           // � change pois no construtor do ambiente
	    aux.changeValor(new Id("this"),vr);                    // invocado na linha anterior ja � feito
	                                                           //  um mapeamento
	
	    ListaValor valoresDosParametros = parametrosReais.avaliar(ambiente);
	    new ChamadaProcedimento(metodo, parametrosReais, valoresDosParametros).executar(aux);
	    return ambiente;
    }
    
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente) throws VariavelNaoDeclaradaException, 
    			VariavelJaDeclaradaException, ClasseNaoDeclaradaException {
        boolean resposta;
        //Antes de incrementar o ambiente, verifico se o m�todo
        //� v�lido para a definicao de classe obtida a partir de expressao.
        //Se n�o for v�lido, a exce��o ProcedimentoNaoDeclaradoException ser�
        //lan�ada e checaTipo retornar� false.
        Tipo tipoClasse = expressao.getTipo(ambiente);
        if (tipoClasse instanceof TipoDinamico) {
            // dyn: o metodo so e procurado na execucao, pela classe real
            return expressao.checaTipo(ambiente) && checaTipoArgumentos(ambiente);
        }
        if (ambiente instanceof AmbienteCompilacaoOO2
                && ((AmbienteCompilacaoOO2) ambiente).ehProtocolo(tipoClasse.getTipo())) {
            return checaTipoProtocolo((AmbienteCompilacaoOO2) ambiente, tipoClasse.getTipo());
        }
        DefClasse defClasse = ambiente.getDefClasse(tipoClasse.getTipo());
        try {
            Procedimento metodo = this.getProcedimentoHierarquia(ambiente, (DefClasseOO2) defClasse, nomeMetodo);
            ambiente.incrementa();
            ambiente.map(new Id("this"),tipoClasse);
            resposta = new ChamadaProcedimento(metodo, parametrosReais).checaTipo(ambiente);
            ambiente.restaura();
        } catch(ProcedimentoNaoDeclaradoException e){
            resposta = false;
        }
        return resposta;
    }

    /**
     * Receptor do tipo protocolo: o metodo e procurado nas assinaturas do
     * protocolo, e nao na classe real do objeto. Os argumentos sao conferidos
     * com a relacao de compatibilidade, tratando a assinatura como um
     * procedimento sem corpo.
     */
    private boolean checaTipoProtocolo(AmbienteCompilacaoOO2 ambiente, Id idProtocolo)
    		throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException, ClasseNaoDeclaradaException {
        Assinatura assinatura;
        try {
            assinatura = ambiente.getDefProtocolo(idProtocolo).getAssinatura(nomeMetodo);
        } catch (ProtocoloNaoDeclaradoException e) {
            return false;
        }
        if (assinatura == null || !expressao.checaTipo(ambiente) || !checaTipoArgumentos(ambiente)) {
            return false;
        }
        Procedimento semCorpo = new Procedimento(assinatura.getParametros(), new Skip());
        try {
            return new ChamadaProcedimento(semCorpo, parametrosReais).checaTipo(ambiente);
        } catch (ProcedimentoNaoDeclaradoException e) {
            return false;
        }
    }

    /**
     * Confere se cada argumento esta bem tipado isoladamente.
     */
    private boolean checaTipoArgumentos(AmbienteCompilacaoOO1 ambiente)
    		throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
        Lista<Expressao> atual = parametrosReais;
        while (atual != null && atual.getHead() != null) {
            if (!atual.getHead().checaTipo(ambiente)) {
                return false;
            }
            atual = atual.getTail();
        }
        return true;
    }
}
