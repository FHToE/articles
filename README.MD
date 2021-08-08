1) To run application locally Docker should be installed
application starts with the following command:

gradlew build & docker build -f articles.Dockerfile -t articles . & docker-compose up

2) Application is also available on Heroku: https://articles-moliinyk.herokuapp.com/
First request will be time-consuming, since free heroku instance used

3) Only /auth endpoints are available without authentication.
Pre-created 2 users with ADMIN and USER roles.

Credentials:
admin@mail.com admin_password
user@mail.com user_password

- login: 
curl --header "Content-Type: application/json" -d "{\"email\":\"admin@mail.com\", \"password\":\"admin_password\"}" http://localhost:8080/api/auth/login

- register(only USER role registration is available):
curl --header "Content-Type: application/json" -d "{\"email\":\"new_user@mail.com\", \"password\":\"some_user_password\"}" http://localhost:8080/api/auth/signup

Both login and register endpoints returns accessToken in response. Token should be used as header to authorized requests

4) Article endpoints are available with header:
Authorization: Bearer ${JWT_TOKEN}

- create new article:
curl --header "Content-Type: application/json" --header "Authorization: Bearer ${JWT_TOKEN}" -d "{\"title\":\"some-title\", \"author\":\"author-name\", \"content\":\"article-content\", \"datePublished\":\"2021-08-08\"}" http://localhost:8080/api/article

- get paginated articles:
curl --header "Content-Type: application/json" --header "Authorization: Bearer ${JWT_TOKEN}" "http://localhost:8080/api/article?page=0&size=3"

- get article statistic(allowed for admins only)
curl --header "Content-Type: application/json" --header "Authorization: Bearer ${JWT_TOKEN}" http://localhost:8080/api/article/statistic
