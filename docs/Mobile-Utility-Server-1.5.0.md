# Migration from 1.4.x to 1.5.0

This guide provides instructions for migrating from PowerAuth Mobile Utility Server version `1.4.x` to version `1.5.0`.

No migration steps nor database changes are required.

Please note, version `1.4.x` was introduced as a backport of most functionalities from version `1.5.x`, due to specific
library dependency constraints. This strategic approach resulted in a shift in the versioning sequence, directly
influencing the migration path.

It is important to mention that Liquibase changesets are tagged as `1.5.0` even in version `1.4.x`. This is because
retrospectively modifying Liquibase scripts is not considered good practice and might result in mismatches. By
maintaining consistent tagging, we aim to minimize potential confusion and ensure a clear migration path.