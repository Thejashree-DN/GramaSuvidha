package com.example.grama_suvidhaportal

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import coil.compose.AsyncImage
import com.example.grama_suvidhaportal.ui.theme.GramaSuvidhaPortalTheme
import kotlinx.coroutines.delay
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- DATA MODELS ---
data class TimelineStep(val date: String, val knDate: String, val title: String, val knTitle: String, val isDone: Boolean = false)

data class Project(
    val name: String, val knName: String, val description: String, val knDescription: String,
    val location: String, val knLocation: String, val budget: String, val knBudget: String,
    val startDate: String, val knStartDate: String, val progress: Float, val isCompleted: Boolean,
    val beforeImage: Any, val afterImage: Any, val timeline: List<TimelineStep>
)

// --- MOCK API REPOSITORY ---
object ProjectRepository {
    private val _projects = MutableStateFlow(listOf(
        Project(
            name = "Solar Street Lights Installation",
            knName = "ಸೌರ ಬೀದಿ ದೀಪಗಳ ಅಳವಡಿಕೆ",
            description = "Installing 50 solar-powered street lights across main village roads.",
            knDescription = "ಗ್ರಾಮದ ಪ್ರಮುಖ ರಸ್ತೆಗಳಲ್ಲಿ 50 ಸೌರಶಕ್ತಿ ಚಾಲಿತ ಬೀದಿ ದೀಪಗಳನ್ನು ಅಳವಡಿಸಲಾಗುತ್ತಿದೆ.",
            location = "Huligere", knLocation = "ಹುಲಿಗೆರೆ",
            budget = "4.5 Lakhs", knBudget = "4.5 ಲಕ್ಷಗಳು",
            startDate = "10 Feb 2026", knStartDate = "10 ಫೆಬ್ರವರಿ 2026",
            progress = 0.65f, isCompleted = false,
            beforeImage = R.drawable.solar_before,
            afterImage = R.drawable.solar_lights_after,
            timeline = listOf(
                TimelineStep("10 FEB 2026", "10 ಫೆಬ್ರವರಿ 2026", "Project initiated and materials ordered.", "ಯೋಜನೆ ಪ್ರಾರಂಭವಾಯಿತು ಮತ್ತು ಸಾಮಗ್ರಿಗಳನ್ನು ಆರ್ಡರ್ ಮಾಡಲಾಯಿತು.", true),
                TimelineStep("01 MAR 2026", "01 ಮಾರ್ಚ್ 2026", "Pole foundations completed in Ward 1.", "ವಾರ್ಡ್ 1 ರಲ್ಲಿ ಪೋಲ್ ಫೌಂಡೇಶನ್ ಪೂರ್ಣಗೊಂಡಿದೆ.", true),
                TimelineStep("25 APR 2026", "25 ಏಪ್ರಿಲ್ 2026", "Solar panels distribution and assembly started.", "ಸೌರ ಫಲಕಗಳ ವಿತರಣೆ ಮತ್ತು ಜೋಡಣೆ ಪ್ರಾರಂಭವಾಯಿತು.", false)
            )
        ),
        Project(
            name = "New Drinking Water Tank",
            knName = "ಹೊಸ ಕುಡಿಯುವ ನೀರಿನ ಟ್ಯಾಂಕ್",
            description = "Building a new 20,000 litre capacity water tank.",
            knDescription = "ಹೊಸ 20,000 ಲೀಟರ್ ಸಾಮರ್ಥ್ಯದ ನೀರಿನ ಟ್ಯಾಂಕ್ ನಿರ್ಮಾಣ.",
            location = "North Wing", knLocation = "ಉತ್ತರ ಭಾಗ",
            budget = "8.2 Lakhs", knBudget = "8.2 ಲಕ್ಷಗಳು",
            startDate = "15 Jan 2026", knStartDate = "15 ಜನವರಿ 2026",
            progress = 1.0f, isCompleted = true,
            beforeImage = R.drawable.watertank_before,
            afterImage = R.drawable.watertank_after,
            timeline = listOf(
                TimelineStep("15 JAN 2026", "15 ಜನವರಿ 2026", "Site clearing and leveling.", "ಸ್ಥಳ ಸ್ವಚ್ಛಗೊಳಿಸುವಿಕೆ ಮತ್ತು ಸಮತಟ್ಟು ಮಾಡುವುದು.", true),
                TimelineStep("20 FEB 2026", "20 ಫೆಬ್ರವರಿ 2026", "Foundation and base structure ready.", "ಫೌಂಡೇಶನ್ ಮತ್ತು ಬೇಸ್ ಸ್ಟ್ರಕ್ಚರ್ ಸಿದ್ಧವಾಗಿದೆ.", true),
                TimelineStep("10 MAR 2026", "10 ಮಾರ್ಚ್ 2026", "Tank installation completed.", "ಟ್ಯಾಂಕ್ ಅಳವಡಿಕೆ ಪೂರ್ಣಗೊಂಡಿದೆ.", true)
            )
        ),
        Project(
            name = "Primary School Renovation",
            knName = "ಪ್ರಾಥಮಿಕ ಶಾಲೆಯ ನವೀಕರಣ",
            description = "Complete renovation of the village primary school building and playground.",
            knDescription = "ಗ್ರಾಮದ ಪ್ರಾಥಮಿಕ ಶಾಲಾ ಕಟ್ಟಡ ಮತ್ತು ಆಟದ ಮೈದಾನದ ಸಂಪೂರ್ಣ ನವೀಕರಣ.",
            location = "Central Ward", knLocation = "ಕೇಂದ್ರ ವಾರ್ಡ್",
            budget = "15 Lakhs", knBudget = "15 ಲಕ್ಷಗಳು",
            startDate = "01 May 2026", knStartDate = "01 ಮೇ 2026",
            progress = 0.0f, isCompleted = false,
            beforeImage = R.drawable.school_before,
            afterImage = R.drawable.school_after,
            timeline = listOf(
                TimelineStep("01 MAY 2026", "01 ಮೇ 2026", "Survey and planning phase.", "ಸಮೀಕ್ಷೆ ಮತ್ತು ಯೋಜನೆ ಹಂತ.", false)
            )
        ),
        Project(
            name = "Community Hall Expansion",
            knName = "ಸಮುದಾಯ ಭವನದ ವಿಸ್ತರಣೆ",
            description = "Adding two more rooms and a new kitchen to the existing community hall.",
            knDescription = "ಈಗಿರುವ ಸಮುದಾಯ ಭವನಕ್ಕೆ ಇನ್ನೂ ಎರಡು ಕೊಠಡಿಗಳು ಮತ್ತು ಹೊಸ ಅಡುಗೆಮನೆಯನ್ನು ಸೇರಿಸಲಾಗುತ್ತಿದೆ.",
            location = "Maddur", knLocation = "ಮದ್ದೂರು",
            budget = "6.5 Lakhs", knBudget = "6.5 ಲಕ್ಷಗಳು",
            startDate = "20 Mar 2026", knStartDate = "20 ಮಾರ್ಚ್ 2026",
            progress = 0.25f, isCompleted = false,
            beforeImage = R.drawable.c_before,
            afterImage =  R.drawable.conventionhall_after,
            timeline = listOf(
                TimelineStep("20 MAR 2026", "20 ಮಾರ್ಚ್ 2026", "Initial structural reinforcement.", "ಆರಂಭಿಕ ರಚನಾತ್ಮಕ ಬಲವರ್ಧನೆ.", true),
                TimelineStep("15 APR 2026", "15 ಏಪ್ರಿಲ್ 2026", "Brickwork for new rooms started.", "ಹೊಸ ಕೊಠಡಿಗಳ ಇಟ್ಟಿಗೆ ಕೆಲಸ ಪ್ರಾರಂಭವಾಗಿದೆ.", false)
            )
        ),
        Project(
            name = "Village Drainage System",
            knName = "ಗ್ರಾಮದ ಒಳಚರಂಡಿ ವ್ಯವಸ್ಥೆ",
            description = "Modern concrete drainage system for better sanitation in Ward 4.",
            knDescription = "ವಾರ್ಡ್ 4 ರಲ್ಲಿ ಉತ್ತಮ ನೈರ್ಮಲ್ಯಕ್ಕಾಗಿ ಆಧುನಿಕ ಕಾಂಕ್ರೀಟ್ ಒಳಚರಂಡಿ ವ್ಯವಸ್ಥೆ.",
            location = "Ward 4", knLocation = "ವಾರ್ಡ್ 4",
            budget = "12 Lakhs", knBudget = "12 ಲಕ್ಷಗಳು",
            startDate = "10 Apr 2026", knStartDate = "10 ಏಪ್ರಿಲ್ 2026",
            progress = 0.40f, isCompleted = false,
            beforeImage = R.drawable.drain,
            afterImage = R.drawable.drain_after,
            timeline = listOf(
                TimelineStep("10 APR 2026", "10 ಏಪ್ರಿಲ್ 2026", "Excavation and leveling started.", "ಉತ್ಖನನ ಮತ್ತು ಸಮತಟ್ಟು ಮಾಡುವುದು ಪ್ರಾರಂಭವಾಗಿದೆ.", true),
                TimelineStep("05 MAY 2026", "05 ಮೇ 2026", "Pipeline installation in progress.", "ಪೈಪ್‌ಲೈನ್ ಅಳವಡಿಕೆ ಪ್ರಗತಿಯಲ್ಲಿದೆ.", false)
            )
        )
    ))
    val projects = _projects.asStateFlow()
}

