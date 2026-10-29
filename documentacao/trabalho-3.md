# GameCore Trabalho 3

> Registro da etapa anterior. A documentação atual (Trabalho 4, MVVM) está no [README principal](../README.md). Os links abaixo já apontam para os arquivos na nova organização de pacotes.

Nós desenvolvemos o GameCore em Kotlin e Jetpack Compose. Nesta versão, usamos Room para guardar avaliações, itens da Lista de desejos e contas criadas no cadastro local. Continuamos o projeto que começamos no Trabalho 2.

Integrantes: Gabriel Henrique Alves Raatz e Matheus Pupia.

## Como executar

1. Abrimos o projeto no Android Studio e aguardamos a sincronização do Gradle.
2. Executamos o módulo `app` em um aparelho ou emulador com Android 7.0 (API 24) ou superior.
3. No primeiro acesso, criamos uma conta em **Cadastrar** e entramos no aplicativo. Não deixamos uma conta de demonstração pronta.
4. Para testar a Lista de desejos, abrimos um jogo na Loja e usamos o botão do detalhe. Para testar avaliações, concluímos uma compra simulada, abrimos o jogo na Biblioteca e entramos em **Ver avaliações**.

Configuramos `room-runtime`, `room-ktx` e `room-compiler` com KSP em [`app/build.gradle.kts`](../app/build.gradle.kts). Para conferir a compilação e os testes locais pelo terminal, usamos `./gradlew :app:testDebugUnitTest` no macOS/Linux ou `.\gradlew.bat :app:testDebugUnitTest` no Windows.

## Do Trabalho 2 para o Trabalho 3

No Trabalho 2, mantínhamos a Lista de desejos e as avaliações em memória. Conseguíamos usar as telas, mas perdíamos os itens ao fechar o aplicativo. Nesta etapa, mantivemos os botões e passamos a ler e gravar essas listas no SQLite local pelos DAOs do Room. Também criamos cadastro e login com uma tabela de usuários no mesmo banco.

Decidimos manter o carrinho e a Biblioteca temporários. Usamos a compra simulada para colocar um jogo na Biblioteca durante a sessão e liberar o formulário de avaliação. Mantivemos o catálogo em `storeGames`, sem criar uma tabela de jogos nesta etapa.

## Estrutura do banco

Organizamos o banco em três partes: avaliações, Lista de desejos e cadastro/login. Criamos uma entidade para cada uma:

| Entidade | Campos | Por que foram escolhidos |
| --- | --- | --- |
| Avaliações: [`ReviewData`](../app/src/main/java/com/example/gamecore/data/local/ReviewData.kt) → `avaliacoes` | `id`, `gameId`, `author`, `rating`, `text`, `helpful` | Usamos `id` para identificar cada avaliação e `gameId` para ligá-la ao jogo. Guardamos também autor, nota, texto e votos úteis. |
| Lista de desejos: [`WishlistEntry`](../app/src/main/java/com/example/gamecore/data/local/WishlistEntry.kt) → `lista_desejos` | `id`, `gameId`, `adicionadoEm` | Guardamos o jogo e a data de inclusão. Buscamos nome, capa e preço no catálogo para não duplicar esses dados. |
| Cadastro e login: [`Usuario`](../app/src/main/java/com/example/gamecore/data/local/Usuario.kt) → `usuarios` | `id`, `nome`, `usuario`, `email`, `senha` | Salvamos os dados preenchidos no cadastro. No login, consultamos e-mail e senha para encontrar a conta. |

Anotamos as três classes com `@Entity` e usamos `id: Int = 0` com `@PrimaryKey(autoGenerate = true)`. Criamos um índice único em `gameId` na Lista de desejos para não salvar o mesmo jogo duas vezes.

Reunimos as três entidades e os DAOs em [`AppDatabase`](../app/src/main/java/com/example/gamecore/data/local/AppDatabase.kt). Criamos uma única instância com `Room.databaseBuilder`, `companion object`, `@Volatile` e `synchronized`, seguindo o exemplo de aula. Quando acrescentamos os usuários, passamos o banco para a versão 2 e criamos uma migração que preserva avaliações e desejos já salvos.

Criamos cadastro e login como complemento ao Trabalho 3, embora o enunciado não exigisse autenticação. Fizemos tudo localmente, sem servidor ou sincronização. Nesta versão de estudo, guardamos a senha diretamente no banco; sabemos que isso não seria adequado para um aplicativo publicado.

## Consultas dos DAOs

