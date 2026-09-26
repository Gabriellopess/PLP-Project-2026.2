package loo2.plp.orientadaObjetos2.memoria;

import java.util.ArrayList;

import loo2.plp.expressions2.expression.Id;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.memoria.ContextoCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.DefClasse;
import loo2.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo2.plp.orientadaObjetos2.util.SuperClasseMap;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloJaDeclaradoException;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloNaoDeclaradoException;

public class ContextoCompilacaoOO2 extends ContextoCompilacaoOO1 implements AmbienteCompilacaoOO2{
	
	private ArrayList<SuperClasseMap> arraySuperClasse;

	private MapaProtocolos protocolos;
	
	public ContextoCompilacaoOO2(ListaValor entrada) {
		super(entrada);		
		arraySuperClasse = new ArrayList <SuperClasseMap> ();
		protocolos = new MapaProtocolos();
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

	/**
	 * Registra a classe, recusando nomes ja usados por protocolos.
	 */
	@Override
	public void mapDefClasse(Id idArg, DefClasse defClasse) throws ClasseJaDeclaradaException {
		if (protocolos.contem(idArg)) {
			throw new ClasseJaDeclaradaException(idArg);
		}
		super.mapDefClasse(idArg, defClasse);
	}

	/**
	 * Registra o protocolo, recusando nomes ja usados por classes ou protocolos.
	 */
	public void mapProtocolo(Id protocolo, DefProtocolo defProtocolo) throws ProtocoloJaDeclaradoException {
		if (existeClasse(protocolo)) {
			throw new ProtocoloJaDeclaradoException(protocolo);
		}
		protocolos.map(protocolo, defProtocolo);
	}

	public DefProtocolo getDefProtocolo(Id protocolo) throws ProtocoloNaoDeclaradoException {
		return protocolos.get(protocolo);
	}

	public boolean ehProtocolo(Id nome) {
		return protocolos.contem(nome);
	}

	private boolean existeClasse(Id nome) {
		try {
			return getDefClasse(nome) != null;
		} catch (ClasseNaoDeclaradaException e) {
			return false;
		}
	}
}
