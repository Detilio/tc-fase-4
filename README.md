# Sistema de Feedback Serverless - Tech Challenge Fase 4

Este projeto é uma solução **Serverless** desenvolvida em Java para processamento de feedbacks de alunos, utilizando a arquitetura de nuvem do **Microsoft Azure**.

O sistema recebe avaliações, armazena em banco de dados NoSQL, processa notificações de urgência de forma assíncrona via filas e gera relatórios periódicos automaticamente.

## 🚀 Arquitetura da Solução

A solução utiliza o modelo **PaaS (Platform as a Service)** e **FaaS (Function as a Service)** para garantir escalabilidade e baixo custo.

* **Azure Functions (Java 17):** Núcleo da aplicação dividido em 3 funções com responsabilidade única.
* **Azure Cosmos DB (NoSQL):** Armazenamento rápido e flexível dos feedbacks.
* **Azure Queue Storage:** Desacoplamento para processamento de mensagens urgentes.
* **Application Insights:** Monitoramento e logs da aplicação.

### Diagrama de Fluxo
1.  **User** -> [POST /api/feedback] -> **Function 1** (ProcessarFeedback)
2.  **Function 1** -> Salva no **Cosmos DB**
3.  **Function 1** -> (Se Nota <= 4) -> Envia para **Queue Storage**
4.  **Queue Storage** -> Gatilho -> **Function 2** (NotificarAdministrador)
5.  **Function 2** -> Simula envio de E-mail de Alerta
6.  **Timer (Agendado)** -> Gatilho -> **Function 3** (RelatorioSemanal) -> Gera logs com métricas.

---

## 🛠️ Tecnologias Utilizadas

* **Java 17**
* **Maven** (Gerenciamento de dependências)
* **Azure Functions Core Tools**
* **Gson** (Processamento JSON)
* **Git & GitHub**

---

## ⚙️ Configuração e Execução Local

### Pré-requisitos
* Java 17 JDK instalado.
* Maven instalado.
* Azure Functions Core Tools instalado.
* VS Code com extensão "Azure Functions".

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
        "AzureWebJobsStorage": "UseDevelopmentStorage=true",
        "FUNCTIONS_WORKER_RUNTIME": "java",
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
        "data": "2025-11-26"
    }
    ```

### 2. Notificação de Urgência (Queue Trigger)
* **Gatilho:** Automático.
* **Comportamento:** Se a nota for **menor ou igual a 4**, a Função 1 envia uma mensagem para a fila `notificacoes-urgentes`. Esta função consome a fila e simula o envio de um e-mail para o administrador.

### 3. Relatório Semanal (Timer Trigger)
* **Gatilho:** Agendado (Cron: `0 0 8 * * MON` - Toda segunda-feira às 08:00).
* **Comportamento:** Calcula a média de satisfação e o total de feedbacks críticos, gerando um log consolidado.

---

## 📦 Deploy no Azure

Para subir a aplicação para a nuvem:

1.  Gere o pacote de produção:
    ```bash
    mvn clean package
    ```
2.  Faça o deploy via extensão do VS Code ou Azure CLI:
    ```bash
    func azure functionapp publish func-tech-challenge-feedback
    ```
*(Lembre-se de configurar as Variáveis de Ambiente no Portal do Azure em "Configuration").*

---

## ✒️ Autor
Desenvolvido para o Tech Challenge da Pós-Tech FIAP (Arquitetura Java).