Criamos [`ReviewDao`](../app/src/main/java/com/example/gamecore/data/local/ReviewDao.kt), [`WishlistDao`](../app/src/main/java/com/example/gamecore/data/local/WishlistDao.kt) e [`UsuarioDAO`](../app/src/main/java/com/example/gamecore/data/local/UsuarioDAO.kt), um para cada tabela. Nos três, implementamos métodos `suspend` para inserir (`@Insert`), buscar todos (`@Query`), atualizar (`@Update`) e remover (`@Delete`). Acrescentamos buscas por `id` para localizar registros específicos.

Em `ReviewDao`, usamos `buscarPorJogoId(gameId)` para mostrar só as avaliações do jogo aberto. Em `WishlistDao`, usamos a busca por `gameId` para encontrar o item antes de removê-lo. Em `UsuarioDAO`, consultamos `id`, e-mail e nome de usuário; verificamos se já existe uma conta antes do cadastro e usamos `buscarLogin(email, senha)` para entrar.

Executamos as operações no `GameCoreViewModel` (substituído pelos ViewModels do pacote `viewmodel` no Trabalho 4) com `viewModelScope.launch(Dispatchers.IO)`, sem acessar o SQLite na thread da interface. Depois da resposta do banco, atualizamos o estado observado pelo Compose. Tratamos erros com `try/catch` e `Log.e`.

## CRUD nas telas

Na Lista de desejos, salvamos um jogo pelo botão do detalhe. Ao abrir a tela, carregamos os itens com `LaunchedEffect(Unit)`; pelo botão **Remover**, apagamos o registro no DAO. Não fizemos edição de desejos na interface: o DAO tem `atualizar`, mas a tabela guarda somente o jogo e a data.

Em Avaliações, publicamos pelo formulário com `inserir`. Ao abrir a tela, carregamos somente os registros do jogo escolhido. Podemos alterar nota e texto com `atualizar` e apagar o registro com **Remover** e `deletar`. Esse é o fluxo de inserir, buscar, atualizar e remover que apresentamos.

No detalhe da avaliação, enviamos somente `reviewId` pela rota. Em [`MainActivity`](../app/src/main/java/com/example/gamecore/ui/GameCoreApp.kt), chamamos `loadReviewForDetail(id)` e buscamos a avaliação com `ReviewDao.buscarPorId(id)`, sem passar o objeto inteiro entre telas.

No Cadastro, verificamos se e-mail e nome de usuário já existem antes de salvar a conta. No Login, consultamos e-mail e senha no `UsuarioDAO` antes de abrir a Loja. Conseguimos entrar novamente após fechar o app porque mantemos a conta no banco. Criamos `atualizar` e `deletar` no DAO, mas não colocamos essas ações na tela de usuários.

## Dificuldades e soluções

Nós tivemos dificuldade para fazer a interface depender do resultado do banco, em vez de mudar a lista em memória imediatamente. Vimos um jogo aparecer na Lista de desejos antes de o Room confirmar a gravação. Corrigimos o fluxo para atualizar a tela depois de `inserir` e `buscarTodos`. Também passamos a controlar inclusões pendentes para evitar toques repetidos e a reler a lista depois de `deletar`.

Também enfrentamos um erro na compilação do Room com a versão de Kotlin e KSP usada pelo projeto. Atualizamos a dependência para 2.8.5 e conseguimos compilar novamente. Mantivemos as chamadas aos DAOs em `Dispatchers.IO` para não executar acesso ao SQLite na thread da interface.

Nós, Gabriel Henrique Alves Raatz e Matheus Pupia, tivemos dificuldade para decidir como organizar as tabelas do banco. Escolhemos separar avaliações, Lista de desejos e usuários em três tabelas. Mantivemos o catálogo no código e usamos `gameId` para localizar os dados dos jogos. Quando acrescentamos cadastro e login, criamos a migração para incluir `usuarios` sem apagar os registros anteriores.

## Evidências da etapa 3

No [nosso vídeo](https://www.youtube.com/shorts/yGpeYdXlEx0), mostramos cadastro, navegação, Lista de desejos e uma avaliação salva. No trecho final, mostramos a avaliação por volta de 1:39, saímos para a tela inicial do celular e abrimos o GameCore novamente. Por volta de 1:59, mostramos a mesma avaliação após a reabertura. Retiramos as imagens e o vídeo antigos do Trabalho 2 porque não mostravam esta etapa.

Reunimos nosso processo e nossas decisões na [documentação em PDF](GameCore_Trabalho_3_Documentacao.pdf). Usamos o [roteiro de apresentação](roteiro-persistencia-room.md) para repetir ao vivo as ações de inserir, editar, remover e reabrir o aplicativo. Não usamos **Limpar dados** nem desinstalamos o app durante o teste, pois isso apagaria o banco local.
