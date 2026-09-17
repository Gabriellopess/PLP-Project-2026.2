package loo3.plp.orientadaObjetos2.memoria;

import java.util.ArrayList;
import java.util.HashMap;

import loo3.plp.expressions2.expression.Id;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo3.plp.orientadaObjetos1.memoria.ContextoCompilacaoOO1;
import loo3.plp.orientadaObjetos1.memoria.DefClasse;
import loo3.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo3.plp.orientadaObjetos3.excecao.declaracao.ProtocoloJaDeclaradoException;
import loo3.plp.orientadaObjetos3.excecao.declaracao.ProtocoloNaoDeclaradoException;
import loo3.plp.orientadaObjetos2.util.SuperClasseMap;
import loo3.plp.orientadaObjetos3.memoria.DefProtocolo;

public class ContextoCompilacaoOO2 extends ContextoCompilacaoOO1 implements AmbienteCompilacaoOO2{
	
	private ArrayList<SuperClasseMap> arraySuperClasse;

	/**
	 * Mapeamento de nomes de protocolos para suas definicoes.
	 * Como as classes, protocolos vivem em um unico nivel (sem pilha).
	 */
	private HashMap<Id, DefProtocolo> mapDefProtocolo;
	
	public ContextoCompilacaoOO2(ListaValor entrada) {
		super(entrada);		
		arraySuperClasse = new ArrayList <SuperClasseMap> ();
		mapDefProtocolo = new HashMap<Id, DefProtocolo>();
	}

	/**
	 * Mapeia um identificador de protocolo a sua definicao.
	 */
	public void mapDefProtocolo(Id protocolo, DefProtocolo defProtocolo) throws ProtocoloJaDeclaradoException {
		if (mapDefProtocolo.put(protocolo, defProtocolo) != null) {
			throw new ProtocoloJaDeclaradoException(protocolo);
		}
	}

	/**
	 * Recupera a definicao do protocolo cujo nome e dado.
	 */
	public DefProtocolo getDefProtocolo(Id protocolo) throws ProtocoloNaoDeclaradoException {
		DefProtocolo result = mapDefProtocolo.get(protocolo);
		if (result == null) {
			throw new ProtocoloNaoDeclaradoException(protocolo);
		}
		return result;
	}
	
	/**
	 * Mapeia o id da sub-classe em uma super-classe.
	 */
	public void mapSuperClasse(Id classe, Id superClasse) throws ClasseNaoDeclaradaException {
		DefClasse defClasse = getDefClasse(superClasse);
		if (defClasse != null) {
			arraySuperClasse.add(new SuperClasseMap( classe, superClasse ));	
		}
	}

	/**
	 * Dado o id de uma classe, 
	 * recupera a definicao da super-classe. 
	 */
	public SuperClasseMap getSuperClasse(Id classe) throws ClasseNaoDeclaradaException {
		for(int i=0; i < arraySuperClasse.size(); i++){
			String nomeClasse = arraySuperClasse.get(i).getClasse().toString();
			
			if(nomeClasse.equalsIgnoreCase( classe.toString() )) {
				return arraySuperClasse.get(i);
			}
		}
		return null;
	}

}
