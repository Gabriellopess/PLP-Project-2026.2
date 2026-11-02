// Endereco do codigo-fonte no GitHub, usado nos links da pagina da BNF.
// No deploy (.github/workflows/docs.yml), o workflow define o repositorio e o
// commit publicado, para os links mostrarem exatamente o codigo que esta no ar.
// Localmente, os valores padrao abaixo sao usados.

export const GITHUB_REPO: string = import.meta.env.VITE_GITHUB_REPO || "Gabriellopess/PLP-Project-2026.2";
export const GITHUB_REF: string = import.meta.env.VITE_GITHUB_REF || "dev";

/** Link para o arquivo cru (raw) no GitHub; caminho relativo a raiz do repositorio. */
export function rawUrl(caminho: string): string {
  return `https://raw.githubusercontent.com/${GITHUB_REPO}/${GITHUB_REF}/${caminho}`;
}
