package loo2.plp.orientadaObjetos2.memoria;

import java.util.ArrayList;
import java.util.HashMap;

import loo2.plp.expressions2.expression.Id;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.memoria.ContextoCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.DefClasse;
import loo2.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloJaDeclaradoException;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloNaoDeclaradoException;
import loo2.plp.orientadaObjetos2.util.SuperClasseMap;

public class ContextoCompilacaoOO2 extends ContextoCompilacaoOO1 implements AmbienteCompilacaoOO2{

	private ArrayList<SuperClasseMap> arraySuperClasse;

	/**
	 * Protocolos declarados, indexados pelo nome.
	 */
	private HashMap<String, DefProtocolo> mapProtocolo;

	public ContextoCompilacaoOO2(ListaValor entrada) {
		super(entrada);
		arraySuperClasse = new ArrayList <SuperClasseMap> ();
		mapProtocolo = new HashMap<String, DefProtocolo>();
	}

	/**
	 * Mapeia a classe, recusando um nome ja usado por um protocolo.
	 */
	@Override
	public void mapDefClasse(Id idArg, DefClasse defClasse) throws ClasseJaDeclaradaException {
		if (ehProtocolo(idArg)) {
			throw new ClasseJaDeclaradaException(idArg);
		}
		super.mapDefClasse(idArg, defClasse);
	}

	/**
	 * Mapeia o protocolo, recusando um nome ja usado por um protocolo ou
	 * por uma classe.
	 */
	public void mapProtocolo(Id idArg, DefProtocolo defProtocolo) throws ProtocoloJaDeclaradoException {
		if (ehProtocolo(idArg) || ehClasse(idArg)) {
			throw new ProtocoloJaDeclaradoException(idArg);
		}
		mapProtocolo.put(idArg.toString(), defProtocolo);
	}

	public DefProtocolo getDefProtocolo(Id idArg) throws ProtocoloNaoDeclaradoException {
		DefProtocolo resposta = mapProtocolo.get(idArg.toString());
		if (resposta == null) {
			throw new ProtocoloNaoDeclaradoException(idArg);
		}
		return resposta;
	}

	public boolean ehProtocolo(Id idArg) {
		return mapProtocolo.containsKey(idArg.toString());
	}

	private boolean ehClasse(Id idArg) {
		try {
			return getDefClasse(idArg) != null;
		} catch (ClasseNaoDeclaradaException e) {
			return false;
		}
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
