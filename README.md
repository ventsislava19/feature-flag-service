Stack: Java 21, Spring Boot 3.5, Spring Data JPA with H2, Lombok, ModelMapper, JUnit 5 & Mockito, Gradle (Kotlin DSL).

How to build and run:

- Build and run test: ./gradlew build
- Start the application: ./gradlew bootRun

Service starts at http://localhost:8080.
The DB persists across restarts - stored in ./data/featureflags.

All end endpoints use JSON for request and response bodies with base path: /api/flags.

Create a flag:

  curl -X POST http://localhost:8080/api/flags \
  -H "Content-Type: application/json" \
  -d '{"name": "dark-mode", "description": "", "enabled": false}'


Flag names must be kebab-case.
If a duplicate name is sent, 409 with error message is returned.

- To list all the flags: 
curl http://localhost:8080/api/flags.

- Get single flag: 
curl http://localhost:8080/api/flags/1.

- Toggle a flag: 
curl -X PATCH http://localhost:8080/api/flags/1 \
-H "Content-Type: application/json" \
-d '{"enabled": true}'

- Delete a flag: curl -X DELETE http://localhost:8080/api/flags/1.

- Evaluate a flag: curl http://localhost:8080/api/flags/dark-mode/evaluate.

Status codes: 201 (created), 200 (success), 204 (deleted), 400 (validation error), 404 (not found), 409 (duplicate name).

Design decisions:
- File based H2 so flags can survive a restart. To swap to MySql or something else, only application.properties needs to be edited.
- PATCH with Boolean wrapper  instead of primitive boolean so null is available to preventing silent overwrites.
- Kebab-case names enforced by regex because flag names appear in URL paths (/flags/{name}/evaluate).
- Duplicate protection at two layers - service checks existsByName (409 error) and the database has a unique constraint.
- New flags default to disabled - safer than defaulting to enabled, which could expose features in the process of being developed.

Tests:
- FeatureFlagRepositoryTest (7 tests, @DataJpaTest)for the repository layer. Verifies that Spring Data generates correct 
queries from method names (findByName, existsByName), that the unique constraint rejects duplicates at the database level and 
that timestamps are auto-populated. These are tested thoroughly because a typo in a query method name compiles fine, but fails at runtime.

- FeatureFlagServiceImplTest (4 tests, Mockito unit tests) - tests some business rules in isolation with the repository mocked: 
duplicate name rejection on create (and that save() is never called), partial update via PATCH (only enabled changes while name and 
description stay untouched), 404 for a non-existent id and flag evaluation returning the correct enabled state. These four cover the 
core logic that, if broken, would make the service unusable.

- FeatureFlagApiControllerTest (4 tests, @WebMvcTest with MockMvc) - tests the HTTP contract at the boundary: POST returns 201 
Created (not the default 200), blank name is rejected with 400 before reaching the service, DELETE returns 204 No Content and 
evaluating a non-existent flag name returns a 404 JSON error. These verify that the correct status codes and validation are wired
up at the HTTP layer.

Future improvements:
- Audit logging.
- Percentage based rollouts instead of on/off.
- Authentication and authorization.
- More tests.