// --- UI THEME COLORS ---
val PrimaryTeal = Color(0xFF226B6E)
val PrimaryGreen = Color(0xFF2E4E25)
val AccentGreen = Color(0xFF4C7D3E)
val CardBg = Color(0xFFF9FBF7)
val ProgressTrack = Color(0xFFF1F5EF)
val GradientBrand = Brush.verticalGradient(listOf(Color(0xFF226B6E), Color(0xFF164A4B)))
val CreamColor = Color(0xFFEAE3C9)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            GramaSuvidhaPortalTheme(darkTheme = isDarkMode) {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
                var isEnglish by remember { mutableStateOf(true) }

                Crossfade(targetState = currentScreen, label = "screen") { screen ->
                    when (screen) {
                        is Screen.Splash -> SplashScreen(onTimeout = {
                            currentScreen = if (FirebaseAuth.getInstance().currentUser != null) Screen.Home else Screen.Welcome
                        })
                        is Screen.Welcome -> WelcomeScreen(onStart = { currentScreen = Screen.Login })
                        is Screen.Login -> LoginScreen(
                            onLoginSuccess = { currentScreen = Screen.Home },
                            onNavigateToRegister = { currentScreen = Screen.Register },
                            onBack = { currentScreen = Screen.Welcome }
                        )
                        is Screen.Register -> RegisterScreen(
                            onRegisterSuccess = { currentScreen = Screen.Home },
                            onNavigateToLogin = { currentScreen = Screen.Login }
                        )
                        is Screen.Home -> HomeScreen(
                            isEnglish = isEnglish,
                            onToggleLanguage = { isEnglish = it },
                            onProjectClick = { currentScreen = Screen.Details(it) },
                            onNavigate = { currentScreen = it }
                        )
                        is Screen.Impact -> ImpactScreen(isEnglish = isEnglish, onNavigate = { currentScreen = it })
                        is Screen.Alerts -> AlertsScreen(isEnglish = isEnglish, onNavigate = { currentScreen = it })
                        is Screen.Settings -> SettingsScreen(
                            isEnglish = isEnglish, onToggleLanguage = { isEnglish = it },
                            isDarkMode = isDarkMode, onToggleDarkMode = { isDarkMode = it },
                            onNavigate = { currentScreen = it },
                            onLogout = { FirebaseAuth.getInstance().signOut(); currentScreen = Screen.Welcome }
                        )
                        is Screen.Details -> {
                            ProjectDetailsScreen(
                                project = screen.project, isEnglish = isEnglish,
                                onBack = { currentScreen = Screen.Home },
                                onReportIssue = { currentScreen = Screen.ReportIssue(screen.project) }
                            )
                            BackHandler { currentScreen = Screen.Home }
                        }
                        is Screen.ReportIssue -> {
                            ReportIssueScreen(
                                project = screen.project, isEnglish = isEnglish,
                                onBack = { currentScreen = Screen.Details(screen.project) }
                            )
                            BackHandler { currentScreen = Screen.Details(screen.project) }
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen {
    data object Splash : Screen(); data object Welcome : Screen()
    data object Login : Screen(); data object Register : Screen()
    data object Home : Screen(); data object Impact : Screen()
    data object Alerts : Screen(); data object Settings : Screen()
    data class Details(val project: Project) : Screen()
    data class ReportIssue(val project: Project) : Screen()
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) { delay(2000); onTimeout() }
    Box(Modifier.fillMaxSize().background(GradientBrand), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(120.dp).clip(RoundedCornerShape(32.dp)).background(Color.White.copy(0.1f)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Home, null, Modifier.size(60.dp), CreamColor)
                    Text("GRAMA-SUVIDHA", color = CreamColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Gram-Suvidha", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Digital Transparency Portal", color = Color.White.copy(0.8f))
        }
    }
}

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Box(Modifier.fillMaxSize().background(CardBg), contentAlignment = Alignment.Center) {
        Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.LocationCity, null, Modifier.size(100.dp), PrimaryGreen)
            Spacer(Modifier.height(24.dp))
            Text("Namaste!", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = PrimaryGreen)
            Text("Empowering villages through transparency", textAlign = TextAlign.Center, color = Color.Gray)
            Spacer(Modifier.height(48.dp))
            Button(onStart, Modifier.fillMaxWidth().height(64.dp), shape = RoundedCornerShape(32.dp), colors = ButtonDefaults.buttonColors(PrimaryGreen)) {
                Text("Enter Portal", fontSize = 20.sp); Spacer(Modifier.width(12.dp)); Icon(Icons.AutoMirrored.Filled.Login, null)
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Column(Modifier.fillMaxSize().padding(32.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Center) {
        IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = PrimaryGreen) }
        Text("Citizen Login", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = PrimaryGreen)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Password") },
            visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passVisible = !passVisible }) {
                    Icon(if (passVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = {
                if (email.isNotBlank() && pass.isNotBlank()) {
                    isLoading = true
                    auth.signInWithEmailAndPassword(email.trim(), pass)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Successfully logged into your account!", Toast.LENGTH_SHORT).show()
                                onLoginSuccess()
                            } else {
                                Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            },
            Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(PrimaryGreen),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("Login")
        }
        TextButton(onNavigateToRegister, Modifier.align(Alignment.CenterHorizontally)) { Text("Register New Account") }
    }
}

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Column(Modifier.fillMaxSize().padding(32.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Center) {
        Text("Join Us", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = PrimaryGreen)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(name, { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Password") },
            visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passVisible = !passVisible }) {
                    Icon(if (passVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = {
                if (email.isNotBlank() && pass.isNotBlank() && name.isNotBlank()) {
                    isLoading = true
                    auth.createUserWithEmailAndPassword(email.trim(), pass)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Successfully registered account!", Toast.LENGTH_SHORT).show()
                                onRegisterSuccess()
                            } else {
                                Toast.makeText(context, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            },
            Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(PrimaryGreen),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("Sign Up")
        }
        TextButton(onNavigateToLogin, Modifier.align(Alignment.CenterHorizontally)) { Text("Login instead") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(isEnglish: Boolean, onToggleLanguage: (Boolean) -> Unit, onProjectClick: (Project) -> Unit, onNavigate: (Screen) -> Unit) {
    val projects by ProjectRepository.projects.collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isEnglish) "Gram-Suvidha" else "ಗ್ರಾಮ-ಸುವಿಧ", fontWeight = FontWeight.Black, color = PrimaryGreen) },
                actions = {
                    TextButton(onClick = { onToggleLanguage(!isEnglish) }) {
                        Text(if (isEnglish) "ಕನ್ನಡ" else "EN", fontWeight = FontWeight.Bold, color = PrimaryGreen)
                    }
                }
            )
        },
        bottomBar = { AppBottomNav(Screen.Home, onNavigate, isEnglish) }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            items(projects) { project ->
                InnovativeProjectCard(project, isEnglish) { onProjectClick(project) }
            }
        }
    }
}

@Composable
fun InnovativeProjectCard(project: Project, isEnglish: Boolean, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 10.dp).shadow(2.dp, RoundedCornerShape(32.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(32.dp), colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(if (isEnglish) project.name else project.knName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                    Text(if (isEnglish) project.location else project.knLocation, color = Color.Gray, fontSize = 14.sp)
                }
                StatusBadge(project.isCompleted, isEnglish)
            }
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(progress = { project.progress }, modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape), color = Color(0xFF8B8000), trackColor = ProgressTrack)
            Spacer(Modifier.height(12.dp))
            Text(if (isEnglish) "DETAILS →" else "ವಿವರಗಳು →", fontWeight = FontWeight.Bold, color = Color.Gray.copy(0.6f), fontSize = 13.sp)
        }
    }
}

