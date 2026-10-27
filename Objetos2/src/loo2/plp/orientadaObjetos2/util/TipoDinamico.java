package loo2.plp.orientadaObjetos2.util;

import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.util.Tipo;

/**
 * Tipo <code>dyn</code> (duck typing dinamico): uma variavel desse tipo pode
 * referenciar qualquer objeto, e as chamadas de metodo sobre ela so sao
 * verificadas em tempo de execucao.
 *
 * TipoDinamico ::= "dyn"
 */
public class TipoDinamico implements Tipo {

    /**
     * Nome do tipo. Como "dyn" e palavra reservada, nenhuma classe ou
     * protocolo pode ter esse nome.
     */
    public static final Id NOME = new Id("dyn");

    /**
     * Unica instancia do tipo dinamico.
     */
    public static final TipoDinamico DYN = new TipoDinamico();

    /**
     * Construtor privado: use {@link #DYN}.
     */
    private TipoDinamico() {
    }

    /**
     * Retorna o nome do tipo.
     *
     * @return o identificador "dyn".
     */
    public Id getTipo() {
        return NOME;
    }

    /**
     * O tipo dinamico nao depende de nenhuma declaracao, entao e sempre
     * valido.
     *
     * @return <code>true</code>.
     */
    public boolean eValido(AmbienteCompilacaoOO1 ambiente) {
        return true;
    }

    /**
     * Compara este tipo com o tipo dado.
     *
     * @return <code>true</code> se o outro tipo tambem for dinamico;
     *          <code>false</code> caso contrario.
     */
    public boolean equals(Object obj) {
        return obj instanceof TipoDinamico;
    }

    public int hashCode() {
        return NOME.toString().hashCode();
    }

    /**
     * Retorna a descricao textual do tipo.
     *
     * @return "dyn".
     */
    public String toString() {
        return NOME.toString();
    }
}
