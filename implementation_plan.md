# Implementation Plan: Coordinator Data Management System Updates

## Goal Description
The objective is to consolidate the fragmented project structure (currently split across `frontend` and `backend` branches) into a single unified `main` branch with distinct `/frontend` and `/backend` directories, as outlined in the project report. Once consolidated, I will restore missing backend components, fix CORS issues for API communication, and implement remaining ongoing features (Attendance UI refinement, PDF Certificate Engine, and Analytics Dashboard).

## User Review Required
> [!WARNING]
> This plan involves merging branches and restructuring the root folder. Please ensure there are no uncommitted local changes that you do not want to keep, as we will be performing git merges and moving files.

## Proposed Changes

### 1. Git Branch Consolidation & Restructuring
*   **Checkout & Update**: Switch to the `main` branch and ensure it is up-to-date.
*   **Merge Backend**: Merge the `backend` branch into `main`. Create the `/backend` directory if it doesn't exist, and move `pom.xml`, `mvnw`, `src/`, etc., into it.
*   **Merge Frontend**: Merge the `frontend` branch into `main`. Ensure all HTML, CSS, and JS files are moved strictly into the `/frontend` directory.
*   **Commit**: Commit the consolidated structure so we have a unified codebase.

### 2. Backend Restoration & Security Fixes
*   **Verify Backend**: Ensure `SecurityConfig.java`, REST Controllers, and Services are correctly placed in `/backend/src/main/java/com/ticclub/...`.
*   **CORS Configuration**:
    #### [MODIFY] SecurityConfig.java
    Update `SecurityConfig.java` to include a `CorsConfigurationSource` bean that allows `http://127.0.0.1:5500` (or the default Live Server port) to make authenticated requests to `http://localhost:8080`.

### 3. Feature Implementation
*   **Attendance UI Refinement**:
    #### [MODIFY] frontend/assets/js/attendance.js
    Improve the dynamic rendering of attendance state after marking a student present/absent without a full reload. Add robust error handling if the API fails.
*   **PDF Certificate Engine**:
    #### [NEW] backend/src/main/java/com/ticclub/util/PdfGeneratorUtil.java
    Create a utility class using `iText` or `OpenPDF` to generate certificates for students. Add an endpoint in the controller to trigger this.
*   **Analytics Dashboard**:
    #### [NEW] backend/src/main/java/com/ticclub/controller/AnalyticsController.java
    Create an endpoint (e.g., `/api/analytics/summary`) to return JSON data (total students, events, overall attendance rate) to be consumed by the frontend.
    #### [MODIFY] frontend/assets/js/dashboard.js
    Update `dashboard.js` to fetch data from the new `/api/analytics/summary` endpoint and render it using Chart.js.

## Verification Plan

### Automated Tests
*   **Maven Build**: Run `.\mvnw clean install -DskipTests` in the `/backend` directory to ensure the Spring Boot application compiles successfully after the structural move and code additions.
*   **Unit Tests**: Run `.\mvnw test` to execute any existing automated tests.

### Manual Verification
1.  **Backend Startup**: Run the backend application (`.\mvnw spring-boot:run`) and verify it starts without context loading errors on port `8080`.
2.  **Frontend Functionality**:
    *   Open `frontend/index.html` in a browser or Live Server.
    *   Attempt to log in to verify the CORS configuration and JWT token generation.
    *   Navigate to the Dashboard to verify the new Analytics API successfully populates the charts.
    *   Navigate to the Attendance page, mark a student present, and verify the UI updates instantly.