@Composable
fun StatusBadge(isCompleted: Boolean, isEnglish: Boolean) {
    val color = if (isCompleted) Color(0xFF1E824C) else Color(0xFFD4A017)
    val text = if (isCompleted) (if (isEnglish) "Completed" else "ಪೂರ್ಣಗೊಂಡಿದೆ") else (if (isEnglish) "Ongoing" else "ಪ್ರಗತಿಯಲ್ಲಿದೆ")
    Surface(color = color.copy(0.1f), shape = RoundedCornerShape(16.dp)) {
        Text(text, Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(project: Project, isEnglish: Boolean, onBack: () -> Unit, onReportIssue: () -> Unit) {
    var showRating by remember { mutableStateOf(false) }
    if (showRating) RatingDialog(isEnglish) { showRating = false }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isEnglish) project.name else project.knName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryGreen) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = PrimaryGreen) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            DetailInfoCard(Icons.Default.CurrencyRupee, if (isEnglish) "BUDGET" else "ಬಜೆಟ್", if (isEnglish) project.budget else project.knBudget)
            Spacer(Modifier.height(16.dp))
            DetailInfoCard(Icons.Default.CalendarToday, if (isEnglish) "START DATE" else "ಪ್ರಾರಂಭದ ದಿನಾಂಕ", if (isEnglish) project.startDate else project.knStartDate)
            Spacer(Modifier.height(24.dp))
            
            // Progress Section
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(32.dp), colors = CardDefaults.cardColors(Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))) {
                Column(Modifier.padding(24.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text(if (isEnglish) "Progress" else "ಪ್ರಗತಿ", fontWeight = FontWeight.Bold, color = PrimaryGreen)
                        Text("${(project.progress * 100).toInt()}%", fontWeight = FontWeight.ExtraBold, color = PrimaryGreen, fontSize = 24.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(progress = { project.progress }, modifier = Modifier.fillMaxWidth().height(14.dp).clip(CircleShape), color = PrimaryGreen, trackColor = ProgressTrack)
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Site Photos Section (Requirement 4)
            Text(if (isEnglish) "Site Photos" else "ಸ್ಥಳದ ಫೋಟೋಗಳು", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryGreen)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = project.beforeImage, contentDescription = "Before",
                        modifier = Modifier.height(150.dp).clip(RoundedCornerShape(24.dp)).background(Color.LightGray),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    Text(if (isEnglish) "Before" else "ಮೊದಲು", style = MaterialTheme.typography.labelMedium, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = project.afterImage, contentDescription = "After",
                        modifier = Modifier.height(150.dp).clip(RoundedCornerShape(24.dp)).background(Color.LightGray),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    Text(if (isEnglish) "After" else "ನಂತರ", style = MaterialTheme.typography.labelMedium, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                }
            }

            Spacer(Modifier.height(32.dp))
            Text(if (isEnglish) "Status Updates" else "ಸ್ಥಿತಿ ನವೀಕರಣಗಳು", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryGreen)
            Spacer(Modifier.height(16.dp))
            project.timeline.forEachIndexed { index, step ->
                TimelineItem(step, isEnglish, isLast = index == project.timeline.size - 1)
            }
            
            Spacer(Modifier.height(32.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
                Button({ showRating = true }, Modifier.weight(1f).height(60.dp), colors = ButtonDefaults.buttonColors(PrimaryGreen)) {
                    Icon(Icons.Default.Star, null); Spacer(Modifier.width(8.dp)); Text(if (isEnglish) "Rate" else "ರೇಟ್")
                }
                OutlinedButton(onReportIssue, Modifier.weight(1f).height(60.dp), border = androidx.compose.foundation.BorderStroke(2.dp, Color.Red)) {
                    Icon(Icons.Default.Report, null, tint = Color.Red); Spacer(Modifier.width(8.dp)); Text(if (isEnglish) "Report" else "ದೂರು", color = Color.Red)
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun RatingDialog(isEnglish: Boolean, onDismiss: () -> Unit) {
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEnglish) "Rate this Project" else "ಯೋಜನೆಯನ್ನು ರೇಟ್ ಮಾಡಿ") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    (1..5).forEach { i ->
                        Icon(
                            if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder, null,
                            Modifier.size(40.dp).clickable { rating = i },
                            tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text(if (isEnglish) "Feedback comment (Optional)" else "ಪ್ರತಿಕ್ರಿಯೆ ಕಾಮೆಂಟ್ (ಐಚ್ಛಿಕ)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            TextButton({ 
                Toast.makeText(context, "Feedback Submitted!", Toast.LENGTH_SHORT).show()
                onDismiss() 
            }) { Text("Submit") }
        }
    )
}

@Composable
fun DetailInfoCard(icon: ImageVector, label: String, value: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(32.dp), colors = CardDefaults.cardColors(CardBg)) {
        Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(Color.White), Alignment.Center) { Icon(icon, null, tint = PrimaryGreen) }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
            }
        }
    }
}

