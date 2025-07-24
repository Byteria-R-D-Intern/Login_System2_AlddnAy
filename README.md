# Login_System2_AlddnAy
# Login_System2

Bu proje, Spring Boot ile geliştirilmiş JWT tabanlı bir kullanıcı giriş ve kayıt sistemidir. PostgreSQL veritabanı kullanır.

## Gereksinimler

- Java 17+
- Maven 3.8+
- PostgreSQL 12+
- Bir IDE (Örn: IntelliJ IDEA, VS Code)

## Kurulum

### 1. Projeyi Klonlayın

```bash
git clone https://github.com/kullaniciadi/Login_System2.git
cd Login_System2
```

### 2. Veritabanı Oluşturun

PostgreSQL'de aşağıdaki komutla bir veritabanı oluşturun:

```sql
CREATE DATABASE Login_System2;
```

### 3. `application.properties` Ayarları

`src/main/resources/application.properties` dosyasındaki veritabanı kullanıcı adı ve şifresini kendi PostgreSQL bilgilerinizle güncelleyin:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/Login_System2
spring.datasource.username=postgres
spring.datasource.password=****
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

server.port=8081
```

### 4. Bağımlılıklar

Gerekli bağımlılıklar `pom.xml` dosyasında tanımlanmıştır. Maven ile otomatik olarak indirilecektir.

### 5. Uygulamayı Derleyin ve Başlatın

```bash
mvn clean install
mvn spring-boot:run
```

Uygulama varsayılan olarak [http://localhost:8081](http://localhost:8081) adresinde çalışacaktır.

## API Kullanımı

### Kayıt Olma

```
POST /api/auth/register
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "sifre",
  "name": "Ad",
  "surname": "Soyad",
  "role": "USER"
}
```

### Giriş Yapma

```
POST /api/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "sifre"
}
```

Başarılı cevaplarda JWT token döner.

## Ek Bilgiler

- Swagger/OpenAPI arayüzü için: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- Güvenlik ayarları ve JWT ile ilgili kodlar `config` ve `infrastructure/Service` klasörlerinde bulunur.
