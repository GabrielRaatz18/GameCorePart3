# GameCore Trabalho 3

Aplicativo Android de loja de jogos feito em Kotlin e Jetpack Compose. Nesta versão, usamos Room para guardar avaliações, itens da Lista de desejos e contas criadas no cadastro local. O projeto continua o GameCore do Trabalho 2.

Integrantes: Gabriel Henrique Alves Raatz e Matheus Pupia.

## Como executar

1. Abra o projeto no Android Studio e aguarde a sincronização do Gradle.
2. Execute o módulo `app` em um aparelho ou emulador com Android 7.0 (API 24) ou superior.
3. Na primeira execução, toque em **Cadastrar**, crie uma conta e entre no aplicativo. Não existe conta de demonstração pronta.
4. Abra um jogo na Loja. Para testar a lista de desejos, use o botão do detalhe do jogo. Para testar avaliações, conclua uma compra simulada, abra o jogo na Biblioteca e entre em **Ver avaliações**.

O projeto usa `room-runtime`, `room-ktx` e `room-compiler` com KSP, configurados em [`app/build.gradle.kts`](app/build.gradle.kts). Para conferir a compilação e os testes locais pelo terminal, use `./gradlew :app:testDebugUnitTest` no macOS/Linux ou `.\gradlew.bat :app:testDebugUnitTest` no Windows.

## Do Trabalho 2 para o Trabalho 3

No Trabalho 2, os jogos da Lista de desejos e as avaliações apareciam na interface enquanto o app estava aberto, mas eram mantidos em listas na memória. Fechar o aplicativo fazia esses itens desaparecerem. Mantivemos as telas e os botões que já existiam e trocamos a origem dessas duas listas: agora as ações passam pelos DAOs do Room e os itens são lidos do SQLite local. Também acrescentamos cadastro e login, que consultam uma tabela de usuários no mesmo banco.

O carrinho e a Biblioteca continuam temporários. A compra é uma simulação: ela coloca o jogo na Biblioteca durante a sessão para liberar o formulário de avaliação. O catálogo de jogos também continua definido em `storeGames`; não criamos uma tabela de jogos nesta etapa.

## Estrutura do banco

| Entidade | Campos | Por que foram escolhidos |
| --- | --- | --- |
| [`ReviewData`](app/src/main/java/com/example/gamecore/GameData.kt) → `avaliacoes` | `id`, `gameId`, `author`, `rating`, `text`, `helpful` | O `id` identifica a avaliação que será editada ou aberta no detalhe; `gameId` liga o comentário a um jogo do catálogo; os outros campos mostram autor, nota, texto e votos úteis. |
| [`WishlistEntry`](app/src/main/java/com/example/gamecore/data/WishlistEntry.kt) → `lista_desejos` | `id`, `gameId`, `adicionadoEm` | Guardamos o identificador do jogo e a data da inclusão. Nome, capa e preço são obtidos do catálogo ao montar a tela, evitando duplicar esses dados no banco. |
| [`Usuario`](app/src/main/java/com/example/gamecore/data/Usuario.kt) → `usuarios` | `id`, `nome`, `usuario`, `email`, `senha` | Esses dados são preenchidos no cadastro. O e-mail e a senha são consultados no login; o nome e o usuário identificam a conta nas telas. |

As três entidades têm `id: Int = 0` com `@PrimaryKey(autoGenerate = true)`. `WishlistEntry` também tem um índice único em `gameId` para impedir que o mesmo jogo seja salvo duas vezes.

[`AppDatabase`](app/src/main/java/com/example/gamecore/data/AppDatabase.kt) reúne as entidades e expõe seus DAOs. A instância do banco é criada com `Room.databaseBuilder` e reutilizada por meio de `companion object`, `@Volatile` e `synchronized`, como no exemplo de aula. A versão atual do banco é 2 porque a tabela adicional de usuários foi incluída depois; a migração de 1 para 2 cria essa tabela sem apagar avaliações e desejos existentes.

O cadastro e o login foram uma funcionalidade adicional do nosso projeto; o enunciado do Trabalho 3 não exige autenticação. Tudo funciona localmente, sem servidor, API ou sincronização. A senha é guardada diretamente no banco nesta versão de estudo; esse formato não seria adequado para um aplicativo publicado.

## Consultas dos DAOs

[`ReviewDao`](app/src/main/java/com/example/gamecore/data/ReviewDao.kt) e [`WishlistDao`](app/src/main/java/com/example/gamecore/data/WishlistDao.kt) têm `@Insert`, `@Query` para buscar todos, `@Update` e `@Delete`; todos os métodos são `suspend`. Acrescentamos `buscarPorId(id)` para localizar uma linha específica. `ReviewDao` também tem `buscarPorJogoId(gameId)`, que permite selecionar avaliações de um jogo. Em `WishlistDao`, `buscarPorJogoId(gameId)` ajuda a localizar o item correto antes de removê-lo.

