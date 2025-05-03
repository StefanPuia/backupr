FROM docker

ENV PYTHONUNBUFFERED=1

RUN apk update
RUN apk add --update --no-cache python3 py3-pip openjdk21 gcc musl-dev python3-dev libffi-dev openssl-dev cargo make pipx
RUN ln -sf python3 /usr/bin/python

RUN pipx install azure-cli awscli

ENV PATH="/root/.local/bin:$PATH"

WORKDIR /app

COPY . .

RUN /app/gradlew bootRun
RUN /app/gradlew --stop

ENTRYPOINT ["/app/dev/docker/run.sh"]
