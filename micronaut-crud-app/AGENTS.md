# AGENTS.md

## Setup Commands
- Install dependencies: `mvn clean install`
- Start development server: `mvn spring-boot:run`
- Run tests: `mvn test`
- Build for production: `mvn clean install`

## Testing Guidelines
- Write unit tests for all new functions
- Use JUnit 4 for testing framework
- Aim for >80% code coverage
- Run tests before committing

## Project Structure
- `/src` - Main application code
- `/tests` - Test files

## Development Workflow
- Create feature branches from `main`
- Use pull requests for code review
- Squash commits before merging
- Update documentation for new features