# Basic Windows App - GitHub Copilot Instructions

Basic Windows App is a simple JavaFX desktop application that displays "Hello World" in a GUI window. It serves as a template for developing Java-based Windows applications using JavaFX.

**Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.**

## Working Effectively

### Environment Requirements
- **Java**: Java 17 or higher (OpenJDK 17.0.16+ confirmed working)
- **Maven**: 3.6.0 or higher (Maven 3.9.11+ confirmed working) 
- **OS**: Works on Windows, Linux, and macOS
- **Display**: JavaFX applications require a display environment for GUI execution

### Essential Build Commands
Always run these commands in sequence for a fresh repository setup:

```bash
# Navigate to project root
cd /path/to/basic-windows-app

# Clean install with dependencies - NEVER CANCEL: Takes ~20 seconds
mvn clean install
```

**CRITICAL TIMING**: Set timeout to 60+ minutes for `mvn clean install` even though it typically completes in 20 seconds. NEVER CANCEL Maven builds.

### Primary Build Commands
- `mvn clean compile` - Compile source code (~2 seconds)
- `mvn clean package` - Create JAR file (~3 seconds) 
- `mvn clean install` - Full build with dependencies (~20 seconds, NEVER CANCEL: Set 60+ minute timeout)

### Running the Application

**IMPORTANT**: JavaFX applications require a graphical display environment and will fail in headless environments (CI, Docker without X11, remote terminals without display forwarding).

#### Method 1: Maven JavaFX Plugin (Recommended)
```bash
mvn javafx:run
```
**Note**: This will fail with "UnsupportedOperationException: Unable to open DISPLAY" in headless environments. This is expected behavior, not a build failure.

#### Method 2: Direct Java Execution
```bash
# First compile
mvn clean compile

# Run with JavaFX modules (requires JavaFX SDK installed separately)
java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp target/classes com.example.basicwindowsapp.BasicWindowsApp
```

#### Method 3: JAR Execution
```bash
# Create JAR
mvn clean package

# JAR location: target/basic-windows-app-0.1.0.jar
# Note: Standard JAR lacks main manifest - use mvn javafx:run instead
```

## Validation and Testing

### Build Validation
Always run these validation steps after making changes:
1. `mvn clean compile` - Verify code compiles
2. `mvn clean package` - Verify JAR creation
3. `mvn clean install` - Full build validation

### Application Validation
**MANUAL VALIDATION REQUIREMENT**: When working with UI changes, you MUST test the actual application functionality:

1. **Run Application**: `mvn javafx:run` (requires display environment)
2. **Expected Behavior**: 
   - Window opens with title "Basic Windows App - Hello World"
   - Window size: 400x300 pixels initially
   - "Hello World" text displayed in center, large bold font (24px)
   - Window is resizable with minimum size 300x200 pixels
3. **Test Scenarios**:
   - Verify window opens and displays correctly
   - Test window resizing functionality
   - Confirm text is properly centered and styled
   - Test window close functionality

### No Tests Available
This project currently has no unit tests. The `mvn test` phase runs but reports "No tests to run."

### No Linting/Formatting Tools
No Maven formatting or linting plugins are currently configured. Standard Maven compilation warnings are the only code quality checks available.

## Project Structure and Key Files

### Repository Root Structure
```
basic-windows-app/
├── .github/                           # GitHub configuration
├── docs/                              # Documentation
│   └── DESIGN.md                      # UI design specifications
├── src/
│   └── main/
│       └── java/
│           └── com/example/basicwindowsapp/
│               └── BasicWindowsApp.java    # Main application class
├── pom.xml                            # Maven configuration
├── README.md                          # Japanese documentation
├── test-app.sh                        # Test script (informational only)
└── .gitignore                         # Git ignore rules
```

### Key Files
- **BasicWindowsApp.java**: Main JavaFX Application class extending Application
- **pom.xml**: Maven build configuration with Java 17, JavaFX 21 dependencies
- **README.md**: Comprehensive Japanese documentation
- **docs/DESIGN.md**: UI specifications and technical requirements

### Dependencies
- **JavaFX Controls** (version 21): Core JavaFX UI components
- **JavaFX FXML** (version 21): FXML markup support (not currently used)

## Common Development Tasks

### Adding New Features
1. **New Java Classes**: Add to `src/main/java/com/example/basicwindowsapp/`
2. **Resources**: Add to `src/main/resources/` (create directory if needed)
3. **Dependencies**: Add to `pom.xml` dependencies section

### Build Troubleshooting
If build fails:
```bash
mvn clean
mvn compile
```

### JavaFX Runtime Issues
If JavaFX runtime errors occur:
- Ensure Java 17+ is installed
- JavaFX is included via Maven dependencies (no separate SDK needed)
- For manual Java execution, download JavaFX SDK from https://openjfx.io/

## Timing Expectations and Timeout Settings

**CRITICAL**: Always set appropriate timeouts for Maven commands to prevent premature cancellation:

- `mvn clean compile`: ~2 seconds (set 5+ minute timeout)
- `mvn clean package`: ~3 seconds (set 5+ minute timeout)  
- `mvn clean install`: ~20 seconds (set 60+ minute timeout)
- `mvn javafx:run`: Immediate failure in headless environments

**NEVER CANCEL MAVEN BUILDS**: Even fast builds can occasionally take longer due to network issues or system load.

## CI/Build Pipeline Notes
- No GitHub Actions workflows currently exist
- JavaFX applications cannot run in standard CI environments without display setup
- Build validation should use `mvn clean install` only
- Do not attempt `mvn javafx:run` in CI - it will always fail

## Development Environment Setup
For new developers:
1. Install Java 17+ JDK
2. Install Maven 3.6.0+
3. Clone repository
4. Run `mvn clean install` to verify setup
5. Use IDE with JavaFX support (IntelliJ IDEA, Eclipse with e(fx)clipse)

## Error Messages to Expect
- **"UnsupportedOperationException: Unable to open DISPLAY"**: Normal in headless environments
- **"no main manifest attribute"**: When trying to run standard JAR - use `mvn javafx:run` instead
- **JavaFX warnings**: JavaFX dependency warnings during build are normal