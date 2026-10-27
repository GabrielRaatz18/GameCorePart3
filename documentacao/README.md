# Documentação do Trabalho 2 MAF

> Esta pasta preserva as evidências visuais e o relato da etapa anterior. A documentação da evolução para o Trabalho 3, incluindo Room, está no [README principal](../README.md); use o [roteiro de persistência](roteiro-persistencia-room.md) para gravar a prova de fechar e reabrir o app.

## GameCore

O GameCore é um aplicativo Android de loja e comunidade de jogos, feito com Kotlin e Jetpack Compose. Esta documentação acompanha a versão publicada do projeto no commit `151969e3e58ccc69bb11971890382e8abf00c4ce`. O [README principal](../README.md) explica como abrir e executar o projeto no Android Studio.

## Evolução do Trabalho 1

No Trabalho 1, a ideia do GameCore foi representada por telas feitas no Figma. Elas definiram o tema escuro, os detalhes em laranja e o fluxo de loja de jogos. No Trabalho 2, transformamos a proposta visual em um aplicativo Android com navegação entre telas e dados que mudam durante o uso. As imagens do Figma estão identificadas como **referência de design** em [`prints/`](prints/); as capturas com prefixo `app-` vieram do [vídeo do aplicativo em execução](video-demonstracao.mp4).

| Etapa | Evidência |
| --- | --- |
| Ideia visual no Figma | [Loja](prints/figma-loja.png), [Biblioteca](prints/figma-biblioteca.png), [Carrinho](prints/figma-carrinho.png), [Perfil](prints/figma-perfil.png), [Configurações](prints/figma-configuracoes.png), [Lista de desejos](prints/figma-lista-desejos.png), [Avaliações](prints/figma-avaliacoes.png) e [Detalhe de jogo](prints/figma-detalhe-jogo.png) |
| Aplicativo Android | [Loja](prints/app-loja.png), [Detalhe de jogo](prints/app-detalhe-jogo.png), [Lista de desejos](prints/app-lista-desejos.png), [Carrinho](prints/app-carrinho.png), [Biblioteca](prints/app-biblioteca.png) e [Avaliações](prints/app-avaliacoes.png) |

## Telas e navegação

O arquivo [`MainActivity.kt`](../app/src/main/java/com/example/gamecore/MainActivity.kt) contém um `NavHost` central e o objeto `Routes`, com nove destinos: Loja, Carrinho, Biblioteca, Perfil, Configurações, Lista de desejos, Avaliações, Detalhe de jogo e Detalhe de avaliação. A barra inferior conecta as quatro áreas principais: Loja, Biblioteca, Carrinho e Perfil. As telas secundárias usam a seta de voltar para retornar à anterior.

O fluxo que usamos na demonstração começa na Loja: abrimos um jogo, adicionamos o item à Lista de desejos e ao Carrinho, concluímos uma compra simulada e conferimos o jogo na Biblioteca. A partir do detalhe do jogo, também abrimos Avaliações, publicamos um comentário e podemos acessar seu detalhe.

## Listas e dados

Há duas `data class` em [`GameData.kt`](../app/src/main/java/com/example/gamecore/GameData.kt): `GameData`, com nome, categoria, preço e informações do jogo; e `ReviewData`, com autor, nota, texto e o identificador do jogo avaliado. Carrinho, Biblioteca, Lista de desejos e Avaliações são mantidos em listas reativas com `mutableStateListOf` dentro de `MainApp`. Os dados adicionados durante o uso ficam somente na memória, como permitido nesta etapa do trabalho.

A [Biblioteca e a Lista de desejos](../app/src/main/java/com/example/gamecore/CommunityScreens.kt) usam `LazyColumn` e cards para exibir jogos. A [lista de Avaliações](../app/src/main/java/com/example/gamecore/ReviewScreens.kt) usa o mesmo padrão para os comentários. Na Lista de desejos, adicionamos um jogo pelo detalhe e o removemos pelo botão do card. Em Avaliações, o formulário recebe nome, nota e texto; após publicar, o comentário aparece na lista e pode ser removido.

## Detalhes e complexidade extra

Ao tocar em um jogo, a navegação envia seu `gameId`, e o detalhe busca o jogo correspondente. Ao tocar em uma avaliação, a navegação usa `reviewId` para abrir o comentário escolhido. Portanto, as telas de detalhe não dependem de um item fixo.

O [detalhe do jogo](../app/src/main/java/com/example/gamecore/GameScreen.kt) calcula a média das notas de suas avaliações e permite adicionar o jogo ao Carrinho ou à Lista de desejos. O [detalhe da avaliação](../app/src/main/java/com/example/gamecore/ReviewScreens.kt) permite editar a nota e o texto e mostra a contagem de caracteres. Escolhemos a edição da avaliação como a funcionalidade extra a apresentar, pois ela altera um item já existente em vez de apenas mostrar seus dados.

## Decisões de organização

Mantivemos as rotas e as listas compartilhadas em `MainActivity.kt` para que uma ação feita em uma tela apareça nas outras. As telas recebem os dados e as funções de que precisam: por exemplo, o Carrinho recebe a ação de remover e a de concluir a compra. Dividimos a interface em arquivos por assunto, como `StoreScreen.kt`, `CartScreen.kt`, `GameScreen.kt`, `CommunityScreens.kt` e `ReviewScreens.kt`, para localizar cada parte do aplicativo com mais facilidade.

## Dificuldades e soluções

Uma dificuldade foi manter o mesmo jogo coerente entre Detalhe, Carrinho, Lista de desejos e Biblioteca. Centralizamos essas listas em `MainApp` e passamos ações às telas. Assim, ao concluir a compra simulada, o item é incluído na Biblioteca e retirado do Carrinho.

Também foi necessário garantir que cada avaliação abrisse o detalhe certo. Para isso, relacionamos a avaliação ao jogo por `gameId`, identificamos cada comentário por `reviewId` e atualizamos o item editado na lista reativa.

> Este relato de dificuldades foi redigido a partir das decisões visíveis no código. O trio deve conferir se ele corresponde ao processo que viveu antes da entrega.

## Como apresentar

1. Mostre a navegação pela barra inferior.
2. Abra um jogo e adicione-o à Lista de desejos e ao Carrinho.
3. Remova um jogo da Lista de desejos e, em seguida, adicione-o novamente pelo detalhe.
4. Conclua a compra simulada e mostre o item na Biblioteca.
5. Abra Avaliações, publique e remova um comentário.
6. Abra uma avaliação para mostrar a edição de texto e nota.
7. Demonstre a seta de voltar em uma tela secundária.

O [vídeo](video-demonstracao.mp4) mostra parte desse fluxo. Para a apresentação ao vivo, é importante demonstrar também o detalhe da avaliação e a remoção em cada lista.

## Situação dos requisitos

O [checklist de entrega](checklist-entrega.md) separa os requisitos comprovados no código dos pontos ainda pendentes. Em especial, Avaliações têm formulário para criar itens, mas não existe formulário para cadastrar um jogo novo: os jogos são escolhidos do catálogo. A versão atual também tem controles visuais sem ação completa, como o filtro da busca e “Ver todas” na Loja. Esses pontos devem ser considerados antes de declarar que todos os requisitos foram cumpridos.
