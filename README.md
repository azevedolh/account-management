# account-management
Projeto para gerenciar contas de clientes e suas transações. Projeto desenvolvido para o teste no link: https://github.com/vieiraitalo/Back-End-Challenge

## Tecnologias utilizadas
- **Spring Boot**
- **Java 17**
- **Spring Data JPA**
- **H2 em memória**
- **Junit/Mockito**
- **Mapstruct**
- **Lombok**

## Rodar Localmente

Para rodar o projeto Localmente é necessário ter a versão 17 do Java e o Maven instalados na máquina. 

Execute a classe **AccountManagementApplication** através de uma IDE ou utilize o comando do maven abaixo na raiz do projeto:
```
mvn spring-boot:run 
```

## Documentação
Após rodar o projeto localmente é possível acessar a documentação através do swagger no link: 
```
http://localhost:8080/account-management/swagger-ui/index.html
```

Uma Collection do postman de exemplo pode ser encontrada no diretório abaixo:
```
https://github.com/azevedolh/account-management/tree/main/src/main/resources/docs
```

## Detalhamento Endpoints

### Cadastro de clientes


#### POST /customers
A primeira ação no sistema precisa ser a criação do cliente, demais funções só estão disponíveis com um código válido de cliente.

Request:
```json
{
    "name": "Teste da silva",
    "document": "123456789",
    "documentType": "PF",
    "address": "Cidade do teste",
    "password": "StrongPassword951!#@"
}
```
Response:
```json
{
    "id": 1
}
```

#### GET /customers?name=teste&document=123456789&page=1&size=10&sort=name,desc
Para seguir para as próximas funcionalidades, será necessário recuperar um cliente através da lista, podendo realizar filtro por nome ou documento. Também é possível informar parametraos para a paginação: page, size e _sort. Todos os parametros são opcionais.

Response:
```json
{
  "_pageable": {
    "_limit": 10,
    "_offset": 0,
    "_pageNumber": 1,
    "_pageElements": 1,
    "_totalPages": 1,
    "_totalElements": 1,
    "_moreElements": false
  },
  "_content": [
    {
      "id": 1,
      "document": "123456789",
      "documentType": "PF",
      "name": "teste da silva",
      "address": "rua do teste",
      "createdAt": "2024-01-14T00:28:22.571962",
      "updatedAt": "2024-01-14T00:28:22.571962"
    }
  ]
}
```
### Cadastro de Contas

#### POST /customers/{customer_id}/accounts
Para criar a conta é necessário informar um id do cliente no path. Como o numero da conta é único independente da agência, sempre que é solicitado a criação um novo numero de conta é gerado.

Request:
```json
{
  "agency": "1234",
  "balance": 1343.45
}
```
Response:
```json
{
    "id": 1
}
```

#### GET /customers/{customer_id}/accounts?page=1&size=10&sort=createdAt,desc
Aqui também é utilizada a paginação e os parametros são opcionais. As contas são listadas baseadas no id do cliente informado no path

Response:
```json
{
  "_pageable": {
    "_limit": 10,
    "_offset": 0,
    "_pageNumber": 1,
    "_pageElements": 5,
    "_totalPages": 1,
    "_totalElements": 5,
    "_moreElements": false
  },
  "_content": [
    {
      "id": 1,
      "agency": "1234",
      "balance": 1343.45,
      "createdAt": "2024-01-14T00:28:31.579899",
      "updatedAt": "2024-01-14T00:28:31.579899",
      "isActive": true
    }
  ]
}
```

### Criação de transações

#### POST /customers/{customer_id}/accounts/{account_id}/transactions
Para criar uma transação (pagamento) é necessário informar no path id do cliente e da conta de origem e no body informações da conta destino e valor. Na resposta está sendo devolvido os dados da transação criada e o status do envio da notificação, o sistema foi construido para concretizar o pagamento mesmo em caso de erro no envio da notificação. Caso o pagamento seja entre contas do mesmo cliente apenas uma notificação é enviada. 

Request:
```json
{
  "destinationAccount": 2,
  "amount": 1000
}
```
Response:
```json
{
  "id": 1,
  "type": "DEBITO",
  "originAccount": 1,
  "destinationAccount": 2,
  "amount": 1000,
  "status": "EFETIVADO",
  "notificationResult": [
    {
      "accountType": "ORIGIN",
      "notificationStatus": "SENT",
      "message": "SUCESSO"
    },
    {
      "accountType": "DESTINATION",
      "notificationStatus": "SENT",
      "message": "SUCESSO"
    }
  ]
}
```

#### GET /customers/{customer_id}/accounts/{account_id}/transactions?page=1&size=10&sort=createdAt,desc
Aqui também é utilizada a paginação e os parametros são opcionais. Necessário id do cliente e id da conta, são trazidas todas as transações que a conta é origem ou destino.

Response:
```json
{
  "_pageable": {
    "_limit": 10,
    "_offset": 0,
    "_pageNumber": 1,
    "_pageElements": 2,
    "_totalPages": 1,
    "_totalElements": 2,
    "_moreElements": false
  },
  "_content": [
    {
      "id": 2,
      "type": "DEBITO",
      "originAccount": 1,
      "destinationAccount": 2,
      "amount": 1000.00,
      "status": "EFETIVADO",
      "createdAt": "2024-01-14T00:29:13.07109",
      "updatedAt": "2024-01-14T00:29:13.07109"
    }
  ]
}
```

#### POST /customers/{customer_id}/accounts/{account_id}/transactions/{transaction_id}/cancel
Um endpoint extra foi adicionado para realizar cancelamento de transação, ele faz o processo inverso da transação cancelada que é enviada no corpo da requisição. Assim como na criação é devolvido a informação se a notificação foi enviada e os dados da nova transação criada para reversão.

Request:
```json
{
  "destinationAccount": 2,
  "amount": 1000
}
```
Response:
```json
{
  "id": 1,
  "type": "DEBITO",
  "originAccount": 1,
  "destinationAccount": 2,
  "amount": 1000,
  "status": "EFETIVADO",
  "notificationResult": [
    {
      "accountType": "ORIGIN",
      "notificationStatus": "SENT",
      "message": "SUCESSO"
    },
    {
      "accountType": "DESTINATION",
      "notificationStatus": "SENT",
      "message": "SUCESSO"
    }
  ]
}
```

## Pontos de melhoria
Foram identificados alguns pontos de melhoria que podem ir sendo implementados para melhorar o projeto:
- Separar em mais Microserviços. (À avaliar dependendo de quantidade de desenvolvedores, escalabilidade.)
- Mudar estratégia de numero da conta para considerar a Agência utilizando @EmbbededId e ir incrementando a conta considerando a agência.
- Envio de notificação ser Assíncrono (avaliar se resultado do envio é necessário) **OU** Fazer processo ser desfeito caso a notificação não seja enviada lançando uma exceção.
- Configurar spring security baseado em perfis de acesso e autenticação.
- Utilizar criptografia nos dados sensíveis.
- Utilizar message.properties nas mensagens e utilizar internacionalização para exibição nos tratamentos de erro.
- Revisar queries geradas pelo hibernate visando eliminar complexidade desnecessária (joins) e visando queries mais performáticas.
- Melhorar tratamento de erro.
- Endpoint para alterar dados de cliente e contas.
- Acrescentar mais cenários de teste