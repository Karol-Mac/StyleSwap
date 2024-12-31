# Dokumentacja projektu

## Uruchomienie aplikacji
#### Wymagania:
 - java 20 (lub wyższa)
 - zainstalowany i aktywny docker engine
 - wolne porty: 8080 i 3306

#### Uruchomienie bazy danych:
Baza danych jest automatycznie inicjalizowana jako komponent Docker <br>
 i zapełniana przykładowymi danymi przy starcie aplikacji.

#### Uruchomienie Serwera:
 - wywołać metodę main z klasy [VintedApplication](src/main/java/com/restapi/styleswap/VintedApplication.java)
    - lub wpisać w terminalu `./mvnw spring-boot:run` w katalogu projektu

## Endpointy aplikacji:
| Metoda | Endpoint                           | Zastosowanie                           | Kod odpowiedzi^ | Role użytkowników^^ |
|--------|------------------------------------|----------------------------------------|-----------------|---------------------|
| GET    | /api/categories                    | Lista kategorii                        | 200             | PUBLIC              |
| POST   | /api/categories                    | Dodanie kategorii                      | 201             | ADMIN               |
| GET    | /api/categories/{id}               | pojedyncza Kategoria                   | 200             | PUBLIC              |
| PUT    | /api/categories/{id}               | aktualizacja kategorii                 | 200             | ADMIN               |
| DELETE | /api/categories/{id}               | usunięcie kategorii                    | 204             | ADMIN               |
| POST   | /api/auth/login                    | zalogowanie                            | 200             | PUBLIC              |
| POST   | /api/auth/register                 | rejestracja                            | 201             | PUBLIC              |
| GET    | /api/images/{imageName}            | pobranie obrazu po nazwie              | 200             | PUBLIC              |
| GET    | /api/clothes/category/{categoryId} | pobranie wszystkich ubrań z kategorii  | 200             | PUBLIC              |
| GET    | /api/clothes/{id}                  | pojedyncze ubranie                     | 200             | PUBLIC              |
| GET    | /api/clothes/my                    | ubrania sprzedawane przez użytkownika  | 200             | ADMIN, USER         |
| POST   | /api/clothes                       | dodanie ubrania                        | 201             | ADMIN, USER         |
| PUT    | /api/clothes/{id}                  | aktualizacja ubrania                   | 200             | ADMIN, USER         |
| DELETE | /api/clothes/{id}                  | usunięcie ubrania                      | 204             | ADMIN, USER         |
| POST   | /api/conversations                 | utwórz nową konwersację                | 204             | ADMIN, USER         |
| GET    | /api/conversations/buying          | wyświetl konwersację (jako kupujący)   | 200             | ADMIN, USER         |
| GET    | /api/conversations/selling         | wyświetl konwersację (jako właściciel) | 200             | ADMIN, USER         |
| GET    | /api/conversations/{id}            | wyświetl konwersację (wiadomości)      | 200             | ADMIN, USER         |
| GET    | /api/conversations/send            | wyślij nową wiadomość                  | 200             | ADMIN, USER         |
 
^ - w przypadku prawidłowego zapytania <br>
^^ - role użytkowników, którzy mogą wykonać tą operację

## Funkcjonalności aplikacji:
- zabezpieczenie ednpointów za pomocą tokenów JWT
- zarządzanie użytkownikami (system ról)
- zapisywanie zdjęć na serwerze
- CRUD ubrań
- CRUD kategorii (specjalne uprawnienia administratora)
- zarządzanie konwersacjami i wiadomościami

## Przykładowe użycie aplikacji:

### Rejestracja i logowanie użytkownika
Endpointy związane z rejestracją i logowaniem użytkownika są dostępne dla wszystkich użytkowników. <br>
Dostępne są one w klasie [AuthController](src/main/java/com/restapi/styleswap/controller/AuthController.java),
który następnie przekazuje zapytanie do serwisu [AuthService](src/main/java/com/restapi/styleswap/service/impl/AuthServiceImpl.java).
1. Rejestracja nowego użytkownika:
   ```
   curl --location 'http://localhost:8080/api/auth/register' \
   --header 'Content-Type: application/json' \
   --data-raw '{   
   "name": "test",
   "username": "testUsername",
   "email": "test@email.com",
   "password": "Password123!"
   }'
   ```
   W przypadku poprawnej rejestracji, otrzymamy odpowiedź z kodem 201 i komunikat,
    niespełnienie walidacji po stronie serwera zwróci kod 400.
    Waldacja pól:

| Pole     | Opis                                                                                                                |
|----------|---------------------------------------------------------------------------------------------------------------------|
| name     | Nie może być puste                                                                                                  |
| username | Musi zawierać od 3 do 20 znaków, nie może być puste                                                                 |
| email    | Musi być w formacie poprawnego adresu email (musi zawierać '@'), nie może być puste                                 |
| password | Musi zawierać co najmniej: 1 wielką literę, 1 małą literę, 1 cyfrę, 1 znak specjalny, minimalna długość to 8 znaków |
w kodzie jest to klasa [RegisterDto](src/main/java/com/restapi/styleswap/payload/RegisterDto.java)

