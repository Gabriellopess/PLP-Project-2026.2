package loo2.plp.orientadaObjetos2.declaracao.protocolo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.imperative1.util.Lista;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;

/**
 * Lista das assinaturas de um protocolo.
 *
 * ListaAssinatura ::= Assinatura | Assinatura "," ListaAssinatura
 */
public class ListaAssinatura extends Lista<Assinatura> {

    /**
     * Construtor.
     */
    public ListaAssinatura() {
    }

    /**
     * Construtor.
     *
     * @param assinatura unica assinatura da lista.
     */
    public ListaAssinatura(Assinatura assinatura) {
        super(assinatura, new ListaAssinatura());
    }

    /**
     * Construtor.
     *
     * @param assinatura primeira assinatura da lista.
     * @param lista restante da lista.
     */
    public ListaAssinatura(Assinatura assinatura, ListaAssinatura lista) {
        super(assinatura, lista);
    }

    /**
     * Retorna as assinaturas na ordem em que foram declaradas.
     *
     * @return as assinaturas em uma lista Java.
     */
    public List<Assinatura> getAssinaturas() {
        List<Assinatura> resposta = new ArrayList<Assinatura>();
        Lista<Assinatura> atual = this;
        while (atual != null && atual.getHead() != null) {
            resposta.add(atual.getHead());
            atual = atual.getTail();
        }
        return resposta;
    }

    /**
     * Procura a assinatura do metodo com o nome dado.
     *
     * @param nome nome do metodo.
     * @return a assinatura, ou <code>null</code> se a lista nao tiver um
     *          metodo com esse nome.
     */
    public Assinatura getAssinatura(Id nome) {
        for (Assinatura assinatura : getAssinaturas()) {
            if (assinatura.getNome().equals(nome)) {
                return assinatura;
            }
        }
        return null;
    }

    /**
     * A lista esta bem tipada se cada assinatura estiver bem tipada e nenhum
     * nome de metodo aparecer duas vezes.
     *
     * @param ambiente o ambiente de compilacao.
     * @return <code>true</code> se a lista estiver bem tipada;
     *          <code>false</code> caso contrario.
     */
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
            throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
        Set<String> nomes = new HashSet<String>();
        for (Assinatura assinatura : getAssinaturas()) {
            if (!nomes.add(assinatura.getNome().toString())) {
                return false;
            }
            if (!assinatura.checaTipo(ambiente)) {
                return false;
            }
        }
        return true;
    }
}
