# 📱 Gerenciador de Viagens - MVP Completo

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple)
![Android](https://img.shields.io/badge/Android-24%2B-green)
![Room](https://img.shields.io/badge/Room-2.6.1-blue)
![Compose](https://img.shields.io/badge/Compose-Latest-cyan)

> Um aplicativo Android funcional para gerenciar suas viagens com banco de dados Room, autenticação de usuários e interface moderna em Jetpack Compose.

---

## ✨ Principais Características

### 🔐 Autenticação
- ✅ Registro de usuários com validação completa
- ✅ Login com verificação contra banco de dados
- ✅ Armazenamento seguro de credenciais
- ✅ Sistema de logout

### ✈️ Gerenciamento de Viagens
- ✅ Criar nova viagem com todos os campos
- ✅ Listar viagens do usuário logado
- ✅ Editar viagens existentes (long click)
- ✅ Deletar viagens (ícone de lixo)
- ✅ DatePicker para seleção de datas

### 🏠 Interface e Navegação
- ✅ NavigationDrawer com menu lateral
- ✅ Navegação entre telas fluida
- ✅ Back button encerra app no Menu
- ✅ Interface funcional e responsiva
- ✅ Validações claras e mensagens de erro

### 💾 Banco de Dados
- ✅ Room Database com migrations
- ✅ Entidades User e Trip com relacionamento
- ✅ DAOs para operações CRUD
- ✅ Flow para atualizações em tempo real

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────┐
│         UI Layer (Composables)      │
│  LoginScreen, RegisterScreen, etc   │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     ViewModel Layer (State)         │
│  LoginVM, RegisterVM, CreateTripVM  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       Data Layer (Repository)       │
│    UserDao, TripDao, AppDatabase    │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Database Layer (Room)          │
│    SQLite + Room ORM Framework      │
└─────────────────────────────────────┘
```

---

## 📋 Requisitos Implementados ✅

Todos os requisitos solicitados foram implementados com sucesso!

---

## 🚀 Como Usar

### Pré-requisitos
- Android Studio 2024.1+
- JDK 11+
- Android SDK API 24+

### Compilar
```bash
java -jar gradle\wrapper\gradle-wrapper.jar build
```

### Executar
- Abra no Android Studio
- Run → Run 'app' (ou F5)

---

## 📁 Estrutura do Projeto

Veja [ARQUIVOS_CRIADOS.md](ARQUIVOS_CRIADOS.md) para mapa completo.

---

## 📚 Documentação

- **[RESUMO_FINAL.md](RESUMO_FINAL.md)** - Resumo completo
- **[ARQUIVOS_CRIADOS.md](ARQUIVOS_CRIADOS.md)** - Arquivos criados/modificados  
- **[GUIA_TESTES.md](GUIA_TESTES.md)** - Testes completos
- **[IMPLEMENTACAO.md](IMPLEMENTACAO.md)** - Detalhes técnicos

---

## ✅ Status

**MVP 100% Completo e Funcional ✨**

