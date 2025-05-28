package com.crimson_code_blog_rest_apis.utils;

public enum DefaultCategory {
	FRONTEND_DEVELOPMENT("Frontend Development"),
    BACKEND_DEVELOPMENT("Backend Development"),
    FULLSTACK_DEVELOPMENT("Fullstack Development"),
    MOBILE_DEVELOPMENT("Mobile Development"),
    WEB_DEVELOPMENT("Web Development"),
    GAME_DEVELOPMENT("Game Development"),
    DATABASES("Databases"),
    DEVOPS("DevOps"),
    CLOUD_COMPUTING("Cloud Computing"),
    SOFTWARE_ARCHITECTURE("Software Architecture"),
    TESTING("Testing"),
    SECURITY("Security"),
    DATA_SCIENCE("Data Science"),
    MACHINE_LEARNING("Machine Learning"),
    ARTIFICIAL_INTELLIGENCE("Artificial Intelligence"),
    COMPUTER_VISION("Computer Vision"),
    PROGRAMMING_LANGUAGES("Programming Languages"),
    ALGORITHMS("Algorithms"),
    SYSTEM_DESIGN("System Design"),
    OPEN_SOURCE("Open Source"),
    INTERNET_OF_THINGS("Internet of Things"),
    BLOCKCHAIN("Blockchain"),
    PRODUCTIVITY_TOOLS("Productivity Tools"),
    PROJECT_MANAGEMENT("Project Management"),
    UI_UX_DESIGN("UI/UX Design"),
    API_DEVELOPMENT("API Development"),
    OPERATING_SYSTEMS("Operating Systems"),
    NETWORKING("Networking"),
    AUTOMATION("Automation"),
    LOW_CODE_NO_CODE("Low-Code / No-Code"),
    CODE_EDITOR_TOOLS("Code Editor Tools");

    private final String displayName;

    DefaultCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
