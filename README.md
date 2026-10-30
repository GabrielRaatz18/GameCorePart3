# GameCore Trabalho 4 — Arquitetura MVVM

Nós desenvolvemos o GameCore em Kotlin e Jetpack Compose. No Trabalho 3, passamos a guardar avaliações, Lista de desejos e contas de usuário com Room. Neste Trabalho 4, não criamos nenhuma tela, campo ou funcionalidade nova: reorganizamos o mesmo app em MVVM (Model — View — ViewModel), com Repository, DAO reativo com `Flow`, `UiState` e `ViewModelFactory`.

Integrantes: Gabriel Henrique Alves Raatz e Matheus Pupia.

A documentação da etapa anterior continua em [documentacao/trabalho-3.md](documentacao/trabalho-3.md).

## Como executar

1. Abrimos o projeto no Android Studio e aguardamos a sincronização do Gradle.
2. Executamos o módulo `app` em um aparelho ou emulador com Android 7.0 (API 24) ou superior.
3. No primeiro acesso, criamos uma conta em **Cadastrar** e entramos no aplicativo.
4. Testamos os mesmos fluxos do Trabalho 3: Lista de desejos pelo detalhe do jogo, compra simulada para liberar a avaliação e **Ver avaliações** na Biblioteca.

Para compilar pelo terminal, usamos `./gradlew :app:assembleDebug` no macOS/Linux ou `.\gradlew.bat :app:assembleDebug` no Windows.

## Como estava no Trabalho 3 e o que mudou

No Trabalho 3, tínhamos um único `GameCoreViewModel` que recebia o `Context`, abria o `AppDatabase` e chamava os três DAOs direto. Cada ação usava `viewModelScope.launch(Dispatchers.IO)` e depois `withContext(Dispatchers.Main)`, e o estado ficava espalhado em várias variáveis `mutableStateOf` (`cart`, `library`, `wishlist`, `reviews`, `currentUser`, `authError`...). A tela de Lista de desejos e a de Avaliações usavam `LaunchedEffect` para buscar os dados de novo, e depois de cada `inserir`/`deletar` precisávamos chamar `buscarTodos()` na mão. Todos os arquivos (telas, modelos e ViewModel) estavam soltos no pacote raiz.

Agora:

- Os arquivos estão divididos em pacotes (`ui`, `data.local`, `data.model`, `data.repository`, `viewmodel`).
- Cada Entity tem um Repository, e só ele fala com o DAO.
- `buscarTodos()` retorna `Flow<List<...>>`, então o Room avisa sozinho quando a tabela muda. Os `LaunchedEffect` de recarregar listas e as releituras manuais depois de inserir/remover deixaram de existir.
- O `GameCoreViewModel` foi dividido em quatro ViewModels, um por responsabilidade, cada um com o seu `UiState` exposto como `StateFlow`.
- As telas só coletam o `uiState` com `collectAsStateWithLifecycle()` e chamam métodos do ViewModel.

## Como dividimos os pacotes

```
com.example.gamecore
├── MainActivity.kt            cria o banco e os Repositories e chama MainApp
├── data
│   ├── local                  Room: Entities, DAOs e AppDatabase
│   │   ├── AppDatabase.kt
│   │   ├── ReviewData.kt      ReviewDao.kt
│   │   ├── WishlistEntry.kt   WishlistDao.kt
│   │   └── Usuario.kt         UsuarioDAO.kt
│   ├── model
│   │   └── GameData.kt        GameData, UserData e o catálogo storeGames (não são tabelas)
│   └── repository
│       ├── ReviewRepository.kt
│       ├── WishlistRepository.kt
│       └── UsuarioRepository.kt
├── viewmodel
│   ├── AuthViewModel.kt       AuthUiState + AuthViewModel + AuthViewModelFactory
│   ├── WishlistViewModel.kt   WishlistUiState + WishlistViewModel + WishlistViewModelFactory
│   ├── ReviewViewModel.kt     ReviewUiState + ReviewViewModel + ReviewViewModelFactory
│   └── CartViewModel.kt       CartUiState + CartViewModel
└── ui
    ├── GameCoreApp.kt         NavHost: obtém os ViewModels e liga cada tela ao seu estado
    ├── AuthScreens.kt, StoreScreen.kt, GameScreen.kt, CartScreen.kt,
    │   CommunityScreens.kt, ReviewScreens.kt
    ├── GameCoreComponents.kt, GameCoreDesign.kt   componentes visuais reaproveitados
    └── theme
```