O [`UsuarioDAO`](app/src/main/java/com/example/gamecore/data/UsuarioDAO.kt) também tem os quatro métodos básicos e consultas por `id`, `email` e nome de usuário. Antes de cadastrar, buscamos o e-mail e o nome de usuário para evitar repetição. No login, `buscarLogin(email, senha)` procura a conta correspondente. Esses métodos também são `suspend`.

As operações do banco são iniciadas em `viewModelScope.launch(Dispatchers.IO)` no [`GameCoreViewModel`](app/src/main/java/com/example/gamecore/GameCoreViewModel.kt). Quando a leitura ou escrita termina, a lista observada pelo Compose é atualizada na thread principal. Os erros são tratados com `try/catch` e `Log.e`.

## CRUD nas telas

Na Lista de desejos, o botão do detalhe do jogo insere uma linha em `lista_desejos`. A tela busca novamente os itens quando abre, usando `LaunchedEffect(Unit)`, e o botão **Remover** apaga a linha pelo DAO. O DAO também contém `atualizar`, mas a interface não oferece edição para esse tipo de item, pois só há a referência ao jogo e a data da inclusão.

Em Avaliações, o formulário publica uma nova avaliação com `inserir`. A lista é carregada ao abrir a tela e mostra somente as avaliações do jogo escolhido. Ao abrir uma avaliação, podemos editar nota e texto e salvar com `atualizar`; o botão **Remover** usa `deletar`. Esse é o fluxo de inserir, buscar, atualizar e remover que apresentaremos em sala.

O detalhe da avaliação recebe apenas o `reviewId` na rota. Ao abrir, [`MainActivity`](app/src/main/java/com/example/gamecore/MainActivity.kt) chama `loadReviewForDetail(id)`, e o ViewModel busca a avaliação por `ReviewDao.buscarPorId(id)`. Assim, o detalhe lê o item diretamente do banco, como pede a seção 3.3 do enunciado.

No Cadastro, o formulário salva um `Usuario` em `usuarios` depois de verificar se o e-mail e o nome de usuário já existem. No Login, a consulta ao `UsuarioDAO` valida o e-mail e a senha informados antes de abrir a Loja. Quando fechamos e abrimos o app, podemos entrar com a conta cadastrada porque ela permanece no banco local. A interface não oferece edição ou exclusão de usuário, embora esses métodos existam no DAO.

## Dificuldades e soluções

Nós tivemos dificuldade para fazer a interface depender do resultado do banco, em vez de mudar a lista em memória imediatamente. Durante a integração, vimos um jogo aparecer na Lista de desejos antes de o Room confirmar a gravação. Corrigimos o fluxo para atualizar a tela depois de `inserir` e `buscarTodos`. Também passamos a controlar inclusões pendentes para evitar toques repetidos e a reler a lista depois de `deletar`.

Também enfrentamos um erro na compilação do Room com a versão de Kotlin e KSP usada pelo projeto. Atualizamos a dependência para 2.8.5 e conseguimos compilar novamente. Mantivemos as chamadas aos DAOs em `Dispatchers.IO` para não executar acesso ao SQLite na thread da interface.

Nós, Gabriel Henrique Alves Raatz e Matheus Pupia, tivemos dificuldade para decidir como organizar as tabelas do banco. Escolhemos separar avaliações, Lista de desejos e usuários em três tabelas. Mantivemos o catálogo no código, pois ele não precisava ser persistido, e fizemos a Lista de desejos usar o `gameId` para mostrar os dados do jogo. Quando acrescentamos o cadastro, criamos a migração para incluir `usuarios` sem apagar os registros que já estavam salvos.

## Evidências da etapa 3

O [vídeo da equipe](https://www.youtube.com/shorts/yGpeYdXlEx0) mostra o cadastro, a navegação pelo aplicativo, a Lista de desejos e o teste de persistência de uma avaliação. No trecho final, a avaliação aparece antes de sair do app (aproximadamente 1:39); depois da volta à tela inicial do celular e da nova abertura do GameCore, a mesma avaliação aparece novamente (aproximadamente 1:59). Esse é o teste de fechar e reabrir com um registro mantido no banco. As imagens e o vídeo antigos do Trabalho 2 foram retirados por não mostrarem esta etapa.

A [documentação em PDF](documentacao/GameCore_Trabalho_3_Documentacao.pdf) reúne o processo e as decisões em um arquivo pronto para entrega. O [roteiro de apresentação](documentacao/roteiro-persistencia-room.md) ajuda a repetir ao vivo as ações de inserir, editar, remover e reabrir o aplicativo. Não use **Limpar dados** nem desinstale o aplicativo durante esse teste, porque isso apagaria o banco local.
