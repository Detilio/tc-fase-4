# Sistema de Feedback Serverless - Tech Challenge Fase 4

Este projeto é uma solução **Serverless** desenvolvida em Java para processamento de feedbacks de alunos, utilizando a arquitetura de nuvem do **Microsoft Azure**.

O sistema foi arquitetado seguindo princípios **SOLID** e padrão **MVC**, garantindo desacoplamento entre as camadas de gatilho (Functions), regras de negócio (Service) e acesso a dados (Repository).

## 🚀 Arquitetura da Solução

A solução utiliza o modelo **PaaS (Platform as a Service)** focada em **Serverless (FaaS)**.

* **Azure Functions (Java 17):** Núcleo da aplicação.
* **Azure Cosmos DB (NoSQL):** Armazenamento dos feedbacks.
* **Azure Queue Storage:** Desacoplamento para processamento assíncrono de urgências.
* **Application Insights:** Monitoramento e logs.

### Diagrama de Fluxo
1.  **User** -> [POST /api/ProcessarFeedback] -> **Controller**
2.  **Controller** -> **Service** (Valida Nota) -> **Repository** (Salva no Cosmos DB)
3.  **Service** -> (Se Nota <= 4) -> **Repository** (Envia p/ Queue)
4.  **Queue Storage** -> Gatilho -> **Function Notificar** -> Simula envio de E-mail
5.  **Timer (Agendado)** -> Gatilho -> **Function Relatorio** -> Service calcula métricas -> Gera Log

---

## 🏛️ Justificativa das Escolhas Tecnológicas

A escolha da arquitetura e dos componentes foi baseada nos seguintes critérios:

### 1. Modelo de Nuvem: PaaS e Serverless
Optamos pelo modelo **PaaS (Platform as a Service)** em detrimento de IaaS ou SaaS, conforme análise:
* **Por que não IaaS?** O IaaS exigiria o gerenciamento de Máquinas Virtuais (VMs), Sistema Operacional e atualizações de segurança. O foco do projeto é a **lógica de negócio**, não a administração de infraestrutura.
* **Por que não SaaS?** Uma solução SaaS pronta não permitiria a personalização necessária para as regras de negócio específicas de cálculo de feedback e integração customizada exigidas no desafio.
* **Vantagem do Serverless (Azure Functions):**
    * **Escalabilidade Automática:** O Azure gerencia a alocação de recursos conforme a demanda de requisições.
    * **Modelo de Custo (Pay-as-you-go):** No plano de consumo, pagamos apenas pelo tempo de execução e memória utilizada, ideal para cargas de trabalho variáveis.
    * **Abstração:** Permite focar puramente no código Java.

### 2. Banco de Dados: Azure Cosmos DB (NoSQL)
* Escolhida a abordagem **NoSQL** devido à natureza flexível do objeto `Feedback`.
* O Cosmos DB oferece integração nativa com Azure Functions (Bindings), baixíssima latência e capacidade de escalar globalmente se necessário.

### 3. Mensageria: Azure Queue Storage
* Utilizado para desacoplar o recebimento do feedback do envio de notificações.
* Garante **resiliência**: Se o serviço de e-mail falhar, a mensagem persiste na fila para nova tentativa, garantindo que nenhum alerta crítico seja perdido.

---

## 🛠️ Tecnologias Utilizadas

* **Java 17**
* **Maven** (Gerenciamento de dependências e Build)
* **IntelliJ IDEA**
* **Azure Functions Core Tools**
* **Gson** (Processamento JSON)
* **Git & GitHub**

---

## ⚙️ Configuração e Execução Local

### Pré-requisitos
* Java 17 JDK instalado.
* Maven instalado.
* Azure Functions Core Tools instalado.
* Azure CLI instalado (para login).

### Passo a Passo
1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/SEU-USUARIO/SEU-REPO.git](https://github.com/SEU-USUARIO/SEU-REPO.git)
    cd feedback-app
    ```

2.  **Configure as Variáveis de Ambiente:**
    Crie um arquivo `local.settings.json` na raiz do projeto com as suas credenciais do Azure:
    ```json
    {
      "IsEncrypted": false,
      "Values": {
        "FUNCTIONS_WORKER_RUNTIME": "java",
        "AzureWebJobsStorage": "SUA_CONNECTION_STRING_DA_STORAGE_ACCOUNT",
        "COSMOS_DB_ENDPOINT": "SUA_URI_DO_COSMOS_DB",
        "COSMOS_DB_KEY": "SUA_CHAVE_PRIMARIA_DO_COSMOS",
        "QUEUE_CONNECTION_STRING": "SUA_CONNECTION_STRING_DA_STORAGE_ACCOUNT"
      }
    }
    ```

3.  **Execute o projeto:**
    ```bash
    mvn clean package
    func host start
    ```

---

## ☁️ Endpoints e Funcionalidades

### 1. Enviar Feedback (HTTP Trigger)
Recebe o feedback do aluno e salva no banco.
* **Método:** `POST`
* **Rota Local:** `http://localhost:7071/api/ProcessarFeedback`
* **Body (JSON):**
    ```json
    {
        "descricao": "Aula excelente, mas o áudio estava baixo.",
        "nota": 8,
        "data": "2025-11-27"
    }
    ```

### 2. Notificação de Urgência (Queue Trigger)
* **Gatilho:** Automático (Assíncrono).
* **Comportamento:** Se a nota for **menor ou igual a 4**, o sistema enfileira uma mensagem. A função de notificação consome essa fila e gera um log simulando o envio de e-mail ao administrador.

### 3. Relatório Semanal (Timer Trigger)
* **Gatilho:** Agendado (Cron: `0 0 8 * * MON` - Toda segunda-feira às 08:00).
* **Comportamento:** Varre o banco de dados, calcula a média geral e totaliza os feedbacks críticos.

---

## 📦 Deploy no Azure

O deploy é realizado via **Maven Plugin**, garantindo que todas as configurações do `pom.xml` sejam respeitadas.

1.  Faça login no Azure via terminal:
    ```bash
    az login
    ```
2.  Execute o comando de deploy:
    ```bash
    mvn clean package azure-functions:deploy
    ```

---

## ✒️ Autor
Rafael Detilio