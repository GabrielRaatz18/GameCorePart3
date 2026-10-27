# GameCore — Trabalho 3: Persistência com Room

Aplicativo Android de loja e comunidade de jogos, desenvolvido em Kotlin com Jetpack Compose. Esta terceira etapa evolui o Trabalho 2: as avaliações e a lista de desejos deixam de existir somente em memória e passam a ser salvas localmente com Room/SQLite.

## Como executar

1. Abra esta pasta no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Execute o app em um emulador ou aparelho com Android 7.0 (API 24) ou superior.
4. Entre pela tela inicial, abra um jogo, adicione-o à lista de desejos ou compre-o para poder publicar uma avaliação.

## O que mudou do Trabalho 2

No Trabalho 2, `GameCoreViewModel` mantinha as listas em memória. Isso permitia inserir, editar e remover itens durante a sessão, mas tudo se perdia quando a Activity era fechada. Nesta versão, o ViewModel continua oferecendo estado reativo para o Compose, porém lê e grava as listas persistentes no banco Room.

| Recurso | Trabalho 2 | Trabalho 3 |
| --- | --- | --- |
| Lista de desejos | Lista em memória | Tabela `lista_desejos` no Room |
| Avaliações | Lista em memória | Tabela `avaliacoes` no Room |
| Adicionar / editar / remover avaliação | Apenas durante a sessão | CRUD persistido no SQLite local |
| Detalhe de avaliação | Procurava na lista em memória | Busca a avaliação pelo ID recebido na rota |

O carrinho e a biblioteca continuam sendo estados temporários porque esta etapa concentra a persistência nas listas que já possuíam interações completas na interface: Lista de desejos e Avaliações.

## Estrutura do banco

| Entidade | Campos principais | Motivo |
| --- | --- | --- |
| `ReviewData` (`avaliacoes`) | `id`, `gameId`, autor, nota, texto e contagem de utilidade | Representa a avaliação publicada para um jogo. O `gameId` mantém a relação com o catálogo. |
| `WishlistEntry` (`lista_desejos`) | `id`, `gameId` e data de adição | Persiste apenas a referência do jogo; os dados visuais do catálogo continuam centralizados em `storeGames`. |
| `Usuario` (`usuarios`) | `id`, nome, usuário, e-mail e senha | Guarda os dados usados no cadastro e na validação do login local. |

As entidades usam `@PrimaryKey(autoGenerate = true)` com `id: Int = 0`.

- [ReviewDao](app/src/main/java/com/example/gamecore/data/ReviewDao.kt) possui `inserir`, `buscarTodos`, `atualizar`, `deletar`, `buscarPorId` e `buscarPorJogoId`.
- [WishlistDao](app/src/main/java/com/example/gamecore/data/WishlistDao.kt) possui o mesmo CRUD e consultas por ID e por jogo.
- [UsuarioDAO](app/src/main/java/com/example/gamecore/data/UsuarioDAO.kt) possui inserção, consultas para cadastro/login e os métodos de atualizar e excluir.
- [AppDatabase](app/src/main/java/com/example/gamecore/data/AppDatabase.kt) reúne as entidades e expõe os três DAOs por meio de um singleton com `@Volatile` e `synchronized`.

Todas as operações dos DAOs são `suspend` e são chamadas dentro de `viewModelScope.launch(Dispatchers.IO)`, com `try/catch` e registro via `Log.e`. Após cada alteração, o estado observado pelo Compose é atualizado na thread principal.

### Cadastro e login

O cadastro grava um `Usuario` na tabela `usuarios` e verifica se o e-mail ou o nome de usuário já foi usado. O login consulta o mesmo banco por e-mail e senha antes de abrir a loja. Para facilitar a demonstração, o primeiro acesso cria a conta local `matheus@gmail.com` com a senha `12345678`.

Esse fluxo é didático e local, seguindo a estrutura simples trabalhada em aula. Em uma aplicação publicada, a autenticação deveria ser feita por um servidor e a senha não deveria ser guardada em texto puro.

## Fluxos demonstráveis

### Lista de desejos

1. Abra um jogo e toque em **Adicionar à lista de desejos**.
2. A entrada é inserida na tabela `lista_desejos`.
3. Abra Configurações → Lista de desejos e remova o item para executar o `@Delete` persistido.
4. Feche e reabra o app: os itens não removidos continuam na lista.

### Avaliações (CRUD completo)

1. Adicione um jogo ao carrinho e finalize a compra para colocá-lo na biblioteca.
2. Abra o jogo e toque em **Ver avaliações**.
3. Publique uma avaliação: a tela executa o `@Insert` no Room.
4. Abra a própria avaliação, edite nota ou texto e salve: a tela executa o `@Update`.
5. Remova a avaliação: a tela executa o `@Delete`.
6. Feche e abra o aplicativo para confirmar que a avaliação que permaneceu no banco continua disponível.

As telas de Lista de desejos e Avaliações disparam uma atualização com `LaunchedEffect` ao serem abertas. O detalhe de avaliação recebe apenas seu `reviewId` pela navegação e chama `ReviewDao.buscarPorId(id)` através do ViewModel; o objeto não é passado pronto entre telas.

## Evidências e apresentação

As capturas de interface e o vídeo da etapa anterior estão em [documentacao/](documentacao/README.md). Para registrar a evidência específica desta etapa — inserir um item, fechar o app e reabri-lo — siga o [roteiro de persistência Room](documentacao/roteiro-persistencia-room.md) em um aparelho ou emulador. O roteiro também cobre inserir, editar e excluir uma avaliação para a apresentação ao vivo.

## Tecnologias

- Kotlin
- Jetpack Compose e Material 3
- Navigation Compose
- Room (`room-runtime`, `room-ktx` e KSP `room-compiler`)
- Coroutines / `Dispatchers.IO`
