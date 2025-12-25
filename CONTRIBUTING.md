# Contributing to NiceCommerce Spring Boot

Thank you for your interest in contributing to NiceCommerce! This document provides guidelines and instructions for contributing.

## 🤝 Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and help them learn
- Focus on constructive feedback
- Respect different viewpoints and experiences

## 🚀 Getting Started

1. **Fork the repository**
2. **Clone your fork**
   ```bash
   git clone https://github.com/yourusername/nicecommerce-springboot.git
   cd nicecommerce-springboot
   ```

3. **Set up development environment**
   ```bash
   # Install dependencies
   mvn clean install
   
   # Run tests
   mvn test
   ```

4. **Create a branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

## 📝 Development Workflow

### 1. Make Changes

- Write clean, readable code
- Follow existing code style
- Add comments for complex logic
- Update documentation as needed

### 2. Write Tests

- Add unit tests for new features
- Add integration tests for API endpoints
- Maintain 80%+ test coverage
- All tests must pass

### 3. Commit Changes

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add product search functionality
fix: resolve cart persistence issue
docs: update API documentation
test: add tests for user service
refactor: improve error handling
```

### 4. Push and Create PR

```bash
git push origin feature/your-feature-name
```

Then create a Pull Request on GitHub.

## 📋 Pull Request Guidelines

### Before Submitting

- [ ] Code compiles without errors
- [ ] All tests pass
- [ ] Test coverage is maintained
- [ ] Documentation is updated
- [ ] Code follows style guidelines
- [ ] No sensitive data in code

### PR Description

Include:
- Description of changes
- Related issue number
- Testing performed
- Screenshots (if UI changes)

## 🎨 Code Style

### Java Style Guide

- Use 4 spaces for indentation
- Follow Java naming conventions
- Maximum line length: 120 characters
- Use meaningful variable names
- Add JavaDoc for public methods

### Example

```java
/**
 * Creates a new user in the system.
 * 
 * @param request Sign up request with user details
 * @return User DTO with created user information
 * @throws BusinessException if email already exists
 */
public UserDTO signUp(SignUpRequest request) {
    // Implementation
}
```

## 🧪 Testing Guidelines

### Unit Tests

- Test one thing at a time
- Use descriptive test names
- Follow AAA pattern (Arrange, Act, Assert)
- Mock external dependencies

### Integration Tests

- Test complete workflows
- Use test database (H2)
- Clean up after tests

### Test Naming

```java
@Test
@DisplayName("Should create user successfully")
void testCreateUser_Success() {
    // Test implementation
}
```

## 📚 Documentation

### Code Comments

- Explain **why**, not **what**
- Use JavaDoc for public APIs
- Keep comments up-to-date

### README Updates

- Update README for new features
- Add examples for new APIs
- Update setup instructions if needed

## 🐛 Reporting Bugs

Use the [Bug Report template](.github/ISSUE_TEMPLATE/bug_report.md):

- Clear description
- Steps to reproduce
- Expected vs actual behavior
- Environment details
- Relevant logs

## 💡 Suggesting Features

Use the [Feature Request template](.github/ISSUE_TEMPLATE/feature_request.md):

- Clear problem statement
- Proposed solution
- Use cases
- Alternatives considered

## 🔍 Review Process

1. PR is reviewed by maintainers
2. Feedback is provided
3. Changes are requested if needed
4. PR is approved and merged

## 📞 Getting Help

- Check existing documentation
- Search closed issues
- Ask in discussions
- Contact maintainers

## 🙏 Thank You!

Your contributions make this project better. Thank you for taking the time to contribute!

