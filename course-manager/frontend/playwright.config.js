import { defineConfig, devices } from '@playwright/test'
import { fileURLToPath } from 'node:url'

export default defineConfig({
  testDir: './e2e',
  fullyParallel: false,
  workers: 1,
  retries: process.env.CI ? 1 : 0,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: { baseURL: 'http://127.0.0.1:15173', trace: 'retain-on-failure' },
  projects: [
    { name: 'desktop', use: { ...devices['Desktop Chrome'] } },
    {
      name: 'mobile',
      use: { ...devices['Desktop Chrome'], viewport: { width: 390, height: 844 } },
    },
  ],
  webServer: [
    {
      command:
        process.env.COURSE_BACKEND_COMMAND ||
        './mvnw spring-boot:run -Dspring-boot.run.profiles=e2e',
      cwd: fileURLToPath(new URL('../', import.meta.url)),
      url: 'http://127.0.0.1:18080/settings',
      timeout: 120000,
      reuseExistingServer: false,
    },
    {
      command: 'npm run dev -- --port 15173 --strictPort',
      url: 'http://127.0.0.1:15173',
      env: { API_TARGET: 'http://127.0.0.1:18080' },
      reuseExistingServer: false,
    },
  ],
})
