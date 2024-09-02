# Repositório pessoal - Sankhya

Um repositório criado para controlar as personalizações realizadas utilizando Java ☕.

## 🚀 Começando

Essas instruções permitirão que você obtenha uma cópia do projeto em operação na sua máquina local para fins de desenvolvimento e teste.

### 📋 Pré-requisitos

Instale/faça download dos seguintes arquivos abaixo:

* [IntelliJ IDEA Community Edition](https://www.jetbrains.com/pt-br/idea/download/?section=windows)
* Bibliotecas Sankhya da versão atual do sistema
  `
  https://grfetvhg7pdl.compat.objectstorage.sa-saopaulo-1.oraclecloud.com/downloads-sankhya-pkgs/sankhya-w_4.versao.release.pkg2
  `
```
https://grfetvhg7pdl.compat.objectstorage.sa-saopaulo-1.oraclecloud.com/downloads-sankhya-pkgs/sankhya-w_4.25b221.pkg2
```
* [JDK do Java 8](https://downloads.sankhya.com.br/downloads?app=JAVA&c=1)
* [WinRAR](https://www.win-rar.com/predownload.html?&L=9)

## ⚙️ Configurações
### 🛠️ Configuração do ambiente para desenvolvimento

Uma série de passo-a-passo que informam o que você deve executar para ter um ambiente de desenvolvimento em execução.
Essa opção do passo-a-passo deverá ser realizada após a instalação de todos os softwares citados acima.

#### Extração das bibliotecas do Sankhya
* Faça o download do arquivo com extensão .pkg2
* Abra o arquivo utilizando o WinRAR
* Navegue pelo arquivo, selecionando as pastas bin > default > deploy
* Abra o arquivo sankhya-w_versao.release.ear
* Extraia as pastas /lib e /ejb para uma pasta em seu computador

#### Configuração do Intellij IDEA
* Após a instalação, abra o Intellij IDEA e clique na opção 'Get from VCS'
* Clique na opção 'Repository URL', configurando-a da forma abaixo:
```
Version control: GIT 
URL: https://github.com/rafanere/portfolio_Sankhya
Directory: Escolha o seu diretório local para salvar o projeto 
```
* Faça o login no GitHub e autorize o acesso
* Trust Project ao abrir a mensagem "Trust and Open Project 'Sankhya'?"

#### Configuração do projeto no Intellij IDEA
* Após abrir o projeto no intellij, clique encima do projeto 'Sankhya' com o botão direito e clique na opção 'Open Module Settings', ou aperte o botão F4
* Configure o projeto igual abaixo, seguindo o padrão citado
```
Aba Project
Name: Não modificar
SDK: 1.8 Oracle OpenDJK 1.8.0_231
Language Level: 8 - Lambdas, type annotations etc.
Compiler Output: Não modificar

Aba Modules
Não Modificar

Aba Libraries 
+ > Java > Selecione as bibliotecas /lib e /ejb, uma por vez 
Choose Modules > OK

Aba Artifacts
+ > Jar > Empty
Name: XXXXX
Output directory: Não modificar
Include in project build: Não modificar
Output Layout > Arrastar o arquivo encontrado em 'Available Elements > Sankhya > 
    Compile Output XXXXX.jar' para dentro do arquivo XXXXX.jar, encontrado em Output Layout
```
* Aplique as alterações e salve

#### Configuração do debug no Intellij IDEA
* Na parte superior da tela, terá uma opção "Current File". Clique em Current File > Edit Configurations ...
* '+' OU 'Add new' > 'Remote JVM Debug'
```
Name: Debug statusVolume 
Debugger mode: Não modificar
Transport: Não modificar
Host: http://xxxxx.com.br    Port: 8787
Use module classpath: Não modificar
```
* Aplique as configurações e salve

### 🪲 Testes

Para testar as configurações, ativaremos o debug e faremos a compilação do projeto.

#### Ativando o debug
* Clique no ícone de inseto 🪲 ou pressione Shift + F9

#### Compilação do arquivo .JAR
* Clique em Main Menu (Alt + \\)
* Clique em Build > Build Artifacts
* Selecione a opção Build

## 📦 Implantação no Sankhya

Para rodar o seu código no sistema sankhya, será necessário seguir os passos abaixo.
* Acesse o sistema Sankhya
* Acesse a tela "Módulo Java"
* Crie um novo registro ou selecione o registro que você criou
* Inclua ou substitua o arquivo .jar que você compilou dentro do registro criado
* Configure o seu desenvolvimento de acordo com o que foi feito.

## 📌 Links úteis
Segue artigos ensinando a configurar as personalizações criadas:
+ [https://ajuda.sankhya.com.br/hc/pt-br/articles/360045110573-Módulo-Java](https://ajuda.sankhya.com.br/hc/pt-br/articles/360045110573-Módulo-Java)
+ [https://developer.sankhya.com.br](https://developer.sankhya.com.br/docs/rotina-java)
+ [https://comunidade.sankhya.com.br](https://comunidade.sankhya.com.br)

## ✒️ Autores
* **Rafaela Nere** - *Trabalho Inicial e Documentação* - [github](https://github.com/rafanere)

## 🎁 Expressões de gratidão

* Conte a outras pessoas sobre este projeto 📢;
* Me convide para tomar uma cerveja 🍺;