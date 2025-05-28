package com.crimson_code_blog_rest_apis.utils;

public enum DefaultTag {
	// Programming Languages
    JAVA("Java"),
    PYTHON("Python"),
    JAVASCRIPT("JavaScript"),
    TYPESCRIPT("TypeScript"),
    KOTLIN("Kotlin"),
    GO("Go"),
    RUST("Rust"),
    CSHARP("C#"),
    PHP("PHP"),
    CPLUSPLUS("C++"),
    C("C"),
    SWIFT("Swift"),
    DART("Dart"),
    RUBY("Ruby"),
    VALA("Vala"),
    NIM("Nim"),
    PERL("Perl"),
    JULIA("Julia"),
    SCALA("Scala"),
    HASKELL("Haskell"),
    LUA("Lua"),
    COBOL("COBOL"),
    FORTRAN("Fortran"),
    MATLAB("MATLAB"),
    ASSEMBLY("Assembly"),
    R("R"),

    // Web Frameworks & Tools
    SPRING_BOOT("Spring Boot"),
    DJANGO("Django"),
    EXPRESS("Express"),
    LARAVEL("Laravel"),
    ASP_NET("ASP.NET"),
    NEXT_JS("Next.js"),
    NUXT_JS("Nuxt.js"),
    REACT("React"),
    ANGULAR("Angular"),
    VUE_JS("Vue.js"),
    NODE_JS("Node.js"),
    SVELTE("Svelte"),
    HTMX("HTMX"),
    RAILS("Rails"),
    STRAPI("Strapi"),
    FASTAPI("FastAPI"),

    // Frontend Tools
    TAILWIND_CSS("Tailwind CSS"),
    BOOTSTRAP("Bootstrap"),
    BLAZOR("Blazor"),
    JQUERY("jQuery"),
    ES6("ES6"),
    VITE("Vite"),
    WEBPACK("Webpack"),
    BABEL("Babel"),

    // Mobile Development
    ANDROID("Android"),
    IOS("iOS"),
    FLUTTER("Flutter"),
    REACT_NATIVE("React Native"),

    // Databases
    MYSQL("MySQL"),
    POSTGRESQL("PostgreSQL"),
    MONGODB("MongoDB"),
    REDIS("Redis"),
    FIREBASE("Firebase"),
    SQL_SERVER("SQL Server"),
    BIGQUERY("BigQuery"),

    // DevOps & Cloud
    DOCKER("Docker"),
    KUBERNETES("Kubernetes"),
    JENKINS("Jenkins"),
    GITHUB_ACTIONS("GitHub Actions"),
    AWS("AWS"),
    AZURE("Azure"),
    GCP("GCP"),
    ANSIBLE("Ansible"),
    TERRAFORM("Terraform"),
    HELM("Helm"),
    PROMETHEUS("Prometheus"),
    GRAFANA("Grafana"),
    VAGRANT("Vagrant"),

    // Tools, Practices & Methodologies
    GIT("Git"),
    GITHUB("GitHub"),
    GITLAB("GitLab"),
    BITBUCKET("Bitbucket"),
    MARKDOWN("Markdown"),
    YAML("YAML"),
    JSON("JSON"),
    XML("XML"),
    MAVEN("Maven"),
    GRADLE("Gradle"),
    NPM("NPM"),
    YARN("Yarn"),
    PNPM("PNPM"),
    PIP("Pip"),
    BUNDLER("Bundler"),
    CARGO("Cargo"),
    AGILE("Agile"),
    SCRUM("Scrum"),
    TDD("TDD"),
    BDD("BDD"),
    DESIGN_PATTERNS("Design Patterns"),
    SOLID("SOLID"),
    DDD("DDD"),

    // APIs & Integration
    REST("REST"),
    REST_API("REST API"),
    GRAPHQL("GraphQL"),
    GRPC("gRPC"),
    SOAP("SOAP"),
    WEBHOOKS("Webhooks"),
    SOCKET_IO("Socket.IO"),

    // Security & Auth
    AUTHENTICATION("Authentication"),
    JWT("JWT"),
    OAUTH2("OAuth2"),
    OPENID_CONNECT("OpenID Connect"),
    SAML("SAML"),
    ENCRYPTION("Encryption"),
    HASHICORP_VAULT("HashiCorp Vault"),

    // Testing
    UNIT_TESTING("Unit Testing"),
    INTEGRATION_TESTING("Integration Testing"),
    CI_CD("CI/CD"),

    // Architecture
    MVC("MVC"),
    CLEAN_ARCHITECTURE("Clean Architecture"),

    // AI, ML & Data Science
    TENSORFLOW("TensorFlow"),
    PYTORCH("PyTorch"),
    PANDAS("Pandas"),
    NUMPY("NumPy"),
    SCIKIT_LEARN("Scikit-learn"),
    DATA_VISUALIZATION("Data Visualization"),
    MATPLOTLIB("Matplotlib"),
    SEABORN("Seaborn"),
    JUPYTER("Jupyter"),
    OPENAI("OpenAI"),
    LANGCHAIN("LangChain"),
    TRANSFORMERS("Transformers"),
    HUGGINGFACE("Hugging Face"),
    DBT("dbt");

    private final String displayName;

    DefaultTag(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