2. Logowanie użytkownika:
    ```
   curl --location 'http://localhost:8080/api/auth/login' \
    --header 'Content-Type: application/json' \
    --data '{   
    "usernameOrEmail": "test@email.com",
    "password": "Password123!"
    }'
    ```
    W przypadku poprawnego logowania, otrzymamy odpowiedź z kodem 200 oraz Odpowiedź z tokenem:
    ```JSON
    {
        "accessToken": "example_JWT_access_token",
        "tokenType": "Bearer",
        "userId": 3,
        "usernameOrEmail": "user2@email.com",
        "role": "[ROLE_USER]"
    }
    ```
    Wprowadzenie niepopranych danych skutkuje kodem 403 i wyrzuceniem wyjątku:
   ```JSON
   {
       "timestamp": "2024-10-21T07:55:09.732+00:00",
       "message": "Wrong username/email or password",
       "details": "uri=/api/auth/login"
   }
    ```

### Ubrania
Endpointy związane z przeglądaniem oferty sklepu są dostępne dla wszystkich użytkowników, <br>
    ale tutaj skupimy się na operacjach dla zalogowanych użytkowników (np. pisaniu wiadomości do włąścicieli i edycji ubrań). <br>
Dostępne są one w klasie [ClotheController](src/main/java/com/restapi/styleswap/controller/ClotheController.java),<br>
który następnie przekazuje zapytanie do serwisu [ClothesService](src/main/java/com/restapi/styleswap/service/impl/ClothesServiceImpl.java).<br>

1. Pobranie wszystkich ubrań z danej kategorii:<br>
    Akcja dostępna dla wszystkich użytkowników:
    ```
    curl --location 'http://localhost:8080/api/clothes/category/1'
    ```
    W przypadku poprawnego zapytania, otrzymamy odpowiedź z kodem 200 i listą ubrań w danej kategorii. <br>
    W przypadku podania nieistniejącego ID, otrzymamy kod 404:
   ```JSON
   {
       "timestamp": "2024-10-21T10:59:14.816+00:00",
       "message": "Category not found with id = 11",
       "details": "uri=/api/clothes/category/11"
   }
   ```

2. Dodanie nowego ubrania:<br>
    Akcja dostępna _tylko_ dla zalogowanych użytkowników: <br>
    Dodawane jest na raz ubranie oraz jego zdjęcia (max 5). <br>
    Reprezentacja dodawanego ubrania: [ClotheDto](src/main/java/com/restapi/styleswap/payload/ClotheDto.java) <br>
    Zdjęcie jest zapisywane na serwerze w folderze [images](src/main/resources/static/images). <br>
    Podczas zapisywania do oryginalnej nazwy pliku dodawany jest UUID, aby uniknąć konfliktów. <br>
   (Żeby pobrać zdjęcie należy użyć endpointu /api/images/{imageName})<br>
    ```
   curl --location 'http://localhost:8080/api/clothes' \
    --header 'Authorization: Bearer {acces_token_generated_while_logging}' \
    --form 'clothe="{
    \"name\":\"Knit Hoodie\",
    \"description\":\"Knit hoodie with a unique texture, perfect for fall\",
    \"price\":55.50,
    \"size\":\"M\",
    \"categoryId\":3,
    \"material\":\"cotton\"
    }";type=application/json' \
    --form 'images=@"Path_to_image_on_PC"' \
    ```
    W odpowiedzi otrzymamy kod 201 i informację o dodanym ubraniu:
   ```JSON
    {
       "id": 7,
       "name": "Knit Hoodie",
       "description": "Knit hoodie with a unique texture, perfect for fall",
       "price": 55.50,
       "size": "M",
       "images": [
          "fe63ba83-1494-4735-8165-1d6d125cbd3f_biedronka.png"
       ],
       "createdAt": "2024-10-21T13:47:57.541917+02:00",
       "updatedAt": "2024-10-21T13:47:57.542918+02:00",
       "material": "cotton",
       "views": 0,
       "categoryId": 3,
       "userId": 3,
       "conversasations": [],
       "available": false
   }
   ```
       W przypadku niepoprawnego zapytania, otrzymamy kod i odpowiednią informację o błędzie

3. Aktualizacja ubrania:<br>
    Akcja dostępna _tylko_ dla zalogowanych użytkowników, ponadto użytkownik musi być **właścicielem** ubrania.<br>
    Umożliwia zmianę danych ubrania oraz dodanie/usunięcie zdjęć - ich liczba cały czas musi być mniejsza niż 5.
    ```
   curl --location --request PUT 'http://localhost:8080/api/myclothes/3' \
    --header 'Authorization: Bearer {acces_token_generated_while_logging}' \
    --form 'newImages=@"path_to_new_image"' \
    --form 'deletedImages="[\"image_name_to_be_removed\"]";type=application/json'
    ```
    Przykłądowa odpowiedź:
    ```JSON
   {
      "id": 3,
      "name": "Vintage Graphic T-Shirt",
      "description": "Vintage-style graphic t-shirt for retro lovers",
      "price": 18.50,
      "size": "L",
      "images": [],
      "createdAt": "2024-10-21T00:00:00Z",
      "updatedAt": "2024-10-21T00:00:00Z",
      "material": null,
      "views": 15,
      "categoryId": 3,
      "userId": 2,
      "conversasations": [],
      "available": true
    }
    ```
