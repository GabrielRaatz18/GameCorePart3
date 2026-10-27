# GameCore Trabalho 3

Aplicativo Android de loja de jogos feito em Kotlin e Jetpack Compose. Neste trabalho, usamos Room para guardar avaliações e itens da lista de desejos no próprio aparelho. O projeto continua o GameCore do Trabalho 2.

Integrantes: Gabriel Henrique Alves Raatz e Matheus Pupia.

## Como executar

1. Abra o projeto no Android Studio e aguarde a sincronização do Gradle.
2. Execute o módulo `app` em um aparelho ou emulador com Android 7.0 (API 24) ou superior.
3. Na primeira execução, toque em **Cadastrar**, crie uma conta e entre no aplicativo. Não existe conta de demonstração pronta.
4. Abra um jogo na Loja. Para testar a lista de desejos, use o botão do detalhe do jogo. Para testar avaliações, conclua uma compra simulada, abra o jogo na Biblioteca e entre em **Ver avaliações**.

O projeto usa `room-runtime`, `room-ktx` e `room-compiler` com KSP, configurados em [`app/build.gradle.kts`](app/build.gradle.kts). Para conferir a compilação e os testes locais pelo terminal, use `./gradlew :app:testDebugUnitTest` no macOS/Linux ou `.\gradlew.bat :app:testDebugUnitTest` no Windows.

## Do Trabalho 2 para o Trabalho 3

No Trabalho 2, os jogos da lista de desejos e as avaliações apareciam na interface enquanto o app estava aberto, mas eram mantidos em listas na memória. Fechar o aplicativo fazia esses itens desaparecerem. Mantivemos as telas e os botões que já existiam e trocamos a origem dessas duas listas: agora as ações passam pelos DAOs do Room e os itens são lidos do SQLite local.

O carrinho e a Biblioteca continuam temporários. A compra é uma simulação: ela coloca o jogo na Biblioteca durante a sessão para liberar o formulário de avaliação. O catálogo de jogos também continua definido em `storeGames`; não criamos uma tabela de jogos nesta etapa.

## Estrutura do banco

| Entidade | Campos | Por que foram escolhidos |
| --- | --- | --- |
| [`ReviewData`](app/src/main/java/com/example/gamecore/GameData.kt) → `avaliacoes` | `id`, `gameId`, `author`, `rating`, `text`, `helpful` | O `id` identifica a avaliação que será editada ou aberta no detalhe; `gameId` liga o comentário a um jogo do catálogo; os outros campos mostram autor, nota, texto e votos úteis. |
| [`WishlistEntry`](app/src/main/java/com/example/gamecore/data/WishlistEntry.kt) → `lista_desejos` | `id`, `gameId`, `adicionadoEm` | Guardamos o identificador do jogo e a data da inclusão. Nome, capa e preço são obtidos do catálogo ao montar a tela, evitando duplicar esses dados no banco. |

As duas entidades têm `id: Int = 0` com `@PrimaryKey(autoGenerate = true)`. `WishlistEntry` também tem um índice único em `gameId` para impedir que o mesmo jogo seja salvo duas vezes.

[`AppDatabase`](app/src/main/java/com/example/gamecore/data/AppDatabase.kt) reúne as entidades e expõe seus DAOs. A instância do banco é criada com `Room.databaseBuilder` e reutilizada por meio de `companion object`, `@Volatile` e `synchronized`, como no exemplo de aula. A versão atual do banco é 2 porque a tabela adicional de usuários foi incluída depois; a migração de 1 para 2 cria essa tabela sem apagar avaliações e desejos existentes.

As telas de cadastro e login usam uma terceira entidade, [`Usuario`](app/src/main/java/com/example/gamecore/data/Usuario.kt), e o [`UsuarioDAO`](app/src/main/java/com/example/gamecore/data/UsuarioDAO.kt). Essa funcionalidade local foi acrescentada ao projeto, embora o enunciado do Trabalho 3 não peça login. Ela não usa servidor, API ou sincronização. A demonstração de persistência solicitada pelo professor deve se concentrar em avaliações e lista de desejos.

## Consultas dos DAOs

