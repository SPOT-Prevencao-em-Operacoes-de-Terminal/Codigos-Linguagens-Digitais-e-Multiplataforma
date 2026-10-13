# Linguagens Digitais Multiplataformas

Repositório acadêmico desenvolvido para a disciplina de **Linguagens Digitais Multiplataformas (LDM)**, como parte do desenvolvimento do projeto **SPOT — Sistema de Prevenção Operacional de Terminais**.

O projeto tem como objetivo desenvolver uma solução de **monitoramento da infraestrutura computacional**, permitindo acompanhar informações relacionadas aos componentes de hardware dos computadores utilizados para sustentar as operações do sistema.

## Objetivo

Este repositório tem como objetivo desenvolver a aplicação **Java em modo console (Client)** responsável por coletar informações dos componentes do computador.

Para realizar essa coleta, a aplicação utiliza a biblioteca **OSHI**, responsável por obter informações relacionadas ao hardware e aos recursos do sistema. Os dados obtidos pela aplicação também são **conectados ao banco de dados**, permitindo seu armazenamento e utilização no projeto.

Dessa forma, este repositório representa a parte desenvolvida em **Java** dentro do projeto acadêmico SPOT, aplicando os conceitos estudados na disciplina de **Linguagens Digitais Multiplataformas**.

## Java

A aplicação é desenvolvida utilizando **Java** e possui uma aplicação em **modo console (Client)**.

Entre suas principais funcionalidades estão:

- Listagem e coleta de informações dos componentes do computador;
- Utilização da biblioteca **OSHI** para obtenção das informações de hardware e monitoramento de rede;
- Conexão com o banco de dados;
- Armazenamento dos dados coletados para utilização no sistema de monitoramento.

## Biblioteca OSHI

A biblioteca **OSHI** é utilizada para obter informações sobre os componentes e recursos do computador diretamente pela aplicação Java.

Por meio dela, é possível acessar informações que serão utilizadas no monitoramento da infraestrutura computacional do projeto SPOT, incluindo informações relacionadas à rede.

## Banco de Dados

A aplicação Java possui conexão com o **banco de dados**, permitindo que as informações coletadas pelo sistema sejam armazenadas e posteriormente utilizadas pelas outras partes do projeto.

## Como acessar o arquivo JAR

O projeto disponibiliza o arquivo compilado da aplicação Java para facilitar o acesso dos integrantes do grupo.

Para acessá-lo:

1. Baixe o arquivo ZIP do repositório no GitHub.
2. Extraia os arquivos para uma pasta no computador.
3. Acesse a pasta `dist/`.
4. Localize o arquivo `java-spot-rede.jar`.

Para executar o arquivo, abra o terminal na pasta `dist/` e utilize o comando:

```bash
java -jar java-spot-rede.jar
```

**Observação:** a execução depende das bibliotecas necessárias e da configuração de conexão com o banco de dados MySQL.

## Projeto acadêmico

Este repositório faz parte do desenvolvimento acadêmico do **projeto SPOT**, sendo utilizado para aplicar os conhecimentos estudados na disciplina de **Linguagens Digitais Multiplataformas**.

O objetivo é colocar em prática conceitos de desenvolvimento em **Java**, utilização de bibliotecas para acesso às informações do computador e integração com **banco de dados**, contribuindo para o desenvolvimento da solução de monitoramento proposta pelo projeto.