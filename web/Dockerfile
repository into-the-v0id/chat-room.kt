FROM node:18-alpine AS build
COPY . /app
WORKDIR /app
RUN npm install \
    && npx nuxi generate \
    && rm -rf node_modules/

FROM caddy:2-alpine
COPY Caddyfile /etc/caddy/Caddyfile
COPY --from=build /app/.output/public /var/www/html
