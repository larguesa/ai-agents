## Sobre

Meu nome é Ricardo Pupo Larguesa, professor da Fatec (Santos e Praia grande). Leciono Programação Orientada a Objetos e Machine Learning.
https://linktr.ee/ricardo.pupo

Criei este projeto para compartilhar exemplos de agentes de inteligência artificial utilizando uma LLM (no caso, Google Gemini) e ajudar analistas de sistemas a aumentar a produtividade e se preparar para as novas formas de trabalho que a inteligência artificial está criando.

Use à vontade, compartilhe e colabore.

# Relatório CodeScribeAgent em Wed May 14 20:11:47 BRT 2025

# Relatório Técnico Detalhado: Projeto "my-agents" - Exemplos de Agentes de IA com Google Gemini

Este relatório apresenta uma análise detalhada do projeto "my-agents", que consiste em exemplos de agentes de IA construídos para interagir com a API do Google Gemini. O projeto visa demonstrar como analistas de sistemas podem aumentar sua produtividade e se preparar para novas formas de trabalho impulsionadas pela IA. O relatório aborda a arquitetura do projeto, seus componentes, pontos críticos de implementação e possíveis melhorias.

## 1. Estrutura do Projeto

O projeto "my-agents" possui a seguinte estrutura de diretórios e arquivos:

```
[DIR] my-agents
  [DIR] .vscode
    settings.json
  api_key.txt
  [DIR] bin
    App.class
    ChainOfThoughtAgent.class
    CodeScribeAgent.class
    DeepSearchAgent.class
    SimpleAgent.class
    SimpleSearchAgent.class
    StocksLoggerAgent$1.class
    StocksLoggerAgent.class
  [DIR] lib
    json-20250107.jar
  README.md
  requestBody.json (Conteúdo não fornecido)
  response.json
  response.md
  responseBody.json
  searchResponseBody.json
  [DIR] src
    App.java
    ChainOfThoughtAgent.java
    CodeScribeAgent.java
    DeepSearchAgent.java
    SimpleAgent.java
    SimpleSearchAgent.java
    StocksLoggerAgent.java
```

**Descrição dos Diretórios e Arquivos:**

*   **.vscode/:** Contém configurações específicas do Visual Studio Code para o projeto.
    *   **settings.json:** Define as configurações do ambiente Java, incluindo a pasta de código fonte (`src`), a pasta de saída para os arquivos compilados (`bin`) e as bibliotecas referenciadas (`lib`).
*   **api_key.txt:** Armazena a chave de API para autenticação com o serviço Google Gemini.
*   **bin/:** Contém os arquivos `.class` compilados a partir dos arquivos `.java` na pasta `src/`.
*   **lib/:** Contém a biblioteca `json-20250107.jar`, utilizada para manipulação de objetos JSON.
*   **README.md:** Documento de introdução ao projeto, descrevendo o objetivo, autoria e convite à colaboração.
*   **requestBody.json:** (Conteúdo não fornecido) Deverá conter o corpo da requisição enviada para a API Gemini. Essencial para entender os prompts utilizados em cada agente.
*   **response.json:** Contém dados de cotações de ações (MSFT, GOOGL, AAPL) com timestamps, incluindo um snapshot com valores zerados, possivelmente indicando um erro ou indisponibilidade.
*   **response.md:** Contém um relatório sobre engenharia de prompt para profissionais de TI, resumindo pesquisas, detalhando técnicas, aplicações práticas, personalização, comparação de abordagens e desafios.
*   **responseBody.json:** Armazena a resposta do modelo Gemini a um prompt, incluindo o texto da resposta, o motivo da finalização e metadados sobre o uso de tokens.
*   **searchResponseBody.json:** Contém a resposta do modelo Gemini a uma pesquisa sobre engenharia de prompt, incluindo a resposta detalhada, links para páginas web relevantes e informações sobre o uso de tokens.
*   **src/:** Contém os arquivos de código fonte Java (.java) que implementam os diferentes agentes de IA.
    *   **App.java:** Ponto de entrada da aplicação, responsável por interagir com a API do Google Gemini, ler a chave da API e realizar requisições para gerar conteúdo baseado em prompts.
    *   **ChainOfThoughtAgent.java:** Demonstra a estratégia "Chain of Thought" (CoT) para obter respostas mais precisas do modelo Gemini.
    *   **CodeScribeAgent.java:** Automatiza a geração de um relatório técnico detalhado de um projeto, utilizando o modelo Gemini.
    *   **DeepSearchAgent.java:** Implementa uma estratégia de pesquisa aprofundada utilizando o modelo Gemini, gerando múltiplos prompts, realizando pesquisas e consolidando os resultados.
    *   **SimpleAgent.java:** Demonstra a utilização mais básica da API Gemini.
    *   **SimpleSearchAgent.java:** Demonstra uma pesquisa simples na web utilizando a API Gemini.
    *   **StocksLoggerAgent.java:** Implementa um agente que coleta periodicamente cotações de ações da API Gemini e as armazena em um arquivo JSON.

