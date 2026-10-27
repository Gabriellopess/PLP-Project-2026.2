package loo2.plp.orientadaObjetos2.declaracao.protocolo;

import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.declaracao.procedimento.ListaDeclaracaoParametro;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;

/**
 * Assinatura de um metodo dentro de um protocolo: nome e parametros, sem
 * corpo.
 *
 * Assinatura ::= "proc" Id "(" [ ListaDeclaracaoParametro ] ")"
 */
public class Assinatura {

    /**
     * Nome do metodo.
     */
    private Id nome;

    /**
     * Parametros formais do metodo (lista vazia quando nao ha parametros).
     */
    private ListaDeclaracaoParametro parametros;

    /**
     * Construtor.
     *
     * @param nome nome do metodo.
     * @param parametros parametros formais do metodo.
     */
    public Assinatura(Id nome, ListaDeclaracaoParametro parametros) {
        this.nome = nome;
        this.parametros = parametros;
    }

    public Id getNome() {
        return nome;
    }

    public ListaDeclaracaoParametro getParametros() {
        return parametros;
    }

    /**
     * A assinatura esta bem tipada se os tipos de todos os seus parametros
     * forem validos.
     *
     * @param ambiente o ambiente de compilacao.
     * @return <code>true</code> se os tipos dos parametros forem validos;
     *          <code>false</code> caso contrario.
     */
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
            throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
        return parametros.checaTipo(ambiente);
    }

    public String toString() {
        return "proc " + nome + "(" + parametros + ")";
    }
}
