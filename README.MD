gradlew build & docker build -f articles.Dockerfile -t articles . & docker-compose up

curl --header "Content-Type: application/json" --request POST --data '{"email":"admin@mail.com","password":"admin_password"}' http://localhost:8080/api/auth/login