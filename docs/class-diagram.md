````mermaid
erDiagram
    Product {
        String sku PK
        String name
        String description
        String animalType
        String category
        String img
        double price
        Date createdAt
        Date updatedAt
    }
    Basket {
        String user_id FK
        String product_sku FK
        int quantity
    }
    User {
        String id PK
        String firstName
        String lastName 
        String email 
        String phone 
        String password 
        String address
        boolean consent_spam
        Date createdAt
        Date updatedAt
    }
    Roles {
        int id PK
        String role
    }
    Orders {
        String id PK
        Enum status
        Date dateOrdered 
        String user_id FK
    }
    Products_in_order {
        String order_id FK
        String product_sku FK
        int quantity
    }
    Product }o--|| Basket : in
    User }o--|| Basket : has
    User }o--|{ Roles : has
    User }o--|| Orders : places
    Products_in_order ||--o{ Product : contains
    Products_in_order ||--|| Orders : is_of
````