4. usuwanie ubrania
   Akcja dostępna _tylko_ dla zalogowanych użytkowników, ponadto użytkownik musi być **właścicielem** ubrania.<br>
    ```
   curl --location --request DELETE 'http://localhost:8080/api/clothes/3' \
    --header 'Authorization: Bearer {acces_token_generated_while_logging}'
    ```
   W odpowiedzi otrzymamy kod 204, a ubranie zostanie oznaczony jako niedostępne.
                                                             (available=false)

### Konwersacje
Operacje związanie z konwersacjami są dostępne tylko **dla zalogowanych** użytkowników, <br>
    endpointy związane z konwersacjami są dostępne w klasie [MessagingController](src/main/java/com/restapi/styleswap/controller/MessagingController.java),<br>
    która przekazuje zapytanie do serwisu [MessagingService](src/main/java/com/restapi/styleswap/service/impl/MessagingServiceImpl.java).<br>

    Logika tworzenia i operowania na konwersacjach może być myląca, ponieważ zakłada ona,
    że kupujący (buyer) piszę do ubrania.
    Tak na prawdę wiadomości są przesyłane do właściciela danego ubrania (owner).
    Zaimplementowałem przesyłanie wiadomości w taki sposób, ponieważ możliwa jest sytuacja, w której
    buyer kupuje kilka ubrań od jednego właściciela, wtedy powinno istnieć kilka oddzielnych konwersacji między nimi.

1. Utworzenie nowej konwersacji:<br>
    Rozpoczęcie konwersacji z konkretnym 'ubraniem' (właścicielem ubrania)
    ```
   curl --location --request POST 'http://localhost:8080/api/conversations?clotheId=3' \
    --header 'Authorization: Bearer {acces_token_generated_while_logging}'
   ```
   W przypadku prawidłowego zapytania, otrzymamy kod 204, <br>
    Właściciel nie może rozpocząć konwersacji z samym sobą, skutkuje to błędem:
    ```JSON
   {
      "timestamp": "2024-10-22T09:02:12.197+00:00",
      "message": "We dont't talk to ourselves",
      "details": "uri=/api/conversations"
   }
    ```
   
2. Wyświetlenie listy konwersacji jako kupujący:<br>
    Wyświetlenie konwersacji, w których kupujący jest uczestnikiem
    ```
    curl --location 'http://localhost:8080/api/conversations/buying' \
   --header 'Authorization: Bearer {acces_token_generated_while_logging}'
   ```
   Przykładowa prawidłowa odpowiedź:
    ```JSON
    [
      {
          "id": 1,
          "buyerId": 3,
          "clotheId": 1
      }
    ]
    ```

3. Wyświetlenie _faktycznej_ konwersacji (listy wiadomości): 
    ```
   curl --location 'http://localhost:8080/api/conversations?conversationId=1' \
    --header 'Authorization: Bearer {acces_token_generated_while_logging}'
   ```
   Przy prawidłowym zapytaniu: 
   ```JSON
    [
       {
        "buyerId": 3,
        "clotheId": 1,
        "messageContent": "Hello, I would like to buy this T-Shirt, but half the price",
        "isBuyer": true
       },
       {
        "buyerId": 3,
        "clotheId": 1,
        "messageContent": "No, I won't sold it for 10$",
        "isBuyer": false
       }
    ]
   ```
   W przypadku, gdy osoba niebędąca kupującym/sprzedawcą będzie chciała sprawdzić konwersację, otrzyma błąd:
   ```JSON
   {
       "timestamp": "2024-10-22T09:02:12.197+00:00",
       "message": "You don't have permission to see this message",
       "details": "uri=/api/conversations?conversationId=1"
   }
   ```
   
4. Wysłanie nowej wiadomości:
    ```
   curl --location --request POST 'http://localhost:8080/api/conversations/send?conversationId=1' \
    --header 'Authorization: Bearer {acces_token_generated_while_logging}' \
   ```
    W odpowiedzi otrzymamy kod 200, a wiadomość zostanie dodana do konwersacji. <br>
    Podczas wysyłania wiadomości sprawdzane są uprawnienia użytkownika, <br>
    oraz ustawiane jest pole isBuyer na podstawie tego, czy użytkownik jest kupującym czy właścicielem ubrania. <br>
    Pole to jest wykorzystywane do wyświetlania wiadomości w odpowiednim miejscu (podczas tworzenia cztu na front-endzie)
    
    W headerze odpowiedzi dodana jest sekcja Location, która zawiera link do konwersacji:
    Location: `http://localhost:8080/api/conversations?conversationId=1`

## Testy
Przykłądowe testy jednostkowe znajdują się w folderze testowym [test](src/test/java/com/restapi/styleswap).<br>

komenda do uruchomienia testów:
`./mvnw test`