## 2. Arquitetura do Projeto

A arquitetura do projeto pode ser descrita em termos de seus componentes principais e suas interações:

*   **Agentes:** São as unidades funcionais do projeto, cada uma demonstrando uma abordagem diferente para utilizar a API Gemini.  Exemplos incluem `SimpleAgent`, `ChainOfThoughtAgent`, `CodeScribeAgent`, `DeepSearchAgent`, `SimpleSearchAgent`, e `StocksLoggerAgent`.  Cada agente define um prompt, interage com a API Gemini e processa a resposta.
*   **App.java (Camada de Acesso à API):** Esta classe centraliza a lógica de interação com a API Gemini. Ela gerencia a leitura da chave da API, a construção das requisições HTTP, o envio dos prompts ao modelo, o tratamento das respostas e o salvamento dos dados em arquivos.  A classe `App` atua como uma camada de abstração, permitindo que os agentes se concentrem na lógica de seus prompts e no processamento dos resultados.
*   **API Google Gemini:** O serviço de Inteligência Artificial que fornece os modelos de linguagem e as capacidades de pesquisa utilizadas pelos agentes.
*   **Arquivos de Configuração e Dados:** Arquivos como `api_key.txt`, `requestBody.json`, `response.json`, `responseBody.json`, e `searchResponseBody.json` armazenam dados de configuração, requisições enviadas, respostas recebidas e resultados de pesquisa.

**Fluxo de Execução Típico:**

1.  O usuário executa um dos agentes (e.g., `ChainOfThoughtAgent`).
2.  O agente define um prompt específico.
3.  O agente utiliza a classe `App` para enviar o prompt para a API Gemini.
4.  A classe `App` recebe a resposta da API, processa-a e salva-a em um arquivo (e.g., `response.md`, `responseBody.json`, `searchResponseBody.json`).
5.  O agente, opcionalmente, processa a resposta e executa ações adicionais (e.g., o `ChainOfThoughtAgent` envia um segundo prompt baseado na primeira resposta).
6.  O resultado final é apresentado ao usuário ou armazenado em um arquivo.

## 3. Análise dos Agentes Implementados

*   **SimpleAgent:**  Demonstra a interação mais básica com a API, enviando um prompt simples e exibindo a resposta.  É útil para entender o fluxo básico de comunicação com a API.
*   **ChainOfThoughtAgent:** Ilustra uma técnica avançada para melhorar a precisão das respostas, dividindo o problema em etapas menores e solicitando uma explicação detalhada antes da resposta final.  Destaca a importância da engenharia de prompt.
*   **CodeScribeAgent:** Demonstra o potencial de automação da geração de relatórios técnicos, analisando a estrutura de um projeto e utilizando o Gemini para gerar resumos e um relatório consolidado.  Apresenta um caso de uso prático para analistas de sistemas.
*   **DeepSearchAgent:** Explora a combinação da geração de prompts com a pesquisa na web, permitindo a obtenção de informações mais completas e atualizadas.  Demonstra uma abordagem para lidar com problemas complexos que exigem conhecimento externo.
*   **SimpleSearchAgent:** Similar ao `SimpleAgent`, mas com a adição de pesquisa na web. Útil para comparar a resposta sem pesquisa com a resposta enriquecida pela pesquisa.
*   **StocksLoggerAgent:** Implementa um agente que coleta dados de forma contínua e armazena em um arquivo.  Demonstra a possibilidade de monitoramento e coleta de dados automatizada usando a API Gemini.

