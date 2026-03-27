# Guia de Desenvolvimento - Gerenciador de Viagens

## 📌 Estrutura do Projeto

### Fluxo de Navegação

```
Login
  ├─ Login bem-sucedido → Menu
  ├─ "Novo Usuário" → Registro → Login
  └─ "Esqueci Senha" → Recuperação → Login

Menu (entrada após login)
```

## 🔐 Validações Implementadas

### LoginViewModel
```kotlin
- Email: Obrigatório + padrão EMAIL válido
- Password: Obrigatório
- Visibilidade: Toggle de senha implementado
```

### RegisterViewModel
```kotlin
- Name: Obrigatório
- Email: Obrigatório + padrão EMAIL válido
- Phone: Obrigatório + mín 10 dígitos
- Password: Obrigatório + mín 6 caracteres
- ConfirmPassword: Obrigatório + deve coincidir com Password
- Feedback: Mensagens por campo
```

### ForgotPasswordViewModel
```kotlin
- Email: Obrigatório + padrão EMAIL válido
- Success Message: Exibida após validação bem-sucedida
```

## 🎨 Componentes Reutilizáveis

### OutlinedTextField
- Usado em todos os formulários
- Suporta erro visual (isError)
- Suporta ícones de trailing

### Button
- Botões primários para ações principais
- TextButton para ações secundárias

### IconButton + PasswordVisualTransformation
- Toggle de visibilidade de senha
- Padrão para campos sensíveis

## 📱 StateFlow e Reatividade

Todos os ViewModels usam `StateFlow` para reatividade:
```kotlin
private val _uiState = MutableStateFlow(InitialState())
val uiState: StateFlow<UiState> = _uiState.asStateFlow()

// Atualização
_uiState.update { it.copy(field = newValue) }

// Leitura
val state = viewModel.uiState.collectAsState().value
```

## 🧭 Navegação

### Rotas Definidas
```kotlin
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Menu : Screen("menu")
}
```

### Navegação com NavController
```kotlin
// Navegar simples
navController.navigate(Screen.Register.route)

// Navegar com cleanup
navController.navigate(Screen.Menu.route) {
    popUpTo(Screen.Login.route) { inclusive = true }
}

// Voltar
navController.popBackStack()
```

## 🔄 Como Adicionar Nova Tela

1. **Criar ViewModel**
```kotlin
data class NewScreenUiState(/* fields */)

class NewScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewScreenUiState())
    val uiState: StateFlow<NewScreenUiState> = _uiState.asStateFlow()
    
    // Métodos para atualizar estado
}
```

2. **Criar Screen Composable**
```kotlin
@Composable
fun NewScreen(
    viewModel: NewScreenViewModel = viewModel(),
    onNavigate: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    // UI aqui
}
```

3. **Adicionar Rota ao Navigation**
```kotlin
object NewScreen : Screen("new_screen")

// No NavHost
composable(Screen.NewScreen.route) {
    NewScreen(onNavigate = { /* ... */ })
}
```

## 📚 Padrões de Código

### Atualizar Campo no ViewModel
```kotlin
fun updateField(value: String) {
    _uiState.update { it.copy(field = value, errorMessage = "") }
}
```

### Validação
```kotlin
fun validate(): Boolean {
    val errors = mutableMapOf<String, String>()
    if (field.isBlank()) {
        errors["field"] = "Campo é obrigatório"
    }
    _uiState.update { it.copy(fieldErrors = errors) }
    return errors.isEmpty()
}
```

### Exibir Erro no Composable
```kotlin
OutlinedTextField(
    isError = uiState.fieldErrors.containsKey("field")
)
if (uiState.fieldErrors.containsKey("field")) {
    Text(
        text = uiState.fieldErrors["field"] ?: "",
        color = MaterialTheme.colorScheme.error,
        fontSize = 12.sp
    )
}
```

## 🧪 Testando as Telas

### Teste de Login
1. Campo vazio → Erro "E-mail é obrigatório"
2. E-mail inválido → Erro "E-mail inválido"
3. Sem senha → Erro "Senha é obrigatória"
4. Clique em "Novo Usuário" → Vai para Registro

### Teste de Registro
1. Deixar campos vazios → Erros específicos por campo
2. Senhas diferentes → Erro "As senhas não coincidem"
3. Telefone inválido → Erro "Telefone inválido"
4. Senha < 6 caracteres → Erro

### Teste de Recuperação
1. E-mail inválido → Erro
2. E-mail válido → Mensagem de sucesso

## 🚀 Performance e Boas Práticas

✅ **StateFlow** em vez de LiveData (melhor com Compose)
✅ **update** para mudanças de estado (thread-safe)
✅ **collectAsState** apenas em Composables
✅ **Lazy composition** com NavHost
✅ Validação no ViewModel (não na UI)
✅ Separação de responsabilidades

## 🔗 Dependências Importantes

```gradle
implementation(libs.androidx.lifecycle.viewmodel.compose) // ViewModel
implementation(libs.androidx.navigation.compose)          // Navigation
implementation(libs.androidx.compose.material3)           // UI Components
implementation(libs.androidx.activity.compose)            // Activity Compose
```

## 📦 Próximas Features

- [ ] Persistência com SharedPreferences/DataStore
- [ ] Backend integration (Retrofit)
- [ ] Autenticação com JWT
- [ ] Tela de Gerenciamento de Viagens
- [ ] Tela de Detalhes da Viagem
- [ ] Edição/Exclusão de Viagens
- [ ] Sincronização offline

## 🐛 Troubleshooting

### "NavHost not found"
→ Certifique-se de importar `androidx.navigation.compose.NavHost`

### "ViewModel factory error"
→ Verifique se o ViewModel estende `ViewModel` corretamente

### "State not updating"
→ Use `.update()` em vez de `= MutableStateFlow(value)`

### "Navigation doesn't work"
→ Confirme que a rota existe em `sealed class Screen`

## 📞 Contato e Suporte

Para dúvidas sobre o projeto, consulte a documentação oficial:
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Navigation](https://developer.android.com/guide/navigation/navigation-getting-started)
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)

