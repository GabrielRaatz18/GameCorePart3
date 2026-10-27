# Checklist de entrega do Trabalho 2 MAF

Verificação da versão publicada no commit `151969e3e58ccc69bb11971890382e8abf00c4ce`, com apoio do vídeo em [`video-demonstracao.mp4`](video-demonstracao.mp4). Este checklist não inclui mudanças locais de código ainda não publicadas.

| Requisito | Estado | Evidência ou pendência |
| --- | --- | --- |
| Novo repositório GitHub com README de execução | Atendido | O repositório `GameCorePart2` tem [`README.md`](../README.md) com passos para abrir no Android Studio. |
| Documentação do processo com prints ou vídeo no repositório | Preparado nesta pasta | Este arquivo, o [relato do processo](README.md), os [prints](prints/) e o [vídeo](video-demonstracao.mp4) estarão no GitHub depois que esta pasta for commitada e enviada. |
| Pelo menos 7 telas navegáveis | Atendido no código | O `NavHost` de [`MainActivity.kt`](../app/src/main/java/com/example/gamecore/MainActivity.kt) define 9 destinos. O vídeo cobre parte deles. |
| Barra inferior funcionando | Atendido | Loja, Biblioteca, Carrinho e Perfil têm ações de navegação. |
| 2 data classes e 2 listas com `LazyColumn` e `Card` | Atendido | `GameData` e `ReviewData`; Lista de desejos e Avaliações são as duas listas principais para demonstrar. |
| Adicionar item novo em cada lista por formulário | Pendente para jogos | O formulário de Avaliações cria `ReviewData`. Jogos são adicionados a partir do catálogo, sem formulário para cadastrar um novo `GameData`. |
| Remover ou marcar item em cada lista | Atendido | Há remoção na Lista de desejos e em Avaliações. |
| 2 detalhes com o item selecionado | Atendido no código | O detalhe do jogo usa `gameId`; o da avaliação usa `reviewId`. Mostrar ambos ao vivo. |
| Detalhe com funcionalidade extra | Atendido | A avaliação pode ser editada; o detalhe do jogo calcula a média de notas. |
| Objeto `Rotas` com constantes | Parcial no nome | O objeto existe, mas se chama `Routes`. As rotas são constantes `String`. |
| Botão de voltar funcional | Atendido | Telas secundárias usam `popBackStack()`. |
| Clicar em todos os botões antes de entregar | Não comprovado | O vídeo não cobre todos. Filtro da Loja e “Ver todas” não têm ação efetiva; várias opções de Configurações só exibem mensagens. |

## Antes de enviar

- [ ] Conferir se o relato de dificuldades descreve o processo real do trio.
- [ ] Confirmar a exigência de formulário para cadastrar jogos e implementar, se necessário.
- [ ] Fazer os controles sem ação executarem uma função real ou retirá-los da interface.
- [ ] Testar todos os botões e o retorno entre as telas em um aparelho ou emulador.
- [ ] Commitar e enviar somente `documentacao/` se essa for a intenção. Não usar um commit que inclua outras alterações já adicionadas ao índice do Git.
