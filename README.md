# Exercício - Podcast Player

Esta tarefa envolve os conceitos de UI widgets, Threads, Services, Custom Adapters, 
Intents, Permissions, SharedPreferences, SQLite, Broadcast Receivers. 
Faça um *fork* deste projeto, siga os passos na ordem sugerida abaixo e identifique quais 
os passos completados no seu repositório. Inclua o link do seu repositório na planilha
circulada no Slack da disciplina. 

A versão atual da aplicação lê um arquivo XML obtido a partir de um feed, usando `AsyncTask`
e faz o parsing para obter as informações úteis para exibição. Atualmente, toda vez que a 
aplicação é aberta, é feito o download e exibição da lista de itens. 

Este exercício consiste em implementar as funcionalidades básicas de um player de podcast,
como persistência dos dados, gerenciar o download de episódios do podcast, escutar um 
episódio, atualizações em segundo plano, notificações de novos episódios, etc. 

01. Inicialmente, teste a aplicação e certifique-se de que está tudo funcionando. Fique à vontade para melhorar o visual. :)
02. Ajuste o parser de XML (`XmlFeedParser`) para obter o link de download do arquivo XML em questão. Já há um método criado `readEnclosure`, basta obter o valor do atributo correspondente e retornar;
03. Faça com que a aplicação passe a usar um banco de dados SQLite (`PodcastDBHelper`) como forma de persistir os dados. Isto é, após o download e parsing do RSS, a lista de episódios deve ser armazenada no banco;
04. A manipulação do banco de dados deve ser feita por meio do `XmlFeedProvider`, já criado no respectivo pacote, sem implementação de nenhum método;
05. Altere a aplicação de forma que, ao clicar em um título, o usuário seja direcionado para `EpisodeDetailActivity`, onde devem ser exibidos os detalhes do episódio em questão;
06. Altere a fonte de dados do ListView para usar o banco de dados ao invés do resultado do AsyncTask. Ou seja, mesmo que esteja sem conectividade, deve ser possível ao menos listar todos os itens obtidos na última vez que o app rodou. Reforçando que o acesso aos dados deve ser feito por meio do content provider;
07. Altere o comportamento da aplicação de forma que ao clicar em download, o episódio seja baixado, e a URI de localização do arquivo seja armazenada no banco; 
08. Após o download, o visual do botão deve ser modificado (use um texto ou imagem), de forma que ao clicar no botão, o episódio seja tocado;
09. Altere a aplicação de forma a usar um Service para fazer o download e persistência dos episódios no banco. Dica: use IntentService;
10. Ao finalizar a tarefa, o Service deve enviar um broadcast avisando que terminou; 
11. Se o usuário estiver com o app em primeiro plano, a atualização da lista de itens deve ser automática;
12. Se o usuário não estiver com o app em primeiro plano, deve ser exibida uma notificação; 
13. Usando SharedPreferences e JobScheduler, estabeleça uma periodicidade para o carregamento periódico de dados em `SettingsActivity`.
14. Ao dar pausa em um episódio, deve ser registrado o ponto onde parou, para que o usuário, ao retornar ao aplicativo, continue escutando do ponto onde parou. 
15. Ao terminar de tocar um episódio, remova o arquivo da memória.

---

# Orientações

  - Comente o código que você desenvolver, explicando o que cada parte faz.
  - Entregue o exercício mesmo que não tenha completado todos os itens listados acima. (marque no arquivo README.md do seu repositório o que completou, usando o template abaixo)

----

# Status

| Passo | Completou? |
| ------ | ------ |
| 1 | ✅ |
| 2 | ✅ |
| 3 | ✅ |
| 4 | ✅ |
| 5 | ✅ |
| 6 | **não** |
| 7 | **não** |
| 8 | **não** |
| 9 | **não** |
| 10 | **não** |
| 11 | **não** |
| 12 | **não** |
| 13 | **não** |
| 14 | **não** |
| 15 | **não** |
