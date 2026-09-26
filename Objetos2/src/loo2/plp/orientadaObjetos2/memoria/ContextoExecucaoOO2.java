package loo2.plp.orientadaObjetos2.memoria;

import java.util.ArrayList;
import java.util.HashMap;

import loo2.plp.expressions2.expression.Id;
import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.valor.Valor;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorNull;
import loo2.plp.orientadaObjetos1.memoria.ContextoExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.DefClasse;
import loo2.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo2.plp.orientadaObjetos2.util.SuperClasseMap;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloJaDeclaradoException;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloNaoDeclaradoException;

public class ContextoExecucaoOO2 extends ContextoExecucaoOO1 implements AmbienteExecucaoOO2 {
	private ArrayList<SuperClasseMap> arraySuperClasse;

	private MapaProtocolos protocolos;

	public ContextoExecucaoOO2() {
		super();
		arraySuperClasse = new ArrayList <SuperClasseMap> ();
		protocolos = new MapaProtocolos();
	}
	
	public ContextoExecucaoOO2(AmbienteExecucaoOO2 ambiente) throws VariavelJaDeclaradaException {
		super(ambiente);
		arraySuperClasse = ((AmbienteExecucaoOO2) ambiente).getMapSuperClasse();
		protocolos = ((AmbienteExecucaoOO2) ambiente).getMapaProtocolos();
		HashMap<Id, Valor> aux = new HashMap<Id, Valor>();
		aux.put(new Id("super"), new ValorNull());
		getPilha().push(aux);
	}
	
	public ContextoExecucaoOO2(ListaValor entrada) throws VariavelJaDeclaradaException {
		super(entrada);
		arraySuperClasse = new ArrayList <SuperClasseMap> ();
		protocolos = new MapaProtocolos();
		HashMap<Id, Valor> aux = new HashMap<Id, Valor>();
		aux.put(new Id("super"), new ValorNull());
		getPilha().push(aux);
	}

	@Override
	public ContextoExecucaoOO2 getContextoIdValor() throws VariavelJaDeclaradaException {
		ContextoExecucaoOO2 ambiente = new ContextoExecucaoOO2(this.getEntrada());
		ambiente.setPilha( getPilha() );
		ambiente.setSaida( getSaida() );
		ambiente.protocolos = protocolos;
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

	public MapaProtocolos getMapaProtocolos() {
		return protocolos;
	}

	private boolean existeClasse(Id nome) {
		try {
			return getDefClasse(nome) != null;
		} catch (ClasseNaoDeclaradaException e) {
			return false;
		}
	}
}