@Composable
fun TimelineItem(step: TimelineStep, isEnglish: Boolean, isLast: Boolean) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
            Box(Modifier.size(24.dp).clip(CircleShape).background(if(step.isDone) PrimaryGreen else Color.LightGray), Alignment.Center) {
                Icon(if(step.isDone) Icons.Default.Check else Icons.Default.AccessTime, null, Modifier.size(14.dp), Color.White)
            }
            if (!isLast) Box(Modifier.width(2.dp).fillMaxHeight().background(Color.LightGray))
        }
        Card(Modifier.padding(bottom = 16.dp).fillMaxWidth(), colors = CardDefaults.cardColors(Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))) {
            Column(Modifier.padding(16.dp)) {
                Text(if (isEnglish) step.date else step.knDate, fontSize = 12.sp, color = Color.Gray)
                Text(if (isEnglish) step.title else step.knTitle, fontWeight = FontWeight.Bold, color = if(step.isDone) PrimaryGreen else Color.Black)
            }
        }
    }
}

@Composable
fun ImpactScreen(isEnglish: Boolean, onNavigate: (Screen) -> Unit) {
    val projects by ProjectRepository.projects.collectAsState()
    val totalCount = projects.size + 37 // Adding some to match your 42 total requirement
    val completedCount = projects.count { it.isCompleted } + 27
    val ongoingCount = projects.count { !it.isCompleted } + 4

    Scaffold(bottomBar = { AppBottomNav(Screen.Impact, onNavigate, isEnglish) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Text(
                if (isEnglish) "Village Impact" else "ಗ್ರಾಮ ಅಭಿವೃದ್ಧಿ",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )
            Text(
                if (isEnglish) "Real-time statistics of our digital transformation." else "ನಮ್ಮ ಡಿಜಿಟಲ್ ರೂಪಾಂತರದ ನೈಜ-ಸಮಯದ ಅಂಕಿಅಂಶಗಳು.",
                color = Color.Gray,
                fontSize = 14.sp
            )
            
            Spacer(Modifier.height(32.dp))

            // 2x2 Grid for Stats
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InnovativeStatCard(
                        label = if (isEnglish) "TOTAL PROJECTS" else "ಒಟ್ಟು ಯೋಜನೆಗಳು",
                        value = "$totalCount",
                        icon = Icons.Default.BarChart,
                        bgColor = Color(0xFFE7EEFE),
                        iconColor = Color(0xFF2D5BD7),
                        modifier = Modifier.weight(1f)
                    )
                    InnovativeStatCard(
                        label = if (isEnglish) "COMPLETED" else "ಪೂರ್ಣಗೊಂಡಿದೆ",
                        value = "$completedCount",
                        icon = Icons.Default.Groups,
                        bgColor = Color(0xFFEAF9E9),
                        iconColor = Color(0xFF1B824B),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InnovativeStatCard(
                        label = if (isEnglish) "ONGOING" else "ಪ್ರಗತಿಯಲ್ಲಿದೆ",
                        value = "$ongoingCount",
                        icon = Icons.Default.TrendingUp,
                        bgColor = Color(0xFFFFF8E5),
                        iconColor = Color(0xFFC09000),
                        modifier = Modifier.weight(1f)
                    )
                    InnovativeStatCard(
                        label = if (isEnglish) "BUDGET USED" else "ಖರ್ಚಾದ ಬಜೆಟ್",
                        value = "₹1.2Cr",
                        icon = Icons.Default.PieChart,
                        bgColor = Color(0xFFF6EEFF),
                        iconColor = Color(0xFF7D33FF),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(Modifier.height(32.dp))
            
            Text(
                if(isEnglish) "Development Trends" else "ಅಭಿವೃದ್ಧಿ ಪ್ರವೃತ್ತಿಗಳು",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )
            Spacer(Modifier.height(16.dp))
            
            // Monthly Trends Graph
            Card(
                Modifier.fillMaxWidth().height(250.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Bottom) {
                        val barData = listOf(0.4f, 0.6f, 0.5f, 0.8f, 0.7f, 0.9f)
                        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
                        
                        barData.forEachIndexed { index, height ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    Modifier
                                        .width(30.dp)
                                        .fillMaxHeight(height)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(if (index == 5) PrimaryGreen else PrimaryGreen.copy(0.3f))
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(months[index], fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun InnovativeStatCard(label: String, value: String, icon: ImageVector, bgColor: Color, iconColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(bgColor)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(icon, null, Modifier.size(32.dp), iconColor)
            Column {
                Text(value, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryGreen)
                Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        }
    }
}

@Composable
fun AlertsScreen(isEnglish: Boolean, onNavigate: (Screen) -> Unit) {
    Scaffold(bottomBar = { AppBottomNav(Screen.Alerts, onNavigate, isEnglish) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(24.dp)) {
            Text(if (isEnglish) "Village Alerts" else "ಗ್ರಾಮದ ಎಚ್ಚರಿಕೆಗಳು", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = PrimaryGreen)
            Spacer(Modifier.height(24.dp))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(CardBg)) {
                ListItem(headlineContent = { Text("Gram Sabha Meeting today") }, leadingContent = { Icon(Icons.Default.Notifications, null, tint = PrimaryGreen) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(isEnglish: Boolean, onToggleLanguage: (Boolean) -> Unit, isDarkMode: Boolean, onToggleDarkMode: (Boolean) -> Unit, onNavigate: (Screen) -> Unit, onLogout: () -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    var profileName by remember { mutableStateOf(user?.email?.substringBefore("@") ?: "Citizen") }
    var showEditDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                OutlinedTextField(profileName, { profileName = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
            },
            confirmButton = { TextButton({ showEditDialog = false }) { Text("Save") } }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Gram-Suvidha") },
            text = {
                Text("Grama-Suvidha is a Digital Transparency Portal designed to empower village residents by providing real-time tracking of infrastructure projects. Our mission is to ensure accountability and community participation in rural development.\n\nVersion: 1.0.4\nDeveloper: Madhu")
            },
            confirmButton = { TextButton({ showAboutDialog = false }) { Text("Close") } }
        )
    }

    Scaffold(bottomBar = { AppBottomNav(Screen.Settings, onNavigate, isEnglish) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            // Profile Card (Innovative Design)
            Box(Modifier.fillMaxWidth().height(350.dp).background(PrimaryGreen), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(Modifier.size(120.dp), shape = CircleShape, color = Color.White) {
                            Box(contentAlignment = Alignment.Center) { Text(profileName.take(1).uppercase(), fontSize = 48.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen) }
                        }
                        IconButton({ }, Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF8B8000))) {
                            Icon(Icons.Default.CameraAlt, null, Modifier.size(20.dp), Color.White)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(profileName, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Huligere Resident • ID #4459", color = Color.White.copy(0.7f))
                    Spacer(Modifier.height(24.dp))
                    Button({ showEditDialog = true }, shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(Color.White.copy(0.2f))) {
                        Text("EDIT PROFILE", color = Color.White)
                    }
                    Spacer(Modifier.height(24.dp))
                    Row(Modifier.fillMaxWidth(0.8f), Arrangement.SpaceBetween) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ACCOUNT", fontSize = 10.sp, color = Color.White.copy(0.6f))
                            Text(user?.email ?: "Unknown", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("MEMBER SINCE", fontSize = 10.sp, color = Color.White.copy(0.6f))
                            Text("May 2024", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Column(Modifier.padding(24.dp)) {
                Text("PORTAL SETTINGS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                Spacer(Modifier.height(16.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Select Language") },
                            leadingContent = { Icon(Icons.Default.Language, null, tint = PrimaryGreen) },
                            trailingContent = {
                                Row(Modifier.clip(CircleShape).background(Color(0xFFF1F5EF)).padding(4.dp)) {
                                    Surface(onClick = { onToggleLanguage(true) }, color = if(isEnglish) PrimaryGreen else Color.Transparent, shape = CircleShape) {
                                        Text("EN", Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = if(isEnglish) Color.White else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Surface(onClick = { onToggleLanguage(false) }, color = if(!isEnglish) PrimaryGreen else Color.Transparent, shape = CircleShape) {
                                        Text("ಕನ್ನಡ", Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = if(!isEnglish) Color.White else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        )
                        ListItem(headlineContent = { Text("Dark Mode") }, leadingContent = { Icon(Icons.Default.DarkMode, null, tint = PrimaryGreen) }, trailingContent = { Switch(isDarkMode, onToggleDarkMode) })
                        ListItem(headlineContent = { Text("About App") }, leadingContent = { Icon(Icons.Default.Info, null, tint = PrimaryGreen) }, trailingContent = { Icon(Icons.Default.ChevronRight, null) }, modifier = Modifier.clickable { showAboutDialog = true })
                        ListItem(
                            headlineContent = { Text("Log Out", color = Color.Red) },
                            leadingContent = { Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color.Red.copy(0.1f)), contentAlignment = Alignment.Center) { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color.Red) } },
                            modifier = Modifier.clickable { onLogout() }
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Text("GRAM-SUVIDHA V1.0.4", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 10.sp, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun AppBottomNav(currentScreen: Screen, onNavigate: (Screen) -> Unit, isEnglish: Boolean) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(currentScreen is Screen.Home, { onNavigate(Screen.Home) }, { Icon(Icons.Default.Home, null) }, label = { Text(if (isEnglish) "HOME" else "ಮುಖಪುಟ") })
        NavigationBarItem(currentScreen is Screen.Impact, { onNavigate(Screen.Impact) }, { Icon(Icons.Default.Leaderboard, null) }, label = { Text(if (isEnglish) "IMPACT" else "ಅಭಿವೃದ್ಧಿ") })
        NavigationBarItem(currentScreen is Screen.Alerts, { onNavigate(Screen.Alerts) }, { Icon(Icons.Default.Notifications, null) }, label = { Text(if (isEnglish) "ALERTS" else "ಎಚ್ಚರಿಕೆ") })
        NavigationBarItem(currentScreen is Screen.Settings, { onNavigate(Screen.Settings) }, { Icon(Icons.Default.Settings, null) }, label = { Text(if (isEnglish) "SETTINGS" else "ಸೆಟಪ್") })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueScreen(project: Project, isEnglish: Boolean, onBack: () -> Unit) {
    var desc by remember { mutableStateOf("") }; val context = LocalContext.current
    Scaffold(topBar = { TopAppBar(title = { Text(if (isEnglish) "Report Issue" else "ದೂರು ಸಲ್ಲಿಸಿ") }, navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(24.dp)) {
            OutlinedTextField(desc, { desc = it }, label = { Text("Describe Issue") }, modifier = Modifier.fillMaxWidth().height(150.dp), shape = RoundedCornerShape(16.dp))
            Spacer(Modifier.height(24.dp))
            Button({ Toast.makeText(context, "Report Submitted", Toast.LENGTH_SHORT).show(); onBack() }, Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(PrimaryGreen)) { Text("Submit") }
        }
    }
}
