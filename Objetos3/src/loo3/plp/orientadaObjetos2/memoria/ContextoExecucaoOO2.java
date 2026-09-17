package loo3.plp.orientadaObjetos2.memoria;

import java.util.ArrayList;
import java.util.HashMap;

import loo3.plp.expressions2.expression.Id;
import loo3.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo3.plp.orientadaObjetos1.expressao.valor.Valor;
import loo3.plp.orientadaObjetos1.expressao.valor.ValorNull;
import loo3.plp.orientadaObjetos1.memoria.ContextoExecucaoOO1;
import loo3.plp.orientadaObjetos1.memoria.DefClasse;
import loo3.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo3.plp.orientadaObjetos3.excecao.declaracao.ProtocoloJaDeclaradoException;
import loo3.plp.orientadaObjetos3.excecao.declaracao.ProtocoloNaoDeclaradoException;
import loo3.plp.orientadaObjetos2.util.SuperClasseMap;
import loo3.plp.orientadaObjetos3.memoria.DefProtocolo;

public class ContextoExecucaoOO2 extends ContextoExecucaoOO1 implements AmbienteExecucaoOO2 {
	private ArrayList<SuperClasseMap> arraySuperClasse;

	/**
	 * Definicoes dos protocolos do programa (usadas na checagem em execucao
	 * das chamadas sobre valores dyn).
	 */
	private HashMap<Id, DefProtocolo> mapDefProtocolo;

	public ContextoExecucaoOO2() {
		super();
		arraySuperClasse = new ArrayList <SuperClasseMap> ();
		mapDefProtocolo = new HashMap<Id, DefProtocolo>();
	}
	
	public ContextoExecucaoOO2(AmbienteExecucaoOO2 ambiente) throws VariavelJaDeclaradaException {
		super(ambiente);
		arraySuperClasse = ((AmbienteExecucaoOO2) ambiente).getMapSuperClasse();
		mapDefProtocolo = ((AmbienteExecucaoOO2) ambiente).getMapDefProtocolo();
		HashMap<Id, Valor> aux = new HashMap<Id, Valor>();
		aux.put(new Id("super"), new ValorNull());
		getPilha().push(aux);
	}
	
	public ContextoExecucaoOO2(ListaValor entrada) throws VariavelJaDeclaradaException {
		super(entrada);
		arraySuperClasse = new ArrayList <SuperClasseMap> ();
		mapDefProtocolo = new HashMap<Id, DefProtocolo>();
		HashMap<Id, Valor> aux = new HashMap<Id, Valor>();
		aux.put(new Id("super"), new ValorNull());
		getPilha().push(aux);
	}

	@Override
	public ContextoExecucaoOO2 getContextoIdValor() throws VariavelJaDeclaradaException {
		ContextoExecucaoOO2 ambiente = new ContextoExecucaoOO2(this.getEntrada());
		ambiente.setPilha( getPilha() );
		ambiente.setSaida( getSaida() );
		return ambiente;
	}
	
	public void mapSuperClasse(Id classe, Id superClasse) throws ClasseNaoDeclaradaException {
		DefClasse defClasse = getDefClasse(superClasse);
		if (defClasse != null) {
			arraySuperClasse.add(new SuperClasseMap( classe, superClasse ));	
		}
	}

	public SuperClasseMap getSuperClasse(Id classe) throws ClasseNaoDeclaradaException {
		for(int i = 0; i < arraySuperClasse.size(); i++){
			String nomeClasse = arraySuperClasse.get(i).getClasse().toString();
			
			if (nomeClasse.equalsIgnoreCase( classe.toString() )) {
				return arraySuperClasse.get(i);
			}
		}
		return null;
	}	
	
	public ArrayList<SuperClasseMap> getMapSuperClasse() {
		return arraySuperClasse;
	}

	public void mapDefProtocolo(Id protocolo, DefProtocolo defProtocolo) throws ProtocoloJaDeclaradoException {
		if (mapDefProtocolo.put(protocolo, defProtocolo) != null) {
			throw new ProtocoloJaDeclaradoException(protocolo);
		}
	}

	public DefProtocolo getDefProtocolo(Id protocolo) throws ProtocoloNaoDeclaradoException {
		DefProtocolo result = mapDefProtocolo.get(protocolo);
		if (result == null) {
			throw new ProtocoloNaoDeclaradoException(protocolo);
		}
		return result;
	}

	public HashMap<Id, DefProtocolo> getMapDefProtocolo() {
		return mapDefProtocolo;
	}
}
