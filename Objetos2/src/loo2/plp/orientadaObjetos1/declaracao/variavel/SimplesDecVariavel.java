package loo2.plp.orientadaObjetos1.declaracao.variavel;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoJaDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.expressao.Expressao;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos1.expressao.valor.Valor;
import loo2.plp.orientadaObjetos2.excecao.ErroDuckTypingException;
import loo2.plp.orientadaObjetos2.util.CompatibilidadeTipos;
import loo2.plp.orientadaObjetos2.util.TipoDinamico;

/**
 * Classe que representa uma declara�ao de vari�vel simples.
 */
public class SimplesDecVariavel implements DecVariavel{
    /**
     * Tipo da vari�vel declada.
     */
    private Tipo tipo;
    /**
     * Vari�vel declarada.
     */
    private Id id;
    /**
     * Express�o cujo valor ser� atribu�do � vari�vel.
     */
    private Expressao expressao;
    /**
     * Marcado no checaTipo quando a expressao e dyn e o tipo declarado nao:
     * a regra R7 precisa ser conferida no elabora, com o valor real.
     */
    private boolean conferirValor = false;
    /**
     * Construtor.
     * @param tipo Tipo da vari�vel declarada.
     * @param id Vari�vel declarada.
     * @param expressao Express�o cujo valor ser� atribu�do � vari�vel.
     */
    public SimplesDecVariavel(Tipo tipo, Id id, Expressao expressao){
        this.tipo = tipo;
        this.id = id;
        this.expressao = expressao;
    }

    /**
    * Retorna o tipo do identificador a ser declarado no AmbienteCompilacao
    * @param id o identificador  da declaracao
    * @return o tipo do identificador
    */
    public Tipo getTipo(Id id) throws VariavelNaoDeclaradaException{
           if(this.id.equals(id)){
               return tipo;
           }
           else {
               throw new VariavelNaoDeclaradaException(id);
           }
    }

    /**
     * Cria um mapeamento do identificador para o valor da express�o
     * desta declara��o no AmbienteExecucao
     * @param ambiente o ambiente que contem o mapeamento entre identificadores
     *  e valores.
     * @return o ambiente modificado pela inicializa��o da vari�vel.
     * @throws ClasseNaoDeclaradaException 
     *
     */
    public AmbienteExecucaoOO1 elabora(AmbienteExecucaoOO1 ambiente)
        throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
               ObjetoNaoDeclaradoException,ObjetoJaDeclaradoException, ClasseNaoDeclaradaException  {
        Valor valor = expressao.avaliar(ambiente);
        if (conferirValor && !CompatibilidadeTipos.valorCompativel(valor, tipo, ambiente)) {
            throw new ErroDuckTypingException("valor de " + id + ": "
                    + CompatibilidadeTipos.tipoDoValor(valor, ambiente) + " n\u00e3o satisfaz " + tipo);
        }
        ambiente.map(id , valor);
        return ambiente;
    }

    /**
     * Verifica se a declara��o est� bem tipada, ou seja, se a
     * express�o de inicializa��o est� bem tipada.
     * @param ambiente o ambiente que contem o mapeamento entre identificadores
     *  e seus tipos.
     * @return <code>true</code> se a express�o � bem tipada;
     *          <code>false</code> caso contrario.
     */
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
        throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException,
               ClasseNaoDeclaradaException {
        boolean resposta = false;
        if ( expressao.checaTipo(ambiente) ) {
            // null, heranca, protocolos e dyn sao tratados pela relacao de compatibilidade
            Tipo tipoExpressao = expressao.getTipo(ambiente);
            resposta = CompatibilidadeTipos.ehCompativel(tipoExpressao, tipo, ambiente);
            conferirValor = tipoExpressao instanceof TipoDinamico && !(tipo instanceof TipoDinamico);
        }
        if(resposta) {
            ambiente.map(id, tipo);
        }
        return resposta;
    }

}
