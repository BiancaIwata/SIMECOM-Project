#!/bin/bash

# Array para pacotes a serem instalados;
pacotes=(
    docker.io
    openjdk-21-jre
)

# Atualização do sistema;
sudo apt-get upgrade -y && sudo apt-get update -y

# For para realizar a instalacao dos pacotes
for i in "${pacotes[@]}"
do
    sudo apt-get install $i -y > /dev/null 2>&1

    if [ $? -eq 0 ]; then
        echo "Pacote $i Instalado!"
    else
        echo "Pacote $i Não instalado!"
    fi
done