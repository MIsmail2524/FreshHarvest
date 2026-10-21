# FreshHarvest

Minimal instructions to build and run the FreshHarvest JavaFX app.

Prerequisites
- JDK 17 or later
- Gradle (or use the included Gradle wrapper)

Quick setup
```sh
# make project scripts executable and run setup (only needed once)
chmod +x scripts/setup-project.sh
./scripts/setup-project.sh
```

Optional: Install SceneBuilder
```sh
chmod +x scripts/install-scene-builder.sh
./scripts/install-scene-builder.sh
```

Run the app
```sh
./gradlew run
```

That's it — the app should launch using the Gradle run task.

## Environment File (app/.env)
```.env
DB_URL=jdbc:mysql://localhost:3306/testdb
DB_USER=root
DB_PASS=123
```
## Credits

- [Muhammad Ismail](https://github.com/MIsmail2524): Farmer module (screens, till report), Customer module, integrated final application, database SQL file.
- [Mohammad Saeed Saeedi](https://github.com/msaeedsaeedi): project setup, database integration, login and signup, UI styling.
- [Muhammad Haris](https://github.com/muhammad-haris2): Factory manager dashboard and shipments (branches `Factory` and `haris/factoryDashboard`).

Group project. Full commit history and authorship are preserved; the original repository is msaeedsaeedi/FreshHarvest.
