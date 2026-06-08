## Service Specification
### product-service, user-service, groupbuy-service 
-Gradle-Groovy\
-Spring Boot 4.0.5\
-Java 21

### inventory-service
-Gradle-Kotlin\
-Spring Boot 4.0.5\
-Java 21


## Service Responsibility Classification

1.groupbuy-service  
└─ groupbuy  
└─ participation  
└─ product 👉 product-service로 분리 가능

2.inventory-service

3.user-service
