FROM node:20-alpine

WORKDIR /app

RUN npm install -g pnpm

# 👇 importante pro pnpm funcionar direito
ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"

# 👇 copia só dependências primeiro
COPY package.json pnpm-lock.yaml* ./

# 👇 instala deps com store correto
RUN pnpm install --frozen-lockfile

# 👇 copia o resto
COPY . .

EXPOSE 3000

CMD ["pnpm", "dev"]