test:
    ./gradlew :app:test --rerun

format:
    ./gradlew :app:spotlessApply

compile:
    ./gradlew :app:compileKotlin

build:
    ./gradlew :app:installDist

update_dependencies:
    ./gradlew :app:dependencies --configuration runtimeClasspath | rg -oP '[a-zA-Z0-9\\.-]+:[a-zA-Z0-9\\.-]+:[a-zA-Z0-9\\.-]+.*' | rg -vF '>' | rg -vF '(c)' | rg -vF '(*)' | sort | uniq | sort > dependencies.txt
