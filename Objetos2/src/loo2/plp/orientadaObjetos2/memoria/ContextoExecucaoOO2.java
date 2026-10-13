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
import loo2.plp.orientadaObjetos2.excecao.ProtocoloJaDeclaradoException;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloNaoDeclaradoException;
import loo2.plp.orientadaObjetos2.util.SuperClasseMap;

public class ContextoExecucaoOO2 extends ContextoExecucaoOO1 implements AmbienteExecucaoOO2 {
	private ArrayList<SuperClasseMap> arraySuperClasse;

	/**
	 * Protocolos declarados, indexados pelo nome. Assim como o mapa de
	 * classes, e compartilhado com os ambientes criados a partir deste.
	 */
	private HashMap<String, DefProtocolo> mapProtocolo;

	public ContextoExecucaoOO2() {
		super();
		arraySuperClasse = new ArrayList <SuperClasseMap> ();
		mapProtocolo = new HashMap<String, DefProtocolo>();
	}

	public ContextoExecucaoOO2(AmbienteExecucaoOO2 ambiente) throws VariavelJaDeclaradaException {
		super(ambiente);
		arraySuperClasse = ((AmbienteExecucaoOO2) ambiente).getMapSuperClasse();
		mapProtocolo = ambiente.getMapProtocolo();
		HashMap<Id, Valor> aux = new HashMap<Id, Valor>();
		aux.put(new Id("super"), new ValorNull());
		getPilha().push(aux);
	}
	
	public ContextoExecucaoOO2(ListaValor entrada) throws VariavelJaDeclaradaException {
		super(entrada);
		arraySuperClasse = new ArrayList <SuperClasseMap> ();
		mapProtocolo = new HashMap<String, DefProtocolo>();
		HashMap<Id, Valor> aux = new HashMap<Id, Valor>();
		aux.put(new Id("super"), new ValorNull());
		getPilha().push(aux);
	}

	@Override
	public ContextoExecucaoOO2 getContextoIdValor() throws VariavelJaDeclaradaException {
		ContextoExecucaoOO2 ambiente = new ContextoExecucaoOO2(this.getEntrada());
		ambiente.setPilha( getPilha() );
		ambiente.setSaida( getSaida() );
		ambiente.mapProtocolo = this.mapProtocolo;
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

	public HashMap<String, DefProtocolo> getMapProtocolo() {
		return mapProtocolo;
	}

	private boolean ehClasse(Id idArg) {
		try {
			return getDefClasse(idArg) != null;
		} catch (ClasseNaoDeclaradaException e) {
			return false;
		}
	}
}