- **`ui`**: só `@Composable`. Nenhuma tela importa DAO, `AppDatabase`, `CoroutineScope` ou `Dispatchers`.
- **`data.local`**: exatamente as Entities, DAOs e o `AppDatabase` do Trabalho 3. Tiramos o `ReviewData` do arquivo `GameData.kt` e colocamos junto das outras Entities.
- **`data.model`**: classes que não são tabelas (`GameData`, `UserData`) e o catálogo fixo. Separamos para não misturar o que é Room com o que é só modelo.
- **`data.repository`**: um Repository por Entity.
- **`viewmodel`**: os ViewModels, cada um no mesmo arquivo que o seu `UiState` e a sua Factory, para ficar fácil achar tudo de uma tela.

O nome das tabelas (`avaliacoes`, `lista_desejos`, `usuarios`) e a versão do banco (2) não mudaram. Por isso os dados que já estavam salvos no aparelho continuam lá depois da atualização, sem precisar de migração.

## Por que o Repository existe

Antes, quem queria dados chamava o DAO direto. Agora o caminho é sempre **tela → ViewModel → Repository → DAO**. Os Repositories ([`ReviewRepository`](app/src/main/java/com/example/gamecore/data/repository/ReviewRepository.kt), [`WishlistRepository`](app/src/main/java/com/example/gamecore/data/repository/WishlistRepository.kt), [`UsuarioRepository`](app/src/main/java/com/example/gamecore/data/repository/UsuarioRepository.kt)) recebem o DAO no construtor e repassam `buscarTodos()`, `inserir`, `atualizar` e `deletar`.

Hoje ele só repassa, mas é o único ponto que sabe de onde os dados vêm. Se um dia as avaliações vierem de uma API, só o `ReviewRepository` muda; o `ReviewViewModel` e as telas continuam iguais. Também deixou de ser preciso o ViewModel receber `Context` para abrir o banco: o banco e os Repositories são criados uma vez na [`MainActivity`](app/src/main/java/com/example/gamecore/MainActivity.kt).

## DAO reativo com Flow

Em [`ReviewDao`](app/src/main/java/com/example/gamecore/data/local/ReviewDao.kt), [`WishlistDao`](app/src/main/java/com/example/gamecore/data/local/WishlistDao.kt) e [`UsuarioDAO`](app/src/main/java/com/example/gamecore/data/local/UsuarioDAO.kt), a busca geral ficou assim:

```kotlin
@Query("SELECT * FROM avaliacoes ORDER BY id DESC")
fun buscarTodos(): Flow<List<ReviewData>>
```

Não é mais `suspend`. No `init` de cada ViewModel, coletamos esse `Flow` uma vez e copiamos a lista para o `UiState`. Quando publicamos, editamos ou removemos uma avaliação, o Room emite a lista nova e a tela se redesenha sozinha. As outras consultas (`buscarPorId`, `buscarLogin`...) continuam `suspend`, porque são buscas pontuais.

## ViewModels, UiState e Factory

Todos seguem o mesmo padrão:

```kotlin
private val _uiState = MutableStateFlow(ReviewUiState())
val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()
```

A tela só lê `uiState`; quem altera `_uiState` é o próprio ViewModel, dentro dos métodos de ação (`addReview`, `removeReview`, `signIn`, `addToWishlist`...), que chamam o Repository em `viewModelScope.launch { }`. Como as funções `suspend` do Room já rodam fora da thread principal, não precisamos mais de `Dispatchers.IO` nem de `withContext`.

| UiState | Campos | Por que |
| --- | --- | --- |
| [`AuthUiState`](app/src/main/java/com/example/gamecore/viewmodel/AuthViewModel.kt) | `currentUser`, `loginEmail`, `loginPassword`, `registerName`, `registerUsername`, `registerEmail`, `registerPassword`, `authError`, `isAuthenticating` | Tudo que Login e Cadastro precisam: o texto dos formulários (antes eram `remember` soltos dentro das telas), a mensagem de erro e o "Entrando..."/"Cadastrando...". O usuário logado também fica aqui porque é o resultado do login. |
| [`WishlistUiState`](app/src/main/java/com/example/gamecore/viewmodel/WishlistViewModel.kt) | `entries`, `games`, `isLoading` | `entries` são as linhas do Room (usadas para remover), `games` é a mesma lista já convertida para `GameData` para a tela desenhar. |
| [`ReviewUiState`](app/src/main/java/com/example/gamecore/viewmodel/ReviewViewModel.kt) | `reviews`, `reviewForDetail`, `isLoading`, `newReviewText`, `newReviewRating`, `editText`, `editRating` | A lista que alimenta a tela de Avaliações e a nota no detalhe do jogo, a avaliação aberta na tela de detalhe (buscada por `id`) e o texto e a nota dos formulários de publicar e de editar. |
| [`CartUiState`](app/src/main/java/com/example/gamecore/viewmodel/CartViewModel.kt) | `cart`, `library`, `couponCode`, `paymentMethodIndex` | Carrinho e Biblioteca, que como no Trabalho 3 ficam só em memória, além do cupom e da forma de pagamento escolhidos. O desconto (`discountRate`) e o nome do pagamento (`paymentMethod`) são calculados a partir desses campos. |

