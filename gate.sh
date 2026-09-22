#!/usr/bin/env bash
# gate.sh — o portão. Custo zero em token. Se isto passa, a tarefa acabou.
# -q e -B deixam o log curto: é ele que volta pro modelo quando falha.
set -e

echo "▸ compila"
./mvnw -q -B compile

echo "▸ testes + verify (inclui Testcontainers)"
./mvnw -q -B verify

echo "✅ gate verde"
