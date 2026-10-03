# Course Manager

A local course and deadline tracker built with React, Spring Boot, JPA and H2 SQL.

- Manage courses, students, enrollments and assignments.
- Check assignments off and reopen them later.
- View upcoming or overdue work, use 3/7/14/30-day presets, or choose an inclusive date range.
- Browse deadlines on a monthly calendar. Filter preferences stay in your browser.

## Run

Requires Java 21 and Node.js 24+. From the repository root, start the backend:

```sh
cd course-manager
./mvnw spring-boot:run
```

In another terminal, start the frontend:

```sh
cd course-manager/frontend
npm ci
npm run dev
```

Open http://127.0.0.1:5173. On Windows, use `mvnw.cmd` instead of `./mvnw`.

Data is saved in `course-manager/data/` and survives restarts. Completion belongs to an assignment; student records track enrollments. Dates default to `America/Vancouver`; set `APP_TIME_ZONE` to change this.

## Check

Run `./mvnw clean verify` in `course-manager`. In `course-manager/frontend`, run:

```sh
npm test
npm run lint
npm run build
npx playwright install chromium
npm run test:e2e
```

Browser tests start isolated servers on ports 18080 and 15173 with an in-memory database. On Windows, set `COURSE_BACKEND_COMMAND` to `mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=e2e` before running them. GitHub Actions runs these checks for pull requests.
