---
name: Git Specialist (Java/Maven)
description: Expert in Git and Conventional Commits for Java/Maven projects.
---

# Git Specialist (Java/Maven)

## Goal
To create structured commit messages that reflect technical changes in a Java environment.

## Instructions
1. Run `git diff --cached` to analyze changes.
2. Use **Conventional Commits** format.
3. **Java Scopes:** Use relevant scopes like `(pom)`, `(core)`, `(api)`, or `(test)`.
   - *Example:* `feat(pom): add pgvector dependency`
   - *Example:* `fix(core): resolve connection leak in PostgreService`

## Constraints
- Never commit if the `mvn clean install` step in a workflow has failed.
- Always check if `target/` folders are being ignored by `.gitignore`.
