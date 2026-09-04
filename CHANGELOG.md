<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# EnvUi Changelog

## [Unreleased]

### Added

* Environment variable type detection with `Any` fallback support

### Fixed

* `.gitignore` formatting during project initialization
* Incorrect handling of existing environment source types
* Automatic generation of missing environment templates
* Template generation for existing `.env` files containing environment variables

## [1.0.0] - 2026-08-22

### Added

- Custom action for creating `.env` files directly from the IDE
- Immutable environment variables with automatic restoration of their initial values
- IDE shutdown warnings for critical environment variables that have not been reset
- Click-to-copy support for environment variable values
- Overview of the available environment files
- Environment template creation together with the related `.env` file
