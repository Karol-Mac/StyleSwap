## PLAN PRACY NA NAJBLIŻSZY CZAS

 -[x] zastanów się nad zmianą endpointów dla ubrań.

 -[ ] dodatkowe funkcje:
   -[x] dodanie licznika ubrań w kategorii i licznika wyświetleń w ubraniu
   - [x] konwersacje między użytkownikami
       -[x] utwórz encję konwersacji i wiadomości
       -[x] zaimplementuj logikę (brak usuówania - nie widzę sensu tej funkcji)
       -[ ] zastanów się co robić z konwersacjami ubrań, które zostały sprzedane:
        (jak na razie w teorii można w nich pisać <- nie można tylko zacząć konwersacji ze sprzedanym ubraniem)
         - po prostu usunąć 
         - zostawić, ale zablokować możliwość wysyłania wiadomości

-[x] zwieksz poziom API na 3 - żeby działało jak strona internetowa
-[x] dodaj aspekt aktualizujący licznik ubrań w kategorii i licznik wyświetleń w ubraniu

  -[x] dodanie zamówień
    -[x] dodaj encję Order i Storage.
            Storage będzie przechowywał ubrania, które użytkownik chcę zakupić
            Order, to reprezentacja pojedynczego zamówienia - 1 sprzedawca, 1 kupujący
            W momencie kupienia wszystkich ubrań w koszyku tworzone jest kilka zamówień (sprawdź czy się da)
    -[x] połącz się z dostawcą płatności: Adyen/Stripe/PayU <- możliwe, że są tam jakieś opłaty
    -[ ] wysyłanie powiadomienia do sprzedawcy po zakupie jego ubrania
    -[ ] system ocen/opinii użytkowników (po zakupie ubrań)


 -[ ] napisz testy
   -[ ] najpierw jednostowe - zrobione po za funkcjami Storage (refactor trwa)
   -[ ] potem integracyjne (od strony serwera)
   -[ ] pamiętaj o security


Przeanalizuj wywołania Hibernate podczas wyświetlania ubrań ze Storage
(kiedyś był tam bug pomiędzy User i Storage - rozwiązany przez dodanie customowego `hashCode`),
ale sam nie do końca rozumiem czemu występował