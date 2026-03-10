# Best practices
source: https://antigravity.google/docs/skills

## Keep skills focused
Each skill should do one thing well. Instead of a "do everything" skill, create separate skills for distinct tasks.

## Write clear descriptions
The description is how the agent decides whether to use your skill. Make it specific about what the skill does and when it's useful.

## Use scripts as black boxes
If your skill includes scripts, encourage the agent to run them with --help first rather than reading the entire source code. This keeps the agent's context focused on the task.

## Include decision trees
For complex skills, add a section that helps the agent choose the right approach based on the situation.