# Roteiro de gravação do Trabalho 3

## Preparação

Instale e abra a versão atual do GameCore em um aparelho ou emulador. Crie uma conta pela tela **Cadastrar**; o app não possui usuário de demonstração. Ative a gravação de tela antes de iniciar o teste.

## Persistência da Lista de desejos

1. Entre na Loja, abra um jogo e toque em **Adicionar à lista de desejos**.
2. Abra **Perfil → Configurações → Lista de desejos** e mostre o jogo salvo.
3. Feche o aplicativo por completo, removendo-o da lista de apps recentes.
4. Abra o app novamente, faça login e volte à Lista de desejos.
5. Mostre o mesmo jogo ainda na lista. Grave a tela antes e depois da reabertura.

Não desinstale o app nem use **Limpar dados** entre os passos 2 e 4: essas ações apagam o SQLite local e invalidam o teste.

## CRUD de avaliações

1. Adicione um jogo ao Carrinho e conclua a compra simulada para incluí-lo na Biblioteca desta sessão.
2. Abra o jogo na Biblioteca, entre em **Ver avaliações** e publique uma avaliação com nota e texto.
3. Abra essa avaliação, altere a nota ou o texto e toque em **Salvar alterações**.
4. Volte à lista e remova a avaliação.
5. Se quiser demonstrar persistência também com avaliação, publique outra, feche e reabra o app, faça login e abra as avaliações do mesmo jogo pelo detalhe na Loja. A Biblioteca é temporária e pode aparecer vazia após a reabertura; a avaliação continua salva no Room.

Ao apresentar o código, mostre `ReviewData`, `WishlistEntry`, os dois DAOs, `AppDatabase` e a rota de detalhe que passa `reviewId`. O detalhe deve buscar a avaliação por `buscarPorId(id)`, não receber o objeto completo pela navegação.
