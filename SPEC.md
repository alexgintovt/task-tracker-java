# Task Tracker — Spec v1

## Purpose
A command-line app to track simple to-do tasks.

## Features
1. Add a task with a text description
2. List all tasks, showing their status (done/not done)
3. Mark a task as complete by referencing its number (current position in the list)
4. Tasks are stored in a plain text file named tasks.txt
5. Each line contains one task in the format status|description, where status is either done or not_done. 
6. The order of the lines determines the task numbering.

## Acceptance Criteria
- Adding a task saves it so it's still there next time I list tasks
- The order of lines in the file should be the order shown to the user
- The task number should come from its position in the file, not from a stored ID
- Listing shows every task with a number and status, use the current position in the line for ordering tasks. Use format: number|status|description
- Marking complete updates that task's status, not others
- Data persists between runs (saved to a file, not just memory)
- If a user tries to mark non-existing task, print an error "The task with number X does not exist"
- If a user tries to mark already completed task, print a message "This task is already done."
- The file should be created automatically if it does not exist

## Out of scope (for now)
- Editing or deleting tasks
- Due dates, priorities, categories
- Any web or GUI interface