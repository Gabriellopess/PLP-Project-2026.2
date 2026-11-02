import { rawUrl } from "./github";
import bnf from "./oo2-bnf.txt?raw";

// BNF em texto puro, copiada do README (as producoes novas ou alteradas
// terminam em "(*)"). O nome no inicio de cada linha abaixo vira um link para
// o codigo que implementa a producao. Ao mudar a gramatica, atualize o
// oo2-bnf.txt e, se for o caso, esta lista.
const OO1 = "Objetos2/src/loo2/plp/orientadaObjetos1";
const OO2 = "Objetos2/src/loo2/plp/orientadaObjetos2";

const IMPLEMENTACAO: Record<string, string> = {
  ListaDeclaracaoOO: `${OO2}/declaracao/ListaDeclaracaoOO.java`,
  DecOO: `${OO2}/declaracao/DecOO.java`,
  DecProtocolo: `${OO2}/declaracao/protocolo/DecProtocolo.java`,
  ListaAssinatura: `${OO2}/declaracao/protocolo/ListaAssinatura.java`,
  Assinatura: `${OO2}/declaracao/protocolo/Assinatura.java`,
  Tipo: `${OO2}/util/CompatibilidadeTipos.java`,
  TipoClasse: `${OO1}/util/TipoClasse.java`,
  TipoDinamico: `${OO2}/util/TipoDinamico.java`,
  ChamadaMetodo: `${OO2}/comando/ChamadaMetodoOO2.java`,
  Expressao: `${OO2}/expressao/ExpCast.java`,
  ExpCast: `${OO2}/expressao/ExpCast.java`,
};

function Linha({ texto }: { texto: string }) {
  const nome = texto.match(/^[A-Za-z]+/)?.[0];
  const caminho = nome ? IMPLEMENTACAO[nome] : undefined;
  if (!nome || !caminho) return <>{texto}</>;
  return (
    <>
      <a href={rawUrl(caminho)} target="_blank" rel="noopener noreferrer" className="text-blue-700 underline">
        {nome}
      </a>
      {texto.slice(nome.length)}
    </>
  );
}

export function BnfPage() {
  const linhas = bnf.replace(/\n$/, "").split("\n");
  return (
    <div className="h-full overflow-auto bg-white">
      <pre className="m-0 p-4 font-mono text-sm leading-relaxed">
        <a href={import.meta.env.BASE_URL} className="text-blue-700 underline">
          voltar para a IDE
        </a>
        {"\n\n"}
        {linhas.map((texto, i) => (
          // biome-ignore lint/suspicious/noArrayIndexKey: as linhas nunca mudam de ordem
          <span key={i}>
            <Linha texto={texto} />
            {"\n"}
          </span>
        ))}
      </pre>
    </div>
  );
}
