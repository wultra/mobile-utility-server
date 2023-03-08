#
# Wultra Mobile Utility Server
# Copyright (C) 2023  Wultra s.r.o.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

# Build the application and copy the files in /target folder
docker build --platform linux/arm64 -f deploy/dockerfile/builder/Dockerfile . -t mobile-utility-server-builder

rm -rf ./target
containerId=$(docker create mobile-utility-server-builder)
docker cp "$containerId":/workspace/target/ .
docker rm "$containerId"

# Build the image that upgrades database schema with Liquibase
docker build --platform linux/arm64 -f deploy/dockerfile/database/Dockerfile . -t mobile-utility-server-database

# Build the actual runtime image that runs the application
docker build --platform linux/arm64 -f deploy/dockerfile/runtime/Dockerfile . -t mobile-utility-server