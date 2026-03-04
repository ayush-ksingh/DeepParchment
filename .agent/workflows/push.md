---
description: Builds project with Maven, then stages, commits, and pushes changes.
---
# Maven Push to GitHub

## Steps
1. **Quality Gate:** Run `mvn clean install`.
   - *If this fails, STOP and report the build errors to the user.*
2. **Analyze:** Run `git status` to show the user what is about to be pushed.
3. **Stage:** Run `git add .`.
4. **Commit:** - Use the **Git Specialist** skill to generate a commit message based on the diff.
   - Ask: "Build passed. Commit as: `[message]`? (y/n)"
   - If 'y', run `git commit -m "{{generated_message}}"`.
5. **Push:** - Detect branch: `git rev-parse --abbrev-ref HEAD`.
   - Run `git push origin {{current_branch}}`.

## Error Handling
- **Build Failure:** If `mvn` fails, do not proceed to `git add`.
- **Push Failure:** If "upstream not set," run `git push --set-upstream origin {{current_branch}}`.