## 4. Pontos Críticos e Desafios

*   **Gerenciamento da Chave da API:** A segurança da chave da API é crucial. O projeto armazena a chave em um arquivo de texto simples (`api_key.txt`).  Em um ambiente de produção, seria necessário implementar um mecanismo mais seguro para o armazenamento e acesso à chave, como variáveis de ambiente ou um serviço de gerenciamento de segredos.
*   **Engenharia de Prompt:** A qualidade das respostas geradas pela API Gemini depende fortemente da qualidade dos prompts. A elaboração de prompts eficazes requer experimentação e conhecimento das capacidades e limitações do modelo. A documentação `response.md` ressalta a importância da engenharia de prompt e fornece insights sobre as técnicas mais eficazes.
*   **Tratamento de Erros:** O código precisa ser robusto para lidar com erros de rede, erros da API e respostas inesperadas do modelo.  O tratamento de erros deve incluir logging, retry e mecanismos de fallback.  O exemplo do `response.json` com valores zerados ilustra a necessidade de monitorar a qualidade dos dados recebidos.
*   **Consumo de Tokens:** O uso da API Gemini é cobrado com base no número de tokens utilizados.  É importante otimizar os prompts para reduzir o consumo de tokens e controlar os custos.
*   **Conteúdo de `requestBody.json`:** A ausência do conteúdo deste arquivo impede uma análise completa dos prompts utilizados. É crucial para entender a fundo a engenharia de prompt aplicada em cada agente.
*   **Manipulação de Dados JSON:** A biblioteca `org.json` é utilizada para manipular objetos JSON.  Considerar a utilização de bibliotecas mais modernas e eficientes, como Jackson ou Gson, pode melhorar o desempenho e a legibilidade do código.

## 5. Possíveis Melhorias

*   **Implementar um Sistema de Configuração:** Em vez de hardcoding os parâmetros de configuração (modelo, temperatura, prompts), implementar um sistema de configuração que permita aos usuários personalizar o comportamento dos agentes.
*   **Adicionar Logging:** Implementar um sistema de logging para registrar eventos importantes, erros e informações de depuração.
*   **Criar uma Interface Gráfica:** Desenvolver uma interface gráfica para facilitar a interação com os agentes e a visualização dos resultados.
*   **Implementar Testes Unitários:** Adicionar testes unitários para garantir a qualidade e a estabilidade do código.
*   **Refatorar o Código:** Refatorar o código para melhorar a legibilidade, a manutenibilidade e a extensibilidade.
*   **Abstrair a Interação com a API Gemini:** Criar uma classe abstrata ou interface para a interação com a API Gemini, permitindo a fácil substituição por outras APIs de modelos de linguagem.
*   **Implementar um Mecanismo de Cache:** Implementar um mecanismo de cache para armazenar as respostas da API Gemini e evitar requisições repetidas.
*   **Documentação Detalhada:** Criar uma documentação mais detalhada do código, incluindo diagramas de classe e exemplos de uso.
*   **Segurança da Chave da API:** Implementar um mecanismo mais seguro para o armazenamento e acesso à chave da API.
*   **Análise de Sentimentos:** Adicionar capacidades de análise de sentimentos para avaliar a tonalidade das respostas geradas pela API.

## 6. Conclusão

O projeto "my-agents" fornece um conjunto valioso de exemplos de como utilizar a API Google Gemini para construir agentes de IA. Os diferentes agentes demonstram diversas abordagens para a engenharia de prompt, a pesquisa na web e a automação de tarefas. Ao entender a arquitetura do projeto, os pontos críticos e as possíveis melhorias, os analistas de sistemas podem utilizar este projeto como um ponto de partida para explorar o potencial da IA e aumentar sua produtividade. A inclusão do conteúdo do arquivo `requestBody.json` seria fundamental para uma análise ainda mais completa e precisa. O relatório `response.md` também é um recurso valioso para entender as nuances da engenharia de prompt.