[`ReviewDao`](app/src/main/java/com/example/gamecore/data/ReviewDao.kt) e [`WishlistDao`](app/src/main/java/com/example/gamecore/data/WishlistDao.kt) têm `@Insert`, `@Query` para buscar todos, `@Update` e `@Delete`; todos os métodos são `suspend`. Acrescentamos `buscarPorId(id)` para localizar uma linha específica. `ReviewDao` também tem `buscarPorJogoId(gameId)`, que permite selecionar avaliações de um jogo. Em `WishlistDao`, `buscarPorJogoId(gameId)` ajuda a localizar o item correto antes de removê-lo.

As operações do banco são iniciadas em `viewModelScope.launch(Dispatchers.IO)` no [`GameCoreViewModel`](app/src/main/java/com/example/gamecore/GameCoreViewModel.kt). Quando a leitura ou escrita termina, a lista observada pelo Compose é atualizada na thread principal. Os erros são tratados com `try/catch` e `Log.e`.

## CRUD nas telas

Na Lista de desejos, o botão do detalhe do jogo insere uma linha em `lista_desejos`. A tela busca novamente os itens quando abre, usando `LaunchedEffect(Unit)`, e o botão **Remover** apaga a linha pelo DAO. O DAO também contém `atualizar`, mas a interface não oferece edição para esse tipo de item, pois só há a referência ao jogo e a data da inclusão.

Em Avaliações, o formulário publica uma nova avaliação com `inserir`. A lista é carregada ao abrir a tela e mostra somente as avaliações do jogo escolhido. Ao abrir uma avaliação, podemos editar nota e texto e salvar com `atualizar`; o botão **Remover** usa `deletar`. Esse é o fluxo de inserir, buscar, atualizar e remover que apresentaremos em sala.

O detalhe da avaliação recebe apenas o `reviewId` na rota. Ao abrir, [`MainActivity`](app/src/main/java/com/example/gamecore/MainActivity.kt) chama `loadReviewForDetail(id)`, e o ViewModel busca a avaliação por `ReviewDao.buscarPorId(id)`. Assim, o detalhe lê o item diretamente do banco, como pede a seção 3.3 do enunciado.

## Dificuldades e soluções

A mudança mais importante foi deixar a lista da interface depender do resultado do banco. Durante a integração, a Lista de desejos chegou a mostrar um jogo antes de confirmar que o Room o havia salvo. Corrigimos esse fluxo: agora a tela é atualizada depois de `inserir` e `buscarTodos`, e um conjunto de inclusões pendentes impede toques repetidos enquanto a gravação está em andamento. Na remoção, a lista também é relida depois do `deletar`.

Outro ajuste foi na compilação do Room com a versão de Kotlin e KSP usada pelo projeto. A versão inicial do Room produziu erro no código gerado; a atualização da dependência para 2.8.5 permitiu compilar novamente. Mantivemos as chamadas aos DAOs em `Dispatchers.IO` para não executar acesso ao SQLite na thread da interface.

Gabriel Henrique Alves Raatz e Matheus Pupia tiveram dificuldade para decidir como organizar as tabelas do banco. A escolha foi guardar as avaliações e os identificadores dos jogos da Lista de desejos em tabelas separadas. O catálogo continuou no código, pois não precisava ser persistido nesta etapa. Assim, a Lista de desejos consulta o Room e usa o `gameId` para mostrar os dados do jogo.

## Evidências da etapa 3

O [vídeo da equipe](https://www.youtube.com/shorts/yGpeYdXlEx0) mostra o cadastro, a navegação pelo aplicativo, a Lista de desejos e o teste de persistência de uma avaliação. No trecho final, a avaliação aparece antes de sair do app (aproximadamente 1:39); depois da volta à tela inicial do celular e da nova abertura do GameCore, a mesma avaliação aparece novamente (aproximadamente 1:59). Esse é o teste de fechar e reabrir com um registro mantido no banco. As imagens e o vídeo antigos do Trabalho 2 foram retirados por não mostrarem esta etapa.

A [documentação em PDF](documentacao/GameCore_Trabalho_3_Documentacao.pdf) reúne o processo e as decisões em um arquivo pronto para entrega. O [roteiro de apresentação](documentacao/roteiro-persistencia-room.md) ajuda a repetir ao vivo as ações de inserir, editar, remover e reabrir o aplicativo. Não use **Limpar dados** nem desinstale o aplicativo durante esse teste, porque isso apagaria o banco local.
