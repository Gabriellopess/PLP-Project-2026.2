#!/usr/bin/env bash
# Roda os programas de teste de duck typing de OO2 e compara com o
# resultado esperado anotado no cabecalho de cada arquivo:
#
#   // Resultado: ok | erro-tipo | erro-execucao
#   // > linha de saida esperada        (uma por linha, na ordem)
#
# - ok:            a saida do programa deve ser igual as linhas "// >".
# - erro-tipo:     o programa deve ser recusado pelo checaTipo ("Erro de tipo").
# - erro-execucao: a saida antes do erro deve ser igual as linhas "// >", e a
#                  execucao deve terminar com ErroDuckTypingException.
#
# Uso (a partir da raiz do repositorio, com Objetos2 ja compilado):
#   cd Objetos2 && mvn compile && cd ..
#   Testes/run_duck_tests.sh                 # todos os testes de duck typing
#   Testes/run_duck_tests.sh Testes/X.txt    # testes especificos

cd "$(dirname "$0")/.." || exit 1

CP=Objetos2/target/classes
PARSER=loo2.plp.orientadaObjetos2.parser.OO2Parser
BANNER='^OO2 PLP Parser Version'

if [ ! -d "$CP" ]; then
  echo "Objetos2 nao compilado: rode 'cd Objetos2 && mvn compile' antes." >&2
  exit 2
fi

if [ $# -gt 0 ]; then
  arquivos=("$@")
else
  arquivos=(Testes/TesteOO2_Duck*.txt Testes/TesteOO2_Protocolo*.txt)
fi

passou=0
falhou=0
erro_stderr=$(mktemp)
trap 'rm -f "$erro_stderr"' EXIT

for arquivo in "${arquivos[@]}"; do
  resultado=$(sed -n 's|^// Resultado: *||p' "$arquivo" | tr -d '\r')
  esperado=$(sed -n 's|^// > ||p' "$arquivo" | tr -d '\r')

  stdout=$(java -cp "$CP" "$PARSER" "$arquivo" 2>"$erro_stderr")
  stderr=$(cat "$erro_stderr")
  saida=$(printf '%s\n' "$stdout" | grep -v -E "$BANNER" | grep -v -x 'Erro de tipo')

  motivo=""
  if ! grep -q 'parsed successfully' <<<"$stdout"; then
    motivo="sintaxe: programa nao foi reconhecido pelo parser"
  else
    case "$resultado" in
      ok)
        if grep -q -E "$BANNER.*Encountered errors" <<<"$stdout"; then
          motivo="esperava execucao sem erro: $(grep -m1 -E 'Exception|Error' <<<"$stderr")"
        elif grep -q -x 'Erro de tipo' <<<"$stdout"; then
          motivo="esperava execucao sem erro, veio 'Erro de tipo'"
        elif [ "$saida" != "$esperado" ]; then
          motivo="saida diferente da esperada"
        fi
        ;;
      erro-tipo)
        if ! grep -q -x 'Erro de tipo' <<<"$stdout"; then
          motivo="esperava 'Erro de tipo'"
        fi
        ;;
      erro-execucao)
        if ! grep -q 'ErroDuckTypingException' <<<"$stderr"; then
          motivo="esperava ErroDuckTypingException"
        elif [ "$saida" != "$esperado" ]; then
          motivo="saida antes do erro diferente da esperada"
        fi
        ;;
      *)
        motivo="cabecalho sem '// Resultado: ok|erro-tipo|erro-execucao'"
        ;;
    esac
  fi

  if [ -z "$motivo" ]; then
    passou=$((passou + 1))
    printf 'PASSOU  %s\n' "$arquivo"
  else
    falhou=$((falhou + 1))
    printf 'FALHOU  %s\n        %s\n' "$arquivo" "$motivo"
  fi
done

printf '\n%d passaram, %d falharam, %d no total\n' "$passou" "$falhou" "${#arquivos[@]}"
[ "$falhou" -eq 0 ]
