@echo off
echo ====== GERENCIADOR DOCKER POSTGRES ======
echo 1. Subir banco
echo 2. Parar banco
echo 3. Reiniciar banco
echo 4. Remover banco (com dados)
echo 5. Sair
set /p option=Escolha uma opção (1-5): 

if "%option%"=="1" (
    docker-compose up -d
) else if "%option%"=="2" (
    docker stop spring_pg_db
) else if "%option%"=="3" (
    docker start spring_pg_db
) else if "%option%"=="4" (
    docker-compose down -v
) else if "%option%"=="5" (
    exit
) else (
    echo Opcao invalida!
)
pause
