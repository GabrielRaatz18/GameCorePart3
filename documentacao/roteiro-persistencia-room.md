# Roteiro de evidência — Trabalho 3 (Room)

Use este roteiro para gravar o vídeo curto ou gerar os prints pedidos na apresentação. A ideia é deixar visível que os dados foram recuperados do SQLite depois que o aplicativo deixou de existir em memória.

## Preparação

1. Instale uma build nova em um emulador ou aparelho.
2. Abra o app, faça login e deixe a gravação de tela ligada.
3. Para uma demonstração limpa, desinstale a versão anterior do app antes de começar; não use “Limpar dados” depois de inserir o item que será usado como prova.

## Prova de persistência da lista de desejos

1. Na Loja, abra qualquer jogo e toque em **Adicionar à lista de desejos**.
2. Volte para **Configurações → Lista de desejos** e mostre o item na lista.
3. Feche o aplicativo completamente (remova-o da tela de recentes).
4. Abra o aplicativo novamente, faça login e retorne à Lista de desejos.
5. Mostre o mesmo item ainda visível.

Capturas recomendadas:

- `room-01-item-adicionado.png`: item recém-adicionado na Lista de desejos.
- `room-02-app-reaberto.png`: o mesmo item após fechar e reabrir o app.

## CRUD de avaliações

1. Adicione um jogo ao carrinho e conclua a compra para liberá-lo na Biblioteca.
2. Abra o jogo e entre em **Ver avaliações**.
3. Preencha a nota e o comentário e toque em **Publicar avaliação** (`@Insert`).
4. Toque em **Editar sua avaliação**, altere texto ou nota e salve (`@Update`).
5. Toque em **Remover** na sua avaliação (`@Delete`).
6. Publique outra avaliação, feche e reabra o app para demonstrar a persistência desse registro.

## Pontos para explicar durante a apresentação

- `ReviewData` e `WishlistEntry` são as duas entidades Room, ambas com chave primária inteira autogerada.
- `ReviewDao` e `WishlistDao` têm os quatro métodos CRUD obrigatórios, todos `suspend`.
- `AppDatabase` é um singleton e concentra os DAOs.
- O detalhe da avaliação recebe somente o ID da rota e busca o registro via `buscarPorId`.
- As operações em banco usam `Dispatchers.IO`; a interface é atualizada depois da conclusão.
