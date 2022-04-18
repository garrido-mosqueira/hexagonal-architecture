# Celonis Programming Challenge

Dear applicant,

Congratulations, you made it to the Celonis Programming Challenge!

The challenge itself should be timeboxed to 3 hours. This means you should not work longer than that on the completion.
If you can't make all points within the timeframe this is _not_ an issue. We will be happy to discuss any problems and ideas during the Challenge Interview.

Why do we ask you to complete this challenge?

First of all, we need to have some way of comparing different applicants, and we try to answer certain questions which
we cannot out-right ask in an interview - also we don't want to ask too many technical questions
in a face-to-face interview to not be personally biased in a potentially stressful situation.
To be as transparent as possible, we want to give you some insights into what we look at and how we evaluate.
This challenge gives you the possibility to shine :)

Note that there is nothing wrong with googling when you have certain questions or are unsure about some APIs,
but you should not outright copy code. If you decide to copy code, please mark it as such, citing the source.

## Complete and extend a java application

For this challenge, you have received a project which has a few problems.
You first have to fix those problems in order to get the application running, and then you should extend it with the requirements below.

What we are looking into:
  - Understanding and implementation of a specification
  - Java implementation skills (Java 11, Spring Boot)
  - Multithreading / locking execution
  - **Note**: performance and scalability are important, please apply reasonable balance between solution performance and invested time

      We expect to do some demo during the next technical interview,
      so please ensure the API works and prepare some mocks
      (Postman, curl or any preferred HTTP/REST tools)

How to understand the task:
  - consider the provided challenge as an application with some existing functionality,
    which was used to "generate" a file and download it
  - fix current issues to make the application runnable
  - keep existing behavior and API. Refactorings are allowed and welcome
  - extend and generalize the supplied sources according to the description below


### Task 1: Dependency injection

The project you received fails to start correctly due to a problem in the dependency injection.
Identify that problem and fix it.

### Task 2: Extend the application

The task is to extend the current functionality of the backend by
- implementing a new task type
- showing the progress of the task execution
- implementing a task cancellation mechanism.

The new task type is a simple counter which is configured with two input parameters, `x` and `y` of type `integer`.
When the task is executed, counter should start in the background and progress should be monitored.
Counting should start from `x` and get increased by one every second.
When counting reaches `y`, the task should finish successfully.

The progress of the task should be exposed via the API so that a web client can monitor it.
Canceling a task that is being executed should be possible, in which case the execution should stop.

### Task 3: Periodically clean up the tasks

The API can be used to create tasks, but the user is not required to execute those tasks.
The tasks that are not executed after an extended period (e.g. a week) should be periodically cleaned up (deleted).


### Run Application

## Creating Docker image 
``` docker build -t challenge-java . ```

## Running application
``` docker-compose up ```