Os três ViewModels que recebem Repository têm uma Factory que implementa `ViewModelProvider.Factory`. Em [`GameCoreApp.kt`](app/src/main/java/com/example/gamecore/ui/GameCoreApp.kt), obtemos cada um assim:

```kotlin
val reviewViewModel: ReviewViewModel = viewModel(factory = ReviewViewModelFactory(reviewRepository))
val reviewState by reviewViewModel.uiState.collectAsStateWithLifecycle()
```

O `CartViewModel` não recebe nada no construtor, então usa o `viewModel()` simples visto na Aula 12. Para isso, adicionamos `lifecycle-viewmodel-compose` e `lifecycle-runtime-compose` em [`app/build.gradle.kts`](app/build.gradle.kts).

Todos os formulários que salvam ou usam dados (login, cadastro, avaliação e carrinho) ficam no `UiState`. Deixamos em `remember` dentro das telas apenas o que controla a exibição: mostrar/ocultar senha, busca e categoria selecionada na Loja, slide atual do carrossel e o aviso de "adicionando" no botão da Lista de desejos.

## Algo quebrou durante a reorganização?

- **Compra e Lista de desejos em ViewModels diferentes.** No Trabalho 3, `checkout()` movia o carrinho para a Biblioteca e apagava os jogos comprados da Lista de desejos no mesmo método. Ao separar, o `CartViewModel` não tem acesso ao `WishlistRepository`. Resolvemos fazendo `checkout()` devolver os jogos comprados, e a tela repassa essa lista para `wishlistViewModel.removeGames(...)`.
- **Ordem das avaliações.** Antes, uma avaliação recém-publicada era colocada no fim da lista em memória, mas ao reabrir a tela ela aparecia no topo (o banco ordena por `id DESC`). Com `Flow`, a lista sempre vem do banco, então a nova avaliação já aparece no topo. Ficou consistente.
- **Imports depois de mover os arquivos.** Ao trocar o pacote das telas e tirar `ReviewData` de `GameData.kt`, os arquivos passaram a precisar de imports que antes não existiam (`R`, `GameData`, `toBrazilianPrice`...), porque estavam todos no mesmo pacote. Ajustamos os imports e conferimos com o build.
- **Campos que não eram limpos.** Com `remember`, o texto de um formulário sumia ao sair da tela. O ViewModel continua vivo durante a navegação, então, ao mover os campos para o `UiState`, o texto passaria a continuar lá ao voltar. Para manter o comportamento do Trabalho 3, usamos `DisposableEffect` em cada rota para limpar o formulário ao sair da tela.
- **Sair da conta.** O `signOut()` antigo limpava tudo num lugar só. Agora o botão **Sair** chama `authViewModel.signOut()` e `cartViewModel.clear()`. A Lista de desejos e as Avaliações não precisam ser limpas, porque vêm do `Flow` do banco.

<!-- TODO (equipe): completar com o que aconteceu ao testar no aparelho, se algo diferente aparecer. -->

## Dificuldades da equipe

<!-- TODO (equipe): contar com as nossas palavras quem teve dificuldade em qual ponto (por exemplo, entender a diferença entre _uiState e uiState, ou por que a Factory é necessária) e como resolvemos. -->

## Evidências da etapa 4

<!-- TODO (equipe): adicionar prints e o link do vídeo mostrando: cadastro/login, adicionar e remover da Lista de desejos, publicar/editar/remover avaliação, e fechar e reabrir o app com os dados mantidos. -->

Usamos o [roteiro de apresentação](documentacao/roteiro-persistencia-room.md) para repetir ao vivo os mesmos fluxos do Trabalho 3 e mostrar que nada mudou para quem usa o app.
